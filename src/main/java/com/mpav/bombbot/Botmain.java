package com.mpav.bombbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//Next small thing up: Dynamically decide whether to print the whole minefield at once
//or go line-by-line.
//Next big thing up: add enums and helper functions to make this look presentable

public class Botmain extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, IOException {
        String key = new BufferedReader(new FileReader("key")).readLine();
        JDABuilder jdaB = JDABuilder.createDefault(key);
        jdaB.addEventListeners(new Botmain());
        jdaB.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] difficulties = {"easy", "medium", "hard", "brutal"};
        String[] trueMessages = {"do you want to defuse the bomb?", "are you scared?", "are you feeling OK?", "vent gas?", "are you going to win?"};
        String[] trueCorrectResponses = {"You try, but fail. At least you aren't dead.", "Good. A healthy dose of fear is necessary.", "Be safe, alright?", "You vent the gas. That feels better.", "That's the kind of attitude I like!"};
        String[] trueMismatchResponses = {"Inaction leads to consequences!", "Well, you should be.", "Uh oh...", "You start to feel a little weak...", "Hey! Be more confident next time!"};
        String[] falseMessages = {"do you not want to defuse the bomb?", "do you not not not not not want to defuse the bomb?", "do you bravely jump at the bomb?", "is the pressure getting to you?", "ready to quit?"};
        String[] falseCorrectResponses = {"Keep at it!", "Good catch!", "You're not brave, but at least you're not stupid.", "Confident as usual!", "Alright, then. But I want to see progress!"};
        String[] falseMismatchResponses = {"I mean, if you don't want to defuse it...", "Tricky, isn't it?", "You threw yourself... at a lit bomb...", "You start to choke up...", "Alright then. See you!"};

        if (event.getAuthor().isBot()) {
            return;
        }

        //System.out.println("Message received from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());

        Message message = event.getMessage();
        String[] messageComponents = message.getContentRaw().split(" ");

        //Add documentation for each game to reduce clutter here.
        if (messageComponents[0].equals("bomb!help")) {
            event.getChannel().sendMessage("```Hi! I'm a minigame bot for playing tons of games!\nHere's a list of the things I can do. If something's in brackets, it's optional.\n\nMinesweeper : bomb!mine [rows] [columns] [difficulty]\nMaximum size is 20x25, default is 10x10\nDifficulties are easy, medium, hard, and brutal,\ndefault is medium\nRemember to set \"Show Spoiler Content\" to \"On Click\".\n\nWires : bomb!wires @player1 @player2 number-of-sets\n\nBig Bomb : bomb!bigbomb```").queue();
            if (messageComponents.length > 10) {
                event.getChannel().sendMessage("```...dude, chill out a little...```").queueAfter(1, TimeUnit.SECONDS);
            }
        }

        if (messageComponents[0].equals("bomb!mine")) {
            //Rework this whole section to work with exact bomb numbers
            String difficulty = "medium";
            int rows = 10;
            int columns = 10;
            //TODO: could extract this whole part to functions, will do in the enum update
            if (messageComponents.length == 2 || messageComponents.length == 4) {
                if (!Arrays.asList(difficulties).contains(messageComponents[messageComponents.length - 1])) {
                    event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                    return;
                }
                difficulty = messageComponents[messageComponents.length - 1];
            }
            if (messageComponents.length == 3 || messageComponents.length == 4) {
                try {
                    rows = Integer.parseInt(messageComponents[1]);
                    columns = Integer.parseInt(messageComponents[2]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                    return;
                }
                if (rows < 5 || rows > 20 || columns < 5 || columns > 25) {
                    event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                    return;
                }
            }
            if (messageComponents.length > 5) {
                event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                return;
            }
            int difficultyNum = 0;
            switch (difficulty) {
                case "easy":
                    difficultyNum = 20;
                    break;
                case "medium":
                    difficultyNum = 40;
                    break;
                case "hard":
                    difficultyNum = 60;
                    break;
                case "brutal":
                    difficultyNum = 80;
                    break;
            }
            int[][] minefield = new int[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int bomb = (int) (Math.random() * 255);
                    if (bomb < difficultyNum)
                        minefield[i][j] = -1;
                }
            }
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (minefield[i][j] == -1)
                        continue;
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            if (k == 0 && l == 0)
                                continue;
                            if (i + k >= 0 && i + k < rows && j + l >= 0 && j + l < columns && minefield[i + k][j + l] == -1)
                                minefield[i][j]++;
                        }
                    }
                }
            }
            int randZeroX = (int) (Math.random() * minefield[0].length);
            int randZeroY = (int) (Math.random() * minefield.length);

            int failsafeCount = 0;
            while (minefield[randZeroY][randZeroX] != 0) {
                randZeroX = (int) (Math.random() * minefield[0].length);
                randZeroY = (int) (Math.random() * minefield.length);
                failsafeCount++;
                if (failsafeCount > 10000) {
                    event.getChannel().sendMessage("```There are almost definitely no zeroes in this minefield... good luck!```").queue();
                    randZeroX = -1;
                    randZeroY = -1;
                    break;
                }
            }

            String[] s = new String[rows];
            Arrays.fill(s, "");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (minefield[i][j] == -1) {
                        s[i] += "||:bomb:||";
                    }
                    if (minefield[i][j] == 0 && i == randZeroY && j == randZeroX) {
                        s[i] += ":zero:";
                    } else if (minefield[i][j] == 0)
                        s[i] += "||:zero:||";
                    if (minefield[i][j] == 1) {
                        s[i] += "||:one:||";
                    }
                    if (minefield[i][j] == 2) {
                        s[i] += "||:two:||";
                    }
                    if (minefield[i][j] == 3) {
                        s[i] += "||:three:||";
                    }
                    if (minefield[i][j] == 4) {
                        s[i] += "||:four:||";
                    }
                    if (minefield[i][j] == 5) {
                        s[i] += "||:five:||";
                    }
                    if (minefield[i][j] == 6) {
                        s[i] += "||:six:||";
                    }
                    if (minefield[i][j] == 7) {
                        s[i] += "||:seven:||";
                    }
                    if (minefield[i][j] == 8) {
                        s[i] += "||:eight:||";
                    }
                }
            }

            //Cannot send as one message because of character maximum
            //Sending in groups of 9-ish would be optimal, but anything other than all or 1 at a time has weird spacing problems
            //TODO: Let users input a variable for the "fast but uneven" option.
            //Will do when functions are made

            StringBuilder msg = new StringBuilder();
            for (String value : s) {
                msg.append(value).append("\n");
            }
            System.out.println(msg.length());
            if (!msg.toString().equals("") && msg.length() <= 2000) {
                event.getChannel().sendMessage("```Generated!```").queue();
                event.getChannel().sendMessage(msg).queue();
            } else if (!msg.toString().equals("")) {
                event.getChannel().sendMessage("```Generating... please wait!```").queue();
                try {
                    for (String line : s) {
                        event.getChannel().sendMessage(line).queue();
                        Thread.sleep(1100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (messageComponents[0].equals("bomb!wires")) {
            if (message.getMentionedUsers().size() != 2 || messageComponents.length > 4) {
                event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                return;
            }
            int numSets = 1;
            int time = 0;
            if (messageComponents.length == 4) {
                try {
                    numSets = Integer.parseInt(messageComponents[3]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                    return;
                }
            }
            StringBuilder playerOneContent = new StringBuilder("```Rules are prioritized from top to bottom. If a rule cannot be fulfilled, move on.```");
            StringBuilder playerTwoContent = new StringBuilder();
            final String[] colors = {"red", "blue", "black", "white"};
            final String[] ruleTypes = {"noneof", "twoormore", "exactlyone", "else"};
            final String[] placements = {"first", "last", "second"};
            for (int set = 0; set < numSets; set++) {
                int numWires = (int) (Math.random() * 3) + 4;
                int numRules = (int) (Math.random() * 3) + 3;
                int[] numColors = new int[colors.length];
                if (numRules > numWires)
                    numRules = numWires;
                String[] wires = new String[numWires];
                String[][] rules = new String[numRules][4];
                String[] safeRule = new String[4];
                int safeWire = 0;
                boolean safeWirePlaced = false;

                for (int i = 0; i < numWires; i++) {
                    String color = colors[(int) (Math.random() * colors.length)];
                    if (color.equals("red"))
                        numColors[0]++;
                    if (color.equals("blue"))
                        numColors[1]++;
                    if (color.equals("black"))
                        numColors[2]++;
                    if (color.equals("white"))
                        numColors[3]++;
                    wires[i] = color;
                }

                for (int i = 0; i < numRules - 1; i++) {

                    String rule = ruleTypes[(int) (Math.random() * 3)];
                    rules[i][0] = rule;
                    rules[i][1] = colors[(int) (Math.random() * colors.length)];
                    String cutColor = colors[(int) (Math.random() * colors.length)];
                    if (cutColor.equals("red") && numColors[0] <= 1 || cutColor.equals("blue") && numColors[1] <= 1 || cutColor.equals("black") && numColors[2] <= 1 || cutColor.equals("white") && numColors[3] <= 1) {
                        rules[i][2] = placements[(int) (Math.random() * 2)];
                    } else {
                        rules[i][2] = placements[(int) (Math.random() * placements.length)];
                    }
                    rules[i][3] = cutColor;

                    if (cutColor.equals("red") && numColors[0] > 0 || cutColor.equals("blue") && numColors[1] > 0 || cutColor.equals("black") && numColors[2] > 0 || cutColor.equals("white") && numColors[3] > 0) {
                        if (rule.equals("noneof")) {
                            if (rules[i][1].equals("red") && numColors[0] == 0 || rules[i][1].equals("blue") && numColors[1] == 0 || rules[i][1].equals("black") && numColors[2] == 0 || rules[i][1].equals("white") && numColors[3] == 0) {
                                if (!safeWirePlaced) {
                                    safeRule = rules[i];
                                    safeWirePlaced = true;
                                }
                            }
                        }
                        if (rule.equals("twoormore")) {
                            if (rules[i][1].equals("red") && numColors[0] >= 2 || rules[i][1].equals("blue") && numColors[1] >= 2 || rules[i][1].equals("black") && numColors[2] >= 2 || rules[i][1].equals("white") && numColors[3] >= 2) {
                                if (!safeWirePlaced) {
                                    safeRule = rules[i];
                                    safeWirePlaced = true;
                                }
                            }
                        }
                        if (rule.equals("exactlyone")) {
                            if (rules[i][1].equals("red") && numColors[0] == 1 || rules[i][1].equals("blue") && numColors[1] == 1 || rules[i][1].equals("black") && numColors[2] == 1 || rules[i][1].equals("white") && numColors[3] == 1) {
                                if (!safeWirePlaced) {
                                    safeRule = rules[i];
                                    safeWirePlaced = true;
                                }
                            }
                        }
                    }
                }

                rules[numRules - 1][0] = "else";
                String cutColor = colors[(int) (Math.random() * colors.length)];
                while (cutColor.equals("red") && numColors[0] == 0 || cutColor.equals("blue") && numColors[1] == 0 || cutColor.equals("black") && numColors[2] == 0 || cutColor.equals("white") && numColors[3] == 0) {
                    cutColor = colors[(int) (Math.random() * colors.length)];
                }
                if (cutColor.equals("red") && numColors[0] <= 1 || cutColor.equals("blue") && numColors[1] <= 1 || cutColor.equals("black") && numColors[2] <= 1 || cutColor.equals("white") && numColors[3] <= 1) {
                    rules[numRules - 1][2] = placements[(int) (Math.random() * 2)];
                } else {
                    rules[numRules - 1][2] = placements[(int) (Math.random() * placements.length)];
                }
                rules[numRules - 1][3] = cutColor;
                if (!safeWirePlaced)
                    safeRule = rules[numRules - 1];

                for (String[] rule : rules) {
                    if (rule[0].equals("noneof"))
                        playerOneContent.append("```If there are no ").append(rule[1]).append(" wires, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                    if (rule[0].equals("exactlyone"))
                        playerOneContent.append("```If there is exactly one ").append(rule[1]).append(" wire, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                    if (rule[0].equals("twoormore"))
                        playerOneContent.append("```If there are two or more ").append(rule[1]).append(" wires, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                    if (rule[0].equals("else"))
                        playerOneContent.append("```Otherwise, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                }

                if (safeRule[2].equals("first")) {
                    for (int i = 0; i < numWires; i++) {
                        if (wires[i].equals(safeRule[3])) {
                            safeWire = i;
                            break;
                        }
                    }
                }
                if (safeRule[2].equals("second")) {
                    int count = 0;
                    for (int i = 0; i < numWires; i++) {
                        if (wires[i].equals(safeRule[3])) {
                            count++;
                            if (count == 2) {
                                safeWire = i;
                                break;
                            }
                        }
                    }
                }
                if (safeRule[2].equals("last")) {
                    for (int i = numWires - 1; i >= 0; i--) {
                        if (wires[i].equals(safeRule[3])) {
                            safeWire = i;
                            break;
                        }
                    }
                }

                for (int i = 0; i < numWires; i++) {
                    if (wires[i].equals("red")) {
                        if (i == safeWire)
                            playerTwoContent.append(":red_circle:||:white_check_mark:||:red_circle:\n");

                        else
                            playerTwoContent.append(":red_circle:||:bomb:||:red_circle:\n");
                    }
                    if (wires[i].equals("blue")) {
                        if (i == safeWire)
                            playerTwoContent.append(":blue_circle:||:white_check_mark:||:blue_circle:\n");

                        else
                            playerTwoContent.append(":blue_circle:||:bomb:||:blue_circle:\n");
                    }
                    if (wires[i].equals("black")) {
                        if (i == safeWire)
                            playerTwoContent.append(":black_circle:||:white_check_mark:||:black_circle:\n");

                        else
                            playerTwoContent.append(":black_circle:||:bomb:||:black_circle:\n");
                    }
                    if (wires[i].equals("white")) {
                        if (i == safeWire)
                            playerTwoContent.append(":white_circle:||:white_check_mark:||:white_circle:\n");

                        else
                            playerTwoContent.append(":white_circle:||:bomb:||:white_circle:\n");
                    }
                }

                int indexOne = (int) (Math.random() * 2);
                int indexTwo = indexOne == 0 ? 1 : 0;
                User userOne = message.getMentionedUsers().get(indexOne);
                User userTwo = message.getMentionedUsers().get(indexTwo);
                sendPrivateMessage(userOne, playerOneContent.toString());
                sendPrivateMessage(userTwo, playerTwoContent.toString());
                time += numRules * numWires * 2;
                playerOneContent = new StringBuilder();
                playerTwoContent = new StringBuilder();
            }
            event.getChannel().sendMessage("```The wires were sent to one player, and the instructions to solve were sent to the other. You have " + time + " seconds to solve. Good luck!```").queue();
            event.getChannel().sendMessage("```Time's up! Whether you solved it or not, this was a learning experience.```").queueAfter(time, TimeUnit.SECONDS);
        }

        if (messageComponents[0].equals("bomb!bigbomb")) {

            ArrayList<User> players = new ArrayList<>();
            event.getChannel().sendMessage("```Welcome to Big Bomb! Respond right, or face the consequences! Click the check mark now to join!```").queue(
                    msg -> {
                        msg.addReaction("\u2705").queue();
                        event.getChannel().retrieveMessageById(msg.getId()).queueAfter(10, TimeUnit.SECONDS,
                                msgTwo -> msgTwo.getReactions().get(0).retrieveUsers().forEachAsync((u) ->
                                {
                                    if (!u.isBot()) {
                                        players.add(u);
                                        event.getChannel().sendMessage("```" + u.getName() + ", you're playing!```").queue();
                                    }
                                    return true;
                                }));
                    });
            try {
                Thread.sleep(11000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long time = 5;
            int count = 0;
            if (players.size() == 0)
                event.getChannel().sendMessage("```Aww.... nobody wanted to play!```").queue();
            else if (players.size() == 1)
                event.getChannel().sendMessage("```That's really kind of you... but we need at least 2 people to play!```").queue();
            else {
                event.getChannel().sendMessage("```Time's up! Let's get started!```").queue();
                while (players.size() > 1) {
                    boolean yesStatement = (int) (Math.random() * 2) == 0;
                    RestAction<Message> action;
                    User user = players.get((int) (Math.random() * players.size()));
                    int index;
                    if (yesStatement) {
                        index = (int) (Math.random() * trueMessages.length);
                        action = event.getChannel().sendMessage("```" + user.getName() + ", " + trueMessages[index] + "```");
                    } else {
                        index = (int) (Math.random() * falseMessages.length);
                        action = event.getChannel().sendMessage("```" + user.getName() + ", " + falseMessages[index] + "```");
                    }
                    AtomicBoolean isSafeYes = new AtomicBoolean(false);
                    AtomicBoolean isSafeNo = new AtomicBoolean(false);
                    AtomicInteger oldLength = new AtomicInteger(players.size());
                    long finalTime = time;
                    action.queue(
                            msg -> {
                                msg.addReaction("\uD83C\uDDFE").queue();
                                msg.addReaction("\uD83C\uDDF3").queue();
                                event.getChannel().retrieveMessageById(msg.getId()).queueAfter(finalTime, TimeUnit.SECONDS,
                                        msgTwo -> {
                                            msgTwo.getReactions().get(0).retrieveUsers().forEachAsync((u) ->
                                            {
                                                if (u.equals(user) && !u.isBot()) {
                                                    if (yesStatement) {
                                                        event.getChannel().sendMessage("```" + trueCorrectResponses[index] + "```").queue();
                                                        isSafeYes.set(true);
                                                    } else {
                                                        event.getChannel().sendMessage("```" + falseMismatchResponses[index] + " You're out!```").queue();
                                                        players.remove(user);
                                                    }
                                                    return false;
                                                }
                                                return true;
                                            });
                                            msgTwo.getReactions().get(1).retrieveUsers().forEachAsync((u) ->
                                            {
                                                if (u.getName().equals(user.getName()) && !u.isBot()) {
                                                    if (!yesStatement) {
                                                        event.getChannel().sendMessage("```" + falseCorrectResponses[index] + "```").queue();
                                                        isSafeNo.set(true);
                                                    } else {
                                                        event.getChannel().sendMessage("```" + trueMismatchResponses[index] + "```").queue();
                                                        players.remove(user);
                                                    }
                                                    return false;
                                                }
                                                return true;
                                            });

                                        });
                            });
                    try {
                        Thread.sleep(time * 1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isSafeYes.get() && !isSafeNo.get() && oldLength.get() == players.size()) {
                        event.getChannel().sendMessage("```Oops! Out of time...```").queue();
                        players.remove(user);
                    }
                    count++;
                    if (count >= 10 && time > 1) {
                        time--;
                        count = 0;
                    }
                    try {
                        Thread.sleep(time * 200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                event.getChannel().sendMessage("```Congratulations, " + players.get(0).getName() + ", you win!```").queue();
            }
        }
        //   if(messageComponents[0].equals("bomb!meme")){
        //       event.getChannel().sendMessage().queue();
        //   }

    }

    private void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(content).queue());
    }
}

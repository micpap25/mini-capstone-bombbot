import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Botmain extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaB = new JDABuilder(AccountType.BOT);
        String token = "NTgzNjU3NTU5Nzk0MTIyNzUy.XPAerg.Nx7Nw1VJG5j3Ttc6Roi9RXpAT4Y";
        jdaB.setToken(token);
        jdaB.addEventListener(new Botmain());
        jdaB.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        System.out.println("Message recieved from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());

        Message message = event.getMessage();
        String[] messageComponents = message.getContentRaw().split(" ");

        if(messageComponents[0].equals("bomb!help")){
            event.getChannel().sendMessage("``` Hi! I'm a minigame bot for playing tons of games!\n\n Minesweeper : bomb!mine\n Wires : bomb!wire @player1 @player2\n Server Scatter : bomb!scatter\n BOOM!```").queue();
            if(messageComponents.length > 10){
                event.getChannel().sendMessage("```...dude, chill out a little...```").queueAfter(1, TimeUnit.SECONDS);
            }
        }

        if (messageComponents[0].equals("bomb!mine")){
            int[][] minefield = new int[15][15];

            for (int i = 0; i < minefield.length; i++) {
                for (int j = 0; j < minefield[i].length; j++) {
                    int bomb = (int) (Math.random() * 255);
                    if (bomb < 40)
                        minefield[i][j] = -1;
                }
            }
            for (int i = 0; i < minefield.length; i++) {
                for (int j = 0; j < minefield[i].length; j++) {
                    if(minefield[i][j] == -1)
                        continue;
                    for(int k = -1; k <= 1; k++){
                        for(int l = -1; l <= 1; l++){
                            if(k==0&&l==0)
                                continue;
                            if(i+k >= 0 && i+k < minefield.length && j+l >= 0 && j+l < minefield.length && minefield[i+k][j+l] == -1)
                                minefield[i][j]++;
                        }
                    }
                }
            }
            ArrayList<Point> randomStart = new ArrayList<>();
            for (int i = 0; i < minefield.length; i++) {
                for (int j = 0; j < minefield[i].length; j++) {
                    if (minefield[i][j] == 0) {
                        Point p = new Point(i, j);
                        randomStart.add(p);
                    }
                }
            }
            int randStart = (int) (Math.random() * randomStart.size());

            Point startingPoint = randomStart.get(randStart);
            String[] s = new String[15];
            for (int i = 0; i<s.length;i++ ) {
                s[i] = "";
            }
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < minefield[i].length; j++) {
                    if (minefield[i][j] == -1) {
                        s[i] += "||:bomb:||";
                    }
                    if (minefield[i][j] == 0 && i == startingPoint.y || j == startingPoint.x) {
                        s[i] += ":zero:";
                    }
                    else if (minefield[i][j] == 0 )
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
            event.getChannel().sendMessage("```Generating...```").queue();
            for (String value : s) event.getChannel().sendMessage(value).queue();
        }

        if (messageComponents[0].equals("bomb!wires")){
            if(message.getMentionedUsers().size() != 2){
                event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                return;
            }

            StringBuilder playeronecontent = new StringBuilder("```Rules are prioritized from top to bottom. If a rule cannot be fulfilled, move on.```");
            StringBuilder playertwocontent = new StringBuilder();
            final String[] colors = {"red", "blue", "black", "white"};
            final String[] ruletypes = {"noneof", "twoormore", "exactlyone", "else"};
            final String[] placements = {"first", "last", "second"};
            int numWires = (int)(Math.random()*3) + 3;
            int numRules = (int)(Math.random()*3)+3;
            int[] numColors = new int[colors.length];
            if (numRules > numWires)
                numRules = numWires;
            String[] wires = new String[numWires];
            String[][] rules = new String[numRules][4];
            String[] safeRule = new String[4];
            int safeWire = 0;
            boolean safeWirePlaced = false;

            for(int i = 0; i<numWires; i++){
                String color = colors[(int)(Math.random()*colors.length)];
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

            for(int i = 0; i < numRules - 1; i++) {

                String rule = ruletypes[(int) (Math.random() * 3)];
                rules[i][0] = rule;
                rules[i][1] = colors[(int) (Math.random() * colors.length)];
                String cutcolor = colors[(int) (Math.random() * colors.length)];
                if (cutcolor.equals("red") && numColors[0] <= 1 || cutcolor.equals("blue") && numColors[1] <= 1 || cutcolor.equals("black") && numColors[2] <= 1 || cutcolor.equals("white") && numColors[3] <= 1) {
                    rules[i][2] = placements[(int) (Math.random() * 2)];
                } else {
                    rules[i][2] = placements[(int) (Math.random() * placements.length)];
                }
                rules[i][3] = cutcolor;

                if (cutcolor.equals("red") && numColors[0] > 0|| cutcolor.equals("blue") && numColors[1] > 0||cutcolor.equals("black") && numColors[2] > 0||cutcolor.equals("white") && numColors[3] > 0){
                    if(rule.equals("noneof")){
                        if(rules[i][1].equals("red") && numColors[0] == 0|| rules[i][1].equals("blue") && numColors[1] == 0||rules[i][1].equals("black") && numColors[2] == 0||rules[i][1].equals("white") && numColors[3] == 0) {
                            if (!safeWirePlaced) {
                                safeRule = rules[i];
                                safeWirePlaced = true;
                            }
                        }
                    }
                    if(rule.equals("twoormore")){
                        if(rules[i][1].equals("red") && numColors[0] >=2|| rules[i][1].equals("blue") && numColors[1] >=2||rules[i][1].equals("black") && numColors[2]>=2||rules[i][1].equals("white") && numColors[3] >=2) {
                            if (!safeWirePlaced) {
                                safeRule = rules[i];
                                safeWirePlaced = true;
                            }
                        }
                    }
                    if(rule.equals("exactlyone")){
                        if(rules[i][1].equals("red") && numColors[0] == 1|| rules[i][1].equals("blue") && numColors[1] == 1||rules[i][1].equals("black") && numColors[2] == 1||rules[i][1].equals("white") && numColors[3] == 1) {
                            if (!safeWirePlaced) {
                                safeRule = rules[i];
                                safeWirePlaced = true;
                            }
                        }
                    }
                }
            }

            rules[numRules - 1][0] = "else";
            String cutcolor = colors[(int)(Math.random()*colors.length)];
            while (cutcolor.equals("red") && numColors[0] == 0|| cutcolor.equals("blue") && numColors[1] == 0||cutcolor.equals("black") && numColors[2] == 0||cutcolor.equals("white") && numColors[3] == 0){
                cutcolor = colors[(int)(Math.random()*colors.length)];
            }
            if (cutcolor.equals("red") && numColors[0] <= 1 || cutcolor.equals("blue") && numColors[1] <= 1 || cutcolor.equals("black") && numColors[2] <= 1 || cutcolor.equals("white") && numColors[3] <= 1) {
                rules[numRules - 1][2] = placements[(int) (Math.random() * 2)];
            } else {
                rules[numRules - 1][2] = placements[(int) (Math.random() * placements.length)];
            }
            rules[numRules - 1][3] = cutcolor;
            if (!safeWirePlaced)
                safeRule = rules[numRules - 1];

            for (String[] rule : rules) {
                if (rule[0].equals("noneof"))
                    playeronecontent.append("```If there are no ").append(rule[1]).append(" wires, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                if (rule[0].equals("exactlyone"))
                    playeronecontent.append("```If there is exactly one ").append(rule[1]).append(" wire, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                if (rule[0].equals("twoormore"))
                    playeronecontent.append("```If there are two or more ").append(rule[1]).append(" wires, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
                if (rule[0].equals("else"))
                    playeronecontent.append("```Otherwise, cut the ").append(rule[2]).append(" ").append(rule[3]).append(" wire.```");
            }

            if(safeRule[2].equals("first")) {
                for (int i =0; i < numWires;i++){
                    if(wires[i].equals(safeRule[3])){
                        safeWire = i;
                        break;
                    }
                }
            }
            if(safeRule[2].equals("second")) {
                int count = 0;
                for (int i =0; i < numWires;i++){
                    if(wires[i].equals(safeRule[3])){
                        count++;
                        if (count == 2){
                            safeWire = i;
                            break;
                        }
                    }
                }
            }
            if(safeRule[2].equals("last")) {
                for (int i = numWires - 1; i >=0;i--){
                    if(wires[i].equals(safeRule[3])){
                        safeWire = i;
                        break;
                    }
                }
            }

            for(int i = 0; i < numWires; i++){
                if(wires[i].equals("red")){
                    if(i == safeWire)
                        playertwocontent.append(":red_circle:||:white_check_mark:||:red_circle:\n");

                    else
                        playertwocontent.append(":red_circle:||:bomb:||:red_circle:\n");
                }
                if(wires[i].equals("blue")){
                    if(i == safeWire)
                        playertwocontent.append(":large_blue_circle:||:white_check_mark:||:large_blue_circle:\n");

                    else
                        playertwocontent.append(":large_blue_circle:||:bomb:||:large_blue_circle:\n");
                }
                if(wires[i].equals("black")){
                    if(i == safeWire)
                        playertwocontent.append(":black_circle:||:white_check_mark:||:black_circle:\n");

                    else
                        playertwocontent.append(":black_circle:||:bomb:||:black_circle:\n");
                }
                if(wires[i].equals("white")){
                    if(i == safeWire)
                        playertwocontent.append(":white_circle:||:white_check_mark:||:white_circle:\n");

                    else
                        playertwocontent.append(":white_circle:||:bomb:||:white_circle:\n");
                }
            }

            int indexone = (int)(Math.random() * 2);
            int indextwo = indexone == 0 ? 1 : 0;
            User userone = message.getMentionedUsers().get(indexone);
            User usertwo = message.getMentionedUsers().get(indextwo);
            sendPrivateMessage(userone, playeronecontent.toString());
            sendPrivateMessage(usertwo, playertwocontent.toString());
            int time = numRules * numWires * 5;
            event.getChannel().sendMessage("```The wires were sent to one player, and the instructions to solve were sent to the other. You have " + time + " seconds to solve. Good luck!```").queue();
            event.getChannel().sendMessage("```Time's up! Whether you solved it or not, this was a learning experience.```").queueAfter(time, TimeUnit.SECONDS);
        }

        if (messageComponents[0].equals("bomb!scatter")){
            event.getChannel().sendMessage("```Hahaha no```").queue();
        }

    }
    class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof Point)) {
                return false;
            } else {
                Point p = (Point) obj;
                return this.x == p.x && this.y == p.y;
            }
        }
    }

    private void sendPrivateMessage(User user, String content)
    {
        // notice that we are not placing a semicolon (;) in the callback this time!
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).queue() );
    }
}

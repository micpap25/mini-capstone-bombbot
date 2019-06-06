import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
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

        }

        if (messageComponents[0].equals("bomb!wires")){
            if(message.getMentionedUsers().size() != 2){
                event.getChannel().sendMessage("```Please use the proper format! Type bomb!help for more info.```").queue();
                return;
            }
            event.getChannel().sendMessage("```That's the right format! It's too bad I don't know how to play that!```").queue();

        }

        if (messageComponents[0].equals("bomb!scatter")){

        }

    }
    public void sendPrivateMessage(User user, String content)
    {
        // notice that we are not placing a semicolon (;) in the callback this time!
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).queue() );
    }
}

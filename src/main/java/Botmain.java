import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

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

        if(event.getMessage().getContentRaw().equals("bomb!help")){
            event.getChannel().sendMessage("``` Hi! I'm a minigame bot for playing tons of games!\n\n Minesweeper : bomb!mine\n Wires : bomb!wire @player1 @player2\n Server Scatter : bomb!scatter\n BOOM!```").queue();
        }

        if (event.getMessage().getContentRaw().equals("bomb!mine")){

        }

        if (event.getMessage().getContentRaw().equals("bomb!wires")){

        }

        if (event.getMessage().getContentRaw().equals("bomb!sca")){

        }

    }
}

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Botmain extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        JDABuilder jda = new JDABuilder(AccountType.BOT);
        String token = "NTgzNjU3NTU5Nzk0MTIyNzUy.XPAerg.Nx7Nw1VJG5j3Ttc6Roi9RXpAT4Y";
        jda.setToken(token);
        jda.addEventListener(new Botmain());
        jda.buildAsync();
    }
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }
        System.out.println("Message recieved from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());
        if(event.getMessage().getContentRaw().equals("bomb!help")){
            event.getChannel().sendMessage("Hi! I'm a minigame bot for playing games like Minesweeper, Wires, and Server Scatter! BOOM!").queue();
        }
    }
}

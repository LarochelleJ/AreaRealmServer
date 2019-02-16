package eu.area.handlers;

import eu.area.kernel.Console;
import eu.area.kernel.Main;
import eu.area.objects.Client;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Meow on 2017-01-24.
 */
public class LoginHandler extends IoHandlerAdapter {
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Console.println("> LoginHandlerError: " + cause.toString(), Console.Color.RED);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println(session.getRemoteAddress());
        Console.println("< Nouvelle session : " + session.getId(), Console.Color.CYAN);
        if (Main.isMaintenance()) {
            session.write("M040|Le serveur est en actuellement en maintenance, merci de votre compréhension. N'hésitez pas à rejoindre notre Discord !");
            session.close(false);
        } else {
            Client client = new Client(session);
            client.send("HC" + client.getKey());
            client.setStatus(Client.Status.WAIT_VERSION);
            Main.getClients().put(session.getId(), client);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String packet = message.toString();
        Console.println("< " + packet + " < Session " + session.getId(), Console.Color.CYAN);
        try {
            String[] args = packet.split("\n");
            Main.getClients().get(session.getId()).parse(args);
        } catch (Exception e) {
            Console.println("> messageReceivedError: " + e.toString(), Console.Color.RED);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Main.getClients().remove(session.getId());
        Console.println("> Session fermé : " + session.getId(), Console.Color.CYAN);
    }
}

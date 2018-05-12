package eu.area.servers;

import eu.area.handlers.LoginHandler;
import eu.area.kernel.Console;
import eu.area.kernel.Main;
import lombok.Getter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by Meow on 2017-01-24.
 */


public class LoginServer {
    private IoAcceptor acceptor = new NioSocketAcceptor();

    public void start() {
        Console.println("> Démarrage du LoginServer", Console.Color.GREEN);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, new LineDelimiter("\n\0"))));
        acceptor.setHandler(new LoginHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        ((NioSocketAcceptor)acceptor).setReuseAddress(true);
        try {
            acceptor.bind(new InetSocketAddress(Main.getConfig().getLogin_port()));
        } catch (IOException e) {
            Main.setRunning(false);
            Console.println("> Démarrage du serveur aborté !", Console.Color.RED);
        }

        if (acceptor.isActive()) {
            Console.println("> LoginServer démarré sur le port " + Main.getConfig().getLogin_port() +" !", Console.Color.YELLOW);
        }
    }

    public void stop() {
        Console.println("> Fermeture du serveur ", Console.Color.GREEN);
        if (acceptor.isActive()) {
            for (IoSession client : acceptor.getManagedSessions().values()) {
                client.closeNow();
            }
            acceptor.unbind();
            acceptor.dispose();
        }
        Console.println("> Fermeture réussite !", Console.Color.YELLOW);
    }
}

package eu.area.objects;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Meow on 2017-01-26.
 */
public class Serveur {
    @Getter
    private int id;
    @Getter
    private boolean enabled;
    @Getter
    private int gmRequired;
    @Getter
    private String ip;
    @Getter
    private int port;
    @Getter
    private boolean online;
    @Getter
    private String ipPlayer;
    @Getter
    @Setter
    private boolean onlineBefore;

    public Serveur(int id, int enabled, int gmRequired, String ip, String ipPlayer, int port) {
        this.id = id;
        this.enabled = enabled == 1 ? true : false;
        this.gmRequired = gmRequired;
        this.ip = ip;
        this.ipPlayer = ipPlayer;
        this.port = port;
        verifIfOnline();
        onlineBefore = online;
    }

    public void verifIfOnline() {
        online = true;
        try (Socket s = new Socket(InetAddress.getByName(ip), port)) {
            if (s.isConnected()) {
                s.close();
            }
        } catch (IOException e) {
            online = false;
        }
    }
}

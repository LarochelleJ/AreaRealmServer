package eu.area.objects;

import lombok.Getter;
import lombok.Setter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

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
    @Getter @Setter
    private boolean onlineBefore;

    public Serveur(int id, int enabled, int gmRequired, String ip, int port) {
        this.id = id;
        this.enabled = enabled == 1 ? true : false;
        this.gmRequired = gmRequired;
        this.ip = ip;
        this.port = port;
        verifIfOnline();
        onlineBefore = online;
    }

    public void verifIfOnline() {
        online = true;
        try {
            Socket s  = new Socket(InetAddress.getByName(ip), port);
            s.close();
        } catch (Exception e) {
            online = false;
        }
    }
}

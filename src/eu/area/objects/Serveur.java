package eu.area.objects;

import lombok.Getter;

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

    public Serveur(int id, int enabled, int gmRequired, String ip, int port) {
        this.id = id;
        this.enabled = enabled == 1 ? true : false;
        this.gmRequired = gmRequired;
        this.ip = ip;
        this.port = port;
    }

    public boolean isOnline() {
        boolean online = true;
        try {
            Socket s  = new Socket(InetAddress.getByName(ip), port);
            s.close();
        } catch (Exception e) {
            online = false;
        }
        return online;
    }
}

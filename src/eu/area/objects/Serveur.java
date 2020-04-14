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
    private String ipAdmin;
    @Getter
    private String ipv6;
    @Getter
    private String ipv6Admin;
    @Getter
    @Setter
    private boolean onlineBefore;
    private int accessLevel;

    public Serveur(int id, int enabled, int gmRequired, String ip, String ipPlayer, String ipv6, int port, String ipAdmin, String ipv6Admin, int accessLevel) {
        this.id = id;
        this.enabled = enabled == 1;
        this.accessLevel = accessLevel;
        this.gmRequired = gmRequired;
        this.ip = ip;
        this.ipPlayer = ipPlayer;
        this.ipv6 = ipv6;
        this.ipAdmin = ipAdmin;
        this.ipv6Admin = ipv6Admin;
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

    public boolean canAccess(Compte c){
        return !((gmRequired != 0 && c.getGmLevel() < gmRequired) && (accessLevel != 0 && c.getAccessLevel() < accessLevel));
    }
}

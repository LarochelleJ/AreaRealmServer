package eu.area.objects;

import eu.area.eu.area.mysql.MySql;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Meow on 2017-01-24.
 */
public class Compte {
    @Getter
    private String password;
    @Getter
    private String saltKey;
    @Getter
    private boolean blocked;
    @Getter
    private boolean banned;
    @Getter
    private boolean logged;
    @Getter @Setter
    private String pseudo;
    @Getter
    private String question;
    @Getter
    private int guid;
    @Getter
    private String name;
    @Getter
    private int gmLevel;
    @Getter
    private boolean vip;
    @Getter
    private boolean valid;
    @Getter @Setter
    private List<String> ipAllowed;
    public Compte(String password, String saltKey, int m_password, int banned, int logged, String pseudo, String question, int guid, String name, int gmLevel, int bannedTime, int vip, int m_reg) {
        this.password = password;
        this.saltKey = saltKey;
        blocked = /*m_password > 0 ? true : false*/m_password > 0;
        this.banned = /*banned > 0 ? true : false*/banned > 0;
        if (bannedTime != -1 && bannedTime < System.currentTimeMillis() / 1000) { // Banissement expiré, on le deban
            this.banned = false;
            MySql.unbanAccount(guid);
        }
        this.logged = /*logged > 0 ? true : false*/logged > 0;
        this.pseudo = pseudo;
        this.question = question;
        this.guid = guid;
        this.name = name;
        this.gmLevel = gmLevel;
        this.vip = /*vip > 0 ? true : false*/vip > 0;
        this.valid = /*m_reg > 0 ? false : true*/ m_reg > 0;
    }
}

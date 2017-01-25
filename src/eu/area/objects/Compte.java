package eu.area.objects;

import lombok.Getter;

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
    @Getter
    private String pseudo;
    @Getter
    private String question;
    @Getter
    private int guid;
    public Compte(String password, String saltKey, int m_password, int banned, int logged, String pseudo, String question, int guid) {
        this.password = password;
        this.saltKey = saltKey;
        blocked = m_password > 0 ? true : false;
        this.banned = banned > 0 ? true : false;
        this.logged = logged > 0 ? true : false;
        this.pseudo = pseudo;
        this.question = question;
        this.guid = guid;
    }
}

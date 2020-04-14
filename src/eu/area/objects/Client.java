package eu.area.objects;

import eu.area.eu.area.mysql.MySql;
import eu.area.kernel.Console;
import eu.area.kernel.Main;
import lombok.Getter;
import lombok.Setter;
import org.apache.mina.core.session.IoSession;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by Meow on 2017-01-24.
 */
public class Client {
    public enum Status {
        WAIT_VERSION,
        WAIT_PASSWORD,
        WAIT_ACCOUNT,
        WAIT_NICKNAME,
        SERVER;
    }

    private long sessionId;
    private IoSession session;
    @Getter
    private String key;
    @Setter @Getter
    private Status status;
    private Compte compte;
    private InetSocketAddress socketAddress;
    private InetAddress inetAddress;

    public Client(IoSession session) {
        sessionId = session.getId();
        this.session = session;
        key = genKey();
        socketAddress = (InetSocketAddress) session.getRemoteAddress();
        inetAddress = socketAddress.getAddress();
    }

    public void send(String packet) {
        Console.println("> " + packet + " > Session " + sessionId, Console.Color.CYAN);
        session.write(packet);
    }

    public void parse(String[] args) {
        switch (status) {
            case WAIT_VERSION:
                if (!args[0].equalsIgnoreCase(Main.getConfig().getLogin_version())) {
                    send("AlEv" + Main.getConfig().getLogin_version());
                    kick();
                } else {
                    status = Status.WAIT_ACCOUNT;
                }
                break;
            case WAIT_ACCOUNT:
                compte = MySql.getAccountByName(args[0]);
                Console.println(args[0] + " : " + inetAddress.getHostAddress(), Console.Color.GREEN);
                if (compte == null) {
                    send("AlEf");
                    kick();
                } else if (!compte.getIpAllowed().isEmpty() && !compte.getIpAllowed().contains(inetAddress.getHostAddress())) {
                    send("M0" + 668);
                    kick();
                } else {
                    status = Status.WAIT_PASSWORD;
                    parse(args);
                }
                break;
            case WAIT_PASSWORD:
                String password = decryptPass(args[1]);
                if (md5(password.concat(compte.getSaltKey())).equalsIgnoreCase(compte.getPassword())) {
                    status = Status.SERVER;
                } else {
                    send("AlEf");
                    kick();
                }
                break;
            case WAIT_NICKNAME:
                String nickname = args[0];
                if (!compte.getPseudo().isEmpty()) {
                    kick();
                } else if (compte.getName().equalsIgnoreCase(nickname)){
                    send("AlEr");
                } else {
                    String s[] = {"admin", "modo", " ", "&", "é", "\"", "'",
                            "(", "-", "è", "_", "ç", "à", ")", "=", "~", "#",
                            "{", "[", "|", "`", "^", "@", "]", "}", "°", "+",
                            "^", "$", "ù", "*", ",", ";", ":", "!", "<", ">",
                            "¨", "£", "%", "µ", "?", ".", "/", "§", "\n"};
                    boolean valide = true;
                    for (String chara : s) {
                        if (nickname.contains(chara)) {
                            send("AlEs");
                            valide = false;
                            break;
                        }
                    }

                    if (valide) {
                        if (MySql.pseudoUsed(nickname)) {
                            send("AlEs");
                        } else {
                            Console.println("> Définition nom de compte : " + nickname, Console.Color.YELLOW);
                            compte.setPseudo(nickname);
                            MySql.saveAccount(compte);
                            status = Status.SERVER;
                            sendInformations();
                        }
                    }

                }
                break;
            case SERVER:
                String packet = args[0];
                switch (packet.substring(0, 2)) {
                    case "Af":
                        verifAccount();
                        break;
                    case "Ax": // Envoi des serveurs avec nombre de personnages, 5 par défaut
                        String packetToSend = "AxK31556864852";
                        for (Serveur s : Main.getServeurs().values()) {
                            if (s.isEnabled()) {
                                if (s.canAccess(compte)) {
                                    packetToSend += "|" + s.getId() + ",5";
                                }
                            }
                        }
                        send(packetToSend);
                        break;
                    case "AX":
                        int idServeur = Integer.valueOf(packet.substring(2));
                        Serveur serveurCible = Main.getServeurs().get(idServeur);
                        if (serveurCible.canAccess(compte)) {
                            String ipOldLoader = compte.getGmLevel() > 2 ? serveurCible.getIpAdmin() : serveurCible.getIpPlayer(); // Le vieux loader ne supporte pas le IPv6
                            String ip;
                            if (inetAddress instanceof Inet6Address) { // IPv6
                                ip = compte.getGmLevel() > 2 ? serveurCible.getIpv6Admin() : serveurCible.getIpv6();
                            } else {
                                ip = ipOldLoader;
                            }
                            send("AYK" + ipOldLoader + ":" + serveurCible.getPort() + ";" + compte.getGuid() + ";" + ip);
                        }
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void verifAccount() {
        if (compte.getPseudo().isEmpty()) {
            send("AlEr");
            status = Status.WAIT_NICKNAME;
        } else if (compte.isBanned()) {
            send("AlEb");
            kick();
        } else if (compte.isBlocked()) {
            send("M0667");
            kick();
        } else if (!compte.isValid()) {
            send("AlEf");
            kick();
        } else {
            sendInformations();
        }
    }

    public void sendServersStatus() {
        // Envoi des serveurs et leur status au client
        String packetToSend = "AH";
        for (Serveur s : Main.getServeurs().values()) {
            if (s.isEnabled()) {
                if (compte != null && s.canAccess(compte)) {
                    int state = s.isOnline() == true ? 1 : 0;
                    packetToSend += s.getId() + ";" + state + ";0;1|";
                }
            }
        }
        send(packetToSend);
    }

    private void sendInformations() {
        send("Af0|0|0|1|-1");
        send("Ad" + compte.getPseudo());
        send("Ac0");
        sendServersStatus();
        send("AlK" + (compte.getGmLevel() > 0 ? 1 : 0));
        send("AQ" + compte.getQuestion());
    }

    @SuppressWarnings("deprecation")
    private void kick() {
        session.close(false);
        Main.getClients().remove(sessionId);
    }

    public String genKey() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder hashKey = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 32; i++)
            hashKey.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        return hashKey.toString();
    }

    public String decryptPass(String pass) {
        if (pass.startsWith("#1"))
            pass = pass.substring(2);
        String Chaine = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        char PPass, PKey;
        int APass, AKey, ANB, ANB2, somme1, somme2;

        String decrypted = "";

        for (int i = 0; i < pass.length(); i += 2) {
            PKey = key.charAt(i / 2);
            ANB = Chaine.indexOf(pass.charAt(i));
            ANB2 = Chaine.indexOf(pass.charAt(i + 1));

            somme1 = ANB + Chaine.length();
            somme2 = ANB2 + Chaine.length();

            APass = somme1 - (int) PKey;
            if (APass < 0) APass += 64;
            APass *= 16;

            AKey = somme2 - (int) PKey;
            if (AKey < 0) AKey += 64;

            PPass = (char) (APass + AKey);

            decrypted += PPass;
        }

        return decrypted;
    }

    public String md5(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes("UTF-8"));
            return getString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            String hex = Integer.toHexString((int) 0x00FF & b);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

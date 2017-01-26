package eu.area.kernel;

import lombok.Getter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.List;

/**
 * Created by Meow on 2017-01-24.
 */
public class Config {
    private XMLConfiguration config = null;
    // Database infos
    @Getter
    private String db_host;
    @Getter
    private String db_user;
    @Getter
    private String db_passord;
    @Getter
    private String db_name;

    // Login server infos
    @Getter
    private String login_ip;
    @Getter
    private int login_port;
    @Getter
    private String login_version;

    // Exchange server infos
    @Getter
    private String exchange_ip;
    @Getter
    private int exchange_port;

    public Config() {
        Console.println("> Début du chargement du fichier de configuration", Console.Color.GREEN);
        try {
            config = new XMLConfiguration("config.xml");
            Console.println("> Fichier de configuration chargé !", Console.Color.YELLOW);
        } catch (ConfigurationException e) {
            Console.println("> Erreur au chargement du fichier de configuration", Console.Color.RED);
            Console.println("> Démarrage du serveur aborté !", Console.Color.RED);
            Main.setRunning(false);
        }
        if (config != null) {
            db_host = config.getString("database.host");
            db_user = config.getString("database.user");
            db_passord = config.getString("database.password");
            db_name = config.getString("database.name");

            login_ip = config.getString("server.login.ip");
            login_port = config.getInt("server.login.port");
            login_version = config.getString("server.login.version");

            exchange_ip = config.getString("server.exchange.ip");
            exchange_port = config.getInt("server.exchange.port");
        }
    }

    public List<HierarchicalConfiguration> getServers() {
        if (config != null) {
            return config.configurationsAt("GameServers.Server");
        }
        return null;
    }
}

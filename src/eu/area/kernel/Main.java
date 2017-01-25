package eu.area.kernel;

import eu.area.objects.Client;
import eu.area.servers.LoginServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;

public class Main {
    @Getter
    private static Config config;
    private static LoginServer login;
    @Setter
    private static boolean isRunning = true;
    private static java.io.Console systemConsole = System.console();
    @Getter
    private static HashMap<Long, Client> clients = new HashMap<Long, Client>();

    public static void main(String[] args) {
        config = new Config();
        login = new LoginServer();
        login.start();
        Console.println("> Début de la lecture des commandes", Console.Color.GREEN);
        while (isRunning) {
            executeCommand(systemConsole.readLine());
        }

        login.stop();
    }

    public static void executeCommand(String command) {
        switch (command.toLowerCase()) {
            case "exit":
                isRunning = false;
                break;
            case "?":
                Console.println("[Liste des commandes]", Console.Color.GREEN);
                Console.println("exit - ferme le serveur", Console.Color.YELLOW);
                Console.println("[Fin liste des commandes]", Console.Color.GREEN);
                break;
            default:
                Console.println("Cette commande n'existe pas !", Console.Color.MAGENTA);
                break;
        }
    }
}
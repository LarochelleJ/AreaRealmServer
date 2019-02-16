package eu.area.kernel;

import eu.area.objects.Client;
import eu.area.objects.Serveur;
import eu.area.servers.LoginServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.mina.core.session.IoSession;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Main {
    @Getter
    private static Config config;
    private static LoginServer login;
    @Getter @Setter
    private static boolean isRunning = true, maintenance = false;
    private static java.io.Console systemConsole = System.console();
    @Getter
    private static HashMap<Long, Client> clients = new HashMap<Long, Client>();
    @Getter
    private static HashMap<Integer, Serveur> serveurs;
    private static Timer refreshServerState = new Timer();

    public static void main(String[] args) {
        config = new Config();
        login = new LoginServer();
        login.start();
        Console.println("> Chargements des informations sur les serveurs de jeu", Console.Color.GREEN);
        loadServer();
        Console.println("> Lancement du timer d'actualisation du status des serveurs de jeu", Console.Color.YELLOW);
        refreshServerState.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Serveur s : serveurs.values()) {
                    s.verifIfOnline();
                    if (s.isOnline() != s.isOnlineBefore()) {
                        for (Client c : clients.values()) {
                            try {
                                if (c.getStatus() == Client.Status.SERVER) {
                                    c.sendServersStatus();
                                }
                            } catch (Exception e) {
                                Console.println("> Erreur : " + e.toString(), Console.Color.RED);
                            }
                        }
                    }
                    s.setOnlineBefore(s.isOnline());
                }
            }
        }, 0, 500); // 500 ms (1/2 seconde)
        Console.println("> Début de la lecture des commandes", Console.Color.GREEN);
        while (isRunning) {
            executeCommand(systemConsole.readLine());
        }

        login.stop();
    }

    private static void executeCommand(String command) {
        switch (command.toLowerCase()) {
            case "exit":
                isRunning = false;
                break;
            case "maintenance":
                maintenance = !maintenance;
                if (maintenance) {
                    Console.println("Le serveur est désormais en mode maintenance", Console.Color.MAGENTA);
                } else {
                    Console.println("Le serveur n'est plus en mode maintenance", Console.Color.MAGENTA);
                }
                break;
            case "?":
                Console.println("[Liste des commandes]", Console.Color.GREEN);
                Console.println("exit - ferme le serveur", Console.Color.YELLOW);
                Console.println("maintenance - active / désactive le mode maintenance", Console.Color.YELLOW);
                Console.println("[Fin liste des commandes]", Console.Color.GREEN);
                break;
            default:
                Console.println("Cette commande n'existe pas !", Console.Color.MAGENTA);
                break;
        }
    }

    private static void loadServer() {
        List<HierarchicalConfiguration> serversConfig = config.getServers();
        HashMap<Integer, Serveur> serveurs = new HashMap<Integer, Serveur>();
        Console.println("> Tentative du chargement des configurations serveurs", Console.Color.GREEN);
        if (serversConfig != null) {
            Console.println("> Début du chargement des configurations serveurs", Console.Color.YELLOW);
            try {
                for (HierarchicalConfiguration hc : serversConfig) {
                    Serveur s = new Serveur(hc.getInt("id"), hc.getInt("enabled"), hc.getInt("gmAccess"), hc.getString("ip"), hc.getString("ipPlayer"), hc.getString("ipv6"), hc.getInt("port"), hc.getString("ipAdmin"), hc.getString("ipv6Admin"));
                    serveurs.put(s.getId(), s);
                }
            } catch (Exception e) {
                Console.println(" > Erreur : " + e.toString(), Console.Color.RED);
            }
            Main.serveurs = serveurs;
            Console.println("> Chargement des configurations serveurs réussite !", Console.Color.GREEN);
        } else {
            Console.println("> Erreur au chargement des configurations serveurs", Console.Color.RED);
            isRunning = false;
        }
    }
}
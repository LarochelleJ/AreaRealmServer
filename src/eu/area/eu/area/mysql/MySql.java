package eu.area.eu.area.mysql;

import eu.area.kernel.Main;
import eu.area.objects.Compte;

import java.sql.*;


/**
 * Created by Meow on 2017-01-24.
 */
public class MySql {
    public static Connection createConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Informations
        String host = Main.getConfig().getDb_host();
        String name = Main.getConfig().getDb_name();
        String user = Main.getConfig().getDb_user();
        String pass = Main.getConfig().getDb_passord();

        return DriverManager.getConnection("jdbc:mysql://" + host + "/" + name + "?user=" + user + "&password=" + pass +"&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
    }

    public static Compte getAccountByName(String name) {
        Compte compte = null;
        try {
            Connection co = createConnection();
            String query = "SELECT m_password, banned, pass, saltKey, logged, pseudo, question, guid FROM accounts WHERE account LIKE ? LIMIT 1;";
            PreparedStatement ps = co.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                compte = new Compte(rs.getString("pass"), rs.getString("saltKey"), rs.getInt("m_password"), rs.getInt("banned"), rs.getInt("logged"), rs.getString("pseudo"), rs.getString("question"), rs.getInt("guid"));
            }
            rs.close();
            ps.close();
            co.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compte;
    }
}
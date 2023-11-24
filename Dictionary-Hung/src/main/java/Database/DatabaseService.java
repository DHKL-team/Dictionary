package Database;

import Interface.DBInterface;

import javax.xml.transform.Result;
import java.sql.*;

public class DatabaseService implements DBInterface {
    protected Connection connection = null;
    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String search(String search_word) {
        String tmp ='"' + search_word + "%" + '"';

        String sql = "SELECT * FROM wordlist WHERE english LIKE " + tmp + "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String word = null;
            String def = null;

            while(resultSet.next()) {
                word = resultSet.getString("english");
                def = resultSet.getString("definition");
                int id = resultSet.getInt("id");

                System.out.println(word + " ------ " + def );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


/*    public static void main(String[] args) {
        DatabaseService databaseService = new DatabaseService();
        databaseService.connect();
        databaseService.search("a");
    }*/
}

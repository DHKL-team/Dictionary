package Database;

import Interface.DBInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseController implements DBInterface {
    protected Connection connection = null;

    @Override
    public void connectdataBase() {
        try {
            connection = DriverManager.getConnection(db_url, db_user, db_pass);
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String search(String search_word) {
        String tmp = '"' + search_word + "%" + '"';

        String sql = "SELECT * FROM wordlist WHERE english LIKE " + tmp + "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String word = null;
            String def = null;

            while (resultSet.next()) {
                word = resultSet.getString("english");
                def = resultSet.getString("definition");
                int id = resultSet.getInt("id");

                System.out.println(word + " ------ " + def);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public ObservableList<String> mywordList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT * from myword";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String word = null;
            while (resultSet.next()) {
                word = resultSet.getString("english");
                list.add(word);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ObservableList<String> mydefList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT * from myword";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String word = null;
            while (resultSet.next()) {
                word = resultSet.getString("definition");
                list.add(word);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void addwordtodataBase(String english, String definition) {
        String sql = "INSERT INTO myWord (english, definition) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, english);
            preparedStatement.setString(2, definition);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Word added successfully!");
            } else {
                System.out.println("Failed to add word.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeWordfromdataBase(String english) {
        String sql = "DELETE FROM myword WHERE english = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, english);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Word removed successfully!");
            } else {
                System.out.println("Word not found or failed to remove.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

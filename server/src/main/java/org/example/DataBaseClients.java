package org.example;

import java.sql.*;

public class DataBaseClients {

    public DataBaseClients() throws SQLException, ClassNotFoundException {
        setConnection();
        createDB();
    }

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public static void setConnection() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:TEST1.firstDB");
    }

    public static void createDB() throws SQLException {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'clients'"+"('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'name' text, 'password' text);");
    }

    public void addClient(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO 'user' ('login', password) VALUES (?,?)")){
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            preparedStatement.execute();

        }
    }

    public  boolean checkExists(String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT login FROM clients WHERE name = ?")){
            preparedStatement.setString(1,login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.wasNull()){
                //Если нет совпадений, то всё в порядке
               return true;
            }else
                return false;
        }
    }

    public  boolean checkAuthorization(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT login, password FROM clients WHERE name = ? AND password = ?")){
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.wasNull()){
                //Если нет совпадений, то авторизация не должна пройти
                return false;
            }else
                return true;
        }
    }
}

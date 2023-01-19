package org.example;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class DBOperations {

    private final String SELECT_ALL = "SELECT * FROM Account";
    private final String SELECT = "SELECT * FROM Account WHERE Id_Account=?";
    private final String INSERT = "INSERT INTO Account(Type, Balance) VALUES(?,?)";
    private final String DELETE = "DELETE FROM Account WHERE Id_Account=?";
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement prepStmt;

    public boolean displayAllAccounts(Connection connection){

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(SELECT_ALL);

            while (rs.next()) {
                log.info("id: " + rs.getLong("Id_Account"));
                log.info("type: " + rs.getString("Type"));
                log.info("balance: " + rs.getDouble("Balance") + "\n");
            }
            return true;
        }catch (SQLException e){
            return false;
        }
    }


    public boolean selectCertainAccount(Connection connection, long id){
        try {
            prepStmt = connection.prepareStatement(SELECT);

            prepStmt.setLong(1, id);

            rs = prepStmt.executeQuery();

            if (rs.next()) {
                log.info("id: " + rs.getLong("Id_Account"));
                log.info("type: " + rs.getString("Type"));
                log.info("balance: " + rs.getDouble("Balance") + "\n");
            }else{
                log.info("Brak takiego id");
                return false;
            }
            return true;
        }catch (SQLException e){
            return false;
        }
    }

    public boolean deleteAccount(Connection connection, long id){
        try {
            prepStmt = connection.prepareStatement(DELETE);

            prepStmt.setLong(1, id);

            int countDelete = prepStmt.executeUpdate();
            // Extract data from result set

            if(countDelete > 0) {
                log.info("Popawnie usunięto konto");
                return true;
            }else {
                return false;
            }

        }catch (SQLException e){
            return false;
        }
    }

    public boolean insertAccount(Connection connection, String type ,double balance){

        try {
            prepStmt = connection.prepareStatement(INSERT);
            prepStmt.setString(1, type);
            prepStmt.setDouble(2, balance);
            prepStmt.executeUpdate();
            return true;
        }catch (SQLException e){
            return false;
        }

    }

    public void close(){
        try{
            if(prepStmt != null)
                prepStmt.close();
        }catch(SQLException e) {
            log.info("Błąd zamykania prepStmt");
        }

        try{
            if(rs != null)
                rs.close();
        }catch(SQLException e) {
            log.info("Błąd zamykania rs");
        }
    }

}

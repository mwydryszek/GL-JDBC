package org.example;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:postgresql://localhost/test";
    static final String USER = "postgres";
    static final String PASS = "postgres";
    static final String SELECT_ID = "SELECT id FROM account order by id desc limit 1";
    static final String SELECT_ALL = "SELECT * FROM account";
    static final String SELECT = "SELECT * FROM account WHERE id=?";
    static final String INSERT = "INSERT INTO account VALUES(?,?)";
    static final String DELETE = "DELETE FROM account WHERE id=?";
    private static long id;
    private static Statement stmt;
    private static ResultSet rs;

    private static PreparedStatement prepStmt;


    public static boolean displayAllAccounts(Connection connection){

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(SELECT_ALL);
            // Extract data from result set
            while (rs.next()) {
                System.out.println("id: " + rs.getLong("id"));
                System.out.println("balance: " + rs.getDouble("balance") + "\n");
            }
            return true;
        }catch (SQLException e){
            return false;
        }
    }

    public static boolean getLastAccountId(Connection connection){

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(SELECT_ID);
            // Extract data from result set
            if (rs.next()) {
                id = rs.getLong("id");
            }
            return true;
        }catch (SQLException e){
            return false;
        }
    }

    public static boolean selectCertainAccount(Connection connection, long id){
        try {
            prepStmt = connection.prepareStatement(SELECT);

            prepStmt.setLong(1, id);

            rs = prepStmt.executeQuery();
            // Extract data from result set
            if (rs.next()) {
                System.out.println("id: " + rs.getLong("id"));
                System.out.println("balance: " + rs.getDouble("balance") + "\n");
            }else{
                System.out.println("Brak takiego id");
                return false;
            }
            return true;
        }catch (SQLException e){
            return false;
        }
    }

    public static boolean deleteAccount(Connection connection, long id){
        try {
            prepStmt = connection.prepareStatement(DELETE);

            prepStmt.setLong(1, id);

            int countDelete = prepStmt.executeUpdate();
            // Extract data from result set

            if(countDelete > 0) {
                System.out.println("Popawnie usunięto konto");
                return true;
            }else {
                return false;
            }

        }catch (SQLException e){
            return false;
        }
    }

    public static boolean insertAccount(Connection connection, double balance){

        try {
            prepStmt = connection.prepareStatement(INSERT);
            id++;
            prepStmt.setLong(1, id);
            prepStmt.setDouble(2, balance);
            prepStmt.executeUpdate();
            return true;
        }catch (SQLException e){
            return false;
        }

    }



    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        final Scanner scannerValue = new Scanner(System.in);

        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)){


            boolean askForOption = true;

            while (askForOption) {

                int option = 1;

                System.out.println("Wybierz opcję (1-4)");
                System.out.println("1. INSERT, 2. SELECT, 3. DELETE, 4. DISPLAY ALL, 5. EXIT");

                try {
                    option = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Opcja musi być cyfrą 1-4\n");
                    scanner.nextLine();
                    continue;
                }

                switch (option) {
                    case 1:
                        if(!getLastAccountId(conn)){
                            System.out.println("Nie można było pobrać id");
                            return;
                        }

                        System.out.println("Podaj balance: ");
                        double balance = scannerValue.nextDouble();

                        if(!insertAccount(conn, balance)){
                            System.out.println("Błąd dodawania konta");
                            return;
                        }

                        break;
                    case 2:
                        System.out.println("Podaj id: ");
                        long id_acc = scannerValue.nextLong();

                        if(!selectCertainAccount(conn, id_acc)){
                            System.out.println("Błąd pobierania konta");
                            return;
                        }
                        break;
                    case 3:
                        System.out.println("Podaj id: ");
                        long id_acc_del = scannerValue.nextLong();

                        if(!deleteAccount(conn, id_acc_del)){
                            System.out.println("Błąd usuwania konta");
                            return;
                        }
                        break;
                    case 4:
                        if(!displayAllAccounts(conn)){
                            System.out.println("Błąd wyświetlania wszystkich danych");
                            return;
                        }
                        break;
                    case 5:
                        askForOption = false;
                        break;
                    default:
                        System.out.println("Podaj poprawną opcje\n");
                        break;
                }

            }

            scanner.close();



        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (prepStmt != null) {
                prepStmt.close();
            }

            if (stmt != null) {
                stmt.close();
            }

            if (rs != null) {
                rs.close();
            }
        }catch (SQLException e){
            System.out.println("Błąd zamykania");
        }

    }
}
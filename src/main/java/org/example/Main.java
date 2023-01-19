package org.example;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

@Slf4j
public class Main {

    private static final String DB_URL = "jdbc:postgresql://localhost/bank";
    private static final String USER = "postgres";
    private static final String PASS = "root";
    static DBOperations dbOperations = new DBOperations();


    public static void main(String[] args) {

        runLiquibase();

        final Scanner scanner = new Scanner(System.in);

        final Scanner scannerValue = new Scanner(System.in);

        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)){

            boolean askForOption = true;

            while (askForOption) {

                int option = 1;

                log.info("Wybierz opcję (1-4)");
                log.info("1. INSERT, 2. SELECT, 3. DELETE, 4. DISPLAY ALL, 5. EXIT");

                try {
                    option = scanner.nextInt();
                } catch (InputMismatchException e) {
                    log.info("Opcja musi być cyfrą 1-4\n");
                    scanner.nextLine();
                    continue;
                }

                switch (option) {
                    case 1:

                        String accType = null;
                        double balance = 0;

                        log.info("Podaj typ konta: ");
                        try {
                            accType = scannerValue.next();
                        } catch (InputMismatchException e) {
                            log.info("Niepoprawny typ");
                            continue;
                        }

                        log.info("Podaj balance: ");
                        try {
                            balance = scannerValue.nextDouble();
                        } catch (InputMismatchException e) {
                            log.info("Niepoprawna wartość");
                            continue;
                        }


                        if(!dbOperations.insertAccount(conn,accType, balance)){
                            log.info("Błąd dodawania konta");
                            continue;
                        }

                        log.info("Poprawnie dodano konto");
                        break;

                    case 2:

                        long accId = 0;

                        log.info("Podaj id: ");
                        try {
                            accId = scannerValue.nextLong();
                        }catch (InputMismatchException e){
                            log.info("Niepoprawne id");
                            continue;
                        }

                        if(!dbOperations.selectCertainAccount(conn, accId)){
                            log.info("Błąd pobierania konta");
                            continue;
                        }
                        break;

                    case 3:

                        long accDelId = 0;

                        log.info("Podaj id: ");

                        try {
                            accDelId = scannerValue.nextLong();
                        }catch (InputMismatchException e){
                            log.info("Niepoprawne id");
                            continue;
                        }

                        if(!dbOperations.deleteAccount(conn, accDelId)){
                            log.info("Błąd usuwania konta");
                            continue;
                        }
                        break;

                    case 4:

                        if(!dbOperations.displayAllAccounts(conn)){
                            log.info("Błąd wyświetlania wszystkich danych");
                            continue;
                        }
                        break;

                    case 5:

                        askForOption = false;
                        break;

                    default:
                        log.info("Podaj poprawną opcje\n");
                        break;
                }

            }

            scanner.close();



        } catch (SQLException e) {
            log.info("Błąd połączenia do bazy");
            return;
        }

        dbOperations.close();

    }

    private static void runLiquibase() {

        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)) {

            try{

                // load properties from classpath Liquibase
                Properties properties = new Properties();
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
                try (Liquibase liquibase = new liquibase.Liquibase("db/change-log.xml", new ClassLoaderResourceAccessor(), database)){
                    properties.forEach((key, value) -> liquibase.setChangeLogParameter(Objects.toString(key), value));
                    liquibase.update(new Contexts(), new LabelExpression());
                } catch (LiquibaseException e) {
                    e.printStackTrace();
                }

            }catch (LiquibaseException e){

                e.printStackTrace();

            }

        }catch(SQLException e) {
            e.printStackTrace();
        }






    }
}
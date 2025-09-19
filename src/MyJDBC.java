import java.sql.*;
import java.util.*;

public class MyJDBC {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        int choice;
        double balance = 0;
        boolean InRunning = true;
        boolean runningAcc = true;

        while (InRunning) {
            System.out.println("********************");
            System.out.println("WELCOME TO THE BANK");
            System.out.println("********************");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("********************");
            System.out.print("Enter: ");
            String action = scanner.nextLine();

            try {
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/login_schema",
                        "root",
                        "#Xelle111406"
                );

                if (action.equalsIgnoreCase("Register")) {

                    System.out.print("Enter your full name: ");
                    String newFullName = scanner.nextLine();

                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();

                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();

                    String insertQuery = "INSERT INTO USERS (username, password, fullName) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                    insertStmt.setString(1, newUsername);
                    insertStmt.setString(2, newPassword);
                    insertStmt.setString(3, newFullName);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Registration successful!");
                    } else {
                        System.out.println("Registration failed.");
                    }
                } else if (action.equalsIgnoreCase("Login")) {
                    System.out.println("********************");
                    System.out.print("Enter username: ");
                    String inputUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String inputPassword = scanner.nextLine();
                    System.out.println("********************");

                    String query = "SELECT * FROM USERS WHERE username = ? AND password = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, inputUsername);
                    preparedStatement.setString(2, inputPassword);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String fullName = resultSet.getString("fullName");
                        System.out.println("Login successful!");
                        System.out.println("Welcome " + fullName);
                        InRunning = false;
                        //DISPLAY MENU HERE
                        while (runningAcc) {

                            System.out.println("********************");
                            System.out.println("1. Balance ");
                            System.out.println("2. Deposit ");
                            System.out.println("3. Withdraw");
                            System.out.println("4. Transfer");
                            System.out.println("5. Transaction History");
                            System.out.println("6. Logout");
                            System.out.println("********************");

                            System.out.print("Enter: ");
                            choice = scanner.nextInt();
                            scanner.nextLine();

                        switch (choice) {
                            case 1 -> showBalance(connection, inputUsername);
                            case 2 -> deposit(connection, inputUsername);
                            case 3 -> System.out.println("Withdraw");
                            case 4 -> System.out.println("Transfer");
                            case 5 -> System.out.println("Transaction History");
                            case 6 -> runningAcc = false;
                            default -> System.out.println("Invalid choice.");
                        }
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                } else if (action.equalsIgnoreCase("Exit")) {
                    InRunning = false;
                }
                else {
                    System.out.println("Invalid action.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    static void showBalance(Connection connection, String username) {
        try {
            String query = "SELECT balance FROM USERS WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("********************");
            if (resultSet.next()) {
                double balance = resultSet.getDouble("BALANCE");
                System.out.printf("Your balance: ₱%.2f\n", balance);
            } else {
                System.out.println("No balance information found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving balance: " + e.getMessage());
        }
    }
    static double deposit(Connection connection, String username) {
        try {
            // First get current balance
            String query = "SELECT balance FROM USERS WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            double currentBalance = 0;
            if (resultSet.next()) {
                currentBalance = resultSet.getDouble("balance");
            }

            // Ask for deposit amount
            System.out.println("********************");
            System.out.print("Enter amount to deposit: ₱");
            double depositAmount = scanner.nextDouble();
            scanner.nextLine(); // Clear nextline

            if (depositAmount <= 0) {
                System.out.println("Invalid amount. Deposit must be greater than zero.");
                return currentBalance;
            }

            double newBalance = currentBalance + depositAmount;
            String updateQuery = "UPDATE USERS SET balance = ? WHERE username = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newBalance);
            updateStmt.setString(2, username);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("********************");
                System.out.printf("Deposited: ₱%.2f\n", depositAmount);
                System.out.printf("New balance: ₱%.2f\n", newBalance);
                System.out.println("********************");
            } else {
                System.out.println("Deposit failed.");
            }

            return newBalance;
        } catch (SQLException e) {
            System.out.println("Error processing deposit: " + e.getMessage());
            return 0;
        }
    }

}

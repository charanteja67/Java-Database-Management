package dbConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/employeeDB";
    private static final String username = "root";
    private static final String password = "EnterYourPassword";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            Scanner scanner = new Scanner(System.in);
            int choice;
            do {
                printMenu();
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        addEmployee();
                        break;
                    case 2:
                        retrieveEmployee();
                        break;
                    case 3:
                        updateEmployee();
                        break;
                    case 4:
                        deleteEmployee();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);

            if (connection != null) {
                connection.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printMenu() {
        System.out.println("Choose operation:");
        System.out.println("1. Add Employee");
        System.out.println("2. Retrieve Employee");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("0. Exit");
    }

    public static void addEmployee() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter employee ID:");
        int id = scanner.nextInt();
        System.out.println("Enter employee name:");
        String name = scanner.next();
        System.out.println("Enter employee designation:");
        String designation = scanner.next();
        System.out.println("Enter employee address:");
        String address = scanner.next();

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO employees (id, name, designation, address) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, designation);
            statement.setString(4, address);
            statement.executeUpdate();
            System.out.println("Employee added successfully.");
        }
    }

    public static void retrieveEmployee() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter employee ID to retrieve:");
        int id = scanner.nextInt();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Retrieved Employee: " + new Employee(resultSet.getInt("id"),
                            resultSet.getString("name"), resultSet.getString("designation"),
                            resultSet.getString("address")));
                } else {
                    System.out.println("Employee not found.");
                }
            }
        }
    }

    public static void updateEmployee() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter employee ID to update:");
        int id = scanner.nextInt();
        System.out.println("Enter new designation:");
        String newDesignation = scanner.next();
        System.out.println("Enter new address:");
        String newAddress = scanner.next();

        try (PreparedStatement statement = connection
                .prepareStatement("UPDATE employees SET designation = ?, address = ? WHERE id = ?")) {
            statement.setString(1, newDesignation);
            statement.setString(2, newAddress);
            statement.setInt(3, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee details updated successfully.");
            } else {
                System.out.println("Employee not found.");
            }
        }
    }

    public static void deleteEmployee() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter employee ID to delete:");
        int id = scanner.nextInt();

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE id = ?")) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Employee not found.");
            }
        }
    }
}

class Employee {
    private int id;
    private String name;
    private String designation;
    private String address;

    public Employee(int id, String name, String designation, String address) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name='" + name + '\'' + ", designation='" + designation + '\''
                + ", address='" + address + '\'' + '}';
    }
}
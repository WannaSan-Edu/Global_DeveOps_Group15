package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * US32 - View Population by Language.
 *
 * This application connects to the MySQL database
 * and executes only the US32 population by language report.
 */
public class App {

    // Maximum number of database connection attempts.
    private static final int MAX_ATTEMPTS = 100;

    // Delay between connection attempts (3 seconds).
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {

        // Load the MySQL JDBC driver.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "Could not load MySQL JDBC driver."
            );

            return;
        }

        // Use IPv4 localhost and Docker's exposed MySQL port.
        String host = System.getenv().getOrDefault(
                "DB_HOST",
                "127.0.0.1:33060"
        );

        // Build the MySQL database connection URL.
        String url = "jdbc:mysql://" + host
                + "/world"
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&connectTimeout=5000"
                + "&socketTimeout=30000";

        // Database login credentials.
        String username = System.getenv().getOrDefault(
                "DB_USER", "root"
        );

        String password = System.getenv().getOrDefault(
                "DB_PASSWORD", "example"
        );

        Connection con = null;

        // Retry the connection if MySQL is not immediately available.
        for (int i = 0; i < MAX_ATTEMPTS; i++) {

            System.out.println("Connecting to database...");

            try {

                // Establish a connection to the world database.
                con = DriverManager.getConnection(
                        url,
                        username,
                        password
                );

                System.out.println(
                        "Successfully connected to MySQL."
                );

                break;

            } catch (SQLException e) {

                System.err.println(
                        "Database connection attempt "
                                + (i + 1)
                                + " failed: "
                                + e.getMessage()
                );

                // Wait before attempting to reconnect.
                if (i < MAX_ATTEMPTS - 1) {

                    try {
                        Thread.sleep(RETRY_DELAY_MS);

                    } catch (InterruptedException ie) {

                        // Restore the interrupted status.
                        Thread.currentThread().interrupt();

                        System.err.println(
                                "Database connection was interrupted."
                        );

                        return;
                    }
                }
            }
        }

        // Stop execution if the database connection failed.
        if (con == null) {

            System.err.println(
                    "Could not connect to the database after "
                            + MAX_ATTEMPTS + " attempts."
            );

            return;
        }

        // Automatically close the database connection after the report.
        try (Connection connection = con) {

            System.out.println(
                    "Database connection established."
            );

            // Check that the required database tables exist.
            if (!isDatabaseReady(connection)) {

                System.err.println(
                        "Required database tables are unavailable."
                );

                return;
            }

            // Display the selected report name.
            System.out.println();
            System.out.println("==============================================");
            System.out.println(
                    "Running: US32 - Population by Language"
            );
            System.out.println("==============================================");

            // Create the US32 report using the database connection.
            US32_PopulationByLanguageReport report =
                    new US32_PopulationByLanguageReport(connection);

            // Execute the population by language report.
            report.run();

            System.out.println();
            System.out.println(
                    "US32 report completed successfully."
            );

        } catch (SQLException e) {

            // Handle database errors during report execution.
            System.err.println(
                    "Error executing US32 report: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Check whether all required database tables are available.
     *
     * @param connection active MySQL database connection
     * @return true if all required tables exist
     */
    private static boolean isDatabaseReady(
            Connection connection) {

        // Tables required by the US32 SQL query.
        String[] requiredTables = {
                "country",
                "countrylanguage"
        };

        try {

            for (String table : requiredTables) {

                String sql =
                        "SELECT COUNT(*) "
                                + "FROM information_schema.tables "
                                + "WHERE table_schema = DATABASE() "
                                + "AND table_name = ?";

                try (var statement =
                             connection.prepareStatement(sql)) {

                    // Safely provide the table name.
                    statement.setString(1, table);

                    try (var result = statement.executeQuery()) {

                        if (!result.next()
                                || result.getInt(1) == 0) {

                            System.err.println(
                                    "Missing required table: " + table
                            );

                            return false;
                        }
                    }
                }
            }

            System.out.println(
                    "Database check passed."
            );

            return true;

        } catch (SQLException e) {

            System.err.println(
                    "Database validation failed: "
                            + e.getMessage()
            );

            return false;
        }
    }
}
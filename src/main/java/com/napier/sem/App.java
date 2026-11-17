package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * US31 - Total Population of a Selected City.
 *
 * This application connects to the MySQL database
 * and executes only the US31 city population report.
 */
public class App {

    // Maximum number of database connection attempts.
    private static final int MAX_ATTEMPTS = 100;

    // Delay between connection attempts (3 seconds).
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {

        // Read the selected city name from program arguments.
        String cityName = args.length > 0 ? args[0].trim() : "";

        // Read the optional country name.
        // This helps distinguish cities with the same name.
        String countryName = args.length > 1 ? args[1].trim() : "";

        // Validate the selected city name.
        if (cityName.isBlank()) {

            System.err.println(
                    "Please provide a city name."
            );

            System.err.println(
                    "Example: Kabul Afghanistan"
            );

            return;
        }

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

        // Retry the database connection if necessary.
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

            // Check whether the required database tables exist.
            if (!isDatabaseReady(connection)) {

                System.err.println(
                        "Required database tables are unavailable."
                );

                return;
            }

            // Display the selected city and report information.
            System.out.println();
            System.out.println("==============================================");
            System.out.println(
                    "Running: US31 - Total Population of a Selected City"
            );
            System.out.println("==============================================");

            System.out.println(
                    "Selected City: " + cityName
            );

            // Create the US31 report using the database connection.
            US31_CityPopulationReport report =
                    new US31_CityPopulationReport(connection);

            // Execute the selected city population report.
            report.run(cityName, countryName);

            System.out.println();
            System.out.println(
                    "US31 report execution completed."
            );

        } catch (SQLException e) {

            // Handle database errors during report execution.
            System.err.println(
                    "Error executing US31 report: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Check whether the required database tables are available.
     *
     * @param connection active MySQL database connection
     * @return true if all required tables exist
     */
    private static boolean isDatabaseReady(
            Connection connection) {

        // Tables required by the US31 SQL query.
        String[] requiredTables = {
                "city",
                "country"
        };

        try {

            for (String table : requiredTables) {

                String sql =
                        "SELECT COUNT(*) "
                                + "FROM information_schema.tables "
                                + "WHERE table_schema = DATABASE() "
                                + "AND table_name = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    // Safely provide the table name.
                    statement.setString(1, table);

                    try (ResultSet result =
                                 statement.executeQuery()) {

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
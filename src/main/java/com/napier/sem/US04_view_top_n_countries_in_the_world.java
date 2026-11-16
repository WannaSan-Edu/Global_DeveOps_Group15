
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * US04 - View the top N most populated countries in the world,
 * ordered by population descending.
 */
public class US04_view_top_n_countries_in_the_world {

    public static void printReport(Connection connection, int n) {

        /*
         * JUSTIFICATION (Input Validation):
         * - N must be a positive integer greater than zero.
         * - Invalid values display a clear error message.
         */
        if (n <= 0) {
            System.err.println(
                    "Invalid input: N must be a positive integer greater than zero."
            );
            return;
        }

        /*
         * JUSTIFICATION (SQL):
         * - Joins country with city to retrieve the capital name.
         * - Orders countries by population DESC.
         * - LIMIT ? displays only the top N countries.
         * - If N exceeds the available countries, all are displayed.
         */
        String sql = "SELECT c.Code, c.Name, c.Continent, c.Region, "
                + "c.Population, capital.Name AS Capital "
                + "FROM country c "
                + "LEFT JOIN city capital ON c.Capital = capital.ID "
                + "ORDER BY c.Population DESC, c.Name ASC "
                + "LIMIT ?";

        /*
         * JUSTIFICATION (Border):
         * - 150 characters width to accommodate code, country name,
         *   continent, region, population and capital.
         */
        String line = "-".repeat(150);
        int count = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, n);

            try (ResultSet rs = statement.executeQuery()) {

                System.out.println("Top " + n
                        + " Most Populated Countries in the World");
                System.out.println(line);

                System.out.printf(
                        "| %-6s | %-48s | %-18s | %-28s | %15s | %-20s |%n",
                        "Code", "Country", "Continent", "Region",
                        "Population", "Capital"
                );

                System.out.println(line);

                while (rs.next()) {

                    long population = rs.getLong("Population");

                    String capital = rs.getString("Capital");
                    if (capital == null) {
                        capital = "Unknown";
                    }

                    String region = rs.getString("Region");
                    if (region == null) {
                        region = "Unknown";
                    }

                    /*
                     * JUSTIFICATION (Formatting):
                     * - %-48.48s prevents long country names from
                     *   overflowing the table.
                     * - %,15d displays population with commas.
                     * - Results are sorted from largest to smallest.
                     */
                    System.out.printf(
                            Locale.US,
                            "| %-6s | %-48.48s | %-18.18s | %-28.28s | %,15d | %-20.20s |%n",
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            region,
                            population,
                            capital
                    );

                    count++;
                }

                System.out.println(line);

                if (count == 0) {
                    System.out.println("No country records found.");
                } else {
                    System.out.println("Countries displayed: " + count);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error retrieving top N countries: " + e.getMessage()
            );
        }
    }
}

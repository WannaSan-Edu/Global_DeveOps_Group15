
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * US31 - View Population of a Selected City.
 *
 * This report retrieves and displays the total population
 * of a selected city from the world database.
 */
public class ViewPopulationOfSelectedCity_US31 {

    // Shared database connection provided by App.java.
    private final Connection connection;

    /**
     * SQL query to retrieve the selected city's population.
     *
     * The country name is included to distinguish cities
     * that have the same name in different countries.
     */
    private static final String POPULATION_QUERY = """
            SELECT
                city.Name AS CityName,
                country.Name AS CountryName,
                city.Population AS CityPopulation
            FROM city
            INNER JOIN country
                ON city.CountryCode = country.Code
            WHERE city.Name = ?
                AND country.Name = ?
            ORDER BY city.Population DESC
            """;

    /**
     * Constructor.
     *
     * Receives the shared database connection from App.java.
     *
     * @param connection active MySQL database connection
     */
    public ViewPopulationOfSelectedCity_US31(Connection connection) {
        this.connection = Objects.requireNonNull(
                connection,
                "Database connection cannot be null."
        );
    }

    /**
     * Main method for executing the US31 report.
     *
     * @param cityName selected city name
     * @param countryName selected country name
     * @throws SQLException if a database error occurs
     */
    public void run(String cityName, String countryName)
            throws SQLException {

        // Check that the database connection is open.
        if (connection.isClosed()) {
            System.out.println(
                    "Cannot generate US31 report: "
                            + "Database connection is closed."
            );
            return;
        }

        // Validate the selected city and country.
        if (cityName == null || cityName.isBlank()
                || countryName == null || countryName.isBlank()) {

            System.out.println(
                    "Please provide a valid city and country name."
            );
            return;
        }

        // Retrieve population data from MySQL.
        CityPopulation population =
                getCityPopulation(cityName.trim(), countryName.trim());

        // Handle missing city population data.
        if (population == null) {
            System.out.println(
                    "No population data available for "
                            + cityName + ", " + countryName + "."
            );
            return;
        }

        // Display the final report.
        displayReport(population);
    }

    /**
     * Retrieves the selected city's population.
     *
     * @param cityName selected city name
     * @param countryName selected country name
     * @return city population record or null if not found
     * @throws SQLException if the SQL query fails
     */
    private CityPopulation getCityPopulation(
            String cityName, String countryName)
            throws SQLException {

        // Close the statement and result set automatically.
        // Keep the shared database connection open.
        try (PreparedStatement statement =
                     connection.prepareStatement(POPULATION_QUERY)) {

            statement.setString(1, cityName);
            statement.setString(2, countryName);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    String name = result.getString("CityName");
                    String country = result.getString("CountryName");
                    long population =
                            result.getLong("CityPopulation");

                    // Check whether population data is missing.
                    if (result.wasNull()) {
                        return null;
                    }

                    return new CityPopulation(
                            name,
                            country,
                            population
                    );
                }
            }
        }

        return null;
    }

    /**
     * Displays the population report for the selected city.
     *
     * @param population selected city population record
     */
    private void displayReport(CityPopulation population) {

        NumberFormat numberFormat =
                NumberFormat.getIntegerInstance(Locale.US);

        String line = "-".repeat(75);

        System.out.println();
        System.out.println(
                "US31 - POPULATION OF A SELECTED CITY"
        );

        System.out.println(line);

        System.out.printf(
                "| %-25s | %-20s | %18s |%n",
                "City",
                "Country",
                "Population"
        );

        System.out.println(line);

        System.out.printf(
                "| %-25.25s | %-20.20s | %18s |%n",
                population.cityName,
                population.countryName,
                numberFormat.format(population.population)
        );

        System.out.println(line);
        System.out.println("Cities displayed: 1");
        System.out.println();
    }

    /**
     * Internal data model for storing the selected
     * city's population information.
     */
    private static class CityPopulation {

        private final String cityName;
        private final String countryName;
        private final long population;

        private CityPopulation(
                String cityName,
                String countryName,
                long population) {

            this.cityName = cityName;
            this.countryName = countryName;
            this.population = population;
        }
    }
}
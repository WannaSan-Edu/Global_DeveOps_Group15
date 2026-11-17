package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * US31 - Total Population of a Selected City.
 *
 * This class retrieves and displays the population
 * of a city selected by the user.
 */
public class US31_CityPopulationReport {

    // Shared database connection from App.java
    private final Connection con;

    /**
     * Constructor to receive the database connection.
     */
    public US31_CityPopulationReport(Connection con) {

        if (con == null) {
            throw new IllegalArgumentException(
                    "Database connection cannot be null."
            );
        }

        this.con = con;
    }

    /**
     * Retrieve population information for the selected city.
     *
     * @param cityName selected city name
     * @param countryName optional country name to distinguish
     *                    cities with the same name
     * @return list of matching cities
     * @throws SQLException if a database error occurs
     */
    public List<City> getCityPopulation(
            String cityName, String countryName
    ) throws SQLException {

        // Store matching city records.
        List<City> cities = new ArrayList<>();

        // Check whether the user entered a city name.
        if (cityName == null || cityName.isBlank()) {
            return cities;
        }

        // Check whether the country name was provided.
        boolean countrySelected =
                countryName != null && !countryName.isBlank();

        // SQL query to retrieve city and population information.
        String sql =
                "SELECT ci.Name AS CityName, " +
                        "co.Name AS CountryName, " +
                        "ci.District AS DistrictName, " +
                        "ci.Population AS CityPopulation " +
                        "FROM city ci " +
                        "JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE LOWER(ci.Name) = LOWER(?) " +
                        "AND ci.Population IS NOT NULL";

        // Add country filtering when the user provides a country.
        if (countrySelected) {
            sql += " AND LOWER(co.Name) = LOWER(?)";
        }

        sql += " ORDER BY co.Name ASC, ci.District ASC";

        // PreparedStatement safely passes user input to SQL.
        try (PreparedStatement pstmt =
                     con.prepareStatement(sql)) {

            // Set the selected city name.
            pstmt.setString(1, cityName.trim());

            // Set the country name if provided.
            if (countrySelected) {
                pstmt.setString(2, countryName.trim());
            }

            // Execute the SQL query.
            try (ResultSet rs = pstmt.executeQuery()) {

                // Read every matching city record.
                while (rs.next()) {

                    long population =
                            rs.getLong("CityPopulation");

                    // Ignore records with missing population data.
                    if (rs.wasNull()) {
                        continue;
                    }

                    // Create a City object to store database data.
                    City city = new City();

                    city.setName(rs.getString("CityName"));
                    city.setCountry(rs.getString("CountryName"));
                    city.setDistrict(rs.getString("DistrictName"));
                    city.setPopulation(population);

                    // Add the city to the result list.
                    cities.add(city);
                }
            }
        }

        return cities;
    }

    /**
     * Display the selected city's population report.
     */
    public void displayReport(
            List<City> cities,
            String requestedCity,
            String requestedCountry
    ) {

        // Check whether any matching population data exists.
        if (cities == null || cities.isEmpty()) {

            String selectedCity = requestedCity;

            if (requestedCountry != null
                    && !requestedCountry.isBlank()) {
                selectedCity += ", " + requestedCountry;
            }

            System.out.println(
                    "No population data available for city: "
                            + selectedCity
            );

            return;
        }

        // Inform the user if the city name is not unique.
        if (cities.size() > 1
                && (requestedCountry == null
                || requestedCountry.isBlank())) {

            System.out.println(
                    "Multiple cities with this name were found. "
                            + "Please specify the country."
            );
        }

        // Display the population report.
        String line = "-".repeat(105);

        System.out.println();
        System.out.println(
                "US31 - TOTAL POPULATION OF SELECTED CITY"
        );
        System.out.println(line);

        System.out.printf(
                "| %-30s | %-25s | %-20s | %18s |%n",
                "City",
                "Country",
                "District",
                "Total Population"
        );

        System.out.println(line);

        // Display city details retrieved from the database.
        for (City city : cities) {

            System.out.printf(
                    Locale.US,
                    "| %-30.30s | %-25.25s | %-20.20s | %,18d |%n",
                    city.getName(),
                    city.getCountry(),
                    city.getDistrict() == null
                            ? "" : city.getDistrict(),
                    city.getPopulation()
            );
        }

        System.out.println(line);
        System.out.println();
    }

    /**
     * Main method to run the US31 city population report.
     */
    public void run(String cityName, String countryName) {

        // Validate the selected city name.
        if (cityName == null || cityName.isBlank()) {

            System.out.println(
                    "Please provide a city name for US31."
            );

            return;
        }

        try {

            // Retrieve the selected city's population.
            List<City> cities =
                    getCityPopulation(cityName, countryName);

            // Display the result or a clear error message.
            displayReport(cities, cityName.trim(), countryName);

        } catch (SQLException e) {

            // Handle database errors.
            System.err.println(
                    "Unable to retrieve city population "
                            + "due to a database error."
            );

            System.err.println("Details: " + e.getMessage());
        }
    }
}

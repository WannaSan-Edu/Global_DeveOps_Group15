package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * US21: View the top N most populated capital cities in a selected continent.
 * Results are ordered by population from largest to smallest.
 */
public class ViewTopNCapitalCitiesInAContinent_US21
{
    /**
     * Returns up to N capital cities in the selected continent.
     * A capital is identified by country.Capital = city.ID.
     *
     * @param connection open shared database connection
     * @param continent selected continent, e.g. "Asia"
     * @param n positive number of capital cities to return
     * @return matching capital cities, an empty list if none match, or null on SQL error
     */
    public static ArrayList<CapitalCity> getTopN(
            Connection connection, String continent, int n)
    {
        if (connection == null)
        {
            System.out.println("Database connection is not available.");
            return null;
        }

        if (continent == null || continent.trim().isEmpty())
        {
            System.out.println("Please select a valid continent.");
            return null;
        }

        if (n <= 0)
        {
            System.out.println("Invalid N. Please enter a positive integer.");
            return null;
        }

        ArrayList<CapitalCity> capitals = new ArrayList<>();

        String sql =
                "SELECT city.Name AS CapitalName, " +
                "country.Name AS CountryName, " +
                "city.District AS DistrictName, " +
                "city.Population AS Population " +
                "FROM country INNER JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC, city.Name ASC, country.Name ASC " +
                "LIMIT ?";

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, continent.trim());
            statement.setInt(2, n);

            try (ResultSet results = statement.executeQuery())
            {
                while (results.next())
                {
                    CapitalCity city = new CapitalCity();
                    city.setName(results.getString("CapitalName"));
                    city.setCountry(results.getString("CountryName"));
                    city.setDistrict(results.getString("DistrictName"));
                    city.setPopulation(results.getLong("Population"));
                    capitals.add(city);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Failed to get top capital cities for continent: "
                    + continent);
            e.printStackTrace();
            return null;
        }

        return capitals;
    }

    /**
     * Prints the US21 report using the project's console-table style.
     */
    public static void printReport(
            ArrayList<CapitalCity> capitals, String continent, int n)
    {
        System.out.println("\nUS21: Top " + n
                + " Most Populated Capital Cities in " + continent);

        if (capitals == null)
        {
            System.out.println("Unable to display the report because an error occurred.");
            return;
        }

        if (capitals.isEmpty())
        {
            System.out.println("No capital cities found for the selected continent.");
            return;
        }

        String line = "-".repeat(145);
        System.out.println(line);
        System.out.printf(
                "| %-35s | %-35s | %-30s | %15s |%n",
                "Capital City", "Country", "District", "Population");
        System.out.println(line);

        for (CapitalCity city : capitals)
        {
            System.out.printf(
                    Locale.US,
                    "| %-35.35s | %-35.35s | %-30.30s | %,15d |%n",
                    city.getName(),
                    city.getCountry(),
                    city.getDistrict() == null ? "" : city.getDistrict(),
                    city.getPopulation());
        }

        System.out.println(line);
        System.out.println("Capital cities displayed: " + capitals.size());
    }

    /**
     * Convenience method for running the complete US21 report.
     */
    public static void generateReport(Connection connection, String continent, int n)
    {
        ArrayList<CapitalCity> capitals = getTopN(connection, continent, n);
        printReport(capitals, continent, n);
    }
}

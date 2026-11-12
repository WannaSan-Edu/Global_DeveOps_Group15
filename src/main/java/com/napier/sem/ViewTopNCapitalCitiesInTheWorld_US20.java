package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * US20: View the top N most populated capital cities in the world.
 * Results are ordered from largest to smallest population.
 */
public class ViewTopNCapitalCitiesInTheWorld_US20
{
    /**
     * Retrieve the top N capital cities worldwide.
     */
    public static ArrayList<CapitalCity> getTopN(
            Connection connection, int n)
    {
        if (connection == null)
        {
            System.err.println("Database connection is unavailable.");
            return null;
        }

        if (n <= 0)
        {
            throw new IllegalArgumentException(
                    "N must be a positive integer.");
        }

        ArrayList<CapitalCity> capitals = new ArrayList<>();

        String sql =
                "SELECT city.Name AS CapitalName, " +
                        "country.Name AS CountryName, " +
                        "city.District AS District, " +
                        "city.Population AS Population " +
                        "FROM country " +
                        "INNER JOIN city ON country.Capital = city.ID " +
                        "ORDER BY city.Population DESC, " +
                        "city.Name ASC, country.Name ASC " +
                        "LIMIT ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql))
        {
            statement.setInt(1, n);

            try (ResultSet results = statement.executeQuery())
            {
                while (results.next())
                {
                    CapitalCity capital = new CapitalCity();

                    capital.setName(
                            results.getString("CapitalName"));

                    capital.setCountry(
                            results.getString("CountryName"));

                    capital.setDistrict(
                            results.getString("District"));

                    capital.setPopulation(
                            results.getLong("Population"));

                    capitals.add(capital);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println(
                    "Failed to get the top " + n +
                            " capital cities in the world.");

            e.printStackTrace();
            return null;
        }

        return capitals;
    }

    /**
     * Print the US20 report.
     */
    public static void printReport(
            ArrayList<CapitalCity> capitals, int n)
    {
        System.out.println(
                "\nUS20: Top " + n +
                        " Capital Cities in the World");

        if (capitals == null)
        {
            System.out.println(
                    "Unable to retrieve capital cities due to a database error.");
            return;
        }

        if (capitals.isEmpty())
        {
            System.out.println("No capital cities available.");
            return;
        }

        String line = "-".repeat(145);

        System.out.println(line);

        System.out.printf(
                "| %-35s | %-35s | %-35s | %15s |%n",
                "Capital City",
                "Country",
                "District",
                "Population"
        );

        System.out.println(line);

        for (CapitalCity city : capitals)
        {
            System.out.printf(
                    Locale.US,
                    "| %-35.35s | %-35.35s | %-35.35s | %,15d |%n",
                    city.getName(),
                    city.getCountry(),
                    city.getDistrict() == null
                            ? "" : city.getDistrict(),
                    city.getPopulation()
            );
        }

        System.out.println(line);

        System.out.println(
                "Capital cities displayed: " + capitals.size());
    }
}

package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ViewPopulationOfAContinent_US27
{
    /**
     * US27: View the total population of a selected continent.
     */
    public static void getPopulationOfContinent(
            Connection con, String continent)
    {
        if (con == null)
        {
            System.out.println("Database connection is unavailable.");
            return;
        }

        if (continent == null || continent.trim().isEmpty())
        {
            System.out.println("Please select a valid continent.");
            return;
        }

        String sql =
                "SELECT Continent, SUM(Population) AS TotalPopulation " +
                        "FROM country " +
                        "WHERE Continent = ? " +
                        "GROUP BY Continent";

        try (PreparedStatement statement = con.prepareStatement(sql))
        {
            statement.setString(1, continent.trim());

            try (ResultSet results = statement.executeQuery())
            {
                System.out.println(
                        "\nUS27: Population of " + continent);

                if (results.next())
                {
                    long totalPopulation =
                            results.getLong("TotalPopulation");

                    if (results.wasNull())
                    {
                        System.out.println(
                                "No population data available for "
                                        + continent + ".");
                        return;
                    }

                    System.out.println("------------------------------------------");
                    System.out.printf(
                            "| %-20s | %15s |%n",
                            "Continent", "Total Population");

                    System.out.println("------------------------------------------");

                    System.out.printf(
                            Locale.US,
                            "| %-20s | %,15d |%n",
                            results.getString("Continent"),
                            totalPopulation);

                    System.out.println("------------------------------------------");
                }
                else
                {
                    System.out.println(
                            "No population data available for continent: "
                                    + continent);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println(
                    "Failed to retrieve population for " + continent);

            e.printStackTrace();
        }
    }
}

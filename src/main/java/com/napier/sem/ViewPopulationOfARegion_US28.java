package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ViewPopulationOfARegion_US28
{
    /**
     * US28: View the total population of a selected region.
     */
    public static void getPopulationOfRegion(
            Connection con, String region)
    {
        if (con == null)
        {
            System.out.println("Database connection is unavailable.");
            return;
        }

        if (region == null || region.trim().isEmpty())
        {
            System.out.println("Please select a valid region.");
            return;
        }

        String sql =
                "SELECT Region, SUM(Population) AS TotalPopulation " +
                        "FROM country " +
                        "WHERE Region = ? " +
                        "GROUP BY Region";

        try (PreparedStatement statement = con.prepareStatement(sql))
        {
            statement.setString(1, region.trim());

            try (ResultSet results = statement.executeQuery())
            {
                System.out.println(
                        "\nUS28: Population of " + region);

                if (results.next())
                {
                    long totalPopulation =
                            results.getLong("TotalPopulation");

                    if (results.wasNull())
                    {
                        System.out.println(
                                "No population data available for region: "
                                        + region);
                        return;
                    }

                    System.out.println("------------------------------------------");

                    System.out.printf(
                            "| %-20s | %15s |%n",
                            "Region", "Total Population");

                    System.out.println("------------------------------------------");

                    System.out.printf(
                            Locale.US,
                            "| %-20s | %,15d |%n",
                            results.getString("Region"),
                            totalPopulation);

                    System.out.println("------------------------------------------");
                }
                else
                {
                    System.out.println(
                            "No population data available for region: "
                                    + region);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println(
                    "Failed to retrieve population for region: "
                            + region);

            e.printStackTrace();
        }
    }
}

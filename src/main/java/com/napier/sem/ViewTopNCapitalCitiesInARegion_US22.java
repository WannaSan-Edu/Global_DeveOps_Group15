package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * US22: View the top N most populated capital cities in a selected region.
 */
public class ViewTopNCapitalCitiesInARegion_US22
{
    public static void generateReport(Connection con, String region, int n)
    {
        if (con == null)
        {
            System.out.println("Database connection is not available.");
            return;
        }

        if (region == null || region.trim().isEmpty())
        {
            System.out.println("Please provide a valid region.");
            return;
        }

        if (n <= 0)
        {
            System.out.println("Invalid N. Please enter a positive integer.");
            return;
        }

        String sql =
                "SELECT ci.Name AS city_name, " +
                "co.Name AS country_name, " +
                "ci.District AS district_name, " +
                "ci.Population AS city_population " +
                "FROM country co " +
                "JOIN city ci ON co.Capital = ci.ID " +
                "WHERE co.Region = ? " +
                "ORDER BY ci.Population DESC, ci.Name ASC " +
                "LIMIT ?";

        try (PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, region.trim());
            ps.setInt(2, n);

            try (ResultSet rs = ps.executeQuery())
            {
                System.out.println("\nUS22: TOP " + n
                        + " MOST POPULATED CAPITAL CITIES IN REGION: "
                        + region.trim().toUpperCase(Locale.ROOT));

                String line = "-".repeat(110);
                System.out.println(line);
                System.out.printf("%-30s | %-25s | %-25s | %15s%n",
                        "City Name", "Country", "District", "Population");
                System.out.println(line);

                int count = 0;
                while (rs.next())
                {
                    String district = rs.getString("district_name");
                    if (district == null || district.trim().isEmpty())
                    {
                        district = "N/A";
                    }

                    System.out.printf(Locale.US,
                            "%-30.30s | %-25.25s | %-25.25s | %,15d%n",
                            rs.getString("city_name"),
                            rs.getString("country_name"),
                            district,
                            rs.getLong("city_population"));
                    count++;
                }

                System.out.println(line);
                if (count == 0)
                {
                    System.out.println("No capital cities found for region: " + region.trim());
                }
                else
                {
                    System.out.println("Capital cities displayed: " + count);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Database/report error: " + e.getMessage());
        }
    }
}

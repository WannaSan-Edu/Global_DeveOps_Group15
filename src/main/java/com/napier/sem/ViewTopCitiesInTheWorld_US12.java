
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewTopCitiesInTheWorld_US12 {

    private final Connection con;

    public ViewTopCitiesInTheWorld_US12(Connection con) {
        this.con = con;
    }

    public void getTopNCitiesInWorld(int n) {

        if (n <= 0) {
            System.out.println("US12 Error: N must be greater than zero.");
            return;
        }

        String sql = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "ORDER BY city.Population DESC, city.Name ASC " +
                "LIMIT ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, n);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.println();
                System.out.println("US12 - Top " + n + " Cities in the World");
                System.out.println("-".repeat(105));

                System.out.printf("%-30s %-25s %-30s %15s%n",
                        "City Name", "Country", "District", "Population");

                System.out.println("-".repeat(105));

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf("%-30.30s %-25.25s %-30.30s %,15d%n",
                            rs.getString("CityName"),
                            rs.getString("CountryName"),
                            rs.getString("District"),
                            rs.getLong("Population"));
                }

                if (!found) {
                    System.out.println("No cities found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("US12 Error: " + e.getMessage());
        }
    }
}


package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCitiesInACountry_US10 {

    private Connection con;

    public ViewCitiesInACountry_US10(Connection con) {
        this.con = con;
    }

    public void getCitiesInCountry(String countryName) {

        String sql = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Name = ? " +
                "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, countryName);

            try (ResultSet rs = stmt.executeQuery()) {
                String line = "---------------------------------------------------------------------------------";

                System.out.println();
                System.out.println("US10 - View all cities in a country: " + countryName);
                System.out.println(line);
                System.out.printf("| %-18s | %-18s | %-18s | %15s |%n",
                        "City Name", "Country", "District", "Population");
                System.out.println(line);

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf("| %-18s | %-18s | %-18s | %,15d |%n",
                            rs.getString("CityName"),
                            rs.getString("CountryName"),
                            rs.getString("District"),
                            rs.getLong("Population"));
                }

                if (!found) {
                    System.out.println("No cities found for " + countryName);
                }

                System.out.println(line);
            }

        } catch (SQLException e) {
            System.out.println("Error running US10 report: " + e.getMessage());
        }
    }
}

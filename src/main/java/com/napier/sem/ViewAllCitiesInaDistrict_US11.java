
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAllCitiesInaDistrict_US11 {

    private final Connection con;

    public ViewAllCitiesInaDistrict_US11(Connection con) {
        this.con = con;
    }

    public void getCitiesInDistrict(String districtName) {

        String sql = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.District = ? " +
                "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, districtName);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.println();
                System.out.println("US11 - All Cities in District: " + districtName);
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
            System.out.println("US11 Error: " + e.getMessage());
        }
    }
}

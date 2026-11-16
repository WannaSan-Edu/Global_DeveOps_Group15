
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
                "city.District AS DistrictName, " +
                "city.Population AS CityPopulation " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.District = ? " +
                "ORDER BY city.Population DESC, city.Name ASC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, districtName);

            try (ResultSet rs = stmt.executeQuery()) {

                String line = "-".repeat(118);

                System.out.println();
                System.out.println("US11 - View all cities in a district: " + districtName);
                System.out.println(line);

                System.out.printf("| %-30s | %-30s | %-30s | %15s |%n",
                        "City Name", "Country", "District", "Population");

                System.out.println(line);

                int count = 0;

                while (rs.next()) {
                    System.out.printf("| %-30.30s | %-30.30s | %-30.30s | %,15d |%n",
                            rs.getString("CityName"),
                            rs.getString("CountryName"),
                            rs.getString("DistrictName"),
                            rs.getLong("CityPopulation"));
                    count++;
                }

                if (count == 0) {
                    System.out.println("No cities found for district: " + districtName);
                }

                System.out.println(line);
                System.out.println("Cities displayed: " + count);
            }

        } catch (SQLException e) {
            System.out.println("Error running US11 report: " + e.getMessage());
        }
    }
}

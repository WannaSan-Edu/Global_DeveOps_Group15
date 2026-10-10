
package com.napier.sem;

import java.sql.*;

public class TopCitiesInCountry {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:33061/world";
        String user = "root";
        String password = "example";

        String country = args.length > 0 ? args[0] : "Japan";
        int n = 10;

        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("N must be a positive whole number.");
                return;
            }
        }

        if (n <= 0 || country.trim().isEmpty()) {
            System.out.println(
                    "Please provide a valid country and positive N."
            );
            return;
        }

        String sql =
                "SELECT ci.Name AS city_name, " +
                        "co.Name AS country_name, " +
                        "ci.District AS district_name, " +
                        "ci.Population AS city_population " +
                        "FROM city ci " +
                        "JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE co.Name = ? " +
                        "ORDER BY ci.Population DESC, ci.Name ASC " +
                        "LIMIT ?";

        try (
                Connection con =
                        DriverManager.getConnection(url, user, password);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, country.trim());
            ps.setInt(2, n);

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println(
                        "\nTOP " + n +
                                " MOST POPULATED CITIES IN " +
                                country.toUpperCase()
                );

                System.out.printf(
                        "%-30s | %-25s | %-25s | %15s%n",
                        "Name",
                        "Country",
                        "District",
                        "Population"
                );

                System.out.println("-".repeat(105));

                int count = 0;

                while (rs.next()) {
                    System.out.printf(
                            "%-30s | %-25s | %-25s | %,15d%n",
                            rs.getString("city_name"),
                            rs.getString("country_name"),
                            rs.getString("district_name"),
                            rs.getLong("city_population")
                    );

                    count++;
                }

                if (count == 0) {
                    System.out.println(
                            "No cities found for this country."
                    );
                } else {
                    System.out.println(
                            "\nRows returned: " + count
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database/report error: " + e.getMessage()
            );
        }
    }
}

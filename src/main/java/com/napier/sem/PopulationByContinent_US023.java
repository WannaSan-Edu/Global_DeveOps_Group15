
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopulationByContinent_US023 {

    // US23 - View Population by Continent
    public static void generateReport(Connection con)
            throws SQLException {

        String sql =
                "SELECT " +
                        "co.Continent AS continent, " +
                        "SUM(co.Population) AS total_population, " +
                        "COALESCE(SUM(cp.city_pop), 0) AS city_population " +
                        "FROM country co " +
                        "LEFT JOIN ( " +
                        "    SELECT CountryCode, SUM(Population) AS city_pop " +
                        "    FROM city " +
                        "    GROUP BY CountryCode " +
                        ") cp ON co.Code = cp.CountryCode " +
                        "GROUP BY co.Continent " +
                        "ORDER BY total_population DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==============================================================");
            System.out.println("              POPULATION BY CONTINENT - US23");
            System.out.println("==============================================================");

            System.out.printf(
                    "%-20s | %18s | %18s | %15s | %22s | %18s%n",
                    "Continent",
                    "Total Population",
                    "Living in Cities",
                    "City Population %",
                    "Not Living in Cities",
                    "Non-City Population %"
            );

            System.out.println("-".repeat(135));

            int count = 0;

            while (rs.next()) {

                String continent = rs.getString("continent");

                long totalPopulation = rs.getLong("total_population");
                long cityPopulation = rs.getLong("city_population");

                if (rs.wasNull() || totalPopulation <= 0) {
                    System.out.println(
                            "Population data unavailable for continent: "
                                    + continent
                    );
                    continue;
                }

                long nonCityPopulation =
                        totalPopulation - cityPopulation;

                double cityPercentage =
                        (cityPopulation * 100.0) / totalPopulation;

                double nonCityPercentage =
                        (nonCityPopulation * 100.0) / totalPopulation;

                System.out.printf(
                        "%-20s | %,18d | %,18d | %14.2f%% | %,22d | %17.2f%%%n",
                        continent,
                        totalPopulation,
                        cityPopulation,
                        cityPercentage,
                        nonCityPopulation,
                        nonCityPercentage
                );

                count++;
            }

            if (count == 0) {
                System.out.println(
                        "No population data is available."
                );
            }

            System.out.println("-".repeat(135));
            System.out.println("Total continents displayed: " + count);
        }
    }
}


package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopulationByRegion_US24 {

    /**
     * Displays population statistics for each region.
     *
     * Shows:
     * - Total population of the region
     * - Population living in cities
     * - Population not living in cities
     * - Percentage of population living in cities
     * - Percentage of population not living in cities
     *
     * @param con Database connection
     * @throws SQLException if a database error occurs
     */
    public static void generateReport(Connection con) throws SQLException {

        String sql = """
            SELECT
                country.Region AS region,
                SUM(country.Population) AS total_population,
                COALESCE(SUM(city_population.city_pop), 0) AS city_population
            FROM country
            LEFT JOIN (
                SELECT
                    CountryCode,
                    SUM(Population) AS city_pop
                FROM city
                GROUP BY CountryCode
            ) AS city_population
                ON country.Code = city_population.CountryCode
            GROUP BY country.Region
            ORDER BY total_population DESC
            """;

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n==============================================================");
            System.out.println("             POPULATION BY REGION - US24");
            System.out.println("==============================================================");

            System.out.printf(
                    "%-25s %-18s %-18s %-18s %-15s %-15s%n",
                    "Region",
                    "Total Population",
                    "City Population",
                    "Non-City Population",
                    "City (%)",
                    "Non-City (%)"
            );

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------"
            );

            while (rs.next()) {

                String region = rs.getString("region");

                long totalPopulation = rs.getLong("total_population");
                long cityPopulation = rs.getLong("city_population");

                long nonCityPopulation = totalPopulation - cityPopulation;

                double cityPercentage = 0.0;
                double nonCityPercentage = 0.0;

                if (totalPopulation > 0) {
                    cityPercentage =
                            (cityPopulation * 100.0) / totalPopulation;

                    nonCityPercentage =
                            (nonCityPopulation * 100.0) / totalPopulation;
                }

                System.out.printf(
                        "%-25s %-18d %-18d %-18d %-15.2f %-15.2f%n",
                        region,
                        totalPopulation,
                        cityPopulation,
                        nonCityPopulation,
                        cityPercentage,
                        nonCityPercentage
                );
            }

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------"
            );
        }
    }
}

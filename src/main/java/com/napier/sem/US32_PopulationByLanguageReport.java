package com.napier.sem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * US32 - View Population by Language.
 *
 * This report calculates the estimated number of people who speak
 * Chinese, English, Hindi, Spanish and Arabic.
 *
 * It also calculates the percentage of the total world population
 * represented by speakers of each language.
 */
public class US32_PopulationByLanguageReport {

    // The five languages required by the user story.
    private static final List<String> REQUIRED_LANGUAGES = List.of(
            "Chinese",
            "English",
            "Hindi",
            "Spanish",
            "Arabic"
    );

    // Constant used in percentage calculations.
    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    // Shared database connection provided by App.java.
    private final Connection connection;

    /**
     * SQL query to calculate the number of speakers for each language.
     *
     * The CTEs (Common Table Expressions) are used to:
     * 1. Define all five required languages.
     * 2. Calculate the total world population.
     * 3. Calculate the number of speakers in each country.
     *
     * LEFT JOIN ensures that all five languages are returned,
     * even when language data is missing for a particular language.
     *
     * PreparedStatement parameters prevent unsafe SQL input.
     */
    private static final String POPULATION_QUERY = """
            WITH required_languages AS (
                SELECT ? AS LanguageName
                UNION ALL SELECT ?
                UNION ALL SELECT ?
                UNION ALL SELECT ?
                UNION ALL SELECT ?
            ),

            world_population AS (
                SELECT SUM(Population) AS TotalWorldPopulation
                FROM country
            ),

            language_speakers AS (
                SELECT
                    cl.Language AS LanguageName,

                    SUM(
                        c.Population * cl.Percentage / 100.0
                    ) AS TotalSpeakers

                FROM countrylanguage cl

                INNER JOIN country c
                    ON c.Code = cl.CountryCode

                WHERE cl.Language IN (?, ?, ?, ?, ?)

                GROUP BY cl.Language
            )

            SELECT
                rl.LanguageName,
                ls.TotalSpeakers,
                wp.TotalWorldPopulation

            FROM required_languages rl

            CROSS JOIN world_population wp

            LEFT JOIN language_speakers ls
                ON ls.LanguageName = rl.LanguageName

            ORDER BY
                CASE
                    WHEN ls.TotalSpeakers IS NULL THEN 1
                    ELSE 0
                END,
                ls.TotalSpeakers DESC,
                rl.LanguageName ASC
            """;

    /**
     * Constructor.
     *
     * Receives the shared database connection from App.java.
     *
     * @param connection active MySQL database connection
     */
    public US32_PopulationByLanguageReport(Connection connection) {

        // Ensure that a valid database connection is provided.
        this.connection = Objects.requireNonNull(
                connection,
                "Database connection cannot be null."
        );
    }

    /**
     * Main method for executing the US32 report.
     *
     * @throws SQLException if a database error occurs
     */
    public void run() throws SQLException {

        // Check that the shared database connection is still open.
        if (connection.isClosed()) {
            System.out.println(
                    "Cannot generate US32 report: Database connection is closed."
            );
            return;
        }

        // Retrieve the language population data from MySQL.
        List<LanguagePopulation> results =
                getLanguagePopulations();

        // Handle the situation where no records are returned.
        if (results.isEmpty()) {
            System.out.println(
                    "No language population data is available."
            );
            return;
        }

        // Retrieve the total world population from the query.
        BigDecimal worldPopulation =
                results.get(0).worldPopulation;

        // A valid world population is required to calculate percentages.
        if (worldPopulation == null ||
                worldPopulation.signum() <= 0) {

            System.out.println(
                    "World population data is unavailable or zero. "
                            + "Language percentages cannot be calculated."
            );
            return;
        }

        // Display the final report.
        displayReport(results, worldPopulation);
    }

    /**
     * Retrieves population data for all five required languages.
     *
     * @return list containing language names and speaker counts
     * @throws SQLException if the SQL query fails
     */
    private List<LanguagePopulation> getLanguagePopulations()
            throws SQLException {

        // Store the retrieved language population records.
        List<LanguagePopulation> populations = new ArrayList<>();

        // Use try-with-resources to close the PreparedStatement safely.
        // The shared Connection must remain open for other reports.
        try (PreparedStatement statement =
                     connection.prepareStatement(POPULATION_QUERY)) {

            int parameterIndex = 1;

            // Set the five language names for the required_languages CTE.
            for (String language : REQUIRED_LANGUAGES) {
                statement.setString(parameterIndex++, language);
            }

            // Set the same five language names for the SQL filter.
            for (String language : REQUIRED_LANGUAGES) {
                statement.setString(parameterIndex++, language);
            }

            // Execute the query and automatically close the ResultSet.
            try (ResultSet result = statement.executeQuery()) {

                // Read every language returned by the database.
                while (result.next()) {

                    String languageName =
                            result.getString("LanguageName");

                    // Retrieve the estimated number of speakers.
                    BigDecimal speakers =
                            result.getBigDecimal("TotalSpeakers");

                    // Retrieve the total world population.
                    BigDecimal worldPopulation =
                            result.getBigDecimal(
                                    "TotalWorldPopulation"
                            );

                    // Create a report record and store it in the list.
                    populations.add(
                            new LanguagePopulation(
                                    languageName,
                                    speakers,
                                    worldPopulation
                            )
                    );
                }
            }
        }

        return populations;
    }

    /**
     * Calculates the percentage of the world's population
     * represented by speakers of a language.
     *
     * Formula:
     * (Number of Speakers / World Population) * 100
     *
     * BigDecimal is used instead of double to improve
     * decimal calculation accuracy.
     *
     * @param speakers estimated number of language speakers
     * @param worldPopulation total world population
     * @return percentage rounded to two decimal places
     */
    static BigDecimal calculateWorldPercentage(
            BigDecimal speakers,
            BigDecimal worldPopulation) {

        // Validate the speaker count.
        if (speakers == null || speakers.signum() < 0) {
            throw new IllegalArgumentException(
                    "Speaker count must be a valid non-negative number."
            );
        }

        // Prevent division by zero or invalid population values.
        if (worldPopulation == null ||
                worldPopulation.signum() <= 0) {

            throw new IllegalArgumentException(
                    "World population must be greater than zero."
            );
        }

        // Apply the required percentage formula.
        return speakers
                .multiply(ONE_HUNDRED)
                .divide(
                        worldPopulation,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    /**
     * Displays the language population report in descending order.
     *
     * @param populations language population records
     * @param worldPopulation total world population
     */
    private void displayReport(
            List<LanguagePopulation> populations,
            BigDecimal worldPopulation) {

        // Used to format large population numbers with commas.
        NumberFormat numberFormat =
                NumberFormat.getIntegerInstance(Locale.US);

        // Track languages that have missing database records.
        List<String> missingLanguages = new ArrayList<>();

        String line = "-".repeat(75);

        System.out.println();
        System.out.println(
                "US32 - POPULATION BY LANGUAGE"
        );

        System.out.println(line);

        // Display the total world population used in the calculation.
        System.out.println(
                "Total World Population: "
                        + numberFormat.format(worldPopulation)
        );

        System.out.println(line);

        // Display report column headings.
        System.out.printf(
                "| %-12s | %22s | %20s |%n",
                "Language",
                "Number of Speakers",
                "World Population %"
        );

        System.out.println(line);

        // Display each language in the database-sorted order.
        for (LanguagePopulation population : populations) {

            // Check whether speaker data is missing.
            if (population.speakers == null) {

                // Record the language with missing information.
                missingLanguages.add(population.language);

                // Still display the language to satisfy the five-language
                // report requirement.
                System.out.printf(
                        "| %-12s | %22s | %20s |%n",
                        population.language,
                        "N/A",
                        "N/A"
                );

                continue;
            }

            // Round the estimated speaker count to the nearest person.
            BigDecimal roundedSpeakers =
                    population.speakers.setScale(
                            0,
                            RoundingMode.HALF_UP
                    );

            // Calculate the percentage using the unrounded speaker count.
            BigDecimal percentage =
                    calculateWorldPercentage(
                            population.speakers,
                            worldPopulation
                    );

            // Format the percentage to two decimal places.
            String percentageText =
                    percentage.toPlainString() + "%";

            // Print the language, speaker count and percentage.
            System.out.printf(
                    Locale.US,
                    "| %-12s | %22s | %20s |%n",
                    population.language,
                    numberFormat.format(roundedSpeakers),
                    percentageText
            );
        }

        System.out.println(line);

        // Display a clear warning if any required language data is missing.
        if (!missingLanguages.isEmpty()) {

            System.out.println(
                    "WARNING: No language data available for: "
                            + String.join(", ", missingLanguages)
            );
        }

        System.out.println(
                "Languages displayed: " + populations.size()
        );

        System.out.println();
    }

    /**
     * Internal data model for storing one language population record.
     *
     * This keeps database results organised and avoids using
     * unrelated variables throughout the report.
     */
    private static class LanguagePopulation {

        private final String language;
        private final BigDecimal speakers;
        private final BigDecimal worldPopulation;

        private LanguagePopulation(
                String language,
                BigDecimal speakers,
                BigDecimal worldPopulation) {

            this.language = language;
            this.speakers = speakers;
            this.worldPopulation = worldPopulation;
        }
    }
}
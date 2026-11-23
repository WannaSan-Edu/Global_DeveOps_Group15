package com.napier.sem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/** Database integration tests for US01-US32 using the real MySQL world database. */
class AppIntegrationTest {

    private PrintStream originalOut;
    private PrintStream originalErr;
    private Locale originalLocale;
    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;

    @BeforeEach
    void captureConsole() {
        originalOut = System.out;
        originalErr = System.err;
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        Locale.setDefault(originalLocale);
    }

    private Connection connection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }

        String url = setting("WORLD_DB_URL",
                "jdbc:mysql://localhost:33060/world?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = setting("WORLD_DB_USER", "root");
        String password = setting("WORLD_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String name, String fallback) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            value = System.getenv(name);
        }
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private long firstLong(Connection con, String sql) throws SQLException {
        try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            if (!rs.next()) {
                throw new SQLException("Query returned no rows: " + sql);
            }
            return rs.getLong(1);
        }
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    // --- Country reports (US01-US03) ---

    @Test
    void us01_reportsCountriesFromWorldDatabase() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM country") > 0);
            US01_view_all_countries_in_the_world.printReport(con);
            assertTrue(out().contains("China") || out().contains("India"), out());
        }
    }

    @Test
    void us02_reportsCountriesGroupedByContinent() throws Exception {
        try (Connection con = connection()) {
            US02_view_all_countries_in_a_continent.printReport(con);
            assertTrue(out().contains("Asia"), out());
        }
    }

    @Test
    void us03_reportsCountriesGroupedByRegion() throws Exception {
        try (Connection con = connection()) {
            US03_view_all_countries_in_a_region.printReport(con);
            assertTrue(out().contains("Region") || out().contains("region"), out());
        }
    }

    // --- Top N country reports (US04-US06) ---

    @Test
    void us04_returnsTopCountriesWorldwide() throws Exception {
        try (Connection con = connection()) {
            US04_view_top_n_countries_in_the_world.printReport(con, 5);
            assertTrue(out().contains("China") || out().contains("India"), out());
        }
    }

    @Test
    void us05_returnsTopCountriesInAsia() throws Exception {
        try (Connection con = connection()) {
            US05_view_top_n_countries_in_a_continent.printReport(con, "Asia", 5);
            assertTrue(out().contains("Asia") || out().contains("China") || out().contains("India"), out());
        }
    }

    @Test
    void us06_returnsTopCountriesInEasternAsia() throws Exception {
        try (Connection con = connection()) {
            US06_view_top_n_countries_in_a_region.printReport(con, "Eastern Asia", 5);
            assertFalse(out().isBlank(), "US06 should produce report output");
        }
    }

    // --- City reports (US07-US12) ---

    @Test
    void us07_listsCitiesWorldwide() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM city") > 0);
            new ViewAllCitiesInTheWorld(con).getAllCitiesInWorld();
            assertFalse(out().isBlank(), "US07 should produce report output");
        }
    }

    @Test
    void us08_listsCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesinaContinent(con).getCitiesInContinent("Asia");
            assertFalse(out().isBlank(), "US08 should produce report output");
        }
    }

    @Test
    void us09_listsCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesinaRegion(con).getCitiesInRegion("Southeast Asia");
            assertFalse(out().isBlank(), "US09 should produce report output");
        }
    }

    @Test
    void us10_listsCitiesInIndia() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("India");
            assertFalse(out().isBlank(), "US10 should produce report output");
        }
    }

    @Test
    void us11_listsCitiesInMaharashtra() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("Maharashtra");
            assertFalse(out().isBlank(), "US11 should produce report output");
        }
    }

    @Test
    void us12_listsTopCitiesWorldwide() throws Exception {
        try (Connection con = connection()) {
            new ViewTopCitiesInTheWorld_US12(con).getTopNCitiesInWorld(5);
            assertFalse(out().isBlank(), "US12 should produce report output");
        }
    }

    // --- Top N city reports (US13-US16) ---

    @Test
    void us13_returnsTopCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInContinent.generateReport(con, "Asia", 5);
            assertFalse(out().isBlank(), "US13 should produce report output");
        }
    }

    @Test
    void us14_returnsTopCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInRegion.generateReport(con, "Southeast Asia", 5);
            assertFalse(out().isBlank(), "US14 should produce report output");
        }
    }

    @Test
    void us15_returnsTopCitiesInJapan() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInCountry.generateReport(con, "Japan", 5);
            assertFalse(out().isBlank(), "US15 should produce report output");
        }
    }

    @Test
    void us16_returnsTopCitiesInCalifornia() throws Exception {
        try (Connection con = connection()) {
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 5);
            assertFalse(out().isBlank(), "US16 should produce report output");
        }
    }

    // --- Capital city reports (US17-US22) ---

    @Test
    void us17_returnsAllCapitalCities() throws Exception {
        try (Connection con = connection()) {
            var capitals = AllCapitalsCitiesInTheWorld_US17.getAll(con);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            assertFalse(out().isBlank());
        }
    }

    @Test
    void us18_returnsCapitalCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, "Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalCitiesInAcontinent.printReport(capitals, "Asia");
            assertFalse(out().isBlank());
        }
    }

    @Test
    void us19_returnsCapitalCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, "Southeast Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalsCitiesInAregion.printReport(capitals, "Southeast Asia");
            assertFalse(out().isBlank());
        }
    }

    @Test
    void us20_returnsTopFiveCapitalCities() throws Exception {
        try (Connection con = connection()) {
            var capitals = ViewTopNCapitalCitiesInTheWorld_US20.getTopN(con, 5);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            ViewTopNCapitalCitiesInTheWorld_US20.printReport(capitals, 5);
            assertFalse(out().isBlank());
        }
    }

    @Test
    void us21_returnsTopCapitalCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Asia", 5);
            assertFalse(out().isBlank());
        }
    }

    @Test
    void us22_returnsTopCapitalCitiesInCaribbean() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "Caribbean", 5);
            assertFalse(out().isBlank());
        }
    }

    // --- Population breakdown reports (US23-US25) ---

    @Test
    void us23_generatesPopulationBreakdownByContinent() throws Exception {
        try (Connection con = connection()) {
            PopulationByContinent_US023.generateReport(con);
            assertFalse(out().isBlank(), "US23 should produce report output");
        }
    }

    @Test
    void us24_generatesPopulationBreakdownByRegion() throws Exception {
        try (Connection con = connection()) {
            PopulationByRegion_US24.generateReport(con);
            assertFalse(out().isBlank(), "US24 should produce report output");
        }
    }

    @Test
    void us25_generatesPopulationBreakdownByCountry() throws Exception {
        try (Connection con = connection()) {
            var populations = ViewPopulationByCountry.getPopulationByCountry(con);
            assertNotNull(populations);
            assertFalse(populations.isEmpty());
            ViewPopulationByCountry.printReport(populations);
            assertFalse(out().isBlank());
        }
    }

    // --- Population total reports (US26-US31) ---

    @Test
    void us26_reportsTotalWorldPopulation() throws Exception {
        try (Connection con = connection()) {
            TotalWorldPopulation.printReport(con);
            assertFalse(out().isBlank(), "US26 should produce report output");
        }
    }

    @Test
    void us27_reportsPopulationOfAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Asia");
            assertFalse(out().isBlank(), "US27 should produce report output");
        }
    }

    @Test
    void us28_reportsPopulationOfSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "Southeast Asia");
            assertFalse(out().isBlank(), "US28 should produce report output");
        }
    }

    @Test
    void us29_reportsPopulationOfMyanmar() throws Exception {
        try (Connection con = connection()) {
            new CountryPopulationReport(con).run("Myanmar");
            assertFalse(out().isBlank(), "US29 should produce report output");
        }
    }

    @Test
    void us30_reportsPopulationOfMandalayDistrict() throws Exception {
        try (Connection con = connection()) {
            new DistrictPopulationReport(con).run("Mandalay");
            assertFalse(out().isBlank(), "US30 should produce report output");
        }
    }

    @Test
    void us31_reportsPopulationOfYangonInMyanmar() throws Exception {
        try (Connection con = connection()) {
            new ViewPopulationOfSelectedCity_US31(con).run("Rangoon", "Myanmar");
            assertFalse(out().isBlank(), "US31 should produce report output");
        }
    }

    // --- Language report (US32) ---

    @Test
    void us32_reportsPopulationByLanguage() throws Exception {
        try (Connection con = connection()) {
            new US32_PopulationByLanguageReport(con).run();
            assertFalse(out().isBlank(), "US32 should produce report output");
        }
    }
}
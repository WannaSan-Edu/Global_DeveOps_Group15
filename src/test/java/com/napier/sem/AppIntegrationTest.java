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
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end database integration tests for {@link App#main(String[])} and user stories
 * US01 through US32 against a live MySQL {@code world} database.
 */
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
        String password = setting("WORLD_DB_PASSWORD", "example");
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

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    // =========================================================================
    // Application Entry Point Tests (App.main)
    // =========================================================================

    @Test
    void appMain_withCountryAndDistrictArgs_runsAllReports() {
        App.main(new String[]{"Myanmar", "Mandalay"});

        assertTrue(out().contains("Database connection established."),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("Database check passed."),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("US01 - All Countries in the World by Population"),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("US32 - Population by Language"),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("All reports available in this project have finished."),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertFalse(err().contains("ERROR: Could not connect to MySQL"),
                "Errors:\n" + err());
        assertFalse(err().contains("Required world database tables are unavailable"),
                "Errors:\n" + err());
    }

    @Test
    void appMain_withNoArgs_runsDefaultReports() {
        App.main(new String[]{});

        assertTrue(out().contains("Database connection established."),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("US29 - Country Population Report"),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("US30 - District Population Report"),
                "Output:\n" + out() + "\nErrors:\n" + err());
        assertTrue(out().contains("All reports available in this project have finished."),
                "Output:\n" + out() + "\nErrors:\n" + err());
    }

    // =========================================================================
    // Country Reports (US01-US03)
    // =========================================================================

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
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // Top N Country Reports (US04-US06)
    // =========================================================================

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
            US05_view_top_n_countries_in_a_continent.printReport(con, "Atlantis", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us06_returnsTopCountriesInEasternAsia() throws Exception {
        try (Connection con = connection()) {
            US06_view_top_n_countries_in_a_region.printReport(con, "Eastern Asia", 5);
            US06_view_top_n_countries_in_a_region.printReport(con, "NonExistentRegion", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // City Reports (US07-US12)
    // =========================================================================

    @Test
    void us07_listsCitiesWorldwide() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM city") > 0);
            new ViewAllCitiesInTheWorld(con).getAllCitiesInWorld();
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us08_listsCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesinaContinent(con).getCitiesInContinent("Asia");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us09_listsCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesinaRegion(con).getCitiesInRegion("Southeast Asia");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us10_listsCitiesInIndia() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("India");
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("NoSuchCountryXYZ");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us11_listsCitiesInMaharashtra() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("Maharashtra");
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("NoSuchDistrictXYZ");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us12_listsTopCitiesWorldwide() throws Exception {
        try (Connection con = connection()) {
            new ViewTopCitiesInTheWorld_US12(con).getTopNCitiesInWorld(5);
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // Top N City Reports (US13-US16)
    // =========================================================================

    @Test
    void us13_returnsTopCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInContinent.generateReport(con, "Asia", 5);
            TopCitiesInContinent.generateReport(con, "Atlantis", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us14_returnsTopCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInRegion.generateReport(con, "Southeast Asia", 5);
            TopCitiesInRegion.generateReport(con, "NoSuchRegionXYZ", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us15_returnsTopCitiesInJapan() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInCountry.generateReport(con, "Japan", 5);
            TopCitiesInCountry.generateReport(con, "NoSuchCountryXYZ", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us16_returnsTopCitiesInCalifornia() throws Exception {
        try (Connection con = connection()) {
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 5);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "NoSuchDistrictXYZ", 5);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 0);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "  ", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // Capital City Reports (US17-US22)
    // =========================================================================

    @Test
    void us17_returnsAllCapitalCities() throws Exception {
        try (Connection con = connection()) {
            var capitals = AllCapitalsCitiesInTheWorld_US17.getAll(con);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us18_returnsCapitalCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, "Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalCitiesInAcontinent.printReport(capitals, "Asia");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us19_returnsCapitalCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, "Southeast Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalsCitiesInAregion.printReport(capitals, "Southeast Asia");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us20_returnsTopFiveCapitalCities() throws Exception {
        try (Connection con = connection()) {
            var capitals = ViewTopNCapitalCitiesInTheWorld_US20.getTopN(con, 5);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            ViewTopNCapitalCitiesInTheWorld_US20.printReport(capitals, 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us21_returnsTopCapitalCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Asia", 5);
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Atlantis", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us22_returnsTopCapitalCitiesInCaribbean() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "Caribbean", 5);
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "NoSuchRegionXYZ", 5);
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // Population Breakdown & Total Reports (US23-US31)
    // =========================================================================

    @Test
    void us23_generatesPopulationBreakdownByContinent() throws Exception {
        try (Connection con = connection()) {
            PopulationByContinent_US023.generateReport(con);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us24_generatesPopulationBreakdownByRegion() throws Exception {
        try (Connection con = connection()) {
            PopulationByRegion_US24.generateReport(con);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us25_generatesPopulationBreakdownByCountry() throws Exception {
        try (Connection con = connection()) {
            var populations = ViewPopulationByCountry.getPopulationByCountry(con);
            assertNotNull(populations);
            assertFalse(populations.isEmpty());
            ViewPopulationByCountry.printReport(populations);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us26_reportsTotalWorldPopulation() throws Exception {
        try (Connection con = connection()) {
            TotalWorldPopulation.printReport(con);
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us27_reportsPopulationOfAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Asia");
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Atlantis");
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us28_reportsPopulationOfSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "Southeast Asia");
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "NoSuchRegionXYZ");
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, " ");
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us29_reportsPopulationOfMyanmarAndAll() throws Exception {
        try (Connection con = connection()) {
            CountryPopulationReport report = new CountryPopulationReport(con);
            report.run("Myanmar");
            report.run("");
            report.run("NoSuchCountryXYZ");
            assertNull(report.getCountryPopulation(null));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us30_reportsPopulationOfMandalayAndAll() throws Exception {
        try (Connection con = connection()) {
            DistrictPopulationReport report = new DistrictPopulationReport(con);
            report.run("Mandalay");
            report.run("");
            report.run("NoSuchDistrictXYZ");
            assertNull(report.getDistrictPopulation(null));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertFalse(out().isBlank(), out());
        }
    }

    @Test
    void us31_reportsPopulationOfYangonInMyanmar() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfSelectedCity_US31 report = new ViewPopulationOfSelectedCity_US31(con);
            report.run("Rangoon", "Myanmar");
            report.run("NoSuchCityXYZ", "Myanmar");
            report.run("", "Myanmar");
            assertFalse(out().isBlank(), out());
        }
    }

    // =========================================================================
    // Language Report (US32)
    // =========================================================================

    @Test
    void us32_reportsPopulationByLanguage() throws Exception {
        try (Connection con = connection()) {
            new US32_PopulationByLanguageReport(con).run();
            assertFalse(out().isBlank(), out());
        }
    }
}
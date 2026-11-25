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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end database integration tests for {@link App#main(String[])} and user stories
 * US01 through US32 against a live MySQL {@code world} database, exercising valid results,
 * empty/not-found results, invalid input branches, and closed-connection {@link SQLException} paths.
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

    private Connection closedConnection() throws SQLException {
        Connection con = connection();
        con.close();
        return con;
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
    void us01_reportsCountriesFromWorldDatabase_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM country") > 0);
            US01_view_all_countries_in_the_world.printReport(con);
            assertTrue(out().contains("China") || out().contains("India"), out());
        }

        US01_view_all_countries_in_the_world.printReport(closedConnection());
        assertTrue(err().contains("Error retrieving countries of the world:"), err());
    }

    @Test
    void us02_reportsCountriesGroupedByContinent_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            US02_view_all_countries_in_a_continent.printReport(con);
            assertTrue(out().contains("Asia"), out());
        }

        US02_view_all_countries_in_a_continent.printReport(closedConnection());
        assertTrue(err().contains("Error retrieving countries by continent:"), err());
    }

    @Test
    void us03_reportsCountriesGroupedByRegion_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            US03_view_all_countries_in_a_region.printReport(con);
            assertFalse(out().isBlank(), out());
        }

        US03_view_all_countries_in_a_region.printReport(closedConnection());
        assertTrue(err().contains("Error retrieving countries by region:"), err());
    }

    // =========================================================================
    // Top N Country Reports (US04-US06)
    // =========================================================================

    @Test
    void us04_returnsTopCountriesWorldwide_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            US04_view_top_n_countries_in_the_world.printReport(con, 5);
            assertTrue(out().contains("China") || out().contains("India"), out());
        }

        US04_view_top_n_countries_in_the_world.printReport(closedConnection(), 5);
        assertTrue(err().contains("Error retrieving top N countries:"), err());
    }

    @Test
    void us05_returnsTopCountriesInAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            US05_view_top_n_countries_in_a_continent.printReport(con, "Asia", 5);
            US05_view_top_n_countries_in_a_continent.printReport(con, "Atlantis", 5);
            assertTrue(out().contains("No country records found for continent: Atlantis"), out());
        }

        US05_view_top_n_countries_in_a_continent.printReport(closedConnection(), "Asia", 5);
        assertTrue(err().contains("Error retrieving top N countries in a continent:"), err());
    }

    @Test
    void us06_returnsTopCountriesInEasternAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            US06_view_top_n_countries_in_a_region.printReport(con, "Eastern Asia", 5);
            US06_view_top_n_countries_in_a_region.printReport(con, "NonExistentRegion", 5);
            assertTrue(out().contains("No country records found for region: NonExistentRegion"), out());
        }

        US06_view_top_n_countries_in_a_region.printReport(closedConnection(), "Eastern Asia", 5);
        assertTrue(err().contains("Error retrieving top N countries in a region:"), err());
    }

    // =========================================================================
    // City Reports (US07-US12)
    // =========================================================================

    @Test
    void us07_listsCitiesWorldwide_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM city") > 0);
            new ViewAllCitiesInTheWorld(con).getAllCitiesInWorld();
            assertFalse(out().isBlank(), out());
        }

        new ViewAllCitiesInTheWorld(closedConnection()).getAllCitiesInWorld();
        assertTrue(out().contains("Error running US07 report:"), out());
    }

    @Test
    void us08_listsCitiesInAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesinaContinent(con).getCitiesInContinent("Asia");
            new ViewCitiesinaContinent(con).getCitiesInContinent("Atlantis");
            assertFalse(out().isBlank(), out());
        }

        new ViewCitiesinaContinent(closedConnection()).getCitiesInContinent("Asia");
        assertTrue(out().contains("Error running US08 report:"), out());
    }

    @Test
    void us09_listsCitiesInSoutheastAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesinaRegion(con).getCitiesInRegion("Southeast Asia");
            new ViewAllCitiesinaRegion(con).getCitiesInRegion("NoSuchRegionXYZ");
            assertFalse(out().isBlank(), out());
        }

        new ViewAllCitiesinaRegion(closedConnection()).getCitiesInRegion("Southeast Asia");
        assertTrue(out().contains("Error running US09 report:"), out());
    }

    @Test
    void us10_listsCitiesInIndia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("India");
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("NoSuchCountryXYZ");
            assertTrue(out().contains("No cities found."), out());
        }

        new ViewCitiesInACountry_US10(closedConnection()).getCitiesInCountry("India");
        assertTrue(out().contains("US10 Error:"), out());
    }

    @Test
    void us11_listsCitiesInMaharashtra_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("Maharashtra");
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("NoSuchDistrictXYZ");
            assertTrue(out().contains("No cities found."), out());
        }

        new ViewAllCitiesInaDistrict_US11(closedConnection()).getCitiesInDistrict("Maharashtra");
        assertTrue(out().contains("US11 Error:"), out());
    }

    @Test
    void us12_listsTopCitiesWorldwide_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new ViewTopCitiesInTheWorld_US12(con).getTopNCitiesInWorld(5);
            assertFalse(out().isBlank(), out());
        }

        new ViewTopCitiesInTheWorld_US12(closedConnection()).getTopNCitiesInWorld(5);
        assertTrue(out().contains("US12 Error:"), out());
    }

    // =========================================================================
    // Top N City Reports (US13-US16)
    // =========================================================================

    @Test
    void us13_returnsTopCitiesInAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInContinent.generateReport(con, "Asia", 5);
            TopCitiesInContinent.generateReport(con, "Atlantis", 5);
            TopCitiesInContinent.generateReport(con, null, 5);
            TopCitiesInContinent.generateReport(con, "", 5);
            TopCitiesInContinent.generateReport(con, "Asia", 0);
            assertTrue(out().contains("No cities found for this continent."), out());
        }

        TopCitiesInContinent.generateReport(closedConnection(), "Asia", 5);
        assertTrue(out().contains("Database/report error:"), out());
    }

    @Test
    void us14_returnsTopCitiesInSoutheastAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInRegion.generateReport(con, "Southeast Asia", 5);
            TopCitiesInRegion.generateReport(con, "NoSuchRegionXYZ", 5);
            TopCitiesInRegion.generateReport(con, null, 5);
            TopCitiesInRegion.generateReport(con, "", 5);
            TopCitiesInRegion.generateReport(con, "Southeast Asia", 0);
            assertTrue(out().contains("No cities found for this region."), out());
        }

        TopCitiesInRegion.generateReport(closedConnection(), "Southeast Asia", 5);
        assertTrue(out().contains("Database/report error:"), out());
    }

    @Test
    void us15_returnsTopCitiesInJapan_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInCountry.generateReport(con, "Japan", 5);
            TopCitiesInCountry.generateReport(con, "NoSuchCountryXYZ", 5);
            TopCitiesInCountry.generateReport(con, null, 5);
            TopCitiesInCountry.generateReport(con, "", 5);
            TopCitiesInCountry.generateReport(con, "Japan", 0);
            assertTrue(out().contains("No cities found for this country."), out());
        }

        TopCitiesInCountry.generateReport(closedConnection(), "Japan", 5);
        assertTrue(out().contains("Database/report error:"), out());
    }

    @Test
    void us16_returnsTopCitiesInCalifornia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 5);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "NoSuchDistrictXYZ", 5);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 0);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, null, 5);
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "  ", 5);
            assertTrue(out().contains("No cities found in district: NoSuchDistrictXYZ"), out());
        }

        assertThrows(SQLException.class,
                () -> TopNPopulatedCitiesInDistrict_US016.generateReport(closedConnection(), "California", 5));
    }

    // =========================================================================
    // Capital City Reports (US17-US22)
    // =========================================================================

    @Test
    void us17_returnsAllCapitalCities_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            var capitals = AllCapitalsCitiesInTheWorld_US17.getAll(con);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            AllCapitalsCitiesInTheWorld_US17.printReport(new ArrayList<>());
            assertFalse(out().isBlank(), out());
        }

        assertNull(AllCapitalsCitiesInTheWorld_US17.getAll(closedConnection()));
    }

    @Test
    void us18_returnsCapitalCitiesInAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, "Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalCitiesInAcontinent.printReport(capitals, "Asia");
            CapitalCitiesInAcontinent.printReport(new ArrayList<>(), "Atlantis");
            assertFalse(out().isBlank(), out());
        }

        assertNull(CapitalCitiesInAcontinent.getCapitalCitiesByContinent(closedConnection(), "Asia"));
    }

    @Test
    void us19_returnsCapitalCitiesInSoutheastAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, "Southeast Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalsCitiesInAregion.printReport(capitals, "Southeast Asia");
            CapitalsCitiesInAregion.printReport(new ArrayList<>(), "NoSuchRegionXYZ");
            assertFalse(out().isBlank(), out());
        }

        assertNull(CapitalsCitiesInAregion.getCapitalCitiesByRegion(closedConnection(), "Southeast Asia"));
    }

    @Test
    void us20_returnsTopFiveCapitalCities_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            var capitals = ViewTopNCapitalCitiesInTheWorld_US20.getTopN(con, 5);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            ViewTopNCapitalCitiesInTheWorld_US20.printReport(capitals, 5);
            ViewTopNCapitalCitiesInTheWorld_US20.printReport(new ArrayList<>(), 5);
            assertThrows(IllegalArgumentException.class,
                    () -> ViewTopNCapitalCitiesInTheWorld_US20.getTopN(con, 0));
            assertFalse(out().isBlank(), out());
        }

        assertNull(ViewTopNCapitalCitiesInTheWorld_US20.getTopN(closedConnection(), 5));
    }

    @Test
    void us21_returnsTopCapitalCitiesInAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Asia", 5);
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Atlantis", 5);
            assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(con, null, 5));
            assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(con, "", 5));
            assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(con, "Asia", 0));
            assertFalse(out().isBlank(), out());
        }

        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(closedConnection(), "Asia", 5));
    }

    @Test
    void us22_returnsTopCapitalCitiesInCaribbean_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "Caribbean", 5);
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "NoSuchRegionXYZ", 5);
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, null, 5);
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "", 5);
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "Caribbean", 0);
            assertTrue(out().contains("No capital cities found for region: NoSuchRegionXYZ"), out());
        }

        ViewTopNCapitalCitiesInARegion_US22.generateReport(closedConnection(), "Caribbean", 5);
        assertTrue(out().contains("Database/report error:"), out());
    }

    // =========================================================================
    // Population Breakdown & Total Reports (US23-US31)
    // =========================================================================

    @Test
    void us23_generatesPopulationBreakdownByContinent_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            PopulationByContinent_US023.generateReport(con);
            assertFalse(out().isBlank(), out());
        }

        assertThrows(SQLException.class,
                () -> PopulationByContinent_US023.generateReport(closedConnection()));
    }

    @Test
    void us24_generatesPopulationBreakdownByRegion_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            PopulationByRegion_US24.generateReport(con);
            assertFalse(out().isBlank(), out());
        }

        assertThrows(SQLException.class,
                () -> PopulationByRegion_US24.generateReport(closedConnection()));
    }

    @Test
    void us25_generatesPopulationBreakdownByCountry_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            var populations = ViewPopulationByCountry.getPopulationByCountry(con);
            assertNotNull(populations);
            assertFalse(populations.isEmpty());
            ViewPopulationByCountry.printReport(populations);
            ViewPopulationByCountry.printReport(new ArrayList<>());
            assertFalse(out().isBlank(), out());
        }

        assertNull(ViewPopulationByCountry.getPopulationByCountry(closedConnection()));
    }

    @Test
    void us26_reportsTotalWorldPopulation_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            TotalWorldPopulation.printReport(con);
            assertFalse(out().isBlank(), out());
        }

        TotalWorldPopulation.printReport(closedConnection());
        assertTrue(out().contains("Unable to generate Total World Population Report:"), out());
    }

    @Test
    void us27_reportsPopulationOfAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Asia");
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Atlantis");
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, null);
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "");
            assertTrue(out().contains("No population data available for continent: Atlantis"), out());
        }

        ViewPopulationOfAContinent_US27.getPopulationOfContinent(closedConnection(), "Asia");
        assertTrue(err().contains("Failed to retrieve population for Asia"), err());
    }

    @Test
    void us28_reportsPopulationOfSoutheastAsia_emptyAndClosedConnection() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "Southeast Asia");
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "NoSuchRegionXYZ");
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, null);
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, " ");
            assertTrue(out().contains("No population data available for region: NoSuchRegionXYZ"), out());
        }

        ViewPopulationOfARegion_US28.getPopulationOfRegion(closedConnection(), "Southeast Asia");
        assertTrue(err().contains("Failed to retrieve population for region: Southeast Asia"), err());
    }

    @Test
    void us29_reportsPopulationOfMyanmarAndAll_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            CountryPopulationReport report = new CountryPopulationReport(con);
            report.run("Myanmar");
            report.run("");
            report.run(null);
            report.run("NoSuchCountryXYZ");
            assertNull(report.getCountryPopulation(null));
            assertNull(report.getCountryPopulation(""));
            assertNull(report.getCountryPopulation("NoSuchCountryXYZ"));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertFalse(out().isBlank(), out());
        }

        CountryPopulationReport closedReport = new CountryPopulationReport(closedConnection());
        assertThrows(SQLException.class, () -> closedReport.getCountryPopulation("Myanmar"));
        assertThrows(SQLException.class, closedReport::getAllCountryPopulations);
    }

    @Test
    void us30_reportsPopulationOfMandalayAndAll_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            DistrictPopulationReport report = new DistrictPopulationReport(con);
            report.run("Mandalay");
            report.run("");
            report.run(null);
            report.run("NoSuchDistrictXYZ");
            assertNull(report.getDistrictPopulation(null));
            assertNull(report.getDistrictPopulation(""));
            assertNull(report.getDistrictPopulation("NoSuchDistrictXYZ"));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertFalse(out().isBlank(), out());
        }

        DistrictPopulationReport closedReport = new DistrictPopulationReport(closedConnection());
        assertThrows(SQLException.class, () -> closedReport.getDistrictPopulation("Mandalay"));
        assertThrows(SQLException.class, closedReport::getAllDistrictPopulations);
    }

    @Test
    void us31_reportsPopulationOfYangonInMyanmar_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfSelectedCity_US31 report = new ViewPopulationOfSelectedCity_US31(con);
            report.run("Rangoon (Yangon)", "Myanmar");
            assertTrue(out().contains("US31 - POPULATION OF A SELECTED CITY"), out());
            assertTrue(out().contains("3,361,700"), out());

            report.run("NoSuchCityXYZ", "Myanmar");
            assertTrue(out().contains("No population data available for NoSuchCityXYZ, Myanmar."), out());

            report.run("", "Myanmar");
            report.run(null, "Myanmar");
            report.run("Rangoon (Yangon)", "");
            report.run("Rangoon (Yangon)", null);
            assertTrue(out().contains("Please provide a valid city and country name."), out());
        }

        ViewPopulationOfSelectedCity_US31 closedReport = new ViewPopulationOfSelectedCity_US31(closedConnection());
        closedReport.run("Rangoon (Yangon)", "Myanmar");
        assertTrue(out().contains("Cannot generate US31 report: Database connection is closed."), out());
    }

    // =========================================================================
    // Language Report (US32)
    // =========================================================================

    @Test
    void us32_reportsPopulationByLanguage_andClosedConnection() throws Exception {
        try (Connection con = connection()) {
            new US32_PopulationByLanguageReport(con).run();
            assertFalse(out().isBlank(), out());
        }

        US32_PopulationByLanguageReport closedReport = new US32_PopulationByLanguageReport(closedConnection());
        closedReport.run();
        assertTrue(out().contains("Cannot generate US32 report: Database connection is closed."), out());
    }
}
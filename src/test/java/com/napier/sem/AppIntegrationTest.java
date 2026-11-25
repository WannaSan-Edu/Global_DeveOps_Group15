package com.napier.sem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
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
 * End-to-end database integration and branch/edge-case tests for {@link App#main(String[])}
 * and user stories US01 through US32 against a live MySQL {@code world} database, plus
 * validation, empty-result, null/closed-connection, and exception branches.
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

        assertTrue(out().contains("Database connection established."));
        assertTrue(out().contains("Database check passed."));
        assertTrue(out().contains("US01 - All Countries in the World by Population"));
        assertTrue(out().contains("US32 - Population by Language"));
        assertTrue(out().contains("All reports available in this project have finished."));
        assertFalse(err().contains("ERROR: Could not connect to MySQL"));
        assertFalse(err().contains("Required world database tables are unavailable"));
    }

    @Test
    void appMain_withNoArgs_runsDefaultReports() {
        App.main(new String[]{});

        assertTrue(out().contains("Database connection established."));
        assertTrue(out().contains("Country Population Report"));
        assertTrue(out().contains("District Population Report"));
        assertTrue(out().contains("All reports available in this project have finished."));
    }

    // =========================================================================
    // Country Reports (US01-US06)
    // =========================================================================

    @Test
    void us01_reportsCountriesFromWorldDatabase() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM country") > 0);
            US01_view_all_countries_in_the_world.printReport(con);
            assertTrue(out().contains("China") || out().contains("India"));
            assertTrue(out().contains("Countries displayed:"));
        }
    }

    @Test
    void us02_reportsCountriesGroupedByContinent() throws Exception {
        try (Connection con = connection()) {
            US02_view_all_countries_in_a_continent.printReport(con);
            assertTrue(out().contains("Asia"));
            assertTrue(out().contains("Continent:"));
        }
    }

    @Test
    void us03_reportsCountriesGroupedByRegion() throws Exception {
        try (Connection con = connection()) {
            US03_view_all_countries_in_a_region.printReport(con);
            assertTrue(out().contains("Region:"));
        }
    }

    @Test
    void us04_topNCountriesWorldwide_happyPathAndInvalidN() throws Exception {
        try (Connection con = connection()) {
            US04_view_top_n_countries_in_the_world.printReport(con, 5);
            assertTrue(out().contains("Top 5 Most Populated Countries in the World"));
            assertTrue(out().contains("Countries displayed: 5"));
        }

        US04_view_top_n_countries_in_the_world.printReport(null, 0);
        assertTrue(err().contains("Invalid input: N must be a positive integer greater than zero."));
    }

    @Test
    void us05_topNCountriesInContinent_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            US05_view_top_n_countries_in_a_continent.printReport(con, "Asia", 5);
            assertTrue(out().contains("Top 5 Most Populated Countries in Asia"));

            US05_view_top_n_countries_in_a_continent.printReport(con, "Atlantis", 5);
            assertTrue(out().contains("No country records found for continent: Atlantis"));
        }

        US05_view_top_n_countries_in_a_continent.printReport(null, "  ", 5);
        assertTrue(err().contains("Invalid input: Please enter a valid continent."));

        US05_view_top_n_countries_in_a_continent.printReport(null, "Asia", -1);
        assertTrue(err().contains("Invalid input: N must be a positive integer greater than zero."));
    }

    @Test
    void us06_topNCountriesInRegion_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            US06_view_top_n_countries_in_a_region.printReport(con, "Eastern Asia", 5);
            assertTrue(out().contains("Top 5 Most Populated Countries in Eastern Asia"));

            US06_view_top_n_countries_in_a_region.printReport(con, "NonExistentRegion", 5);
            assertTrue(out().contains("No country records found for region: NonExistentRegion"));
        }

        US06_view_top_n_countries_in_a_region.printReport(null, "", 5);
        assertTrue(err().contains("Invalid input: Please enter a valid region."));

        US06_view_top_n_countries_in_a_region.printReport(null, "Eastern Asia", 0);
        assertTrue(err().contains("Invalid input: N must be a positive integer greater than zero."));
    }

    // =========================================================================
    // City Reports (US07-US12)
    // =========================================================================

    @Test
    void us07_listsCitiesWorldwide() throws Exception {
        try (Connection con = connection()) {
            assertTrue(firstLong(con, "SELECT COUNT(*) FROM city") > 0);
            new ViewAllCitiesInTheWorld(con).getAllCitiesInWorld();
            assertTrue(out().contains("US07 - View all cities in the world"));
            assertTrue(out().contains("Cities displayed:"));
        }
    }

    @Test
    void us08_listsCitiesInAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesinaContinent(con).getCitiesInContinent("Asia");
            assertTrue(out().contains("US08 - View all cities in a continent: Asia"));
        }
    }

    @Test
    void us09_listsCitiesInSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesinaRegion(con).getCitiesInRegion("Southeast Asia");
            assertTrue(out().contains("US09 - View all cities in a region: Southeast Asia"));
        }
    }

    @Test
    void us10_citiesInCountry_foundAndNotFound() throws Exception {
        try (Connection con = connection()) {
            new ViewCitiesInACountry_US10(con).getCitiesInCountry("India");
            assertTrue(out().contains("US10 - All Cities in India"));

            new ViewCitiesInACountry_US10(con).getCitiesInCountry("NoSuchCountryXYZ");
            assertTrue(out().contains("No cities found."));
        }
    }

    @Test
    void us11_citiesInDistrict_foundAndNotFound() throws Exception {
        try (Connection con = connection()) {
            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("Maharashtra");
            assertTrue(out().contains("US11 - All Cities in District: Maharashtra"));

            new ViewAllCitiesInaDistrict_US11(con).getCitiesInDistrict("NoSuchDistrictXYZ");
            assertTrue(out().contains("No cities found."));
        }
    }

    @Test
    void us12_topCitiesWorldwide_happyPathAndInvalidN() throws Exception {
        try (Connection con = connection()) {
            new ViewTopCitiesInTheWorld_US12(con).getTopNCitiesInWorld(5);
            assertTrue(out().contains("US12 - Top 5 Cities in the World"));
        }

        new ViewTopCitiesInTheWorld_US12(null).getTopNCitiesInWorld(0);
        assertTrue(out().contains("US12 Error: N must be greater than zero."));
    }

    // =========================================================================
    // Top N City Reports (US13-US16)
    // =========================================================================

    @Test
    void us13_topCitiesInContinent_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInContinent.generateReport(con, "Asia", 5);
            assertTrue(out().contains("TOP 5 MOST POPULATED CITIES IN ASIA"));

            TopCitiesInContinent.generateReport(con, "Atlantis", 5);
            assertTrue(out().contains("No cities found for this continent."));
        }

        TopCitiesInContinent.generateReport(null, "Asia", 5);
        assertTrue(out().contains("Database connection is not available."));

        try (Connection con = connection()) {
            TopCitiesInContinent.generateReport(con, "", 5);
            TopCitiesInContinent.generateReport(con, "Asia", 0);
            assertTrue(out().contains("Please provide a valid continent and positive N."));
        }
    }

    @Test
    void us14_topCitiesInRegion_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInRegion.generateReport(con, "Southeast Asia", 5);
            assertTrue(out().contains("TOP 5 MOST POPULATED CITIES IN REGION: SOUTHEAST ASIA"));

            TopCitiesInRegion.generateReport(con, "NoSuchRegionXYZ", 5);
            assertTrue(out().contains("No cities found for this region."));
        }

        TopCitiesInRegion.generateReport(null, "Southeast Asia", 5);
        assertTrue(out().contains("Database connection is not available."));

        try (Connection con = connection()) {
            TopCitiesInRegion.generateReport(con, " ", 5);
            TopCitiesInRegion.generateReport(con, "Southeast Asia", -2);
            assertTrue(out().contains("Please provide a valid region and positive N."));
        }
    }

    @Test
    void us15_topCitiesInCountry_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            TopCitiesInCountry.generateReport(con, "Japan", 5);
            assertTrue(out().contains("TOP 5 MOST POPULATED CITIES IN JAPAN"));

            TopCitiesInCountry.generateReport(con, "NoSuchCountryXYZ", 5);
            assertTrue(out().contains("No cities found for this country."));
        }

        TopCitiesInCountry.generateReport(null, "Japan", 5);
        assertTrue(out().contains("Database connection is not available."));

        try (Connection con = connection()) {
            TopCitiesInCountry.generateReport(con, null, 5);
            TopCitiesInCountry.generateReport(con, "Japan", 0);
            assertTrue(out().contains("Please provide a valid country and positive N."));
        }
    }

    @Test
    void us16_topCitiesInDistrict_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 5);
            assertTrue(out().contains("US016 - TOP 5 MOST POPULATED CITIES IN CALIFORNIA"));

            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "NoSuchDistrictXYZ", 5);
            assertTrue(out().contains("No cities found in district: NoSuchDistrictXYZ"));

            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "California", 0);
            assertTrue(out().contains("Error: N must be a positive integer."));

            TopNPopulatedCitiesInDistrict_US016.generateReport(con, "  ", 5);
            assertTrue(out().contains("Error: District cannot be empty."));
        }
    }

    // =========================================================================
    // Capital City Reports (US17-US22)
    // =========================================================================

    @Test
    void us17_allCapitalCities_happyPathAndEmptyPrint() throws Exception {
        try (Connection con = connection()) {
            var capitals = AllCapitalsCitiesInTheWorld_US17.getAll(con);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            assertTrue(out().contains("US17: All Capital Cities in the World"));
        }

        AllCapitalsCitiesInTheWorld_US17.printReport(null);
        AllCapitalsCitiesInTheWorld_US17.printReport(new ArrayList<>());
        assertTrue(out().contains("No data available"));
    }

    @Test
    void us18_capitalCitiesByContinent_happyPathAndEmptyPrint() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, "Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalCitiesInAcontinent.printReport(capitals, "Asia");
            assertTrue(out().contains("US18: All Capital Cities in Asia by population"));
        }

        CapitalCitiesInAcontinent.printReport(null, "Asia");
        CapitalCitiesInAcontinent.printReport(new ArrayList<>(), "Asia");
        assertTrue(out().contains("No data available"));
    }

    @Test
    void us19_capitalCitiesByRegion_happyPathAndEmptyPrint() throws Exception {
        try (Connection con = connection()) {
            var capitals = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, "Southeast Asia");
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            CapitalsCitiesInAregion.printReport(capitals, "Southeast Asia");
            assertTrue(out().contains("US19: All Capital Cities in Southeast Asia by population"));
        }

        CapitalsCitiesInAregion.printReport(null, "Caribbean");
        CapitalsCitiesInAregion.printReport(new ArrayList<>(), "Caribbean");
        assertTrue(out().contains("No data available"));
    }

    @Test
    void us20_topNCapitalCitiesWorldwide_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            var capitals = ViewTopNCapitalCitiesInTheWorld_US20.getTopN(con, 5);
            assertNotNull(capitals);
            assertFalse(capitals.isEmpty());
            ViewTopNCapitalCitiesInTheWorld_US20.printReport(capitals, 5);
            assertTrue(out().contains("US20: Top 5 Capital Cities in the World"));
        }

        assertNull(ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 0));

        ViewTopNCapitalCitiesInTheWorld_US20.printReport(null, 5);
        assertTrue(out().contains("Unable to retrieve capital cities due to a database error."));

        ViewTopNCapitalCitiesInTheWorld_US20.printReport(new ArrayList<>(), 5);
        assertTrue(out().contains("No capital cities available."));
    }

    @Test
    void us21_topNCapitalCitiesInContinent_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Asia", 5);
            assertTrue(out().contains("US21: Top 5 Most Populated Capital Cities in Asia"));

            ViewTopNCapitalCitiesInAContinent_US21.generateReport(con, "Atlantis", 5);
            assertTrue(out().contains("No capital cities found for the selected continent."));
        }

        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "Asia", 5));
        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "", 5));
        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "Asia", 0));

        ViewTopNCapitalCitiesInAContinent_US21.printReport(null, "Asia", 5);
        assertTrue(out().contains("Unable to display the report because an error occurred."));
    }

    @Test
    void us22_topNCapitalCitiesInRegion_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "Caribbean", 5);
            assertTrue(out().contains("US22: TOP 5 MOST POPULATED CAPITAL CITIES IN REGION: CARIBBEAN"));

            ViewTopNCapitalCitiesInARegion_US22.generateReport(con, "NoSuchRegionXYZ", 5);
            assertTrue(out().contains("No capital cities found for region: NoSuchRegionXYZ"));
        }

        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "Caribbean", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "Caribbean", -1);
        assertTrue(out().contains("Database connection is not available."));
    }

    // =========================================================================
    // Population Breakdown & Total Reports (US23-US31)
    // =========================================================================

    @Test
    void us23_generatesPopulationBreakdownByContinent() throws Exception {
        try (Connection con = connection()) {
            PopulationByContinent_US023.generateReport(con);
            assertTrue(out().contains("POPULATION BY CONTINENT - US23"));
        }
    }

    @Test
    void us24_generatesPopulationBreakdownByRegion() throws Exception {
        try (Connection con = connection()) {
            PopulationByRegion_US24.generateReport(con);
            assertTrue(out().contains("POPULATION BY REGION - US24"));
        }
    }

    @Test
    void us25_populationByCountry_happyPathAndNullGuards() throws Exception {
        try (Connection con = connection()) {
            var populations = ViewPopulationByCountry.getPopulationByCountry(con);
            assertNotNull(populations);
            assertFalse(populations.isEmpty());
            ViewPopulationByCountry.printReport(populations);
            assertTrue(out().contains("US25 - View Population by Country"));
        }

        assertNull(ViewPopulationByCountry.getPopulationByCountry(null));
        ViewPopulationByCountry.printReport(null);
        ViewPopulationByCountry.printReport(new ArrayList<>());
        assertTrue(out().contains("Population data is unavailable or no records were found."));
    }

    @Test
    void us26_totalWorldPopulation_happyPathAndNullConnection() throws Exception {
        try (Connection con = connection()) {
            TotalWorldPopulation.printReport(con);
            assertTrue(out().contains("TOTAL WORLD POPULATION REPORT"));
        }

        TotalWorldPopulation.printReport(null);
        assertTrue(out().contains("Unable to generate report: database connection is unavailable."));
    }

    @Test
    void us27_populationOfContinent_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Asia");
            assertTrue(out().contains("US27: Population of Asia"));

            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Atlantis");
            assertTrue(out().contains("No population data available for continent: Atlantis"));

            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "");
            assertTrue(out().contains("Please select a valid continent."));
        }

        ViewPopulationOfAContinent_US27.getPopulationOfContinent(null, "Asia");
        assertTrue(out().contains("Database connection is unavailable."));
    }

    @Test
    void us28_populationOfRegion_happyPathAndValidationBranches() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "Southeast Asia");
            assertTrue(out().contains("US28: Population of Southeast Asia"));

            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "NoSuchRegionXYZ");
            assertTrue(out().contains("No population data available for region: NoSuchRegionXYZ"));

            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, " ");
            assertTrue(out().contains("Please select a valid region."));
        }

        ViewPopulationOfARegion_US28.getPopulationOfRegion(null, "Southeast Asia");
        assertTrue(out().contains("Database connection is unavailable."));
    }

    @Test
    void us29_countryPopulationReport_singleAllAndEdgeBranches() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new CountryPopulationReport(null));

        try (Connection con = connection()) {
            CountryPopulationReport report = new CountryPopulationReport(con);
            report.run("Myanmar");
            assertTrue(out().contains("Country Population Report"));

            report.run("");
            assertTrue(out().contains("All Countries Population Report"));

            report.run("NoSuchCountryXYZ");
            assertTrue(out().contains("No population data available for country: NoSuchCountryXYZ"));

            assertNull(report.getCountryPopulation(null));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertTrue(out().contains("No population data available."));
        }
    }

    @Test
    void us30_districtPopulationReport_singleAllAndEdgeBranches() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new DistrictPopulationReport(null));

        try (Connection con = connection()) {
            DistrictPopulationReport report = new DistrictPopulationReport(con);
            report.run("Mandalay");
            assertTrue(out().contains("District Population Report"));

            report.run("");
            assertTrue(out().contains("All Districts Population Report"));

            report.run("NoSuchDistrictXYZ");
            assertTrue(out().contains("No population data available for district: NoSuchDistrictXYZ"));

            assertNull(report.getDistrictPopulation(null));
            report.display(null, "Missing");
            report.displayAll(null);
            report.displayAll(List.of());
            assertTrue(out().contains("No district population data available."));
        }
    }

    @Test
    void us31_populationOfSelectedCity_happyPathAndEdgeBranches() throws Exception {
        assertThrows(NullPointerException.class, () -> new ViewPopulationOfSelectedCity_US31(null));

        try (Connection con = connection()) {
            ViewPopulationOfSelectedCity_US31 report = new ViewPopulationOfSelectedCity_US31(con);
            report.run("Rangoon", "Myanmar");
            assertTrue(out().contains("US31 - POPULATION OF A SELECTED CITY"));

            report.run("NoSuchCityXYZ", "Myanmar");
            assertTrue(out().contains("No population data available for NoSuchCityXYZ, Myanmar."));

            report.run("", "Myanmar");
            report.run("Rangoon", " ");
            assertTrue(out().contains("Please provide a valid city and country name."));
        }

        Connection closedCon = connection();
        closedCon.close();
        new ViewPopulationOfSelectedCity_US31(closedCon).run("Rangoon", "Myanmar");
        assertTrue(out().contains("Cannot generate US31 report: Database connection is closed."));
    }

    // =========================================================================
    // Language Report (US32)
    // =========================================================================

    @Test
    void us32_populationByLanguage_happyPathMathAndClosedConnection() throws Exception {
        assertThrows(NullPointerException.class, () -> new US32_PopulationByLanguageReport(null));

        assertEquals(new BigDecimal("0.00"),
                US32_PopulationByLanguageReport.calculateWorldPercentage(
                        BigDecimal.ZERO, new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(
                        new BigDecimal("-1"), new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(
                        BigDecimal.ONE, BigDecimal.ZERO));

        try (Connection con = connection()) {
            new US32_PopulationByLanguageReport(con).run();
            assertTrue(out().contains("US32 - POPULATION BY LANGUAGE"));
        }

        Connection closedCon = connection();
        closedCon.close();
        new US32_PopulationByLanguageReport(closedCon).run();
        assertTrue(out().contains("Cannot generate US32 report: Database connection is closed."));
    }
}
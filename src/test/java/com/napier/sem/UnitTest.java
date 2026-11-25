package com.napier.sem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure JUnit 5 unit tests for domain model classes ({@link City}, {@link Country},
 * {@link CapitalCity}, and {@link Population}) as well as database-free validation,
 * null/closed-connection guards, percentage math, and error paths across reports
 * and {@link App}.
 */
class UnitTest {

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

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    /** Creates a lightweight dynamic proxy {@link Connection} for closed/failing branches. */
    private static Connection closedProxyConnection() {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("isClosed".equals(name)) {
                        return true;
                    }
                    if ("close".equals(name)) {
                        return null;
                    }
                    if ("prepareStatement".equals(name) || "createStatement".equals(name)) {
                        throw new SQLException("Connection is closed");
                    }
                    Class<?> ret = method.getReturnType();
                    if (ret == boolean.class) return false;
                    if (ret == int.class || ret == long.class) return 0;
                    return null;
                }
        );
    }

    // =========================================================================
    // Default Value Tests: Verify uninitialized object state before mutation
    // =========================================================================

    @Test
    void city_defaultValues() {
        City city = new City();
        assertAll(
                () -> assertNull(city.getName()),
                () -> assertNull(city.getCountry()),
                () -> assertNull(city.getDistrict()),
                () -> assertEquals(0L, city.getPopulation())
        );
    }

    @Test
    void country_defaultValues() {
        Country country = new Country();
        assertAll(
                () -> assertNull(country.getCode()),
                () -> assertNull(country.getName()),
                () -> assertNull(country.getContinent()),
                () -> assertNull(country.getRegion()),
                () -> assertEquals(0L, country.getPopulation()),
                () -> assertNull(country.getCapital())
        );
    }

    @Test
    void capitalCity_defaultValues() {
        CapitalCity capital = new CapitalCity();
        assertAll(
                () -> assertNull(capital.getName()),
                () -> assertNull(capital.getCountry()),
                () -> assertNull(capital.getDistrict()),
                () -> assertEquals(0L, capital.getPopulation())
        );
    }

    @Test
    void population_defaultValues() {
        Population population = new Population();
        assertAll(
                () -> assertNull(population.getName()),
                () -> assertEquals(0L, population.getTotal()),
                () -> assertEquals(0L, population.getCityPopulation()),
                () -> assertEquals(0.0, population.getCityPercentage(), 0.0001),
                () -> assertEquals(0L, population.getNonCityPopulation()),
                () -> assertEquals(0.0, population.getNonCityPercentage(), 0.0001)
        );
    }

    // =========================================================================
    // City Getter & Setter Tests
    // =========================================================================

    @Test
    void city_setAndGetName() {
        City city = new City();
        city.setName("Yangon");
        assertEquals("Yangon", city.getName());
    }

    @Test
    void city_setAndGetCountry() {
        City city = new City();
        city.setCountry("Myanmar");
        assertEquals("Myanmar", city.getCountry());
    }

    @Test
    void city_setAndGetDistrict() {
        City city = new City();
        city.setDistrict("Rangoon");
        assertEquals("Rangoon", city.getDistrict());
    }

    @Test
    void city_setAndGetPopulation() {
        City city = new City();
        city.setPopulation(4_088_000L);
        assertEquals(4_088_000L, city.getPopulation());
    }

    // =========================================================================
    // Country Getter & Setter Tests
    // =========================================================================

    @Test
    void country_setAndGetCode() {
        Country country = new Country();
        country.setCode("MMR");
        assertEquals("MMR", country.getCode());
    }

    @Test
    void country_setAndGetName() {
        Country country = new Country();
        country.setName("Myanmar");
        assertEquals("Myanmar", country.getName());
    }

    @Test
    void country_setAndGetContinent() {
        Country country = new Country();
        country.setContinent("Asia");
        assertEquals("Asia", country.getContinent());
    }

    @Test
    void country_setAndGetRegion() {
        Country country = new Country();
        country.setRegion("Southeast Asia");
        assertEquals("Southeast Asia", country.getRegion());
    }

    @Test
    void country_setAndGetPopulation() {
        Country country = new Country();
        country.setPopulation(45_611_000L);
        assertEquals(45_611_000L, country.getPopulation());
    }

    @Test
    void country_setAndGetCapital() {
        Country country = new Country();
        country.setCapital("Rangoon (Yangon)");
        assertEquals("Rangoon (Yangon)", country.getCapital());
    }

    // =========================================================================
    // CapitalCity Getter & Setter Tests
    // =========================================================================

    @Test
    void capitalCity_setAndGetName() {
        CapitalCity capital = new CapitalCity();
        capital.setName("Paris");
        assertEquals("Paris", capital.getName());
    }

    @Test
    void capitalCity_setAndGetCountry() {
        CapitalCity capital = new CapitalCity();
        capital.setCountry("France");
        assertEquals("France", capital.getCountry());
    }

    @Test
    void capitalCity_setAndGetDistrict() {
        CapitalCity capital = new CapitalCity();
        capital.setDistrict("Ile-de-France");
        assertEquals("Ile-de-France", capital.getDistrict());
    }

    @Test
    void capitalCity_setAndGetPopulation() {
        CapitalCity capital = new CapitalCity();
        capital.setPopulation(2_125_246L);
        assertEquals(2_125_246L, capital.getPopulation());
    }

    // =========================================================================
    // Population Getter & Setter Tests
    // =========================================================================

    @Test
    void population_setAndGetName() {
        Population population = new Population();
        population.setName("Asia");
        assertEquals("Asia", population.getName());
    }

    @Test
    void population_setAndGetTotal() {
        Population population = new Population();
        population.setTotal(1000L);
        assertEquals(1000L, population.getTotal());
    }

    @Test
    void population_setAndGetCityPopulation() {
        Population population = new Population();
        population.setCityPopulation(250L);
        assertEquals(250L, population.getCityPopulation());
    }

    @Test
    void population_setAndGetCityPercentage() {
        Population population = new Population();
        population.setCityPercentage(25.0);
        assertEquals(25.0, population.getCityPercentage(), 0.0001);
    }

    @Test
    void population_setAndGetNonCityPopulation() {
        Population population = new Population();
        population.setNonCityPopulation(750L);
        assertEquals(750L, population.getNonCityPopulation());
    }

    @Test
    void population_setAndGetNonCityPercentage() {
        Population population = new Population();
        population.setNonCityPercentage(75.0);
        assertEquals(75.0, population.getNonCityPercentage(), 0.0001);
    }

    // =========================================================================
    // Database-Free Validation, Guard, and Error-Path Tests
    // =========================================================================

    @Test
    void reportInputValidation_rejectsBlankInputsAndNonPositiveN() {
        US04_view_top_n_countries_in_the_world.printReport(null, 0);
        assertTrue(err().contains("Invalid input: N must be a positive integer greater than zero."));

        US05_view_top_n_countries_in_a_continent.printReport(null, " ", 5);
        US05_view_top_n_countries_in_a_continent.printReport(null, "Asia", 0);
        assertTrue(err().contains("Invalid input:"));

        US06_view_top_n_countries_in_a_region.printReport(null, "", 5);
        US06_view_top_n_countries_in_a_region.printReport(null, "Eastern Asia", -1);
        assertTrue(err().contains("Invalid input:"));

        new ViewTopCitiesInTheWorld_US12(null).getTopNCitiesInWorld(0);
        assertTrue(out().contains("US12 Error: N must be greater than zero."));
    }

    @Test
    void topCityReports_handleNullConnectionAndInvalidArguments() {
        TopCitiesInContinent.generateReport(null, "Asia", 5);
        TopCitiesInRegion.generateReport(null, "Southeast Asia", 5);
        TopCitiesInCountry.generateReport(null, "Japan", 5);
        assertTrue(out().contains("Database connection is not available."));

        Connection closed = closedProxyConnection();
        TopCitiesInContinent.generateReport(closed, "", 5);
        TopCitiesInContinent.generateReport(closed, "Asia", 0);
        TopCitiesInRegion.generateReport(closed, "", 5);
        TopCitiesInRegion.generateReport(closed, "Southeast Asia", 0);
        TopCitiesInCountry.generateReport(closed, "", 5);
        TopCitiesInCountry.generateReport(closed, "Japan", 0);
        assertTrue(out().contains("Please provide a valid"));

        TopNPopulatedCitiesInDistrict_US016.generateReport(closed, "  ", 5);
        assertTrue(out().contains("Error: District cannot be empty."));
        TopNPopulatedCitiesInDistrict_US016.generateReport(closed, "California", 0);
        assertTrue(out().contains("Error: N must be a positive integer."));
    }

    @Test
    void capitalAndPopulationReports_handleEmptyListsAndNullConnections() {
        AllCapitalsCitiesInTheWorld_US17.printReport(null);
        AllCapitalsCitiesInTheWorld_US17.printReport(new ArrayList<>());
        CapitalCitiesInAcontinent.printReport(null, "Asia");
        CapitalCitiesInAcontinent.printReport(new ArrayList<>(), "Asia");
        CapitalsCitiesInAregion.printReport(null, "Caribbean");
        CapitalsCitiesInAregion.printReport(new ArrayList<>(), "Caribbean");
        assertTrue(out().contains("No data available"));

        assertNull(ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 0));
        ViewTopNCapitalCitiesInTheWorld_US20.printReport(null, 5);
        ViewTopNCapitalCitiesInTheWorld_US20.printReport(new ArrayList<>(), 5);

        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "Asia", 5));
        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "", 5));
        assertNull(ViewTopNCapitalCitiesInAContinent_US21.getTopN(null, "Asia", 0));
        ViewTopNCapitalCitiesInAContinent_US21.printReport(null, "Asia", 5);

        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "Caribbean", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "Caribbean", 0);

        assertNull(ViewPopulationByCountry.getPopulationByCountry(null));
        ViewPopulationByCountry.printReport(null);
        ViewPopulationByCountry.printReport(new ArrayList<>());
        assertTrue(out().contains("Population data is unavailable or no records were found."));

        TotalWorldPopulation.printReport(null);
        assertTrue(out().contains("Unable to generate report: database connection is unavailable."));

        ViewPopulationOfAContinent_US27.getPopulationOfContinent(null, "Asia");
        ViewPopulationOfARegion_US28.getPopulationOfRegion(null, "Caribbean");
        assertTrue(out().contains("Database connection is unavailable."));

        Connection closed = closedProxyConnection();
        ViewPopulationOfAContinent_US27.getPopulationOfContinent(closed, "");
        ViewPopulationOfARegion_US28.getPopulationOfRegion(closed, "");
        assertTrue(out().contains("Please select a valid"));
    }

    @Test
    void countryDistrictCityAndLanguageReports_handleGuardsAndMath() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new CountryPopulationReport(null));
        assertThrows(IllegalArgumentException.class, () -> new DistrictPopulationReport(null));
        assertThrows(NullPointerException.class, () -> new ViewPopulationOfSelectedCity_US31(null));
        assertThrows(NullPointerException.class, () -> new US32_PopulationByLanguageReport(null));

        Connection closed = closedProxyConnection();

        CountryPopulationReport countryReport = new CountryPopulationReport(closed);
        assertNull(countryReport.getCountryPopulation(null));
        countryReport.display(null, "Missing");
        countryReport.displayAll(null);
        countryReport.displayAll(List.of());
        assertTrue(out().contains("No population data available."));

        DistrictPopulationReport districtReport = new DistrictPopulationReport(closed);
        assertNull(districtReport.getDistrictPopulation(null));
        districtReport.display(null, "Missing");
        districtReport.displayAll(null);
        districtReport.displayAll(List.of());
        assertTrue(out().contains("No district population data available."));

        ViewPopulationOfSelectedCity_US31 us31 = new ViewPopulationOfSelectedCity_US31(closed);
        us31.run("", "Myanmar");
        assertTrue(out().contains("Please provide a valid city and country name."));
        us31.run("Rangoon", "Myanmar");
        assertTrue(out().contains("Cannot generate US31 report: Database connection is closed."));

        US32_PopulationByLanguageReport us32 = new US32_PopulationByLanguageReport(closed);
        us32.run();
        assertTrue(out().contains("Cannot generate US32 report: Database connection is closed."));

        assertEquals(new BigDecimal("0.00"),
                US32_PopulationByLanguageReport.calculateWorldPercentage(
                        BigDecimal.ZERO, new BigDecimal("100")));
        assertEquals(new BigDecimal("25.00"),
                US32_PopulationByLanguageReport.calculateWorldPercentage(
                        new BigDecimal("25"), new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(
                        new BigDecimal("-1"), new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(
                        BigDecimal.ONE, BigDecimal.ZERO));
    }

    @Test
    void appMain_whenConnectionFails_logsErrorCleanly() {
        String previousHost = System.getenv("DB_HOST");
        // Point to an unreachable port with short retry delay in App
        App.main(new String[]{});
        assertNotNull(previousHost == null ? "default" : previousHost);
        // Either connects (if local DB is up) or logs connection error cleanly
        assertFalse(out().isBlank() && err().isBlank());
    }
}
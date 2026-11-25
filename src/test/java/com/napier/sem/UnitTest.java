package com.napier.sem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure JUnit 5 unit tests for domain model classes ({@link City}, {@link Country},
 * {@link CapitalCity}, and {@link Population}) and database-free validation, null-guard,
 * empty-list, and calculation branches across report classes.
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
        // Do not leak an interrupt flag into another JUnit test.
        Thread.interrupted();
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    // =========================================================================
    // Default Value Tests
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
    // Database-Free Validation, Null-Guard, and Math Tests
    // =========================================================================

    @Test
    void reportInputValidation_rejectsNonPositiveNAndBlankInputs() {
        US04_view_top_n_countries_in_the_world.printReport(null, 0);
        assertFalse(err().isBlank());

        US05_view_top_n_countries_in_a_continent.printReport(null, " ", 5);
        US05_view_top_n_countries_in_a_continent.printReport(null, "Asia", 0);
        assertFalse(err().isBlank());

        US06_view_top_n_countries_in_a_region.printReport(null, "", 5);
        US06_view_top_n_countries_in_a_region.printReport(null, "Eastern Asia", -1);
        assertFalse(err().isBlank());

        new ViewTopCitiesInTheWorld_US12(null).getTopNCitiesInWorld(0);
        assertFalse(out().isBlank());
    }

    @Test
    void topCityReports_handleNullConnection() {
        TopCitiesInContinent.generateReport(null, "Asia", 5);
        TopCitiesInRegion.generateReport(null, "Southeast Asia", 5);
        TopCitiesInCountry.generateReport(null, "Japan", 5);
        assertTrue(out().contains("Database connection is not available."));
    }

    @Test
    void capitalAndPopulationReports_handleNullAndEmptyInputs() {
        AllCapitalsCitiesInTheWorld_US17.printReport(null);
        AllCapitalsCitiesInTheWorld_US17.printReport(new ArrayList<>());
        CapitalCitiesInAcontinent.printReport(null, "Asia");
        CapitalCitiesInAcontinent.printReport(new ArrayList<>(), "Asia");
        CapitalsCitiesInAregion.printReport(null, "Caribbean");
        CapitalsCitiesInAregion.printReport(new ArrayList<>(), "Caribbean");
        assertTrue(out().contains("No data available"));

        assertNull(ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 5));
        assertNull(ViewTopNCapitalCitiesInTheWorld_US20.getTopN(null, 0));
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
    }

    @Test
    void constructorsAndPercentageMath_validateArguments() {
        assertThrows(IllegalArgumentException.class, () -> new CountryPopulationReport(null));
        assertThrows(IllegalArgumentException.class, () -> new DistrictPopulationReport(null));
        assertThrows(NullPointerException.class, () -> new ViewPopulationOfSelectedCity_US31(null));
        assertThrows(NullPointerException.class, () -> new US32_PopulationByLanguageReport(null));

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

    // =========================================================================
    // App Internal Unit Tests (database readiness and report error handling)
    // =========================================================================

    /** Creates a JDBC ResultSet proxy for App.isDatabaseReady tests. */
    private ResultSet readinessResultSet(boolean hasRow, int tableCount) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "next": return hasRow;
                        case "getInt": return tableCount;
                        case "close": return null;
                        default: return primitiveDefault(method.getReturnType());
                    }
                });
    }

    /** Creates a JDBC Connection proxy; failAtPrepare simulates an SQL error. */
    private Connection readinessConnection(boolean hasRow, int tableCount,
                                           boolean failAtPrepare) {
        PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "executeQuery":
                            return readinessResultSet(hasRow, tableCount);
                        case "setString":
                        case "close":
                            return null;
                        default:
                            return primitiveDefault(method.getReturnType());
                    }
                });

        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("prepareStatement")) {
                        if (failAtPrepare) {
                            throw new SQLException("simulated database failure");
                        }
                        return statement;
                    }
                    if (method.getName().equals("close")) return null;
                    if (method.getName().equals("isClosed")) return false;
                    return primitiveDefault(method.getReturnType());
                });
    }

    private Object primitiveDefault(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        return null;
    }

    private boolean invokeDatabaseReady(Connection connection) throws Exception {
        Method method = App.class.getDeclaredMethod("isDatabaseReady", Connection.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, connection);
    }

    @Test
    void app_databaseReady_returnsTrueWhenRequiredTablesExist() throws Exception {
        assertTrue(invokeDatabaseReady(readinessConnection(true, 1, false)));
        assertTrue(out().contains("Database check passed."));
    }

    @Test
    void app_databaseReady_returnsFalseWhenRequiredTableIsMissing() throws Exception {
        assertFalse(invokeDatabaseReady(readinessConnection(true, 0, false)));
        assertTrue(err().contains("Missing required table: country"));
    }

    @Test
    void app_databaseReady_returnsFalseWhenQueryThrowsSQLException() throws Exception {
        assertFalse(invokeDatabaseReady(readinessConnection(true, 1, true)));
        assertTrue(err().contains("Could not validate database tables: simulated database failure"));
    }

    @Test
    void app_connectToDatabase_returnsNullWhenRetryIsInterrupted() throws Exception {
        Method method = App.class.getDeclaredMethod(
                "connectToDatabase", String.class, String.class, String.class);
        method.setAccessible(true);

        Thread.currentThread().interrupt();
        Object result;
        try {
            result = method.invoke(null, "jdbc:invalid:test", "user", "password");
        } catch (InvocationTargetException exception) {
            throw new AssertionError("Unexpected connection invocation failure",
                    exception.getCause());
        } finally {
            Thread.interrupted();
        }

        assertNull(result);
        assertTrue(err().contains("Database retry was interrupted."));
    }

    @Test
    void app_runReportLogsSQLExceptionAndContinues() throws Exception {
        invokeRunReport("Unit SQL failure", () -> {
            throw new SQLException("test SQL error");
        });

        assertTrue(out().contains("Running: Unit SQL failure"));
        assertTrue(err().contains("Report failed: Unit SQL failure - test SQL error"));
    }

    @Test
    void app_runReportLogsRuntimeException() throws Exception {
        invokeRunReport("Unit runtime failure", () -> {
            throw new IllegalStateException("test runtime error");
        });

        assertTrue(err().contains(
                "Report failed: Unit runtime failure - test runtime error"));
    }

    @Test
    void app_runReportExecutesSuccessfulReport() throws Exception {
        invokeRunReport("Successful unit report", () ->
                System.out.println("UNIT_REPORT_EXECUTED"));

        assertTrue(out().contains("UNIT_REPORT_EXECUTED"));
        assertFalse(err().contains("Report failed"));
    }

    @FunctionalInterface
    private interface TestSqlAction {
        void run() throws SQLException;
    }

    private void invokeRunReport(String name, TestSqlAction action) throws Exception {
        Class<?> reportType = Class.forName("com.napier.sem.App$SqlReport");
        Object report = Proxy.newProxyInstance(
                reportType.getClassLoader(), new Class<?>[]{reportType},
                (proxy, method, args) -> {
                    if (method.getName().equals("run")) {
                        action.run();
                        return null;
                    }
                    return primitiveDefault(method.getReturnType());
                });

        Method runReport = App.class.getDeclaredMethod(
                "runReport", String.class, reportType);
        runReport.setAccessible(true);
        try {
            runReport.invoke(null, name, report);
        } catch (InvocationTargetException exception) {
            throw new AssertionError("runReport unexpectedly propagated an exception",
                    exception.getCause());
        }
    }

    @Test
    void us27AndUs28_handleSqlExceptionsFromDatabase() {
        Connection failingConnection = readinessConnection(true, 1, true);

        ViewPopulationOfAContinent_US27.getPopulationOfContinent(
                failingConnection, "Asia");
        ViewPopulationOfARegion_US28.getPopulationOfRegion(
                failingConnection, "Southeast Asia");

        assertTrue(err().contains("Failed to retrieve population for Asia"));
        assertTrue(err().contains(
                "Failed to retrieve population for region: Southeast Asia"));
    }

    // =========================================================================
    // Branch-focused JDBC tests for US04, US27, US28, US31 and US32
    // =========================================================================

    /** Build a ResultSet proxy from ordered row maps; null values exercise wasNull branches. */
    private ResultSet branchResultSet(java.util.List<java.util.Map<String, Object>> rows) {
        final int[] index = {-1};
        final boolean[] lastWasNull = {false};
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.equals("next")) return ++index[0] < rows.size();
                    if (name.equals("wasNull")) return lastWasNull[0];
                    if (name.equals("close")) return null;
                    if (name.startsWith("get") && args != null && args.length == 1) {
                        Object value = rows.get(index[0]).get(String.valueOf(args[0]));
                        lastWasNull[0] = value == null;
                        if (name.equals("getString")) return value == null ? null : String.valueOf(value);
                        if (name.equals("getLong")) return value == null ? 0L : ((Number) value).longValue();
                        if (name.equals("getInt")) return value == null ? 0 : ((Number) value).intValue();
                        if (name.equals("getBigDecimal")) {
                            if (value == null) return null;
                            if (value instanceof BigDecimal) return value;
                            return new BigDecimal(value.toString());
                        }
                    }
                    return primitiveDefault(method.getReturnType());
                });
    }

    private Connection branchConnection(java.util.List<java.util.Map<String, Object>> rows,
                                        boolean closed, boolean failPrepare) {
        PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(), new Class<?>[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("executeQuery")) return branchResultSet(rows);
                    if (method.getName().equals("close") || method.getName().startsWith("set")) return null;
                    return primitiveDefault(method.getReturnType());
                });
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(), new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("isClosed")) return closed;
                    if (method.getName().equals("prepareStatement")) {
                        if (failPrepare) throw new SQLException("simulated SQL failure");
                        return statement;
                    }
                    if (method.getName().equals("close")) return null;
                    return primitiveDefault(method.getReturnType());
                });
    }

    private java.util.Map<String, Object> row(Object... values) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            result.put((String) values[i], values[i + 1]);
        }
        return result;
    }

    @Test
    void us27AndUs28_coverNoRowsNullPopulationAndSuccessfulRows() {
        ViewPopulationOfAContinent_US27.getPopulationOfContinent(null, "Asia");
        ViewPopulationOfARegion_US28.getPopulationOfRegion(null, "Asia");
        assertTrue(out().contains("Database connection is unavailable."));
        out.reset();

        // No-row branch for US27 and US28.
        Connection noRowsContinent = branchConnection(java.util.List.of(), false, false);
        ViewPopulationOfAContinent_US27.getPopulationOfContinent(
                noRowsContinent, "Atlantis");
        assertTrue(out().contains(
                "No population data available for continent: Atlantis"));

        Connection noRowsRegion = branchConnection(java.util.List.of(), false, false);
        ViewPopulationOfARegion_US28.getPopulationOfRegion(
                noRowsRegion, "Unknown Region");
        assertTrue(out().contains(
                "No population data available for region: Unknown Region"));

        // wasNull() branch for US27. Use an independent ResultSet and assertion
        // so the result of one report cannot affect the other report's check.
        out.reset();
        Connection nullContinentPopulation = branchConnection(java.util.List.of(
                row("TotalPopulation", null, "Continent", "Asia")), false, false);
        ViewPopulationOfAContinent_US27.getPopulationOfContinent(
                nullContinentPopulation, "Asia");
        assertTrue(out().contains("No population data available for Asia."),
                "US27 should handle SQL NULL population values");

        // wasNull() branch for US28, with a Region column matching its query.
        out.reset();
        Connection nullRegionPopulation = branchConnection(java.util.List.of(
                row("TotalPopulation", null, "Region", "Asia")), false, false);
        ViewPopulationOfARegion_US28.getPopulationOfRegion(
                nullRegionPopulation, "Asia");
        assertTrue(out().contains(
                        "No population data available for region: Asia"),
                "US28 should handle SQL NULL population values");

        // Successful-result branch for both reports.
        out.reset();
        Connection validContinent = branchConnection(java.util.List.of(
                row("TotalPopulation", 123456L, "Continent", "Asia")), false, false);
        ViewPopulationOfAContinent_US27.getPopulationOfContinent(
                validContinent, "Asia");
        assertTrue(out().contains("123,456"),
                "US27 should display a non-null population");

        out.reset();
        Connection validRegion = branchConnection(java.util.List.of(
                row("TotalPopulation", 123456L, "Region", "Asia")), false, false);
        ViewPopulationOfARegion_US28.getPopulationOfRegion(
                validRegion, "Asia");
        assertTrue(out().contains("123,456"),
                "US28 should display a non-null population");
    }

    @Test
    void us31_coversClosedInvalidMissingNullPopulationAndSuccessfulCity() throws Exception {
        ViewPopulationOfSelectedCity_US31 closed =
                new ViewPopulationOfSelectedCity_US31(branchConnection(java.util.List.of(), true, false));
        closed.run("Yangon", "Myanmar");
        assertTrue(out().contains("Database connection is closed."));

        ViewPopulationOfSelectedCity_US31 validConnection =
                new ViewPopulationOfSelectedCity_US31(branchConnection(java.util.List.of(), false, false));
        validConnection.run(null, "Myanmar");
        validConnection.run(" ", "Myanmar");
        validConnection.run("Yangon", null);
        validConnection.run("Yangon", " ");
        assertTrue(out().contains("Please provide a valid city and country name."));

        validConnection.run("Unknown", "Nowhere");
        assertTrue(out().contains("No population data available for Unknown, Nowhere."));

        ViewPopulationOfSelectedCity_US31 nullPopulation =
                new ViewPopulationOfSelectedCity_US31(branchConnection(java.util.List.of(
                        row("CityName", "Yangon", "CountryName", "Myanmar", "CityPopulation", null)), false, false));
        nullPopulation.run("Yangon", "Myanmar");
        assertTrue(out().contains("No population data available for Yangon, Myanmar."));

        ViewPopulationOfSelectedCity_US31 success =
                new ViewPopulationOfSelectedCity_US31(branchConnection(java.util.List.of(
                        row("CityName", "Yangon", "CountryName", "Myanmar", "CityPopulation", 5000000L)), false, false));
        success.run(" Yangon ", " Myanmar ");
        assertTrue(out().contains("Cities displayed: 1"));
        assertTrue(out().contains("5,000,000"));
    }

    @Test
    void us31_propagatesSqlExceptionFromDatabase() {
        ViewPopulationOfSelectedCity_US31 report =
                new ViewPopulationOfSelectedCity_US31(branchConnection(java.util.List.of(), false, true));
        assertThrows(SQLException.class, () -> report.run("Yangon", "Myanmar"));
    }

    @Test
    void us32_coversClosedEmptyInvalidWorldPopulationAndMissingLanguageBranches() throws Exception {
        US32_PopulationByLanguageReport closed = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(), true, false));
        closed.run();
        assertTrue(out().contains("Database connection is closed."));

        US32_PopulationByLanguageReport empty = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(), false, false));
        empty.run();
        assertTrue(out().contains("No language population data is available."));

        US32_PopulationByLanguageReport nullWorld = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(row("LanguageName", "English",
                        "TotalSpeakers", BigDecimal.TEN, "TotalWorldPopulation", null)), false, false));
        nullWorld.run();
        assertTrue(out().contains("World population data is unavailable or zero"));

        US32_PopulationByLanguageReport zeroWorld = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(row("LanguageName", "English",
                        "TotalSpeakers", BigDecimal.TEN, "TotalWorldPopulation", BigDecimal.ZERO)), false, false));
        zeroWorld.run();
        assertTrue(out().contains("World population data is unavailable or zero"));

        US32_PopulationByLanguageReport missingSpeaker = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(
                        row("LanguageName", "Chinese", "TotalSpeakers", null,
                                "TotalWorldPopulation", new BigDecimal("1000")),
                        row("LanguageName", "English", "TotalSpeakers", new BigDecimal("250.5"),
                                "TotalWorldPopulation", new BigDecimal("1000"))), false, false));
        missingSpeaker.run();
        assertTrue(out().contains("N/A"));
        assertTrue(out().contains("WARNING: No language data available for: Chinese"));
        assertTrue(out().contains("25.05%"));
    }

    @Test
    void us32_propagatesSqlExceptionAndValidatesNullAndNegativePercentageInputs() {
        US32_PopulationByLanguageReport report = new US32_PopulationByLanguageReport(
                branchConnection(java.util.List.of(), false, true));
        assertThrows(SQLException.class, report::run);
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(null, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(BigDecimal.ONE, null));
        assertThrows(IllegalArgumentException.class,
                () -> US32_PopulationByLanguageReport.calculateWorldPercentage(BigDecimal.ONE, new BigDecimal("-1")));
    }

    @Test
    void us04_coversEmptyResultsNullCapitalNullRegionAndSqlException() {
        US04_view_top_n_countries_in_the_world.printReport(
                branchConnection(java.util.List.of(), false, false), 5);
        assertTrue(out().contains("No country records found."));

        US04_view_top_n_countries_in_the_world.printReport(
                branchConnection(java.util.List.of(row(
                        "Code", "MMR", "Name", "Myanmar", "Continent", "Asia",
                        "Region", null, "Population", 50000000L, "Capital", null)), false, false), 5);
        assertTrue(out().contains("Unknown"));
        assertTrue(out().contains("Countries displayed: 1"));

        US04_view_top_n_countries_in_the_world.printReport(
                branchConnection(java.util.List.of(), false, true), 5);
        assertTrue(err().contains("Error retrieving top N countries: simulated SQL failure"));
    }


    // =========================================================================
    // Additional branch tests based on the current JaCoCo report
    // =========================================================================

    @Test
    void app_databaseReady_returnsFalseWhenResultSetHasNoRow() throws Exception {
        assertFalse(invokeDatabaseReady(readinessConnection(false, 0, false)));
        assertTrue(err().contains("Missing required table: country"));
    }

    @Test
    void app_connectToDatabase_returnsNullAfterAllConnectionAttemptsFail() throws Exception {
        Method method = App.class.getDeclaredMethod(
                "connectToDatabase", String.class, String.class, String.class);
        method.setAccessible(true);

        Object result = method.invoke(null,
                "jdbc:branch-test-driver-does-not-exist:", "user", "password");

        assertNull(result);
        assertTrue(err().contains("Connection failed:"));
        assertTrue(out().contains("attempt 20/20"));
    }

    @Test
    void us05_coversEmptyResultsNullAndNonNullCapitalAndRegionAndSqlFailure() {
        US05_view_top_n_countries_in_a_continent.printReport(null, null, 5);
        assertTrue(err().contains("Please enter a valid continent."));

        US05_view_top_n_countries_in_a_continent.printReport(
                branchConnection(java.util.List.of(), false, false), "Atlantis", 5);
        assertTrue(out().contains("No country records found for continent: Atlantis"));

        Connection rows = branchConnection(java.util.List.of(
                row("Code", "AAA", "Name", "No Capital", "Continent", "Asia",
                        "Region", null, "Population", 100L, "Capital", null),
                row("Code", "BBB", "Name", "With Capital", "Continent", "Asia",
                        "Region", "South Asia", "Population", 200L, "Capital", "Delhi")
        ), false, false);
        US05_view_top_n_countries_in_a_continent.printReport(rows, " Asia ", 5);
        assertTrue(out().contains("Unknown"));
        assertTrue(out().contains("Delhi"));
        assertTrue(out().contains("Countries displayed: 2"));

        US05_view_top_n_countries_in_a_continent.printReport(
                branchConnection(java.util.List.of(), false, true), "Asia", 5);
        assertTrue(err().contains("Error retrieving top N countries in a continent:"));
    }

    @Test
    void us06_coversEmptyResultsNullAndNonNullContinentAndCapitalAndSqlFailure() {
        US06_view_top_n_countries_in_a_region.printReport(null, null, 5);
        assertTrue(err().contains("Please enter a valid region."));

        US06_view_top_n_countries_in_a_region.printReport(
                branchConnection(java.util.List.of(), false, false), "Unknown Region", 5);
        assertTrue(out().contains("No country records found for region: Unknown Region"));

        Connection rows = branchConnection(java.util.List.of(
                row("Code", "AAA", "Name", "No Capital", "Continent", null,
                        "Region", "Asia", "Population", 100L, "Capital", null),
                row("Code", "BBB", "Name", "With Capital", "Continent", "Asia",
                        "Region", "South Asia", "Population", 200L, "Capital", "Delhi")
        ), false, false);
        US06_view_top_n_countries_in_a_region.printReport(rows, " South Asia ", 5);
        assertTrue(out().contains("Unknown"));
        assertTrue(out().contains("Delhi"));
        assertTrue(out().contains("Countries displayed: 2"));

        US06_view_top_n_countries_in_a_region.printReport(
                branchConnection(java.util.List.of(), false, true), "Asia", 5);
        assertTrue(err().contains("Error retrieving top N countries in a region:"));
    }

    @Test
    void us12_coversEmptyResultsSuccessfulRowsAndSqlFailure() {
        new ViewTopCitiesInTheWorld_US12(
                branchConnection(java.util.List.of(), false, false))
                .getTopNCitiesInWorld(5);
        assertTrue(out().contains("No cities found."));

        out.reset();
        new ViewTopCitiesInTheWorld_US12(branchConnection(java.util.List.of(
                row("CityName", "Yangon", "CountryName", "Myanmar",
                        "District", "Yangon", "Population", 5000000L)
        ), false, false)).getTopNCitiesInWorld(5);
        assertTrue(out().contains("Yangon"));

        out.reset();
        new ViewTopCitiesInTheWorld_US12(
                branchConnection(java.util.List.of(), false, true))
                .getTopNCitiesInWorld(5);
        assertTrue(out().contains("US12 Error: simulated SQL failure"));
    }

    @Test
    void us20_and_us21_printReportsCoverNullAndNonNullDistricts() {
        CapitalCity withoutDistrict = new CapitalCity();
        withoutDistrict.setName("Capital A");
        withoutDistrict.setCountry("Country A");
        withoutDistrict.setDistrict(null);
        withoutDistrict.setPopulation(1000L);

        CapitalCity withDistrict = new CapitalCity();
        withDistrict.setName("Capital B");
        withDistrict.setCountry("Country B");
        withDistrict.setDistrict("Central");
        withDistrict.setPopulation(2000L);

        ArrayList<CapitalCity> capitals = new ArrayList<>();
        capitals.add(withoutDistrict);
        capitals.add(withDistrict);

        ViewTopNCapitalCitiesInTheWorld_US20.printReport(capitals, 2);
        assertTrue(out().contains("Capital cities displayed: 2"));

        out.reset();
        ViewTopNCapitalCitiesInAContinent_US21.printReport(capitals, "Asia", 2);
        assertTrue(out().contains("Capital cities displayed: 2"));
    }

    @Test
    void us22_coversEveryInputDistrictAndResultCountBranch() {
        ViewTopNCapitalCitiesInARegion_US22.generateReport(null, "Asia", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(), false, false), null, 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(), false, false), "   ", 5);
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(), false, false), "Asia", 0);
        assertTrue(out().contains("Database connection is not available."));
        assertTrue(out().contains("Please provide a valid region."));
        assertTrue(out().contains("Invalid N."));

        out.reset();
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(), false, false), "No Such Region", 5);
        assertTrue(out().contains("No capital cities found for region: No Such Region"));

        out.reset();
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(
                        row("city_name", "City Null District", "country_name", "Country A",
                                "district_name", null, "city_population", 100L),
                        row("city_name", "City Blank District", "country_name", "Country B",
                                "district_name", "   ", "city_population", 200L),
                        row("city_name", "City With District", "country_name", "Country C",
                                "district_name", "Central", "city_population", 300L)
                ), false, false), "Asia", 5);
        assertTrue(out().contains("N/A"));
        assertTrue(out().contains("Central"));
        assertTrue(out().contains("Capital cities displayed: 3"));

        out.reset();
        ViewTopNCapitalCitiesInARegion_US22.generateReport(
                branchConnection(java.util.List.of(), false, true), "Asia", 5);
        assertTrue(out().contains("Database/report error: simulated SQL failure"));
    }

    @Test
    void us23_coversEmptyRowsNullAggregateZeroAggregateAndValidAggregate() throws Exception {
        PopulationByContinent_US023.generateReport(
                branchConnection(java.util.List.of(), false, false));
        assertTrue(out().contains("No population data is available."));

        out.reset();
        PopulationByContinent_US023.generateReport(branchConnection(java.util.List.of(
                row("continent", "Asia", "total_population", 1000L,
                        "city_population", null)
        ), false, false));
        assertTrue(out().contains("Population data unavailable for continent: Asia"));

        out.reset();
        PopulationByContinent_US023.generateReport(branchConnection(java.util.List.of(
                row("continent", "Zero Continent", "total_population", 0L,
                        "city_population", 0L)
        ), false, false));
        assertTrue(out().contains("Population data unavailable for continent: Zero Continent"));

        out.reset();
        PopulationByContinent_US023.generateReport(branchConnection(java.util.List.of(
                row("continent", "Europe", "total_population", 1000L,
                        "city_population", 400L)
        ), false, false));
        assertTrue(out().contains("40.00%"));
        assertTrue(out().contains("Total continents displayed: 1"));
    }

}
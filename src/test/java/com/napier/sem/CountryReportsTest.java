package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US01-US03 using the real MySQL world database. */
class CountryReportsTest extends ReportTestBase {

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
}

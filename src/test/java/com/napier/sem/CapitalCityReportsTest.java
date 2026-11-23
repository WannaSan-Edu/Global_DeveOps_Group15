package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US17-US22 using the real world database. */
class CapitalCityReportsTest extends ReportTestBase {

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
}

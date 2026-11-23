package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US07-US12 using the real world database. */
class CityReportsTest extends ReportTestBase {

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
}

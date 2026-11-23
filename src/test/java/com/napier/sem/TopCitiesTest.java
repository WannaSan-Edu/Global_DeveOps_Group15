package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US13-US16 using the real world database. */
class TopCitiesTest extends ReportTestBase {

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
}

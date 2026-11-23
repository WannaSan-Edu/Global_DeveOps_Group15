package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US23-US25 using the real world database. */
class PopulationBreakdownTest extends ReportTestBase {

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
}

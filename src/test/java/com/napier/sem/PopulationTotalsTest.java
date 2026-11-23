package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US26-US31 using the real world database. */
class PopulationTotalsTest extends ReportTestBase {

    @Test
    void us26_reportsTotalWorldPopulation() throws Exception {
        try (Connection con = connection()) {
            TotalWorldPopulation.printReport(con);
            assertFalse(out().isBlank(), "US26 should produce report output");
        }
    }

    @Test
    void us27_reportsPopulationOfAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfAContinent_US27.getPopulationOfContinent(con, "Asia");
            assertFalse(out().isBlank(), "US27 should produce report output");
        }
    }

    @Test
    void us28_reportsPopulationOfSoutheastAsia() throws Exception {
        try (Connection con = connection()) {
            ViewPopulationOfARegion_US28.getPopulationOfRegion(con, "Southeast Asia");
            assertFalse(out().isBlank(), "US28 should produce report output");
        }
    }

    @Test
    void us29_reportsPopulationOfMyanmar() throws Exception {
        try (Connection con = connection()) {
            new CountryPopulationReport(con).run("Myanmar");
            assertFalse(out().isBlank(), "US29 should produce report output");
        }
    }

    @Test
    void us30_reportsPopulationOfMandalayDistrict() throws Exception {
        try (Connection con = connection()) {
            new DistrictPopulationReport(con).run("Mandalay");
            assertFalse(out().isBlank(), "US30 should produce report output");
        }
    }

    @Test
    void us31_reportsPopulationOfYangonInMyanmar() throws Exception {
        try (Connection con = connection()) {
            new ViewPopulationOfSelectedCity_US31(con).run("Rangoon", "Myanmar");
            assertFalse(out().isBlank(), "US31 should produce report output");
        }
    }
}

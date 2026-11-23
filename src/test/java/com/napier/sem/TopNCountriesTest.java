package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for US04-US06 using the real world database. */
class TopNCountriesTest extends ReportTestBase {

    @Test
    void us04_returnsTopCountriesWorldwide() throws Exception {
        try (Connection con = connection()) {
            US04_view_top_n_countries_in_the_world.printReport(con, 5);
            assertTrue(out().contains("China") || out().contains("India"), out());
        }
    }

    @Test
    void us05_returnsTopCountriesInAsia() throws Exception {
        try (Connection con = connection()) {
            US05_view_top_n_countries_in_a_continent.printReport(con, "Asia", 5);
            assertTrue(out().contains("Asia") || out().contains("China") || out().contains("India"), out());
        }
    }

    @Test
    void us06_returnsTopCountriesInEasternAsia() throws Exception {
        try (Connection con = connection()) {
            US06_view_top_n_countries_in_a_region.printReport(con, "Eastern Asia", 5);
            assertFalse(out().isBlank(), "US06 should produce report output");
        }
    }
}

package com.napier.sem;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

/** Integration test for US32 using the real MySQL world database. */
class LanguageReportTest extends ReportTestBase {

    @Test
    void us32_reportsPopulationByLanguage() throws Exception {
        try (Connection con = connection()) {
            new US32_PopulationByLanguageReport(con).run();
            assertFalse(out().isBlank(), "US32 should produce report output");
        }
    }
}

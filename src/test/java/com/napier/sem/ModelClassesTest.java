package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Pure unit tests for the four model classes. No database needed. */
class ModelClassesTest {

    @Test
    void city_storesAllFields() {
        City city = new City();
        city.setName("Yangon");
        city.setCountry("Myanmar");
        city.setDistrict("Rangoon");
        city.setPopulation(4_088_000L);

        assertAll(
                () -> assertEquals("Yangon", city.getName()),
                () -> assertEquals("Myanmar", city.getCountry()),
                () -> assertEquals("Rangoon", city.getDistrict()),
                () -> assertEquals(4_088_000L, city.getPopulation())
        );
    }

    @Test
    void country_storesAllFields() {
        Country country = new Country();
        country.setCode("MMR");
        country.setName("Myanmar");
        country.setContinent("Asia");
        country.setRegion("Southeast Asia");
        country.setPopulation(45_611_000L);
        country.setCapital("Rangoon (Yangon)");

        assertAll(
                () -> assertEquals("MMR", country.getCode()),
                () -> assertEquals("Myanmar", country.getName()),
                () -> assertEquals("Asia", country.getContinent()),
                () -> assertEquals("Southeast Asia", country.getRegion()),
                () -> assertEquals(45_611_000L, country.getPopulation()),
                () -> assertEquals("Rangoon (Yangon)", country.getCapital())
        );
    }

    @Test
    void capitalCity_storesAllFields() {
        CapitalCity capital = new CapitalCity();
        capital.setName("Paris");
        capital.setCountry("France");
        capital.setDistrict("Ile-de-France");
        capital.setPopulation(2_125_246L);

        assertAll(
                () -> assertEquals("Paris", capital.getName()),
                () -> assertEquals("France", capital.getCountry()),
                () -> assertEquals("Ile-de-France", capital.getDistrict()),
                () -> assertEquals(2_125_246L, capital.getPopulation())
        );
    }

    @Test
    void population_storesAllFields() {
        Population population = new Population();
        population.setName("Asia");
        population.setTotal(1000L);
        population.setCityPopulation(250L);
        population.setCityPercentage(25.0);
        population.setNonCityPopulation(750L);
        population.setNonCityPercentage(75.0);

        assertAll(
                () -> assertEquals("Asia", population.getName()),
                () -> assertEquals(1000L, population.getTotal()),
                () -> assertEquals(250L, population.getCityPopulation()),
                () -> assertEquals(25.0, population.getCityPercentage(), 0.0001),
                () -> assertEquals(750L, population.getNonCityPopulation()),
                () -> assertEquals(75.0, population.getNonCityPercentage(), 0.0001)
        );
    }

    @Test
    void newObjects_haveDefaultValues() {
        City city = new City();
        Country country = new Country();
        CapitalCity capital = new CapitalCity();
        Population population = new Population();

        assertAll(
                () -> assertNull(city.getName()),
                () -> assertEquals(0L, city.getPopulation()),
                () -> assertNull(country.getCode()),
                () -> assertEquals(0L, country.getPopulation()),
                () -> assertNull(capital.getName()),
                () -> assertEquals(0L, population.getTotal()),
                () -> assertEquals(0.0, population.getCityPercentage(), 0.0001)
        );
    }
}

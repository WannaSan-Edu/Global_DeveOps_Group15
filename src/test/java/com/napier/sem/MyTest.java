package com.napier.sem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pure unit tests for all getters, setters, and defaults on model classes. */
class MyTest {

    // --- Default value tests ---

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

    // --- City getter & setter tests ---

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

    // --- Country getter & setter tests ---

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

    // --- CapitalCity getter & setter tests ---

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

    // --- Population getter & setter tests ---

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
}
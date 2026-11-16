
package com.napier.sem;

/**
 * Capital city report model for US17–US22.
 * Columns: Name, Country, District, Population.
 */
public class CapitalCity
{
    private String name;
    private String country;
    private String district;
    private long population;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getDistrict()
    {
        return district;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public long getPopulation()
    {
        return population;
    }

    public void setPopulation(long population)
    {
        this.population = population;
    }
}

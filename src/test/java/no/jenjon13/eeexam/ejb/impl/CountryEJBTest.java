package no.jenjon13.eeexam.ejb.impl;

import no.jenjon13.eeexam.ejb.CountryEJB;
import no.jenjon13.eeexam.ejb.abstracts.EntityEJBIT;
import org.junit.*;

import java.util.List;

public class CountryEJBTest extends EntityEJBIT {
    CountryEJB countryEJB;

    @Before
    public void setUp() throws Exception {
        countryEJB = getEJB(CountryEJB.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    public void getCountries() throws Exception {
        List<String> countries = countryEJB.getCountries();
        Assert.assertEquals(247, countries.size());
    }

}

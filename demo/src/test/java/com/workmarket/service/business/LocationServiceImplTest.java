package com.workmarket.service.business;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocationServiceImplTest {
    private LocationServiceImpl locationService;
    private List<String> mismatches;

    private final String VERMONT = "Vermont";
    private final String NOT_VERMONT = "not Vermont";

    private final String USA = "USA";
    private final String US = "US";
    private final String CA = "CA";

    @Before
    public void before() {
        locationService = new LocationServiceImpl();
        mismatches = new ArrayList<>();
    }


    @Test
    public void addMismatchToArrayEqualResultsAreNotAdded() {
        locationService.addMismatchToArray(mismatches, LocationServiceImpl.STATE_TYPE, VERMONT, VERMONT);

        assertTrue(mismatches.isEmpty());
    }

    @Test
    public void addMismatchToArrayMismatchIsAdded() {
        locationService.addMismatchToArray(mismatches, LocationServiceImpl.STATE_TYPE, VERMONT, NOT_VERMONT);

        assertEquals(1, mismatches.size());
        final String expected = String.format("\"%s\": {\"query\": \"%s\", \"return\": \"%s\"}",
            LocationServiceImpl.STATE_TYPE,
            VERMONT,
            NOT_VERMONT);
        assertEquals(expected, mismatches.get(0));
    }

    @Test
    public void addMismatchToArrayDifferentWaysToSpellUSAreNotAdded() {
        locationService.addMismatchToArray(mismatches, LocationServiceImpl.COUNTRY_TYPE, USA, US);

        assertTrue(mismatches.isEmpty());
    }

    @Test
    public void addMismatchToArrayDifferentCountriesAreAdded() {
        locationService.addMismatchToArray(mismatches, LocationServiceImpl.COUNTRY_TYPE, US, CA);

        assertEquals(1, mismatches.size());
        final String expected = String.format("\"%s\": {\"query\": \"%s\", \"return\": \"%s\"}",
            LocationServiceImpl.COUNTRY_TYPE,
            US,
            CA);
        assertEquals(expected, mismatches.get(0));
    }

    @Test
    public void addMismatchToArrayCapitalizationDoesntMatter() {
        locationService.addMismatchToArray(
            mismatches,
            LocationServiceImpl.STATE_TYPE,
            NOT_VERMONT,
            NOT_VERMONT.toUpperCase()
        );

        assertTrue(mismatches.isEmpty());
    }
}

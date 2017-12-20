package com.workmarket.data.solr.indexer.user;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrUserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for solrUserData validator.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrUserDataValidatorTest {

    private static final String USER_UUID = "user_uuid";
    private static final GeoPoint INVALID_GEOPOINT = new GeoPoint(0.0, 0.0);
    private static final GeoPoint VALID_GEOPOINT = new GeoPoint(34.12432, -79.121333);

    private SolrUserDataValidator validator;
    private SolrUserData userData;

    @Before
    public void setUp() {
        validator = new SolrUserDataValidator();
        userData = mock(SolrUserData.class);
    }

    @Test
    public void missing_uuid_returns_false() {
        when(userData.getUuid()).thenReturn(null);
        assertFalse(validator.isDataValid(userData));
    }

    @Test
    public void invalid_geoPoint_returns_false() {
        when(userData.getUuid()).thenReturn(USER_UUID);
        when(userData.getPoint()).thenReturn(INVALID_GEOPOINT);
        assertFalse(validator.isDataValid(userData));
    }

    @Test
    public void valid_data_returns_true() {
        when(userData.getUuid()).thenReturn(USER_UUID);
        when(userData.getPoint()).thenReturn(VALID_GEOPOINT);
        assertTrue(validator.isDataValid(userData));
    }
}

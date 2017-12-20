package com.workmarket.data.solr.indexer.user;

import com.workmarket.data.solr.model.SolrVendorData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for solrVendorData validator.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrVendorDataValidatorTest {

    private static final String VENDOR_UUID = "vendor_uuid";
    private SolrVendorDataValidator validator;
    private SolrVendorData vendorData;

    @Before
    public void setUp() {
        validator = new SolrVendorDataValidator();
        vendorData = mock(SolrVendorData.class);
    }

    @Test
    public void missing_uuid_returns_false() {
        when(vendorData.getUuid()).thenReturn(null);
        assertFalse(validator.isDataValid(vendorData));
    }

    @Test
    public void requried_field_returns_true() {
        when(vendorData.getUuid()).thenReturn(VENDOR_UUID);
        assertTrue(validator.isDataValid(vendorData));
    }
}

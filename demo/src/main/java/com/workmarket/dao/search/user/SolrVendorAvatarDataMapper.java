package com.workmarket.dao.search.user;

import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.utility.FileUtilities;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for vendor avatar.
 */
class SolrVendorAvatarDataMapper implements RowCallbackHandler {

    private final SolrVendorData solrVendorData;

    SolrVendorAvatarDataMapper(final SolrVendorData solrVendorData) {
        this.solrVendorData = solrVendorData;
    }

    @Override
    public void processRow(final ResultSet rs) throws SQLException {
        String smallAvatar = FileUtilities.createRemoteFileandDirectoryStructor(
            rs.getString("smallUriPrefix"), rs.getString("smallUuid"));
        solrVendorData.setAvatarSmallAssetUri(smallAvatar);
    }
}

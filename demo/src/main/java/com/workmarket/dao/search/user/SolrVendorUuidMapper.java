package com.workmarket.dao.search.user;

import com.google.common.collect.Lists;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Mapper to get uuids.
 */
class SolrVendorUuidMapper implements RowCallbackHandler {
    private List<String> uuids = Lists.newArrayList();

    SolrVendorUuidMapper() { }

    @Override
    public void processRow(final ResultSet rs) throws SQLException {
        final String uuid = rs.getString("uuid");
        if (uuid != null) {
            uuids.add(uuid);
        }
    }

    public List<String> getUuids() {
        return uuids;
    }
}

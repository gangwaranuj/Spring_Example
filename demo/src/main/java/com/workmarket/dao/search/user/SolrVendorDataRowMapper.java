package com.workmarket.dao.search.user;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.data.solr.model.SolrVendorData;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper from vendor result set to Solr user data.
 */
class SolrVendorDataRowMapper implements RowMapper<SolrVendorData> {

    SolrVendorDataRowMapper() {
    }

    @Override
    public SolrVendorData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        SolrVendorData vendor = new SolrVendorData();

        vendor.setId(rs.getLong("id"));
        vendor.setUuid(rs.getString("uuid"));
        vendor.setName(rs.getString("name"));
        vendor.setVendorNumber(rs.getString("company_number"));
        vendor.setEffectiveName(rs.getString("effective_name"));
        vendor.setOverview(rs.getString("overview"));
        Boolean isIndividual = getBoolean(rs, "operating_as_individual_flag");
        vendor.setCompanyType(isIndividual ? CompanyType.SOLE_PROPRIETOR : CompanyType.CORPORATION);

        Double latitude = getDouble(rs, "latitude");
        Double longitude = getDouble(rs, "longitude");
        if (latitude != null && longitude != null) {
            GeoPoint point = new GeoPoint(latitude, longitude);
            vendor.setGeoPoint(point);
        }

        vendor.setCity(rs.getString("city"));
        vendor.setState(rs.getString("state"));
        vendor.setPostalCode(rs.getString("postal_code"));
        vendor.setCountry(rs.getString("country"));

        vendor.setCreatedOn(new DateTime(rs.getTimestamp("created_on")));

        vendor.setCompanyStatusType(rs.getString("company_status_type_code"));

        return vendor;
    }

    private Boolean getBoolean(final ResultSet rs, final String col) throws SQLException {
        boolean val = rs.getBoolean(col);
        return !rs.wasNull() && val;
    }

    private Double getDouble(final ResultSet rs, final String col) throws SQLException {
        double val = rs.getDouble(col);
        return !rs.wasNull() ? val : null;
    }
}

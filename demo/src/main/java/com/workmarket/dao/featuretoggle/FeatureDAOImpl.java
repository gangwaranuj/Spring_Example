package com.workmarket.dao.featuretoggle;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.featuretoggle.Feature;
import org.springframework.stereotype.Repository;

/**
 * User: micah
 * Date: 8/4/13
 * Time: 3:01 PM
 */
@Repository
public class FeatureDAOImpl extends AbstractDAO<Feature> implements FeatureDAO {
	@Override
	protected Class<Feature> getEntityClass() {
		return Feature.class;
	}
}

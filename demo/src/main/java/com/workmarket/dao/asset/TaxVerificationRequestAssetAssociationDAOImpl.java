package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.TaxVerificationRequestAssetAssociation;
import org.springframework.stereotype.Repository;

/**
 * Created by nick on 12/2/12 11:13 AM
 */
@Repository
public class TaxVerificationRequestAssetAssociationDAOImpl extends DeletableAbstractDAO<TaxVerificationRequestAssetAssociation> implements TaxVerificationRequestAssetAssociationDAO {
	protected Class<TaxVerificationRequestAssetAssociation> getEntityClass() {
		return TaxVerificationRequestAssetAssociation.class;
	}
}

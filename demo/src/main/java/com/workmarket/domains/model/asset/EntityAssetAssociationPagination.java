package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

@SuppressWarnings("rawtypes")
public class EntityAssetAssociationPagination extends AbstractPagination<EntityAssetAssociation> implements Pagination<EntityAssetAssociation> {
	
	public EntityAssetAssociationPagination() {}
	public EntityAssetAssociationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}
	public enum SORTS {}
}
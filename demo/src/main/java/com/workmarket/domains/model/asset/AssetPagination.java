package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class AssetPagination extends AbstractPagination<Asset> implements Pagination<Asset> {
	
	public AssetPagination() {
    }

    public AssetPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

	public enum FILTER_KEYS {
		CREATION_DATE_FROM, CREATION_DATE_TO, MODIFICATION_DATE_FROM, MODIFICATION_DATE_TO, TYPE, DISPLAYABLE, ACTIVE
	}
	
	public enum SORTS {
		CREATION_DATE, MODIFICATION_DATE, NAME, DESCRIPTION, TYPE, CREATOR
	}
}

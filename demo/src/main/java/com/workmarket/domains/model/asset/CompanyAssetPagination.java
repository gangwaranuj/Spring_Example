package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 4/25/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompanyAssetPagination extends AbstractPagination<CompanyAsset> implements Pagination<CompanyAsset> {

	public CompanyAssetPagination() {
	}

	public CompanyAssetPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		CREATION_DATE_FROM("asset.created_on"),
		CREATION_DATE_TO("asset.created_on"),
		MODIFICATION_DATE_FROM("asset.modified_on"),
		MODIFICATION_DATE_TO("asset.modified_on"),
		TYPE("asset.mime_type"),
		DISPLAYABLE("asset.displayable"),
		ACTIVE("asset.active");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		CREATION_DATE("asset.created_on"),
		MODIFICATION_DATE("asset.modified_on"),
		NAME("asset.name"),
		DESCRIPTION("asset.description"),
		TYPE("asset.mime_type"),
		CREATOR("creator.first_name");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}
}

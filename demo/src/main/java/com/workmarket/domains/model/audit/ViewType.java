package com.workmarket.domains.model.audit;

import com.workmarket.thrift.work.WorkViewType;

public enum ViewType {
	WEB("web"),
	MOBILE("mobile");
	
	public final String typeString;
	
	private ViewType(final String typeString) {
		this.typeString = typeString;
	}
	
	public String getTypeString() {
		return typeString;
	}

	public static ViewType findViewType(String viewTypeString) {
		if (viewTypeString == null) {
			return null;
		}
		for(ViewType viewType : ViewType.values()) {
			if (viewType.getTypeString().equals(viewTypeString)) {
				return viewType;
			}
		}
		return null;
	}

	public static ViewType findViewType(WorkViewType viewType) {
		switch (viewType) {
		case MOBILE:
			return MOBILE;
		case WEB:
			return WEB;
		case OTHER:
			return null;
		}
		return null;
	}
}

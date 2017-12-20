package com.workmarket.thrift;

import com.workmarket.domains.model.SortDirection;

/**
 * Simple pagination helper that, if used with the core pagination, makes it easy to map the 
 * thrift pagination to the service side in a DAO
 * 
 * @author kristian
 *
 */
public class ServicePagination<T extends Enum> {
	private Integer pageSize = 25;
	private Integer cursorPosition = 0;
	private SortDirection sortDirection;
	private T sortBy;


	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(Integer cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public T getSortBy() {
		return sortBy;
	}

	public void setSortBy(T sortBy) {
		this.sortBy = sortBy;
	}
	
	public boolean isSetSortBy() {
		return this.sortBy != null;
	}
	
	public boolean isSetSortDirection() {
		return this.sortDirection != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cursorPosition == null) ? 0 : cursorPosition.hashCode());
		result = prime * result + ((pageSize == null) ? 0 : pageSize.hashCode());
		result = prime * result + ((sortBy == null) ? 0 : sortBy.hashCode());
		result = prime * result + ((sortDirection == null) ? 0 : sortDirection.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServicePagination other = (ServicePagination) obj;
		if (cursorPosition == null) {
			if (other.cursorPosition != null)
				return false;
		} else if (!cursorPosition.equals(other.cursorPosition))
			return false;
		if (pageSize == null) {
			if (other.pageSize != null)
				return false;
		} else if (!pageSize.equals(other.pageSize))
			return false;
		if (sortBy == null) {
			if (other.sortBy != null)
				return false;
		} else if (!sortBy.equals(other.sortBy))
			return false;
		if (sortDirection != other.sortDirection)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServicePagination [pageSize=" + pageSize + ", cursorPosition=" + cursorPosition + ", sortDirection=" + sortDirection + ", sortBy=" + sortBy + "]";
	}
	
	
}

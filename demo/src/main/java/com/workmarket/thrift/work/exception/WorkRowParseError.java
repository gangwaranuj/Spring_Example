package com.workmarket.thrift.work.exception;

import com.workmarket.domains.work.service.upload.WorkUploadColumn;

public class WorkRowParseError {
	
	private WorkRowParseErrorType errorType;
	private String message;
	private Integer rowNumber;
	private String data;
	private WorkUploadColumn column;
	
	public WorkRowParseErrorType getErrorType() {
		return errorType;
	}
	public void setErrorType(WorkRowParseErrorType errorType) {
		this.errorType = errorType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Integer getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public WorkUploadColumn getColumn() {
		return column;
	}
	public void setColumn(WorkUploadColumn column) {
		this.column = column;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((errorType == null) ? 0 : errorType.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((rowNumber == null) ? 0 : rowNumber.hashCode());
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
		WorkRowParseError other = (WorkRowParseError) obj;
		if (column != other.column)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (errorType != other.errorType)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (rowNumber == null) {
			if (other.rowNumber != null)
				return false;
		} else if (!rowNumber.equals(other.rowNumber))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "WorkRowParseError [errorType=" + errorType + ", message="
				+ message + ", rowNumber=" + rowNumber + ", data=" + data
				+ ", column=" + column + "]";
	}
	
	
	
}

package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;

import java.util.List;

public class AddAttachmentsWorkEvent extends AbstractWorkEvent{
	private static final long serialVersionUID = 813638206016853762L;
	final private String associationType;
	final private String mimeType;
	final private String filename;
	final private String description;
	final private long contentLength;
	final private String absoluteFilePath;

	public static class Builder extends AbstractWorkEvent.Builder{
		String associationType;
		String mimeType;
		String filename;
		String description;
		long contentLength;
		String absoluteFilePath;

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey,String associationType,String mimeType,String fileName, String description, long contentLength,String absoluteFilePath) {
			super(workNumbers, user, actionName, messageKey);
			this.associationType = associationType;
			this.mimeType = mimeType;
			this.filename = fileName;
			this.description = description;
			this.contentLength = contentLength;
			this.absoluteFilePath = absoluteFilePath;
		}

		@Override
		public AddAttachmentsWorkEvent build(){
			return new AddAttachmentsWorkEvent(this);
		}
	}

	private AddAttachmentsWorkEvent(Builder builder){
		super(builder);
		this.associationType = builder.associationType;
		this.mimeType = builder.mimeType;
		this.filename = builder.filename;
		this.description = builder.description;
		this.contentLength = builder.contentLength;
		this.absoluteFilePath = builder.absoluteFilePath;
	}

	public boolean isValid(){
		if(!super.isValid()){ return false;}
		if(associationType == null){ return false;}
		if(mimeType == null){ return false;}
		if(filename == null){ return false;}
		if(description == null){ return false;}
		if(!(contentLength >= 0) ){ return false;}
		if(absoluteFilePath == null){ return false;}
		return true;
	}

	public String getAssociationType() {
		return associationType;
	}


	public String getMimeType() {
		return mimeType;
	}

	public String getFilename() {
		return filename;
	}

	public String getDescription() {
		return description;
	}


	public long getContentLength() {
		return contentLength;
	}


	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}

}

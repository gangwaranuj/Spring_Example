package com.workmarket.domains.work.service.actions;

import com.workmarket.domains.model.User;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class RemoveAttachmentsEvent extends AbstractWorkEvent {

	private static final long serialVersionUID = 573286011866725748L;
	final private String assetId;


	public static class Builder extends AbstractWorkEvent.Builder{
		String assetId;

		public Builder(List<String> workNumbers, User user, String actionName, String messageKey,String assetId) {
			super(workNumbers, user, actionName, messageKey);
			this.assetId = assetId;
		}

		@Override
		public RemoveAttachmentsEvent build(){
			return new RemoveAttachmentsEvent(this);
		}
	}

	private RemoveAttachmentsEvent(Builder builder){
		super(builder);
		this.assetId = builder.assetId;
	}

	public String getAssetId() {
		return assetId;
	}

	public boolean isValid(){
		return StringUtils.isNotEmpty(assetId) && super.isValid();
	}

}

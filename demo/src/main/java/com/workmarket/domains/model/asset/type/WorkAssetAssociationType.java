package com.workmarket.domains.model.asset.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="workAssetAssociationType")
@Table(name="work_asset_association_type")
public class WorkAssetAssociationType extends AssetType {

	private static final long serialVersionUID = -1512371291474059507L;

	public static String ATTACHMENT = "attachment"; // Documents
	public static String CLOSING_ASSET = "closing";
	public static String INSTRUCTIONS = "instructs";
	public static String OTHER = "other";
	public static String PHOTOS = "photos";
	public static String SIGN_OFF_SHEET = "sign_off";

	public static final Set<String> TYPES = new ImmutableSet.Builder<String>()
			.add(NONE)
			.add(ATTACHMENT)
			.add(CLOSING_ASSET)
			.add(INSTRUCTIONS)
			.add(OTHER)
			.add(PHOTOS)
			.add(SIGN_OFF_SHEET)
			.build();

	public static final Set<String> DELIVERABLE_TYPES = new ImmutableSet.Builder<String>()
			.add(OTHER)
			.add(PHOTOS)
			.add(SIGN_OFF_SHEET)
			.add(CLOSING_ASSET)
			.build();

	public static final Map<String, String> DELIVERABLE_TYPE_TO_NAME_MAP = new ImmutableMap.Builder<String,String>()
			.put(OTHER, "Other")
			.put(PHOTOS, "Photos")
			.put(SIGN_OFF_SHEET, "Sign Off Form")
			.put(CLOSING_ASSET, "Closing")
			.build();

	public static final Set<String> ATTACHMENT_TYPES = new ImmutableSet.Builder<String>()
			.add(OTHER)
			.add(INSTRUCTIONS)
			.add(SIGN_OFF_SHEET)
			.add(ATTACHMENT)
			.build();

	public WorkAssetAssociationType() {
		super();
	}
	public WorkAssetAssociationType(String code){
		super(code);
	}

	public static String translateDeliverableTypeToName(String type) {
		return DELIVERABLE_TYPE_TO_NAME_MAP.get(type);
	}
	public static WorkAssetAssociationType createPhotoAssociationType() {
		return new WorkAssetAssociationType(WorkAssetAssociationType.PHOTOS);
	}
}
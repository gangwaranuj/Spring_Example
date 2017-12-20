package com.workmarket.domains.model.asset.type;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Entity(name="userAssetAssociationType")
@Table(name="user_asset_association_type")
public class UserAssetAssociationType extends AssetType {

	private static final long serialVersionUID = -142179251770540647L;

	public static final String RESUME = "resume";
	public static final String AVATAR = "avatar";
	public static final String PROFILE_IMAGE = "profile_image";
	public static final String PROFILE_VIDEO = "profile_video";
	public static final String PROFILE_EMBED_VIDEO = "profile_embed_video";
	public static final String BACKGROUND_IMAGE = "background_image";
	public static final String SEARCH_EXPORT = "search_export";

	public static final List<String> TYPES = Arrays.asList(NONE, RESUME, AVATAR, PROFILE_IMAGE, PROFILE_VIDEO, BACKGROUND_IMAGE, SEARCH_EXPORT);

	public UserAssetAssociationType() {
		super();
	}

	public UserAssetAssociationType(String code){
		super(code);
	}

}

package com.workmarket.service.business.dto;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.VisibilityType;
import com.workmarket.domains.model.asset.AbstractEntityAssetAssociation;
import com.workmarket.domains.model.asset.Asset;

import java.util.UUID;

public class AssetDTO {
	private Long assetId;
	private String uuid;
	private boolean displayable = false;
	private boolean active = true;
	private Integer order;
	private TransformerParameters transformerParameters = new TransformerParameters();
	private boolean isDeliverable = false;
	private Long deliverableRequirementId;
	private Integer position;
	private String visibilityTypeCode = VisibilityType.DEFAULT_VISIBILITY;
	private FileDTO fileDTO;

	public AssetDTO() {
		fileDTO = new FileDTO();
	}

	public AssetDTO(Long assetId) {
		fileDTO = new FileDTO();
		this.assetId = assetId;
	}

	public static AssetDTO newAssetDTO() {
		return new AssetDTO();
	}

	public static <T extends AbstractEntity> AssetDTO newDTO(AbstractEntityAssetAssociation<T> abstractEntityAssetAssociation) {
		AssetDTO dto = new AssetDTO();
		dto.setAssetId(abstractEntityAssetAssociation.getAsset().getId());
		return dto;
	}

	public static AssetDTO newDTO(Asset asset) {
		AssetDTO dto = newAssetDTO()
			.setAssetId(asset.getId())
			.setName(asset.getName())
			.setDescription(asset.getDescription())
			.setMimeType(asset.getMimeType())
			.setFileByteSize(asset.getFileByteSize())
			.setOrder(asset.getOrder())
			.setUUID(asset.getUUID());

		if (asset.getAvailability() != null) {
			dto.setAvailabilityTypeCode(asset.getAvailability().getCode());
		}
		return dto;
	}

	public Asset toAsset() {
		Asset asset = new Asset();
		asset.setUUID(UUID.randomUUID().toString());
		asset.setName(getName());
		asset.setDescription(getDescription());
		asset.setAvailability(new AvailabilityType(getAvailabilityTypeCode()));
		asset.setMimeType(getMimeType());
		asset.setFileByteSize(getFileByteSize());
		asset.setOrder(getOrder());
		return asset;
	}

	public Integer getOrder() {
		return order;
	}

	public AssetDTO setOrder(Integer order) {
		this.order = order;
		return this;
	}

	public Long getAssetId() {
		return assetId;
	}

	public AssetDTO setAssetId(Long assetId) {
		this.assetId = assetId;
		return this;
	}

	public boolean isDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUUID() {
		return uuid;
	}

	public AssetDTO setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public TransformerParameters getTransformerParameters() {
		return this.transformerParameters;
	}

	public void setTransformerParameters(TransformerParameters transformerParameters) {
		this.transformerParameters = transformerParameters;
	}

	public boolean isDeliverable() {
		return isDeliverable;
	}

	public AssetDTO setDeliverable(boolean deliverable) {
		this.isDeliverable = deliverable;
		return this;
	}

	public Long getDeliverableRequirementId() {
		return deliverableRequirementId;
	}

	public AssetDTO setDeliverableRequirementId(Long deliverableRequirementId) {
		this.deliverableRequirementId = deliverableRequirementId;
		return this;
	}

	public Integer getPosition() {
		return position;
	}

	public AssetDTO setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public String getVisibilityTypeCode() {
		return visibilityTypeCode;
	}

	public void setVisibilityTypeCode(String visibilityTypeCode) {
		this.visibilityTypeCode = visibilityTypeCode;
	}

	public String getSourceFilePath() {
		return fileDTO.getSourceFilePath();
	}

	public AssetDTO setSourceFilePath(String sourceFilePath) {
		fileDTO.setSourceFilePath(sourceFilePath);
		return this;
	}

	public String getName() {
		return fileDTO.getName();
	}

	public AssetDTO setName(String name) {
		fileDTO.setName(name);
		return this;
	}

	public String getDescription() {
		return fileDTO.getDescription();
	}

	public AssetDTO setDescription(String description) {
		fileDTO.setDescription(description);
		return this;
	}

	public String getMimeType() {
		return fileDTO.getMimeType();
	}

	public AssetDTO setMimeType(String mimeType) {
		fileDTO.setMimeType(mimeType);
		return this;
	}

	public Integer getFileByteSize() {
		return fileDTO.getFileByteSize();
	}

	public AssetDTO setFileByteSize(Integer fileByteSize) {
		fileDTO.setFileByteSize(fileByteSize);
		return this;
	}

	public String getAvailabilityTypeCode() {
		return fileDTO.getAvailabilityTypeCode();
	}

	public AssetDTO setAvailabilityTypeCode(String availabilityTypeCode) {
		fileDTO.setAvailabilityTypeCode(availabilityTypeCode);
		return this;
	}

	public boolean isAddToCompanyLibrary() {
		return fileDTO.isAddToCompanyLibrary();
	}

	public AssetDTO setAddToCompanyLibrary(boolean addToCompanyLibrary) {
		fileDTO.setAddToCompanyLibrary(addToCompanyLibrary);
		return this;
	}

	public boolean isLargeTransformation() {
		return fileDTO.isLargeTransformation();
	}

	public AssetDTO setLargeTransformation(boolean largeTransformation) {
		fileDTO.setLargeTransformation(largeTransformation);
		return this;
	}

	public boolean isSmallTransformation() {
		return fileDTO.isSmallTransformation();
	}

	public AssetDTO setSmallTransformation(boolean smallTransformation) {
		fileDTO.setSmallTransformation(smallTransformation);
		return this;
	}

	public boolean requiresTransformations() {
		return isSmallTransformation() || isLargeTransformation();
	}

	public String getAssociationType() {
		return fileDTO.getAssociationType();
	}

	public AssetDTO setAssociationType(String associationType) {
		fileDTO.setAssociationType(associationType);
		return this;
	}

	public FileDTO getFileDTO() {
		return fileDTO;
	}

	/**
	 * Configurable parameters for image transformation
	 */
	public static class TransformerParameters {
		private Integer x1;
		private Integer y1;
		private Integer x2;
		private Integer y2;
		private Integer width;
		private Integer height;

		public TransformerParameters() {}

		public void setCrop(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public void setDimensions(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public boolean isConfigured() {
			return (x1 != null && x2 != null && y1 != null && y2 != null) || (width != null && height != null);
		}

		public Integer getX1() {
			return x1;
		}

		public void setX1(Integer x1) {
			this.x1 = x1;
		}

		public Integer getY1() {
			return y1;
		}

		public void setY1(Integer y1) {
			this.y1 = y1;
		}

		public Integer getX2() {
			return x2;
		}

		public void setX2(Integer x2) {
			this.x2 = x2;
		}

		public Integer getY2() {
			return y2;
		}

		public void setY2(Integer y2) {
			this.y2 = y2;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}
	}
}

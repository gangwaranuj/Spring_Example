package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.natpryce.makeiteasy.SameValueDonor;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;

import static com.natpryce.makeiteasy.Property.newProperty;
import static com.workmarket.domains.model.VisibilityType.DEFAULT_VISIBILITY;

public class DocumentMaker {
	public static final Property<DocumentDTO, Long> documentId = newProperty();
	public static final Property<DocumentDTO, String> uuid = newProperty();
	public static final Property<DocumentDTO, String> name = newProperty();
	public static final Property<DocumentDTO, String> description = newProperty();
	public static final Property<DocumentDTO, Boolean> uploaded = newProperty();
	public static final Property<DocumentDTO, String> visibilityType = newProperty();

	public static Instantiator<DocumentDTO> DocumentDTO = new Instantiator<DocumentDTO>() {
		@Override
		public DocumentDTO instantiate(PropertyLookup<DocumentDTO> lookup) {
			return new DocumentDTO.Builder()
				.setId(lookup.valueOf(documentId, 99999L))
				.setUuid(lookup.valueOf(uuid, new SameValueDonor<String>(null)))
				.setName(lookup.valueOf(name, new SameValueDonor<String>(null)))
				.setDescription(lookup.valueOf(description, new SameValueDonor<String>(null)))
				.setUploaded(lookup.valueOf(uploaded, false))
				.setVisibilityType(lookup.valueOf(visibilityType, DEFAULT_VISIBILITY))
				.build();
		}
	};
}

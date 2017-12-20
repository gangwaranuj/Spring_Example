package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.natpryce.makeiteasy.SameValueDonor;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverableDTO;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class DeliverableMaker {
	public static Property<DeliverablesGroupDTO, Long> deliverablesGroupId = newProperty();
	public static Property<DeliverablesGroupDTO, String> deliverablesGroupInstructions = newProperty();
	public static Property<DeliverablesGroupDTO, Integer> hoursToComplete = newProperty();
	public static Property<DeliverablesGroupDTO, DeliverableDTO.Builder> deliverable = newProperty();
	public static Property<DeliverableDTO, Long> deliverableId = newProperty();
	public static Property<DeliverableDTO, String> deliverableType = newProperty();
	public static Property<DeliverableDTO, String> deliverableDescription = newProperty();
	public static Property<DeliverableDTO, Integer> numberOfFiles = newProperty();
	public static Property<DeliverableDTO, Integer> priority = newProperty();

	public static final Instantiator<DeliverableDTO> DeliverableDTO = new Instantiator<DeliverableDTO>() {
		@Override
		public DeliverableDTO instantiate(PropertyLookup<DeliverableDTO> lookup) {
			return new DeliverableDTO.Builder()
				.setId(lookup.valueOf(deliverableId, new SameValueDonor<Long>(null)))
				.setType(lookup.valueOf(deliverableType, WorkAssetAssociationType.OTHER))
				.setDescription(lookup.valueOf(deliverableDescription, "do something with this"))
				.setNumberOfFiles(lookup.valueOf(numberOfFiles, 1))
				.setPriority(lookup.valueOf(priority, 1))
				.build();
		}
	};

	public static final Instantiator<DeliverablesGroupDTO> DeliverablesGroupDTO = new Instantiator<DeliverablesGroupDTO>() {
		@Override
		public DeliverablesGroupDTO instantiate(PropertyLookup<DeliverablesGroupDTO> lookup) {
			return new DeliverablesGroupDTO.Builder()
				.setId(lookup.valueOf(deliverablesGroupId, new SameValueDonor<Long>(null)))
				.setInstructions(lookup.valueOf(deliverablesGroupInstructions, ""))
				.setHoursToComplete(lookup.valueOf(hoursToComplete, 24))
				.addDeliverable(lookup.valueOf(deliverable, new DeliverableDTO.Builder(make(a(DeliverableDTO)))))
				.build();
		}
	};
}

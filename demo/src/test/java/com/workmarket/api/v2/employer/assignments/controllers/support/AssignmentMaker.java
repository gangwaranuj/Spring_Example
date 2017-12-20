package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.natpryce.makeiteasy.SameValueDonor;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;

import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.ConfigurationDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.DeliverableMaker.DeliverablesGroupDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.LocationMaker.LocationDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.PricingMaker.PricingDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.RoutingMaker.RoutingDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ScheduleMaker.ScheduleDTO;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.ShipmentGroupDTO;

public class AssignmentMaker {
	public static final Property<AssignmentDTO,String> title = newProperty();
	public static final Property<AssignmentDTO, String> description = newProperty();
	public static final Property<AssignmentDTO, String> instructions = newProperty();
	public static final Property<AssignmentDTO, String> skills = newProperty();
	public static final Property<AssignmentDTO, Long> industryId = newProperty();
	public static final Property<AssignmentDTO, String> supportContactId = newProperty();
	public static final Property<AssignmentDTO, Long> requirementSetId = newProperty();
	public static final Property<AssignmentDTO, String> followerId = newProperty();
	public static final Property<AssignmentDTO, LocationDTO.Builder> location = newProperty();
	public static final Property<AssignmentDTO, ScheduleDTO.Builder> schedule = newProperty();
	public static final Property<AssignmentDTO, PricingDTO.Builder> pricing = newProperty();
	public static final Property<AssignmentDTO, RoutingDTO.Builder> routing = newProperty();
	public static final Property<AssignmentDTO, CustomFieldGroupDTO.Builder> customFieldGroup = newProperty();
	public static final Property<AssignmentDTO, ShipmentGroupDTO.Builder> shipmentGroup = newProperty();
	public static final Property<AssignmentDTO, SurveyDTO.Builder> survey = newProperty();
	public static final Property<AssignmentDTO, ConfigurationDTO.Builder> configuration = newProperty();
	public static final Property<AssignmentDTO, DocumentDTO.Builder> document = newProperty();
	public static final Property<AssignmentDTO, DeliverablesGroupDTO.Builder> deliverablesGroup = newProperty();
	public static final Property<AssignmentDTO, String> uniqueExternalId = newProperty();
	public static final Property<AssignmentDTO, RecurrenceDTO.Builder> recurrence = newProperty();

	public static final Instantiator<AssignmentDTO> AssignmentDTO = new Instantiator<AssignmentDTO>() {
		@Override
		public AssignmentDTO instantiate(PropertyLookup<AssignmentDTO> lookup) {
			List<Long> requirementSetIds = Lists.newArrayList();
			Long reqSetId = lookup.valueOf(requirementSetId, new SameValueDonor<Long>(null));
			if (reqSetId != null) {
				requirementSetIds.add(reqSetId);
			}

			List<String> followerIds = Lists.newArrayList();
			String folId = lookup.valueOf(followerId, new SameValueDonor<String>(null));
			if (folId != null) {
				followerIds.add(folId);
			}

			Set<CustomFieldGroupDTO.Builder> customFieldGroupDTOBuilders = Sets.newHashSet();
			CustomFieldGroupDTO.Builder customFieldGroupDTOBuilder =
				lookup.valueOf(customFieldGroup, new SameValueDonor<CustomFieldGroupDTO.Builder>(null));
			if (customFieldGroupDTOBuilder != null) {
				customFieldGroupDTOBuilders.add(customFieldGroupDTOBuilder);
			}

			Set<SurveyDTO.Builder> surveyDTOBuilders = Sets.newHashSet();
			SurveyDTO.Builder surveyDTOBuilder =
				lookup.valueOf(survey, new SameValueDonor<SurveyDTO.Builder>(null));
			if (surveyDTOBuilder != null) {
				surveyDTOBuilders.add(surveyDTOBuilder);
			}

			Set<DocumentDTO.Builder> documentDTOBuilders = Sets.newHashSet();
			DocumentDTO.Builder documentDTOBuilder =
				lookup.valueOf(document, new SameValueDonor<DocumentDTO.Builder>(null));
			if (documentDTOBuilder != null) {
				documentDTOBuilders.add(documentDTOBuilder);
			}

			return new AssignmentDTO.Builder()
				.setTitle(lookup.valueOf(title, "This is the title"))
				.setDescription(lookup.valueOf(description, "Get paid for doing something."))
				.setInstructions(lookup.valueOf(instructions,"Do it like this."))
				.setSkills(lookup.valueOf(skills, "skillz"))
				.setIndustryId(lookup.valueOf(industryId, 1000L))
				.setSupportContactId(lookup.valueOf(supportContactId, new SameValueDonor<String>(null)))
				.setRequirementSetIds(requirementSetIds)
				.setFollowerIds(followerIds)
				.setLocation(lookup.valueOf(location, new LocationDTO.Builder(make(a(LocationDTO)))))
				.setSchedule(lookup.valueOf(schedule, new ScheduleDTO.Builder(make(a(ScheduleDTO)))))
				.setPricing(lookup.valueOf(pricing, new PricingDTO.Builder(make(a(PricingDTO)))))
				.setRouting(lookup.valueOf(routing, new RoutingDTO.Builder(make(a(RoutingDTO)))))
				.setDeliverablesGroup(lookup.valueOf(deliverablesGroup, new DeliverablesGroupDTO.Builder(make(a(DeliverablesGroupDTO)))))
				.setCustomFieldGroups(customFieldGroupDTOBuilders)
				.setShipmentGroup(lookup.valueOf(shipmentGroup, new ShipmentGroupDTO.Builder(make(a(ShipmentGroupDTO)))))
				.setSurveys(surveyDTOBuilders)
				.setConfiguration(lookup.valueOf(configuration, new ConfigurationDTO.Builder(make(a(ConfigurationDTO)))))
				.setDocuments(documentDTOBuilders)
				.setUniqueExternalId(lookup.valueOf(uniqueExternalId, String.format("work-unique-id-%d", System.currentTimeMillis())))
				.setRecurrence(lookup.valueOf(recurrence, new RecurrenceDTO.Builder()))
				.build();
		}
	};
}

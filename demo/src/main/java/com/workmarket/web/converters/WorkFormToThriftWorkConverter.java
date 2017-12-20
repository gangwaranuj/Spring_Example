package com.workmarket.web.converters;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Project;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.Work;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.DeliverableRequirementForm;
import com.workmarket.web.forms.ResourceCompletionForm;
import com.workmarket.web.forms.work.WorkAssessmentForm;
import com.workmarket.web.forms.work.WorkAssetForm;
import com.workmarket.web.forms.work.WorkForm;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Component
public class WorkFormToThriftWorkConverter implements Converter<WorkForm, Work> {

	@Autowired private InvariantDataService invariantService;
	@Autowired private CRMService crmService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private PricingService pricingService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private PartGroupFormToPartGroupDTOConverter partGroupFormToPartGroupDTOConverter;

	@Override
	public Work convert(WorkForm source) {
		return convert(source, new Work());
	}

	public Work convert(WorkForm source, Work dest) {
		if (source.getId() != null) {
			dest.setId(source.getId());
		}

		dest.setTitle(source.getTitle())
				.setDescription(source.getDescription())
				.setInstructions(source.getInstructions())
				.setPrivateInstructions(source.getPrivateInstructions())
				.setDesiredSkills(source.getDesired_skills());

		Long sourcePricing = source.getPricing();
		PricingStrategy ps = new PricingStrategy();
		if (sourcePricing != null) {
			ps.setId(sourcePricing);
		}

		if (source.getRouting() != null) {
			if (source.getRouting().getAssignToFirstToAcceptGroupIds() != null) {
				dest.setFirstToAcceptGroups(Lists.newArrayList(source.getRouting().getAssignToFirstToAcceptGroupIds()));
			}
			if (source.getRouting().getNeedToApplyGroupIds() != null) {
				dest.setNeedToApplyGroups(Lists.newArrayList(source.getRouting().getNeedToApplyGroupIds()));
			}
		}

		if (sourcePricing != null) {
			com.workmarket.domains.model.pricing.PricingStrategy pricingStrategy = pricingService.findPricingStrategyById(sourcePricing);
			if (pricingStrategy instanceof FlatPricePricingStrategy) {
				ps.setType(PricingStrategyType.FLAT);
				Double flatPrice = source.getFlat_price();
				if (flatPrice != null) {
					ps.setFlatPrice(source.getFlat_price());
				}
			} else if (pricingStrategy instanceof PerHourPricingStrategy) {
				ps.setType(PricingStrategyType.PER_HOUR);
				Double perHourPrice = source.getPer_hour_price();
				Double maxNumberOfHours = source.getMax_number_of_hours();
				if (perHourPrice != null) {
					ps.setPerHourPrice(perHourPrice);
				}
				if (maxNumberOfHours != null) {
					ps.setMaxNumberOfHours(maxNumberOfHours);
				}
			} else if (pricingStrategy instanceof PerUnitPricingStrategy) {
				ps.setType(PricingStrategyType.PER_UNIT);
				Double perUnitPrice = source.getPer_unit_price();
				Double maxNumberOfUnits = source.getMax_number_of_units();
				if (perUnitPrice != null) {
					ps.setPerUnitPrice(perUnitPrice);
				}
				if (maxNumberOfUnits != null) {
					ps.setMaxNumberOfUnits(maxNumberOfUnits);
				}
			} else if (pricingStrategy instanceof BlendedPerHourPricingStrategy) {
				ps.setType(PricingStrategyType.BLENDED_PER_HOUR);
				Double initialPerHourPrice = source.getInitial_per_hour_price();
				Double initialNumberOfHours = source.getInitial_number_of_hours();
				Double additionalPerHourPrice = source.getAdditional_per_hour_price();
				Double maxBlendedNumberOfHours = source.getMax_blended_number_of_hours();
				if (initialPerHourPrice != null) {
					ps.setInitialPerHourPrice(initialPerHourPrice);
				}
				if (initialNumberOfHours != null) {
					ps.setInitialNumberOfHours(initialNumberOfHours);
				}
				if (additionalPerHourPrice != null) {
					ps.setAdditionalPerHourPrice(additionalPerHourPrice);
				}
				if (maxBlendedNumberOfHours != null) {
					ps.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
				}
			} else if (pricingStrategy instanceof InternalPricingStrategy) {
				ps.setType(PricingStrategyType.INTERNAL);
			} else {
				ps.setType(PricingStrategyType.NONE);
			}
			if (!PricingStrategyType.INTERNAL.equals(ps.getType())) {
				ps.setOfflinePayment(source.getOfflinePayment());
			}
		}

		dest.setPricing(ps);
		if (source.getInternal_owner() != null)
			dest.setBuyer(new User().setId(source.getInternal_owner()));

		if (source.getProject() != null) {
			dest.setProject(new Project().setId(source.getProject()));
		} else {
			dest.setProject(null);
		}

		if (source.getClientcompany() != null) {
			dest.setClientCompany(new Company().setId(source.getClientcompany()));
		} else {
			dest.setClientCompany(null);
		}

		if (source.getClientlocations().equals(WorkForm.CLIENT_LOCATION_NEW)) {
			if (StringUtilities.any(source.getLocation_address1(), source.getLocation_address2(), source.getLocation_city(), source.getLocation_state(), source.getLocation_postal_code())
					|| source.getOnetime_location_id() != null) {
				Address address = new Address()
						.setAddressLine1(source.getLocation_address1())
						.setAddressLine2(source.getLocation_address2())
						.setCity(source.getLocation_city())
						.setState(source.getLocation_state())
						.setZip(source.getLocation_postal_code())
						.setCountry(source.getLocation_country())
						.setType(source.getLocation_type());

				if (source.getLocation_latitude() != null && source.getLocation_longitude() != null) {
					address.setPoint(new GeoPoint(source.getLocation_latitude(), source.getLocation_longitude()));
				}

				Location location = new Location()
						.setName(source.getLocation_name())
						.setNumber(source.getLocation_number())
						.setInstructions(source.getLocation_instructions())
						.setAddress(address);

				if (source.getOnetime_location_id() != null)
					location.setId(source.getOnetime_location_id());

				dest.setLocation(location);
				dest.setOffsiteLocation(false);
				dest.setNewLocation(true);
			}
		} else if (source.getClientlocations().equals(WorkForm.CLIENT_LOCATION_CLIENT_COMPANY) && source.getClientlocation_id() != null) {
			dest.setLocation(
				new Location()
					.setId(source.getClientlocation_id())
					.setAddress(new Address())
			);
			dest.setOffsiteLocation(false);
			dest.setNewLocation(false);
		} else if (source.getClientlocations().equals(WorkForm.CLIENT_LOCATION_OFFSITE)) {
			dest.setLocation(null);
			dest.setOffsiteLocation(true);
			dest.setNewLocation(false);
		} else {
			dest.setLocation(null);
			dest.setOffsiteLocation(null);
		}

		// Base the assignment's time zone relative to the location that it's to occur in;
		// Otherwise default to the creator's timezone.

		String timeZone = null;
		if (dest.getLocation() != null && dest.getLocation().getAddress() != null) {
			String zip = null;
			String countryId = null;
			if (StringUtils.isNotEmpty(dest.getLocation().getAddress().getZip())) {
				zip = dest.getLocation().getAddress().getZip();
				countryId = dest.getLocation().getAddress().getCountry();
			} else if (dest.getLocation().isSetId()) {
				ClientLocation location = crmService.findClientLocationById(dest.getLocation().getId());

				if (location == null) { // bad data, abort
					dest.setLocation(null);
					dest.setOffsiteLocation(null);

				} else if (location.getAddress() != null)  {
					zip = location.getAddress().getPostalCode();
					countryId = location.getAddress().getCountry().getId();
					dest.getLocation().setName( location.getName() );
					dest.getLocation().setNumber( location.getLocationNumber() );
					dest.getLocation().getAddress()
						.setAddressLine1(location.getAddress().getAddress1())
						.setAddressLine2(location.getAddress().getAddress2())
						.setCity(location.getAddress().getCity())
						.setState(location.getAddress().getState().getName())
						.setZip(zip)
						.setCountry(location.getAddress().getCountry().getId())
						.setDressCode(location.getAddress().getDressCode().getDescription());
				}
			}
			if (StringUtils.isNotEmpty(zip)) {
				if (StringUtils.isEmpty(countryId)) {
					if (PostalCode.canadaPattern.matcher(zip).matches()) {
						countryId = Country.CANADA;
					} else if (PostalCode.usaPattern.matcher(zip).matches()) {
						countryId = Country.USA;
					}
				}
				PostalCode postalCode = StringUtils.isEmpty(countryId) ?
					invariantService.getPostalCodeByCode(zip) : invariantService.getPostalCodeByCodeAndCountryId(zip, countryId);
				if (postalCode != null) {
					timeZone = postalCode.getTimeZoneName();
				}
			}
		}
		if (timeZone == null) {
			timeZone = securityContextFacade.getCurrentUser().getTimeZoneId();
		}

		Schedule schedule = new Schedule();
		if (source.getScheduling()) {
			schedule.setRange(true);

			if (source.getVariable_from() != null) {
				Calendar date = DateUtilities.getCalendarFromDate(source.getVariable_from());
				date.setTimeZone(TimeZone.getTimeZone(timeZone));

				if (source.getVariable_fromtime() != null) {
					Calendar time = DateUtilities.getCalendarFromDate(source.getVariable_fromtime());
					date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
					date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
				}

				schedule.setFrom(date.getTimeInMillis());
			}

			if (source.getTo() != null) {
				Calendar date = DateUtilities.getCalendarFromDate(source.getTo());
				date.setTimeZone(TimeZone.getTimeZone(timeZone));

				if (source.getTotime() != null) {
					Calendar time = DateUtilities.getCalendarFromDate(source.getTotime());
					date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
					date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
				}

				schedule.setThrough(date.getTimeInMillis());
			}
		} else {
			schedule.setRange(false);

			if (source.getFrom() != null) {
				Calendar date = DateUtilities.getCalendarFromDate(source.getFrom());
				date.setTimeZone(TimeZone.getTimeZone(timeZone));
				dest.setTimeZone(timeZone);

				if (source.getFromtime() != null) {
					Calendar time = DateUtilities.getCalendarFromDate(source.getFromtime());
					date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
					date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
				}

				schedule.setFrom(date.getTimeInMillis());
			}
		}
		dest.setSchedule(schedule);

		// If virtual / off-site, then don't need this block.
		// If on-site contact has been selected.

		if (dest.isSetLocation()) {
			User contact = null;
			if (StringUtils.equals(source.getOnsite_contact(), "new")
					|| source.getClientlocations().equals(WorkForm.CLIENT_LOCATION_NEW)
					&& StringUtils.isNotBlank(source.getContactfirstname())) {

				Profile profile = new Profile();
				profile.addToPhoneNumbers(
						new Phone().setPhone(source.getContactphone()).setExtension(source.getContactphone_extension())
				);
				contact = new User()
						.setName(new Name().setFirstName(source.getContactfirstname()).setLastName(source.getContactlastname()))
						.setEmail(source.getContactemail())
						.setProfile(profile);
			} else if (StringUtils.isNotBlank(source.getOnsite_contact()) && StringUtils.isNumeric(source.getOnsite_contact())) {
				contact = new User().setId(Long.parseLong(source.getOnsite_contact()));
			}
			dest.setLocationContact(contact);

			contact = null;
			if (StringUtils.equals(source.getOnsite_secondary_contact(), "new")
					|| source.getClientlocations().equals(WorkForm.CLIENT_LOCATION_NEW)
					&& StringUtils.isNotBlank(source.getSecondarycontactfirstname())) {

				Profile profile = new Profile();
				profile.addToPhoneNumbers(
						new Phone().setPhone(source.getSecondarycontactphone()).setExtension(source.getSecondarycontactphone_extension())
				);
				contact = new User()
						.setName(new Name().setFirstName(source.getSecondarycontactfirstname()).setLastName(source.getSecondarycontactlastname()))
						.setEmail(source.getSecondarycontactemail())
						.setProfile(profile);
			} else if (StringUtils.isNotBlank(source.getOnsite_secondary_contact())
					&& StringUtils.isNumeric(source.getOnsite_secondary_contact())) {
				contact = new User().setId(Long.parseLong(source.getOnsite_secondary_contact()));
			}
			dest.setSecondaryLocationContact(contact);
		} else {
			dest.setLocationContact(null);
			dest.setSecondaryLocationContact(null);
		}

		if (source.getSupport_contact() != null) {
			dest.setSupportContact(new User().setId(source.getSupport_contact()));
		} else {
			dest.setSupportContact(null);
		}

		ManageMyWorkMarket config = new ManageMyWorkMarket()
				.setCheckinRequiredFlag(source.getCheck_in())
				.setIvrEnabledFlag(source.getIvr_active())
				.setUseMaxSpendPricingDisplayModeFlag(source.getPricing_mode().equals("spend"))
				.setBadgeShowClientName(source.getBadge_show_client_name())
				.setDisablePriceNegotiation(source.getDisablePriceNegotiation())
				.setAssignToFirstResource(source.getAssign_to_first_resource())
				.setShowInFeed(source.getShow_in_feed())
				.setSmartRoute(source.getSmart_route())
				.setUseRequirementSets(source.getUseRequirementSets());

		if (source.getCheck_in_call_required()) {
			config.setCheckinContactName(source.getCheck_in_contact_name());
			config.setCheckinContactPhone(source.getCheck_in_contact_phone());
		} else {
			config.setCheckinContactName(null);
			config.setCheckinContactPhone(null);
		}

		boolean noteRequiredFlag = "required".equals(source.getCheck_out_notes_requiredness());

		config.setShowCheckoutNotesFlag(source.getShow_check_out_notes());
		if (source.getShow_check_out_notes()) {
			config.setCheckoutNoteRequiredFlag(noteRequiredFlag);
			if (!StringUtils.isBlank(source.getCheck_out_notes_instructions()))
				config.setCheckoutNoteInstructions(source.getCheck_out_notes_instructions());
		}

		if (source.getPayment_terms_days() != null)
			config.setPaymentTermsDays(source.getPayment_terms_days());

		dest
				.setConfiguration(config)
				.setTimetrackingRequired(source.getRequire_timetracking())
				.setResourceConfirmationRequired(source.getResource_confirmation())
				.setCheckinCallRequired(source.getCheck_in_call_required())
				.setCheckinContactName(config.getCheckinContactName()) // these are set above also
				.setCheckinContactPhone(config.getCheckinContactPhone());

		dest.setShowCheckoutNotesFlag(source.getShow_check_out_notes());
		if (source.getShow_check_out_notes()) {
			dest.setCheckoutNoteRequiredFlag(noteRequiredFlag);
			if (!StringUtils.isBlank(source.getCheck_out_notes_instructions()))
				dest.setCheckoutNoteInstructions(source.getCheck_out_notes_instructions());
		}


		if (source.getResource_confirmation() && source.getResource_confirmation_hours() != null)
			dest.setResourceConfirmationHours(source.getResource_confirmation_hours());

		if (source.getWork_template_id() != null) {
			dest.setTemplate(new Template().setId(source.getWork_template_id()));
		} else if (StringUtils.isNotEmpty(source.getTemplate_name())) {
			Template template = new Template()
					.setName(source.getTemplate_name())
					.setDescription(source.getTemplate_description());
			if (dest.isSetId())
				template.setId(dest.getId());
			dest.setTemplate(template);
		} else {
			dest.setTemplate(null);
		}

		dest.setAssessments(Lists.<Assessment>newArrayList());
		if (source.getAssessments() != null) {
			for (WorkAssessmentForm f : source.getAssessments()) {
				Assessment a = new Assessment()
						.setId(MoreObjects.firstNonNull(f.getId(), 0L))
						.setIsRequired(f.getIsRequired());
				dest.addToAssessments(a);
			}
		}

		ResourceCompletionForm resourceCompletionForm = source.getResourceCompletionForm();

		if (resourceCompletionForm != null) {
			DeliverableRequirementGroupDTO deliverableRequirementGroupDTO =
					new DeliverableRequirementGroupDTO(resourceCompletionForm.getId(), resourceCompletionForm.getInstructions(), resourceCompletionForm.getHoursToComplete());

			List<DeliverableRequirementForm> deliverableRequirementForms = resourceCompletionForm.getDeliverableRequirements();
			if (deliverableRequirementForms == null) {
				deliverableRequirementForms = Lists.newArrayList();
			}

			List<DeliverableRequirementDTO> deliverableRequirementDTOs = Lists.newArrayList();
			int priority = 0;

			for (DeliverableRequirementForm deliverableRequirementForm : deliverableRequirementForms) {
				DeliverableRequirementDTO deliverableRequirementDTO =
						new DeliverableRequirementDTO(deliverableRequirementForm.getId(), deliverableRequirementForm.getType(), deliverableRequirementForm.getInstructions(), deliverableRequirementForm.getNumberOfFiles(), priority);
				deliverableRequirementDTOs.add(deliverableRequirementDTO);
				priority++;
			}
			deliverableRequirementGroupDTO.setDeliverableRequirementDTOs(deliverableRequirementDTOs);
			dest.setDeliverableRequirementGroupDTO(deliverableRequirementGroupDTO);
		}

		dest.setAssets(Sets.<com.workmarket.thrift.core.Asset>newTreeSet());
		dest.setUploads(Lists.<Upload>newArrayList());
		if (source.getAttachments() != null) {
			for (WorkAssetForm f : source.getAttachments()) {
				if (f.getId() == null && f.getUuid() == null) continue;
				if (f.getIsUpload()) {
					Upload u = new Upload();
					BeanUtilities.copyProperties(u, f, CollectionUtilities.newArray("id"));
					u.setVisibilityCode(f.getVisibilityType());
					dest.addToUploads(u);
				} else {
					Asset a = new Asset();
					BeanUtilities.copyProperties(a, f, null);
					a.setVisibilityCode(f.getVisibilityType());
					dest.addToAssets(a);
				}
			}
		}

		if (source.getIndustry() != null) {
			dest.setIndustry(new Industry().setId(source.getIndustry()));
		}

		if (source.getRequiresParts()) {
			dest.setPartGroup(partGroupFormToPartGroupDTOConverter.convert(source.getPartGroup()));
		}

		if (source.getCustomfield() != null) {
			Map<Long, Map<Long, String>> customFieldSetsMap = source.getCustomfields();
			int position = 0;
			for (Long i : source.getCustomfield()) {
				if (i == null) {
					// Handle non-sequential indexing send via an API call that binds to null values;
					// API docs have been updated accordingly
					continue;
				}
				WorkCustomFieldGroup group = customFieldService.findWorkCustomFieldGroupByCompany(i, securityContextFacade.getCurrentUser().getCompanyId());
				if (group == null) {
					continue;
				}
				CustomFieldGroup thriftGroup = new CustomFieldGroup().setId(i).setPosition(position++);

				Map<Long, String> customFieldSetMap = customFieldSetsMap.get(i);
				if (customFieldSetMap != null) {
					for (WorkCustomField field : group.getWorkCustomFields()) {
						if (!field.getDeleted()) {
							thriftGroup.addToFields(
								new CustomField()
									.setId(field.getId())
									.setName(field.getName())
									.setValue(StringUtilities.defaultString(customFieldSetMap.get(field.getId()), field.getDefaultValue()))
									.setIsRequired(field.getRequiredFlag())
									.setVisibleToResource(field.getVisibleToResourceFlag())
									.setVisibleToOwner(field.getVisibleToOwnerFlag())
									.setType(field.getWorkCustomFieldType().getCode())
									.setDefaultValue(field.getDefaultValue())
							);
						}
					}
				}
				dest.addToCustomFieldGroups(thriftGroup);
			}
		}

		dest.setRequirementSetIds(source.getRequirementSetIds());

		dest.setFollowers(source.getFollowers());

		return dest;
	}
}

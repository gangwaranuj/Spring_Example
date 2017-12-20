package com.workmarket.service.business.requirementsets;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirement;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirementDeserializer;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirementSerializer;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirementDeserializer;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirementSerializer;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirementDeserializer;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirementSerializer;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirementDeserializer;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirementSerializer;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirement;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirementDeserializer;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirementSerializer;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirementDeserializer;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirementSerializer;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirementDeserializer;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirementSerializer;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirement;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirementDeserializer;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirementSerializer;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirementDeserializer;
import com.workmarket.domains.model.requirementset.country.CountryRequirementSerializer;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirement;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirementDeserializer;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirementSerializer;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.document.DocumentRequirementDeserializer;
import com.workmarket.domains.model.requirementset.document.DocumentRequirementSerializer;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirementDeserializer;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirementSerializer;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirement;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirementDeserializer;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirementSerializer;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirement;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirementDeserializer;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirementSerializer;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirementDeserializer;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirementSerializer;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirementDeserializer;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirementSerializer;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirementDeserializer;
import com.workmarket.domains.model.requirementset.license.LicenseRequirementSerializer;
import com.workmarket.domains.model.requirementset.ontime.OntimeRequirement;
import com.workmarket.domains.model.requirementset.ontime.OntimeRequirementDeserializer;
import com.workmarket.domains.model.requirementset.ontime.OntimeRequirementSerializer;
import com.workmarket.domains.model.requirementset.paid.PaidRequirement;
import com.workmarket.domains.model.requirementset.paid.PaidRequirementDeserializer;
import com.workmarket.domains.model.requirementset.paid.PaidRequirementSerializer;
import com.workmarket.domains.model.requirementset.profilepicture.ProfilePictureRequirement;
import com.workmarket.domains.model.requirementset.profilepicture.ProfilePictureRequirementDeserializer;
import com.workmarket.domains.model.requirementset.profilepicture.ProfilePictureRequirementSerializer;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirementDeserializer;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirementSerializer;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirementDeserializer;
import com.workmarket.domains.model.requirementset.rating.RatingRequirementSerializer;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirementDeserializer;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirementSerializer;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirementDeserializer;
import com.workmarket.domains.model.requirementset.test.TestRequirementSerializer;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirementDeserializer;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirementSerializer;
import com.workmarket.utility.StringUtilities;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RequirementSetsSerializationServiceImpl implements RequirementSetsSerializationService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(RequirementSet.class, RequirementSetSerializer.getInstance())
		.registerTypeAdapter(AgreementRequirement.class, AgreementRequirementSerializer.getInstance())
		.registerTypeAdapter(AgreementRequirement.class, AgreementRequirementDeserializer.getInstance())
		.registerTypeAdapter(AvailabilityRequirement.class, AvailabilityRequirementSerializer.getInstance())
		.registerTypeAdapter(AvailabilityRequirement.class, AvailabilityRequirementDeserializer.getInstance())
		.registerTypeAdapter(BackgroundCheckRequirement.class, BackgroundCheckRequirementSerializer.getInstance())
		.registerTypeAdapter(BackgroundCheckRequirement.class, BackgroundCheckRequirementDeserializer.getInstance())
		.registerTypeAdapter(CertificationRequirement.class, CertificationRequirementSerializer.getInstance())
		.registerTypeAdapter(CertificationRequirement.class, CertificationRequirementDeserializer.getInstance())
		.registerTypeAdapter(CompanyTypeRequirement.class, CompanyTypeRequirementSerializer.getInstance())
		.registerTypeAdapter(CompanyTypeRequirement.class, CompanyTypeRequirementDeserializer.getInstance())
		.registerTypeAdapter(CountryRequirement.class, CountryRequirementSerializer.getInstance())
		.registerTypeAdapter(CountryRequirement.class, CountryRequirementDeserializer.getInstance())
		.registerTypeAdapter(DocumentRequirement.class, DocumentRequirementSerializer.getInstance())
		.registerTypeAdapter(DocumentRequirement.class, DocumentRequirementDeserializer.getInstance())
		.registerTypeAdapter(EsignatureRequirement.class, EsignatureRequirementSerializer.getInstance())
		.registerTypeAdapter(EsignatureRequirement.class, EsignatureRequirementDeserializer.getInstance())
		.registerTypeAdapter(DrugTestRequirement.class, DrugTestRequirementSerializer.getInstance())
		.registerTypeAdapter(DrugTestRequirement.class, DrugTestRequirementDeserializer.getInstance())
		.registerTypeAdapter(ProfileVideoRequirement.class, ProfileVideoRequirementSerializer.getInstance())
		.registerTypeAdapter(ProfileVideoRequirement.class, ProfileVideoRequirementDeserializer.getInstance())
		.registerTypeAdapter(IndustryRequirement.class, IndustryRequirementSerializer.getInstance())
		.registerTypeAdapter(IndustryRequirement.class, IndustryRequirementDeserializer.getInstance())
		.registerTypeAdapter(InsuranceRequirement.class, InsuranceRequirementSerializer.getInstance())
		.registerTypeAdapter(InsuranceRequirement.class, InsuranceRequirementDeserializer.getInstance())
		.registerTypeAdapter(LicenseRequirement.class, LicenseRequirementSerializer.getInstance())
		.registerTypeAdapter(LicenseRequirement.class, LicenseRequirementDeserializer.getInstance())
		.registerTypeAdapter(RatingRequirement.class, RatingRequirementSerializer.getInstance())
		.registerTypeAdapter(RatingRequirement.class, RatingRequirementDeserializer.getInstance())
		.registerTypeAdapter(ResourceTypeRequirement.class, ResourceTypeRequirementSerializer.getInstance())
		.registerTypeAdapter(ResourceTypeRequirement.class, ResourceTypeRequirementDeserializer.getInstance())
		.registerTypeAdapter(TestRequirement.class, TestRequirementSerializer.getInstance())
		.registerTypeAdapter(TestRequirement.class, TestRequirementDeserializer.getInstance())
		.registerTypeAdapter(TravelDistanceRequirement.class, TravelDistanceRequirementSerializer.getInstance())
		.registerTypeAdapter(TravelDistanceRequirement.class, TravelDistanceRequirementDeserializer.getInstance())
		.registerTypeAdapter(OntimeRequirement.class, OntimeRequirementDeserializer.getInstance())
		.registerTypeAdapter(OntimeRequirement.class, OntimeRequirementSerializer.getInstance())
		.registerTypeAdapter(DeliverableOnTimeRequirement.class, DeliverableOnTimeRequirementDeserializer.getInstance())
		.registerTypeAdapter(DeliverableOnTimeRequirement.class, DeliverableOnTimeRequirementSerializer.getInstance())
		.registerTypeAdapter(AbandonRequirement.class, AbandonRequirementSerializer.getInstance())
		.registerTypeAdapter(AbandonRequirement.class, AbandonRequirementDeserializer.getInstance())
		.registerTypeAdapter(CancelledRequirement.class, CancelledRequirementSerializer.getInstance())
		.registerTypeAdapter(CancelledRequirement.class, CancelledRequirementDeserializer.getInstance())
		.registerTypeAdapter(ProfilePictureRequirement.class, ProfilePictureRequirementSerializer.getInstance())
		.registerTypeAdapter(ProfilePictureRequirement.class, ProfilePictureRequirementDeserializer.getInstance())
		.registerTypeAdapter(GroupMembershipRequirement.class, GroupMembershipRequirementSerializer.getInstance())
		.registerTypeAdapter(GroupMembershipRequirement.class, GroupMembershipRequirementDeserializer.getInstance())
		.registerTypeAdapter(PaidRequirement.class, PaidRequirementDeserializer.getInstance())
		.registerTypeAdapter(PaidRequirement.class, PaidRequirementSerializer.getInstance())
		.registerTypeAdapter(CompanyWorkRequirement.class, CompanyWorkRequirementDeserializer.getInstance())
		.registerTypeAdapter(CompanyWorkRequirement.class, CompanyWorkRequirementSerializer.getInstance())
		.create();

	@Override
	public String toJson(RequirementSet requirementSet) {
		return gson.toJson(requirementSet);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public RequirementSet fromJson(String json) {
		RequirementSet requirementSet = new RequirementSet();
		return mergeJson(requirementSet, json);
	}

	@Override
	public RequirementSet mergeJson(RequirementSet requirementSet, String json) {
		JsonElement jsonElement = new JsonParser().parse(json);

		JsonObject jsonObject = jsonElement.getAsJsonObject();

		JsonPrimitive name = jsonObject.getAsJsonPrimitive("name");
		if (name != null) {
			requirementSet.setName(StringUtilities.stripHTML(name.getAsString()));
		}

		requirementSet.setRequired(jsonObject.getAsJsonPrimitive("required").getAsBoolean());
		requirementSet.setActive(jsonObject.getAsJsonPrimitive("active").getAsBoolean());

		JsonElement jsonRequirements = jsonObject.get("requirements");
		if (jsonRequirements != null && !jsonRequirements.isJsonNull()) {
			final List<AbstractRequirement> requirements = getAbstractRequirements(jsonRequirements.getAsJsonArray());
			requirementSet.setRequirements(requirements);
		}

		return requirementSet;
	}

	private List<AbstractRequirement> getAbstractRequirements(final JsonArray jsonArray) {
		final List<AbstractRequirement> requirements = Lists.newArrayList();
		for (JsonElement el : jsonArray) {
			final AbstractRequirement requirement = getAbstractRequirement(el);
			requirements.add(requirement);
		}
		return requirements;
	}

	private AbstractRequirement getAbstractRequirement(final JsonElement jsonElement) {
		final String fullClassName = getFullClassName(jsonElement.getAsJsonObject());
		try {
			Class<?> clazz = Class.forName(fullClassName);
			return (AbstractRequirement) gson.fromJson(jsonElement, clazz);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Could not parse. Class " + fullClassName + " does not exist.");
		}
	}

	private String getFullClassName(final JsonObject jsonObject) {
		final String className = jsonObject.get("$type").getAsString();
		final String packageName = className.replace("Requirement", "").toLowerCase();
		return "com.workmarket.domains.model.requirementset." + packageName + "." + className;
	}

	private static class RequirementSetSerializer implements JsonSerializer<RequirementSet> {
		private static final RequirementSetSerializer INSTANCE = new RequirementSetSerializer();

		private RequirementSetSerializer(){}
		public static RequirementSetSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(RequirementSet requirementSet, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", requirementSet.getClass().getSimpleName());
			jsonObject.addProperty("id", requirementSet.getId());
			jsonObject.addProperty("name", StringUtilities.stripHTML(requirementSet.getName()));
			jsonObject.addProperty("required", requirementSet.isRequired());
			jsonObject.addProperty("active", requirementSet.isActive());
			jsonObject.addProperty("creatorName", requirementSet.getCreatorFullName());

			if (requirementSet.getUserGroup() != null) {
				jsonObject.addProperty("groupId", requirementSet.getUserGroup().getId());
			}

			return jsonObject;
		}
	}
}

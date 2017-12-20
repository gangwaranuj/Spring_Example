package com.workmarket.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.json.kpi.AssignmentReportDeserializer;
import com.workmarket.json.kpi.AssignmentReportSerializer;
import com.workmarket.json.kpi.DataPointDeserializer;
import com.workmarket.json.kpi.DataPointSerializer;
import com.workmarket.json.kpi.KPIChartResponseDeserializer;
import com.workmarket.json.kpi.KPIChartResponseSerializer;
import com.workmarket.json.kpi.KPIFilterDeserializer;
import com.workmarket.json.kpi.KPIFilterSerializer;
import com.workmarket.json.kpi.KPIRequestDeserializer;
import com.workmarket.json.kpi.KPIRequestSerializer;
import com.workmarket.json.work.WorkNegotiationDeserializer;
import com.workmarket.json.work.WorkNegotiationFormDeserializer;
import com.workmarket.json.work.WorkNegotiationSerializer;
import com.workmarket.json.work.WorkStatusTypeDeserializer;
import com.workmarket.json.work.WorkStatusTypeSerializer;
import com.workmarket.json.workresource.ResourceNoteDeserializer;
import com.workmarket.json.workresource.ResourceNoteSerializer;
import com.workmarket.json.workresource.WorkResourceDetailDeserializer;
import com.workmarket.json.workresource.WorkResourceDetailPaginationDeserializer;
import com.workmarket.json.workresource.WorkResourceDetailPaginationSerializer;
import com.workmarket.json.workresource.WorkResourceDetailSerializer;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GsonJsonAdapter implements JsonAdapter {
	private Gson gson;

	public GsonJsonAdapter() {
		gson = getDefaultGsonBuilder().create();
	}

	@Override
	public String toJson(Object object) {
		return gson.toJson(object);
	}

	@Override
	public String toJson(Object object, final String... ignore) {

		GsonBuilder builder = getDefaultGsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
			@Override public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return ArrayUtils.contains(ignore, fieldAttributes.getName());
			}

			@Override public boolean shouldSkipClass(Class<?> aClass) {
				return false;
			}
		});

		return builder.create().toJson(object);
	}

	@Override
	public <E> E fromJson(String json, Class<E> clazz) {
		return gson.fromJson(json, clazz);
	}

	@Override
	public <E> E fromJson(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public Gson getGson() {
		return gson;
	}

	private GsonBuilder getDefaultGsonBuilder() {
		return new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapter(GregorianCalendar.class, new JsonSerializer<Calendar>() {
					public JsonElement serialize(Calendar src, Type srcType, JsonSerializationContext context) {
						return new JsonPrimitive(DateUtilities.getISO8601(src));
					}
				})
				.registerTypeAdapter(Calendar.class, new JsonSerializer<Calendar>() {
					public JsonElement serialize(Calendar src, Type srcType, JsonSerializationContext context) {
						return new JsonPrimitive(DateUtilities.getISO8601(src));
					}
				})
				.registerTypeAdapter(Calendar.class, new JsonDeserializer<Calendar>() {
					public Calendar deserialize(JsonElement json, Type srcType, JsonDeserializationContext context) {
						return DateUtilities.getCalendarFromISO8601(json.getAsJsonPrimitive().getAsString());
					}
				})
				.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
					public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
						return new JsonPrimitive(DateUtilities.getISO8601(src));
					}
				})
				.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
					public Date deserialize(JsonElement json, Type srcType, JsonDeserializationContext context) {
						return DateUtilities.getDateFromISO8601(json.getAsJsonPrimitive().getAsString());
					}
				})
				.registerTypeAdapter(KPIRequest.class, KPIRequestSerializer.getInstance())
				.registerTypeAdapter(KPIRequest.class, KPIRequestDeserializer.getInstance())
				.registerTypeAdapter(Filter.class, KPIFilterSerializer.getInstance())
				.registerTypeAdapter(Filter.class, KPIFilterDeserializer.getInstance())
				.registerTypeAdapter(WorkStatusType.class, WorkStatusTypeSerializer.getInstance())
				.registerTypeAdapter(WorkStatusType.class, WorkStatusTypeDeserializer.getInstance())
				.registerTypeAdapter(KPIChartResponse.class, KPIChartResponseSerializer.getInstance())
				.registerTypeAdapter(KPIChartResponse.class, KPIChartResponseDeserializer.getInstance())
				.registerTypeAdapter(DataPoint.class, DataPointSerializer.getInstance())
				.registerTypeAdapter(DataPoint.class, DataPointDeserializer.getInstance())
				.registerTypeAdapter(AssignmentReport.class, AssignmentReportDeserializer.getInstance())
				.registerTypeAdapter(AssignmentReport.class, AssignmentReportSerializer.getInstance())
				.registerTypeAdapter(WorkResourceDetail.class, WorkResourceDetailSerializer.getInstance())
				.registerTypeAdapter(WorkResourceDetail.class, WorkResourceDetailDeserializer.getInstance())
				.registerTypeAdapter(WorkNegotiationForm.class, WorkNegotiationFormDeserializer.getInstance())
				.registerTypeAdapter(WorkNegotiation.class, WorkNegotiationSerializer.getInstance())
				.registerTypeAdapter(WorkNegotiation.class, WorkNegotiationDeserializer.getInstance())
				.registerTypeAdapter(ResourceNote.class, ResourceNoteSerializer.getInstance())
				.registerTypeAdapter(ResourceNote.class, ResourceNoteDeserializer.getInstance())
				.registerTypeAdapter(AddressDTO.class, AddressDTOSerializer.getInstance())
				.registerTypeAdapter(AddressDTO.class, AddressDTODeserializer.getInstance())
				.registerTypeAdapter(FullPricingStrategy.class, FullPricingStrategySerializer.getInstance())
				.registerTypeAdapter(FullPricingStrategy.class, FullPricingStrategyDeserializer.getInstance())
				.registerTypeAdapter(WorkResourceDetailPagination.class, WorkResourceDetailPaginationSerializer.getInstance())
				.registerTypeAdapter(WorkResourceDetailPagination.class, WorkResourceDetailPaginationDeserializer.getInstance())
				.registerTypeAdapter(User.class, TUserSerializer.getInstance())
				.registerTypeAdapter(User.class, TUserDeserializer.getInstance())
				.registerTypeAdapter(License.class, LicenseSerializer.getInstance())
				.registerTypeAdapter(License.class, LicenseDeserializer.getInstance())
				.registerTypeAdapter(NoteDTO.class, NoteDTODeserializer.getInstance())
				.registerTypeAdapter(NoteDTO.class, NoteDTOSerializer.getInstance());
	}
}

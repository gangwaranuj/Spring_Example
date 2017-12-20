package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.model.User;
import com.workmarket.thrift.work.Work;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

@ApiModel(value = "AssignmentApplication")
@JsonDeserialize(builder = AssignmentApplicationDTO.Builder.class)
public class AssignmentApplicationDTO {

	private final NegotiationDTO pricing;
	private final RescheduleDTO schedule;
	private final Long expirationDate;
	private final String message;

	private AssignmentApplicationDTO(Builder builder) {
		pricing = builder.pricingBuilder.build();
		schedule = builder.scheduleBuilder.build();
		expirationDate = builder.expirationDate;
		message = builder.message;
	}

	@ApiModelProperty(name = "pricing")
	@JsonProperty("pricing")
	public NegotiationDTO getPricing() {
		return pricing;
	}

	@ApiModelProperty(name = "schedule")
	@JsonProperty("schedule")
	public RescheduleDTO getSchedule() {
		return schedule;
	}

	@ApiModelProperty(name = "expirationDate")
	@JsonProperty("expirationDate")
	public Long getExpirationDate() {
		return expirationDate;
	}

	@ApiModelProperty(name = "message")
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	public WorkNegotiationForm toWorkNegotiationForm(User user,
																									 Work work) {

		WorkNegotiationForm form = new WorkNegotiationForm();

		//Boolean price_negotiation
		form.setPrice_negotiation(pricing != null);
		//Boolean schedule_negotiation
		form.setSchedule_negotiation(schedule != null);
		//Boolean isform
		form.setIsform(false);
		//String workerNumber
		form.setWorkerNumber(user.getUserNumber());
		//Boolean offer_expiration
		form.setOffer_expiration(expirationDate != null);
		//Long pricing
		form.setPricing(work.getPricing().getId()); // strategy
		//@Min(0L) Double flat_price = 0d
		if (this.pricing != null) {
			form.setFlat_price(this.pricing.getFlatPrice());
			//@Min(0L) Double per_hour_price = 0d
			form.setPer_hour_price(this.pricing.getPerHour());
			//@GreaterThan(0L) Double max_number_of_hours = 0d
			form.setMax_number_of_hours(this.pricing.getMaxHours());
			//@Min(0L) Double per_unit_price = 0d
			form.setPer_unit_price(this.pricing.getPerUnit());
			//@Min(0L) Double max_number_of_units = 0d
			form.setMax_number_of_units(this.pricing.getMaxUnits());
			//@Min(0L) Double initial_per_hour_price = 0d
			form.setInitial_per_hour_price(this.pricing.getBlendedPerHour());
			//@Min(0L) Double initial_number_of_hours = 0d
			form.setInitial_number_of_hours(this.pricing.getInitialHours());
			//@Min(0L) Double additional_per_hour_price = 0d
			form.setAdditional_per_hour_price(this.pricing.getPerAdditionalHour());
			//@Min(0L) Double max_blended_number_of_hours = 0d
			form.setMax_blended_number_of_hours(this.pricing.getMaxAdditionalHours());
			//@Min(0L) Double additional_expenses = 0d
			form.setAdditional_expenses(this.pricing.getReimbursement());
			//@Min(0L) Double bonus = 0d
			form.setBonus(this.pricing.getBonus());
		}
		//String note = ""
		form.setNote(this.message);

		if (schedule != null) {

			if (schedule.getStart() != null) {

				DateTime start = new DateTime(schedule.getStart());

				//String reschedule_option
				form.setReschedule_option("time");
				//@DateTimeFormat(pattern="MM/dd/yyyy") Date from
				form.setFrom(start.toDate());
				//@DateTimeFormat(pattern="hh:mmaa") Date fromtime
				form.setFromtime(start.toDate());
			} else if (schedule.getStartWindowBegin() != null && schedule.getStartWindowEnd() != null) {

				DateTime startWindowBegin = new DateTime(schedule.getStartWindowBegin());
				DateTime startWindowEnd = new DateTime(schedule.getStartWindowEnd());

				//String reschedule_option
				form.setReschedule_option("window");
				//@DateTimeFormat(pattern="MM/dd/yyyy") Date from
				form.setFrom(startWindowBegin.toDate());
				//@DateTimeFormat(pattern="hh:mmaa") Date fromtime
				form.setFromtime(startWindowBegin.toDate());
				//@DateTimeFormat(pattern="MM/dd/yyyy") Date to
				form.setTo(startWindowEnd.toDate());
				//@DateTimeFormat(pattern="hh:mmaa") Date totime
				form.setTotime(startWindowEnd.toDate());
			}
		}

		//@DateTimeFormat(pattern="MM/dd/yyyy") Date variable_from // these are for mobile
		//form.setVariable_from(new Date());
		//@DateTimeFormat(pattern="hh:mmaa") Date variable_fromtime
		//form.setVariable_fromtime(new Date());

		if (expirationDate != null) {

			DateTime expiration = new DateTime(expirationDate);

			//@DateTimeFormat(pattern="MM/dd/yyyy") Date expires_on
			form.setExpires_on(expiration.toDate());
			//@DateTimeFormat(pattern="hh:mmaa") Date expires_on_time
			form.setExpires_on_time(expiration.toDate());
		}

		if (schedule != null || expirationDate != null) {
			form.setTimeZoneId("UTC");
		}

		return form;
	}

	public static final class Builder {
		private NegotiationDTO.Builder pricingBuilder = new NegotiationDTO.Builder();
		private RescheduleDTO.Builder scheduleBuilder = new RescheduleDTO.Builder();
		private Long expirationDate;
		private String message;

		public Builder() {
		}

		public Builder(AssignmentApplicationDTO.Builder copy) {
			this.pricingBuilder = copy.pricingBuilder;
			this.scheduleBuilder = copy.scheduleBuilder;
			this.expirationDate = copy.expirationDate;
			this.message = copy.message;
		}

		@JsonProperty("pricing")
		public Builder withPricing(NegotiationDTO.Builder pricingBuilder) {
			this.pricingBuilder = pricingBuilder;
			return this;
		}

		@JsonProperty("schedule")
		public Builder withSchedule(RescheduleDTO.Builder scheduleBuilder) {
			this.scheduleBuilder = scheduleBuilder;
			return this;
		}

		@JsonProperty("expirationDate")
		public Builder withExpirationDate(Long expirationDate) {
			this.expirationDate = expirationDate;
			return this;
		}

		@JsonProperty("message")
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		public AssignmentApplicationDTO build() {
			return new AssignmentApplicationDTO(this);
		}

		public RescheduleDTO.Builder getSchedule() {
			return scheduleBuilder;
		}

		public NegotiationDTO.Builder getPricing() {
			return pricingBuilder;
		}
	}
}

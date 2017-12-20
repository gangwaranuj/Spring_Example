package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("ScreeningWithPayment")
@JsonDeserialize(builder = ScreeningWithPaymentDTO.Builder.class)
public class ScreeningWithPaymentDTO {

	@NotNull private ScreeningDTO screening;
	@NotNull private PaymentDTO payment;

	private ScreeningWithPaymentDTO(Builder builder) {
		screening = builder.screening;
		payment = builder.payment;
	}

	@ApiModelProperty(name = "screening")
	@JsonProperty("screening")
	public ScreeningDTO getScreening() {
		return screening;
	}

	@ApiModelProperty(name = "payment")
	@JsonProperty("payment")
	public PaymentDTO getPayment() {
		return payment;
	}

	public static final class Builder {
		private ScreeningDTO screening;
		private PaymentDTO payment;

		public Builder() {
		}

		public Builder(ScreeningWithPaymentDTO copy) {
			this.screening = copy.screening;
			this.payment = copy.payment;
		}

		@JsonProperty("screening")
		public Builder withScreening(ScreeningDTO screening) {
			this.screening = screening;
			return this;
		}

		@JsonProperty("payment")
		public Builder withPayment(PaymentDTO payment) {
			this.payment = payment;
			return this;
		}

		public ScreeningWithPaymentDTO build() {
			return new ScreeningWithPaymentDTO(this);
		}
	}
}

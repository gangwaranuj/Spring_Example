package com.workmarket.api.v3.endpoints.internal;

import com.workmarket.api.v3.ApiV3ResponseImpl;
import com.workmarket.api.v3.response.ApiV3Response;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.service.business.account.AccountPricingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by joshlevine on 1/3/17.
 */
@Api(tags = {"constants"})
@RequestMapping("/v3/constants")
@Controller
public class ApiV3ConstantsController {

	@Autowired AccountPricingService accountPricingService;

	@ApiOperation(value = "Get all account service types", tags = {"pricing"})
	@RequestMapping("/account-service-types")
	@ResponseBody
	public ApiV3Response<AccountServiceType> getAccountServiceTypes() {
		return ApiV3ResponseImpl.valueWithResults(accountPricingService.findAllAccountServiceType());
	}

/**
 * DB Tables we may want to expose internally

 account_pricing_type
 -- account_service_type
 acl_role_type
 address_type
 assessment_attempt_response_asset_association_type
 assessment_attempt_status_type
 assessment_status_type
 asset_type
 availability_type
 bank_account_type
 banking_file_asset_association_type
 banking_integration_generation_request_type
 cancellation_reason_type
 company_asset_association_type
 company_asset_library_association_type
 company_status_type
 default_work_sub_status_type
 delivery_status_type
 device_type
 idea_asset_association_type
 idea_type
 integration_event_type
 invitation_status_type
 invoice_status_type
 language_proficiency_type
 location_type
 notification_status_type
 notification_type
 oauth_token_provider_type
 objective_type
 payment_fulfillment_status_type
 profile_action_type
 profile_modification_type
 qualification_type
 register_transaction_type
 request_status_type
 role_type
 screening_status_type
 spend_limit_negotiation_type
 subscription_add_on_type
 subscription_asset_association_type
 subscription_invoice_type
 subscription_payment_tier_status_type
 subscription_period_type
 subscription_status_type
 subscription_type
 tax_entity_asset_association_type
 tax_entity_status_type
 tax_entity_type
 tax_form_1099_set_status_type
 tax_report_set_status_type
 tax_verification_request_asset_association_type
 transaction_display_type
 user_asset_association_type
 user_status_type
 visibility_type
 wm_invoice_number_type
 work_asset_association_type
 work_custom_field_type
 work_resource_label_type
 work_resource_status_type
 work_status_type
 work_sub_status_type
 work_upload_column_type

 */
}

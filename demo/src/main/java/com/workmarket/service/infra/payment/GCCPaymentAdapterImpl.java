package com.workmarket.service.infra.payment;

import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.service.exception.GlobalCashCardApiException;
import com.workmarket.xml.GlobalCashCardStreamXmlAdapter;
import com.workmarket.xml.XmlAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class GCCPaymentAdapterImpl implements PaymentAdapter {

	private static final Log logger = LogFactory.getLog(GCCPaymentAdapterImpl.class);

	@Value("${payment.adapter.globalcashcard.apiUsername}")
	private String gccApiUsername;

	@Value("${payment.adapter.globalcashcard.apiPassword}")
	private String gccApiPassword;

	@Value("${app.isTestEnvironment}")
	private String testEnvironment;

	private GCCApiClientWrapper client;

	private XmlAdapter xmlAdapter = new GlobalCashCardStreamXmlAdapter();

	public GCCPaymentAdapterImpl() {}

	public static final String RESPONSE_FAILURE = "failure";
	public static final String ACTIVE_STATUS = "active";

	public Map<String, String> errorCodesToHumanReadableMessages = Collections.unmodifiableMap(new HashMap<String, String>() {{
		put("13", "Permanent Address: Invalid or missing first name.");
		put("14", "Permanent Address: Invalid or missing last name.");
		put("15", "Permanent Address: Invalid or missing address 1. ");
		put("16", "Permanent Address: Invalid or missing postal code.");
		put("17", "Invalid or missing ID.");
		put("18", "Invalid or missing date of birth.");
		put("23", "Invalid or missing ID type");
		put("24", "Invalid or missing ID issuer");
		put("25", "Permanent Address: Invalid or missing country");
		put("28", "No card numbers are currently available. Please contact customer support at 1-866-395-9200");
		put("29", "City is a required field.");
		put("34", "Permanent Address: State / Provence is a required field.");
		put("35", "Missing or invalid street address.");
		put("36", "Mailing Address: Invalid or missing city. ");
		put("37", "Mailing Address: Invliad or missing state / provence.");
		put("38", "Mailing Address: Invalid or missing postal code ");
		put("39", "Mailing Address: Invalid or missing country. ");
		put("40", "Invalid email address. ");
		put("41", "Invalid phone number.");
		put("55", "Insufficient funds");
		put("99", "An unexpected error was encountered. For additional information please contact customer support at 1-866-395-9200.");
	}});




	@PostConstruct
	public void initialize() {
		this.client = new GCCApiClientWrapper(Boolean.valueOf(this.testEnvironment));
	}

	public GlobalCashCardTransactionResponse debitCard(List<NameValuePair> params) throws Exception {
		return sendRequest("debitCard", params);
	}

	public GlobalCashCardTransactionResponse loadCard(String keyfield, BigDecimal withdrawalAmount) throws Exception {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("keyfield", keyfield));
		params.add(new BasicNameValuePair("net", String.valueOf(withdrawalAmount)));
		params.add(new BasicNameValuePair("description", String.format("wm account %s withdrawing from wm amount %s", keyfield, withdrawalAmount)));
		return loadCard(params);
	}

	public GlobalCashCardTransactionResponse loadCard(List<NameValuePair> params) throws Exception {
		return sendRequest("loadCard", params);
	}

	public GlobalCashCardTransactionResponse addCardholder(List<NameValuePair> params) throws Exception {
		return sendRequest("addCardholder", params);
	}

	public GlobalCashCardTransactionResponse addSignatureCard(List<NameValuePair> params) throws Exception {

		return sendRequest("addSignatureCard", params);
	}

	public boolean isActive(String keyfield) throws Exception {
		GlobalCashCardTransactionResponse response = getCardHolder(keyfield);

		if ("failure".equals(response.getResponsecode()) || response.getCardholder() == null) {
			throw new GlobalCashCardApiException(String.format("GCC returned this error: %s for key: %s raw response: %s", response.getDescription(), keyfield,response.getRawResponse()));
		}

		return ("yes".equals(response.getCardholder().getPinset()));
	}

	public boolean isDeleted(String keyfield) throws Exception {
		GlobalCashCardTransactionResponse response = getCardHolder(keyfield);
		if (RESPONSE_FAILURE.equals(response.getResponsecode()) || response.getCardholder() == null) {
			throw new GlobalCashCardApiException(String.format("GCC returned this error: %s for key: %s raw response: %s", response.getDescription(), keyfield, response.getRawResponse()));
		}else if(StringUtils.isEmpty(response.getCardholder().getStatus())){
			logger.info(String.format("GCC return empty string for card holder with work number: %s",keyfield));
			return Boolean.FALSE;
		}
		return (!ACTIVE_STATUS.equals(response.getCardholder().getStatus().toLowerCase()));
	}

	public GlobalCashCardTransactionResponse getCardHolder(String keyfield) throws Exception{
		List<NameValuePair> params = new LinkedList<>();

		params.add(new BasicNameValuePair("keyfield", String.valueOf(keyfield)));
		return sendRequest("getCardholder",params);
	}

	public GlobalCashCardTransactionResponse addSignatureCard(String keyfield, BankAccountDTO form) throws Exception {

		List<NameValuePair> params = form.toNameValuePairs();

		/* In case if user enters driver license for ID Type we need to send the state as a govidissuer */
		if("NONUSTAXID".equals(form.getGovIdType())){
			params.add(new BasicNameValuePair("govidissuer",String.valueOf(form.getCountry())));
		}

		params.add(new BasicNameValuePair("keyfield", String.valueOf(keyfield)));

		return addSignatureCard(params);


	}

	private GlobalCashCardTransactionResponse sendRequest(String requestMethod, List<NameValuePair> params) throws Exception {

		String httpParams = adjustRequestQuery(URLEncodedUtils.format(params, "ASCII"));
		String response = getClient().sendRequest(gccApiUsername, gccApiPassword, requestMethod, httpParams);
		GlobalCashCardTransactionResponse trResponse = (GlobalCashCardTransactionResponse) xmlAdapter.fromXml(response);
		trResponse.setRawResponse(response);
		return trResponse;
	}

	/* the problem here is that we have to urlencode parameters that needs to be sent except for space and forward slash
		for example GCC expects dob to be in this format: 05/11/1977 (with actual forward slash) otherwise dob wil be
		considered to be invalid. Also address should have actual space and not a plus sign for example 1876 Brown st.
		Also, will be sending actual # pound sign.
		*/
	private String adjustRequestQuery(String httpParams) throws Exception {
		if (StringUtils.isNotEmpty(httpParams)) {
				httpParams = httpParams.replaceAll("\\+", " ");
				httpParams = httpParams.replaceAll("%2F", "\\/");
				httpParams = httpParams.replaceAll("%23", "#");
		}
		return httpParams;
	}

	@Override
	public PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO) {
		return null;
	}

	@Override
	public PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO) {
		return null;
	}

	@Override
	public PaymentResponseDTO auditCreditCardTransaction(PaymentResponseDTO paymentResponseDTO, TransactionAuthorizationAudit authorizationAudit) {
		return null;
	}

	public void setGccApiPassword(String gccApiPassword) {
		this.gccApiPassword = gccApiPassword;
	}

	public void setTestEnvironment(String isTestEnvironment) {
		testEnvironment = isTestEnvironment;
	}

	public void setGccApiUsername(String gccApiUsername) {
		this.gccApiUsername = gccApiUsername;
	}

	/*
				GCC CLient that we've received from GCC packaged as global package and only assessable in the run time
				to overcome that we wrote this reflection which works as a proxy for the needed methods.
			*/
	public static class GCCApiClientWrapper {
		private Object apiClient;

		private Class clazz;

		public GCCApiClientWrapper(boolean testEnvironment) {
			try {
				this.clazz = Class.forName("GCCApiClient");

				this.apiClient = clazz.newInstance();

				Method method = this.clazz.getMethod("SetTestEnvironment", Boolean.class);

				method.invoke(this.apiClient, testEnvironment);

			} catch (Exception e) {
				throw new GlobalCashCardApiException("Unable to initialize GCCApiClient", e);
			}
		}


		public String sendRequest(String username, String password, String requestMethod, String httpParams) {


			try {
				Method sendRequest = clazz.getMethod("SendRequest", String.class, String.class, String.class, String.class);

				return (String) sendRequest.invoke(apiClient, username, password, requestMethod, httpParams);

			} catch (Exception e) {
				throw new GlobalCashCardApiException("GCC Request Exception", e);
			}
		}
	}

	public void setApiClient(GCCApiClientWrapper gccApiClient) {
		this.client = gccApiClient;
	}

	public GCCApiClientWrapper getClient() {
		return client;
	}


	public String getHumanizedError(String errorCode){
		if(Boolean.TRUE.equals(errorCodesToHumanReadableMessages.containsKey(errorCode))){
			return  errorCodesToHumanReadableMessages.get(errorCode);
		}else{
			return errorCodesToHumanReadableMessages.get("99");
		}
	}
}

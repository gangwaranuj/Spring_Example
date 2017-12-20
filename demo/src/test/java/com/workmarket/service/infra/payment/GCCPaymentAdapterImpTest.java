package com.workmarket.service.infra.payment;

import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GCCPaymentAdapterImpTest {

	@InjectMocks GCCPaymentAdapterImpl service;

	GCCPaymentAdapterImpl.GCCApiClientWrapper client;


	private String addSignatureCardResponse;
	private String addSignatureCardFailedResponse;
	private String loadCardSuccessResponse;
	private String debitCardSuccessResponse;
	private String cardHolderResponse;
	private String cardHolderResponseWithActiveCardHolder;
	private String inactiveCardHolderResponse;

	private String cardHolderResponseWitFailure;
	private String wmUserId;

	@Before
	public void initMocks() {
		client = mock(GCCPaymentAdapterImpl.GCCApiClientWrapper.class);
		service.setGccApiPassword("dev");
		service.setTestEnvironment("true");
		service.setApiClient(client);
	}

	@Before
	public void setup() {
		addSignatureCardResponse = "<?xml version = \"1.0\"?><response>    <status>success</status>    <cardnumber>5110310409090538</cardnumber>    <firstname>Greg</firstname>    <lastname>Bluvshteyn</lastname>    <address>1876 brown st</address>    <city>New York</city>    <state>NY</state>    <zipcode>11229</zipcode>    <country>US</country>    <keyfield>1234</keyfield>    <govid>111111111</govid>    <govidtype>SSN</govidtype>    <govidissuer></govidissuer>    <dob>05/11/1977</dob>    <phone></phone>    <email></email>    <tracknum>mc39551308090905389394</tracknum></response>";
		addSignatureCardFailedResponse = "<?xml version = \"1.0\"?><response>    <status>failure</status>    <responsecode>13</responsecode>    <description>missing or invalid required field firstname </description></response>";
		loadCardSuccessResponse = "<?xml version = \"1.0\"?>    <response>    <status>success</status>    <responsecode>00</responsecode>    <custid></custid>    <keyfield></keyfield>    <cardnumber> 511031******0538</cardnumber>    <net>1</net>    <payrollid>L8140971130814074458</payrollid>    <auditno>demo7D496AE0-A2B4-DFD0-4A69F58DCB06CD73</auditno>    <transid>demo7D496AE0-A2B4-DFD0-4A69F58DCB06CD73</transid>    <description>trying to add %24100</description></response>";
		debitCardSuccessResponse = "<?xml version = \"1.0\"?>    <response>    <status>success</status>    <responsecode>00</responsecode>    <custid></custid>    <keyfield></keyfield>    <cardnumber> 511031******0538</cardnumber>    <net>1</net>    <payrollid>D8140971130814075740</payrollid>    <auditno>demo7D550E24-AC5F-31E9-DA70109C936AA9DB</auditno>    <transid>demo7D550E24-AC5F-31E9-DA70109C936AA9DB</transid>    <description>trying to add %24100</description></response>";
		cardHolderResponse = "<?xml version = \"1.0\"?><cardholders>    <records>1</records>    <cardholder>        <keyfield>9da8bf233f3a4fa4bfc69becad87be64</keyfield>        <cardnumber>5110310413115358</cardnumber>        <status>Active</status>        <firstname>MyNew</firstname>        <lastname>Account</lastname>        <address>14 Wall St</address>        <city>New York</city>        <state>NY</state>        <zipcode>10005</zipcode>        <country>US</country>        <govid>078051120</govid>        <govidtype>SSN</govidtype>        <govidissuer></govidissuer>        <dob>05/06/1967</dob>        <pinset>no</pinset>    </cardholder>    </cardholders>";
		inactiveCardHolderResponse = "<?xml version = \"1.0\"?><cardholders>    <records>1</records>    <cardholder>        <keyfield>9da8bf233f3a4fa4bfc69becad87be64</keyfield>        <cardnumber>5110310413115358</cardnumber>        <status>inactive</status>        <firstname>MyNew</firstname>        <lastname>Account</lastname>        <address>14 Wall St</address>        <city>New York</city>        <state>NY</state>        <zipcode>10005</zipcode>        <country>US</country>        <govid>078051120</govid>        <govidtype>SSN</govidtype>        <govidissuer></govidissuer>        <dob>05/06/1967</dob>        <pinset>no</pinset>    </cardholder>    </cardholders>";

		cardHolderResponseWithActiveCardHolder =  "<?xml version = \"1.0\"?><cardholders>    <records>1</records>    <cardholder>        <keyfield>9da8bf233f3a4fa4bfc69becad87be64</keyfield>        <cardnumber>5110310413115358</cardnumber>        <status>active</status>        <firstname>MyNew</firstname>        <lastname>Account</lastname>        <address>14 Wall St</address>        <city>New York</city>        <state>NY</state>        <zipcode>10005</zipcode>        <country>US</country>        <govid>078051120</govid>        <govidtype>SSN</govidtype>        <govidissuer></govidissuer>        <dob>05/06/1967</dob>        <pinset>yes</pinset>    </cardholder>    </cardholders>";
		cardHolderResponseWitFailure = "<?xml version = \"1.0\"?><response>    <status>failure</status>    <responsecode>08</responsecode>    <description>cardnumber not in system </description></response>";
		wmUserId = System.currentTimeMillis() + "";
	}

	@Test
	public void doGetCardholder_withActiveCard(){

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponse);
		service.setApiClient(client);

		try{
			assertFalse(service.isDeleted("9da8bf233f3a4fa4bfc69becad87be64"));
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void doGetCardholder_withInActiveCard(){

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(inactiveCardHolderResponse);
		service.setApiClient(client);

		try{
			assertTrue(service.isDeleted("9da8bf233f3a4fa4bfc69becad87be64"));
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void doGetCardholder_withSuccessResponse() throws Throwable{


		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponse);

		service.setApiClient(client);


		GlobalCashCardTransactionResponse response =  service.getCardHolder("9da8bf233f3a4fa4bfc69becad87be64");

		assertEquals(response.getCardholder().getStatus(), "Active");
		assertEquals("no",response.getCardholder().getPinset());
	}

	@Test
	public void doGetCardholder_withFailureResponse() throws Throwable{

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponseWitFailure);
		service.setApiClient(client);
		GlobalCashCardTransactionResponse response =  service.getCardHolder("somemissingnumber");

		assertNull(response.getCardholder());
		assertEquals(response.getStatus(),"failure");

	}

	@Test
	public void doIsActive_withSuccessAndCardHolderActive() {
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponseWithActiveCardHolder);
		service.setApiClient(client);
		try {
			Boolean isActive = service.isActive("9da8bf233f3a4fa4bfc69becad87be64");
			assertTrue(isActive);
		} catch (Exception e) {
			fail(String.format("%s", e.getMessage()));
		}
	}

	@Test
	public void doIsActive_withSuccessAndCardHolderInActive() {
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponse);
		service.setApiClient(client);
		try {
			Boolean isActive = service.isActive("9da8bf233f3a4fa4bfc69becad87be64");
			assertFalse(isActive);
		} catch (Exception e) {
			fail(String.format("%s", e.getMessage()));
		}
	}

	@Test(expected = RuntimeException.class)
	public void doIsActive_withFailedResponse() throws Exception {
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(cardHolderResponseWitFailure);
		service.setApiClient(client);

		service.isActive("somekey");
	}

	@Test
	public void doAddSignedCard_withSuccessResponse() {


		String wmUserId = System.currentTimeMillis() + "";

		List<NameValuePair> params = new LinkedList<NameValuePair>();

		params.add(new BasicNameValuePair("keyfield", String.valueOf(wmUserId)));
		params.add(new BasicNameValuePair("firstname", String.valueOf("Greg")));
		params.add(new BasicNameValuePair("lastname", String.valueOf("Bluvshteyn")));
		params.add(new BasicNameValuePair("address", String.valueOf("1876 brown st 2F")));
		params.add(new BasicNameValuePair("city", String.valueOf("New York")));
		params.add(new BasicNameValuePair("country", String.valueOf("US")));
		params.add(new BasicNameValuePair("state", String.valueOf("NY")));
		params.add(new BasicNameValuePair("zipcode", String.valueOf("11229")));
		params.add(new BasicNameValuePair("govid", String.valueOf("111-11-1111")));
		params.add(new BasicNameValuePair("govidtype", String.valueOf("SSN")));
		params.add(new BasicNameValuePair("dob", String.valueOf("05/11/1977")));

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(addSignatureCardResponse);


		service.setApiClient(client);

		try {
			GlobalCashCardTransactionResponse response = service.addSignatureCard(params);

			assertEquals("success", response.getStatus());
			assertEquals("5110310409090538", response.getCardnumber());
			assertEquals("mc39551308090905389394", response.getTracknum());
			assertEquals("Greg", response.getFirstname());
		} catch (Exception e) {
			//ExceptionUtils.printRootCauseStackTrace(e);
			Assert.fail(String.format("Exception: %s",ExceptionUtils.getStackTrace(e)));
		}

	}

	@Test
	public void doAddSignatureCard_withFailingResponse() throws Throwable {
		List<NameValuePair> params = new LinkedList<NameValuePair>();

		params.add(new BasicNameValuePair("keyfield", String.valueOf(wmUserId)));
		params.add(new BasicNameValuePair("firstname", String.valueOf("Greg")));
		params.add(new BasicNameValuePair("lastname", String.valueOf("Bluvshteyn")));

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(addSignatureCardFailedResponse);
		service.setApiClient(client);
		GlobalCashCardTransactionResponse response = service.addSignatureCard(params);
		assertNotNull(response);
		assertEquals("13",response.getResponsecode());
	}

	@Test
	public void testLoadCard() throws Throwable {

		List<NameValuePair> params = new LinkedList<NameValuePair>();

		params.add(new BasicNameValuePair("cardnumber", String.valueOf(wmUserId)));
		params.add(new BasicNameValuePair("net", "1"));
		params.add(new BasicNameValuePair("description", "trying to add $100"));

		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(loadCardSuccessResponse);

		service.setApiClient(client);

		GlobalCashCardTransactionResponse response = service.loadCard(params);

		assertEquals("L8140971130814074458", response.getPayrollid());
		assertEquals("1", response.getNet());
		assertEquals("demo7D496AE0-A2B4-DFD0-4A69F58DCB06CD73", response.getAuditno());
		assertEquals("trying to add %24100", response.getDescription());
	}


	@Test
	public void testDebitCard() throws Throwable{

		List<NameValuePair> params = new LinkedList<NameValuePair>();

		params.add(new BasicNameValuePair("cardnumber", String.valueOf(wmUserId)));
		params.add(new BasicNameValuePair("net","1"));
		params.add(new BasicNameValuePair("description","trying to add $1"));


		when(client.sendRequest(anyString(),anyString(),anyString(),anyString())).thenReturn(debitCardSuccessResponse);

		GlobalCashCardTransactionResponse response = service.loadCard(params);

		assertEquals("D8140971130814075740",response.getPayrollid());
		assertEquals("1",response.getNet());
		assertEquals("demo7D550E24-AC5F-31E9-DA70109C936AA9DB",response.getAuditno());
		assertEquals("trying to add %24100",response.getDescription());
		assertEquals("demo7D550E24-AC5F-31E9-DA70109C936AA9DB",response.getTransid());

	}



	@Test
	public void testLastFour() {
		String cc = "12345678901234";
		assertEquals("1234", StringUtils.substring(cc, -4));
	}

}
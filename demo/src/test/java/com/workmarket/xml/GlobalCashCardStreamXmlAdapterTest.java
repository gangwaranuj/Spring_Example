package com.workmarket.xml;

import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class GlobalCashCardStreamXmlAdapterTest {


	public XmlAdapter xmlAdapter = new GlobalCashCardStreamXmlAdapter();

	@Test
	public void testToXmlForSuccessFullGCCResponse() {
		String addSignatureCardResponse = "<?xml version = \"1.0\"?><response>    <status>success</status>    <cardnumber>5110310409090538</cardnumber>    <firstname>Greg</firstname>    <lastname>Bluvshteyn</lastname>    <address>1876 brown st</address>    <city>New York</city>    <state>NY</state>    <zipcode>11229</zipcode>    <country>US</country>    <keyfield>1234</keyfield>    <govid>111111111</govid>    <govidtype>SSN</govidtype>    <govidissuer></govidissuer>    <dob>05/11/1977</dob>    <phone></phone>    <email></email>    <tracknum>mc39551308090905389394</tracknum></response>";

		GlobalCashCardTransactionResponse response = (GlobalCashCardTransactionResponse) xmlAdapter.fromXml(addSignatureCardResponse);

		assertEquals("success", response.getStatus());
		assertEquals("5110310409090538", response.getCardnumber());
	}

	@Test
	public void testToXmlFailedGCCResponse() throws Throwable {
		String failedResponse = "<?xml version = \"1.0\"?><response>    <status>failure</status>    <responsecode>25</responsecode>    <description>missing or invalid required field country1 </description></response>";

		GlobalCashCardTransactionResponse response = (GlobalCashCardTransactionResponse) xmlAdapter.fromXml(failedResponse);

		assertNotNull(response);

		assertEquals("failure", response.getStatus());
		assertEquals("25", response.getResponsecode());
		assertEquals("missing or invalid required field country1 ", response.getDescription());
	}

}

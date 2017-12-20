package com.workmarket.xml;

import com.thoughtworks.xstream.XStream;
import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;


public class GlobalCashCardStreamXmlAdapter implements XmlAdapter {
	private XStream xStream = new XStream();

	public GlobalCashCardStreamXmlAdapter() {
		xStream.ignoreUnknownElements(); // don't die on new xml fields when API gets updated by GCC
		xStream.alias("response", com.workmarket.domains.model.account.GlobalCashCardTransactionResponse.class);
		xStream.alias("cardholders", com.workmarket.domains.model.account.GlobalCashCardTransactionResponse.class);
		xStream.alias("cardholder",GlobalCashCardTransactionResponse.GlobalCashCardCardHolderResponse.class);
		xStream.aliasField("cardholder",GlobalCashCardTransactionResponse.class,"cardholder");

	}

	@Override
	public String toXml(Object object) {
		return xStream.toXML(object);
	}

	public Object fromXml(String xml) {
		return xStream.fromXML(xml);
	}


}
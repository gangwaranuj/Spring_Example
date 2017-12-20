package com.workmarket.xml;

import com.thoughtworks.xstream.XStream;

public class XStreamXmlAdapter implements XmlAdapter {
	private XStream xStream = new XStream();

	public XStreamXmlAdapter() {
		xStream.alias("TwilioResponse", com.workmarket.domains.model.voice.twilio.TwilioXmlRestResponse.class);
		xStream.alias("Call", com.workmarket.domains.model.voice.twilio.TwilioXmlRestResponse.Call.class);
		xStream.aliasField("Call", com.workmarket.domains.model.voice.twilio.TwilioXmlRestResponse.class, "call");
	}

	@Override
	public String toXml(Object object) {
		return xStream.toXML(object);
	}

	@Override
	public Object fromXml(String xml) {
		return xStream.fromXML(xml);
	}

	public XStream getXStream() {
		return xStream;
	}
}

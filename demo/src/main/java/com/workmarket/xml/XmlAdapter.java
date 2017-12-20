package com.workmarket.xml;


public interface XmlAdapter {
	String toXml(Object object);

	Object fromXml(String xml);
}

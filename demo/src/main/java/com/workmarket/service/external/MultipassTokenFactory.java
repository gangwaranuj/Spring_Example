package com.workmarket.service.external;

public interface MultipassTokenFactory {
	String encode(String data);
	String sign(String data);
}

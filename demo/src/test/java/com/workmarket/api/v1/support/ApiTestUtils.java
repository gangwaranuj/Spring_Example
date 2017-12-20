package com.workmarket.api.v1.support;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class ApiTestUtils {

	public static String buildUrl(String apiUrl, String path, Map<String,String> uriVariables) {
		StringBuilder qs = new StringBuilder(apiUrl);
		qs.append(path);

		if (!uriVariables.isEmpty()) {
			qs.append("?");

			for (Iterator<Map.Entry<String,String>> i = uriVariables.entrySet().iterator(); i.hasNext();) {
				Map.Entry<String,String> entry = i.next();

				if (entry.getValue() != null) {
					String value = String.valueOf(entry.getValue());

					try {
						value = URLEncoder.encode(value, "UTF-8");
					} catch (Exception ex) {

						throw new RuntimeException("error encoding: " + value, ex);
					}

					qs.append(entry.getKey()).append("=").append(value);

					if (i.hasNext()) {
						qs.append("&");
					}
				}
			}
		}

		return qs.toString();
	}
}
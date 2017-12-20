package com.workmarket.succession;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaUtil;
import com.workmarket.jan20.IsEqual;

public class SuccessionUtil {
	public static IsEqual<Throwable> makeBothOrNeitherThrow(final String method, final KafkaClient client, final String topic) {
		return new IsEqual<Throwable>() {
			@Override
			public boolean apply(final Throwable a, final Throwable b) {
				final boolean result = ((a == null) == (b == null));
				if (!result) {
					client.send(topic, KafkaUtil.getStringObjectMap(a, b, "bothOrNeitherThrow",
						ImmutableList.<String>of(), method));
				}
				return result;
			}
		};
	}
}

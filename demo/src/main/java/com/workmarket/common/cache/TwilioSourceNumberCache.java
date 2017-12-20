package com.workmarket.common.cache;

import com.google.common.base.Optional;

import java.util.List;

public interface TwilioSourceNumberCache {

	Optional<String> getSourceNumber();

	void putSourceNumbers(List<String> sourceNumbers);

}

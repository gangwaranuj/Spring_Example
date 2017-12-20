package com.workmarket.service.external;

import com.google.common.base.Optional;

public interface RemoteReader<T> {
	void fetch();
	Optional<T> read();
}

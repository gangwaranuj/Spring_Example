package com.workmarket.data.dataimport.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FlatAdapter {
	List<String[]> translate(InputStream input) throws IOException;
}

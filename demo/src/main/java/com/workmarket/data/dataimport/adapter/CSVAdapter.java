package com.workmarket.data.dataimport.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CSVAdapter implements FlatAdapter {
	public List<String[]> translate(InputStream inputStream) throws IOException {
		return new CSVReader(new InputStreamReader(inputStream), ',', '"', true).readAll();
	}
}

package com.workmarket.utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.reporting.format.StringFormat;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


public class FileUploadUtilities {

	public static List<String> handleHeader(String[] rawHeader, List<String> columns) {
		List<String> header = cleanImportHeader(rawHeader);
		columns = cleanImportHeader(columns.toArray(new String[0]));

		Set<String> missingColumns = caseInsensitiveDifference(header, columns);
		String message = "";
		List<String> errors = Lists.newArrayList();

		if (isNotEmpty(missingColumns)) {
			message = String.format("Your upload is missing required %s column %s", StringUtils.join(missingColumns, ", "), StringUtilities.pluralize("header", missingColumns.size()));
			errors.add(message);
		}

		if (isEmpty(missingColumns) && header.size() > columns.size()) {
			message = "Your upload contains a duplicate column. ";
			errors.add(message);
		}

		return errors;
	}

	/**
	 * @param rawHeaders -
	 * @return - A list of Strings with all whitespace removed
	 */
	public static List<String> cleanImportHeader(String[] rawHeaders) {
		List<String> trimmedStrings = Lists.newArrayListWithCapacity(rawHeaders.length);
		int index = 0;
		for (int i = 0; i < rawHeaders.length; i++) {
			String element = rawHeaders[i];
			//check to ignore the last empty header column  for error section introduced by csv view tool
			if (StringUtils.isNotEmpty(element) && index != rawHeaders.length - 1) {
				trimmedStrings.add(element.replaceAll("\\s+", " "));
			}
		}
		return trimmedStrings;
	}

	/**
	 * @param list      -
	 * @param otherList -
	 * @return - The set of Strings present in list, but not present in otherList
	 */
	public static ImmutableSet<String> caseInsensitiveDifference(List<String> list, List<String> otherList) {
		LinkedHashSet<String> set = Sets.newLinkedHashSet(list);
		LinkedHashSet<String> otherSet = Sets.newLinkedHashSet(otherList);

		return Sets.difference(otherSet, set).immutableCopy();
	}

	public static boolean isFileEmpty(File file, Integer offset) throws IOException {
		return getEffectiveNumberOfLines(file, offset) < 0 ? true : false;
	}

	public static boolean hasUserData(File file, Integer offset) throws IOException {
		return getEffectiveNumberOfLines(file, offset) > 0 ? true : false;
	}

	public static boolean IsImportSizeExceeded(File file, Integer offset, Integer limit) throws IOException {
		return getEffectiveNumberOfLines(file, offset) > limit;
	}

	public static int getNumberOfLines(File file) throws IOException {
		int count = 0;
		if (file.exists()) {
			FileReader fr = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr);
			String line;
			while ((line = lnr.readLine()) != null) {
				if (StringUtils.isNotBlank(line) && line.split(",").length != 0) {
					count++;
				}
			}
			lnr.close();
		}
		return count;
	}

	public static int getEffectiveNumberOfLines(File file, int offset) throws IOException {
		return getNumberOfLines(file) - offset;
	}
}

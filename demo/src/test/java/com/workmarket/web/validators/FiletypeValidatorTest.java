package com.workmarket.web.validators;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: andrew
 * Date: 9/11/13
 */

@RunWith(BlockJUnit4ClassRunner.class)
public class FiletypeValidatorTest {

	private FiletypeValidator validator = new FiletypeValidator();
	private Set<String> imageTypes = Sets.newHashSet();
	private Set<String> txtTypes = Sets.newHashSet();

	public FiletypeValidatorTest() {
		imageTypes.addAll(Arrays.asList("png", "jpg", "bmp"));
		txtTypes.addAll(Arrays.asList("txt", "pdf", "docx"));
	}

	@Test
	public void validate_NullFiletype_fail() {
		assertTrue(hasErrorCode(getErrors(null, imageTypes), "NotEmpty"));
	}

	@Test
	public void validate_EmptyFilename_fail() {
		assertTrue(hasErrorCode(getErrors("  \t \n", imageTypes), "NotEmpty"));
	}

	@Test
	public void validate_ValidFilename_success() {
		assertFalse(getErrors("PNG", imageTypes).hasErrors());
	}

	@Test
	public void validate_ValidFiletypeEmployment_success() {
		assertFalse(getErrors("pdf", txtTypes).hasErrors());
	}

	@Test
	public void validate_InvalidFiletypeEmployment_fail() {
		assertTrue(getErrors("zip", txtTypes).hasErrors());
	}

	@Test
	public void validate_InvalidPage_fail() {
		Set<String> s = Sets.newHashSet();
		assertTrue(getErrors("zip", s).hasErrors());
	}

	@Test
	public void validate_ValidSupports_success() {
		assertTrue(validator.supports(Map.class));
	}

	@Test
	public void validate_InvalidSupports_fail() {
		assertFalse(validator.supports(ArrayList.class));
	}

	private Errors getErrors(String filetype, Set s) {
		MapBindingResult errors = new MapBindingResult(Maps.newHashMap(), MoreObjects.firstNonNull(filetype, ""));
		Map<String, Object> fileDesc = new HashMap<>();
		fileDesc.put("filetype", filetype);
		fileDesc.put("pageSet", s);
		validator.validate(fileDesc, errors);
		return errors;
	}

	private boolean hasErrorCode(Errors errors, final String errorCode) {
		return Iterables.tryFind(errors.getAllErrors(), new Predicate<ObjectError>() {
			@Override
			public boolean apply(ObjectError objectError) {
				return Arrays.asList(objectError.getCodes()).contains(errorCode);
			}
		}).isPresent();
	}
}

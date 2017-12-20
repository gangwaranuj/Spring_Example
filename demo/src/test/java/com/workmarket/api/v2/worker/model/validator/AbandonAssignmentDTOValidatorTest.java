package com.workmarket.api.v2.worker.model.validator;

import com.workmarket.api.v2.worker.model.AbandonAssignmentDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by michaelrothbaum on 7/31/17.
 */

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AbandonAssignmentDTOValidatorTest {

	@InjectMocks
	private AbandonAssignmentDTOValidator abandonAssignmentDTOValidator;

	private static int LIMIT = 65535;
	private static String LONG_STRING = RandomStringUtils.random(LIMIT + 1);

	@Before
	public void setup() {
		abandonAssignmentDTOValidator = new AbandonAssignmentDTOValidator();
	}

	@Test
	public void validateValidMessage_noErrorResult() {

		AbandonAssignmentDTO abandonAssignmentDTO = new AbandonAssignmentDTO.Builder()
				.withMessage("Can't make it.")
				.build();

		BindingResult bindingResult = new BindException(abandonAssignmentDTO, "AbandonAssignment");

		abandonAssignmentDTOValidator.validate(abandonAssignmentDTO.getMessage(), bindingResult);

		assertFalse(bindingResult.hasErrors());
	}

	@Test
	public void validateMissingMessage_errorResult() {

		AbandonAssignmentDTO abandonAssignmentDTO = new AbandonAssignmentDTO.Builder()
				.withMessage("")
				.build();

		BindingResult bindingResult = new BindException(abandonAssignmentDTO, "AbandonAssignment");

		abandonAssignmentDTOValidator.validate(abandonAssignmentDTO.getMessage(), bindingResult);

		String errorMessage = bindingResult.getAllErrors().toString();

		assertTrue(bindingResult.hasErrors());
		assertTrue(errorMessage.contains("You must add a message in order to abandon the assignment."));
	}

	@Test
	public void validateLongMessage_errorResult() {

		AbandonAssignmentDTO abandonAssignmentDTO = new AbandonAssignmentDTO.Builder()
				.withMessage(LONG_STRING)
				.build();

		BindingResult bindingResult = new BindException(abandonAssignmentDTO, "AbandonAssignment");

		abandonAssignmentDTOValidator.validate(abandonAssignmentDTO.getMessage(), bindingResult);

		String errorMessage = bindingResult.getAllErrors().toString();

		assertTrue(bindingResult.hasErrors());
		assertTrue(errorMessage.contains("The message is too long."));
	}
}
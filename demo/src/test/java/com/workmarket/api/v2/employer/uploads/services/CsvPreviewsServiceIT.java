package com.workmarket.api.v2.employer.uploads.services;

import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.title;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CsvPreviewsServiceIT extends BaseServiceIT {
	public static final String UUID = "testUUID";

	@Autowired private CsvPreviewsService csvPreviewsService;
	@Autowired private PreviewStorageService previewStorageService;

	@Before
	public void setup() throws Exception {
		previewStorageService.destroy(UUID);
	}

	@Test
	public void processValidateJob_valid() {
		String uuid = UUID;
		int rowNumber = 0;

		previewStorageService.add(uuid, new PreviewDTO.Builder()
			.setUuid(uuid)
			.setRowNumber(rowNumber)
			.setAssignmentDTO(new AssignmentDTO.Builder(make(an(AssignmentMaker.AssignmentDTO)))).build());
		csvPreviewsService.validate(uuid, rowNumber);
		Optional<PreviewDTO> previewDTO = previewStorageService.get(uuid, rowNumber);
		assertTrue(previewDTO.isPresent());
		assertEquals(0, previewDTO.get().getValidationErrors().size());
	}

	@Test
	public void processValidateJob_missingTitle() {
		String uuid = UUID;
		int rowNumber = 0;

		previewStorageService.add(uuid, new PreviewDTO.Builder()
			.setUuid(uuid)
			.setRowNumber(rowNumber)
			.setAssignmentDTO(new AssignmentDTO.Builder(make(an(AssignmentMaker.AssignmentDTO, withNull(title))))).build());
		csvPreviewsService.validate(uuid, rowNumber);
		Optional<PreviewDTO> previewDTO = previewStorageService.get(uuid, rowNumber);
		assertTrue(previewDTO.isPresent());
		assertEquals(1, previewDTO.get().getValidationErrors().size());
		assertEquals("Title is a required field.", previewDTO.get().getValidationErrors().get(0).getMessage());
	}
}

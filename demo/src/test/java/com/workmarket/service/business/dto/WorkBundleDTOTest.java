package com.workmarket.service.business.dto;

import com.workmarket.domains.work.model.WorkBundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WorkBundleDTOTest {

	@Test
	public void fromWorkDTO() {
		WorkBundle workBundle = new WorkBundle();
		workBundle.setId(1L);
		workBundle.setTitle("yeap");
		WorkBundleDTO workBundleDTO = WorkBundleDTO.fromWorkBundle(workBundle);

		assertEquals(workBundleDTO.getId(), workBundle.getId());
		assertEquals(workBundleDTO.getTitle(), workBundle.getTitle());
	}
}

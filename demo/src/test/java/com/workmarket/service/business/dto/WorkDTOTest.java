package com.workmarket.service.business.dto;

import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.work.model.AbstractWork;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WorkDTOTest {

	@Test
	public void newWorkDTO_fromWorkEntity() throws Exception {
		AbstractWork work = new AbstractWork() {
			private static final long serialVersionUID = 7673321031708148651L;
			@Override
			public FulfillmentStrategy getFulfillmentStrategy() {
				return null;
			}
		};
		work.setId(1L);
		work.setTitle("21345");
		WorkDTO workDTO = new WorkDTO(work);

		assertEquals(workDTO.getId(), work.getId());
		assertEquals(workDTO.getTitle(), work.getTitle());
	}
}

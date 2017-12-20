package com.workmarket.service.business;

import com.workmarket.dao.contract.ContractVersionUserSignatureDAO;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.service.business.dto.ContractVersionUserSignatureDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractServiceImplTest {
	@Mock ContractVersionUserSignatureDAO contractVersionUserSignatureDAO;
	@InjectMocks ContractServiceImpl contractService;

	ContractVersionUserSignatureDTO dto;
	ContractVersionUserSignature signature;

	private static final Long CONTRACT_VERSION_ID = -9999999L;
	private static final Long USER_ID = -9999999L;

	@Before
	public void setup() {
		signature = mock(ContractVersionUserSignature.class);

		dto = mock(ContractVersionUserSignatureDTO.class);
			when(dto.getContractVersionId()).thenReturn(CONTRACT_VERSION_ID);
			when(dto.getUserId()).thenReturn(USER_ID);
	}

	@Test
	public void findOrCreateContractVersionUserSignature_WhenSignatureExists_ReturnsExistingSignature() throws Exception {
		when(contractVersionUserSignatureDAO.findBy(
			"contractVersion.id", CONTRACT_VERSION_ID,
			"user.id", USER_ID
		)).thenReturn(signature);

		ContractVersionUserSignature actual = contractService.findOrCreateContractVersionUserSignature(dto);

		assertEquals(actual, signature);
	}
}

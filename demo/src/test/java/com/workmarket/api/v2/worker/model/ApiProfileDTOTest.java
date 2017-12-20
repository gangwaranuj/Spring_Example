package com.workmarket.api.v2.worker.model;

import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ApiProfileDTOTest {

    @Test
    public void testAsWorkerOnboardingDTO() throws Exception {
        final ApiProfileDTO dto = new ApiProfileDTO.Builder()
            .withSecondaryEmail("secondary+unittest@worker.com")
            .withEmail("primary+unittest@worker.com")
            .withFirstName("Balbus")
            .withLastName("Risto")
            .build();

        final WorkerOnboardingDTO onboardingDTO = dto.asWorkerOnboardingDTO();

        assertEquals("secondary+unittest@worker.com", onboardingDTO.getSecondaryEmail());
        assertEquals("primary+unittest@worker.com", onboardingDTO.getEmail());
        assertEquals("Balbus", onboardingDTO.getFirstName());
        assertEquals("Risto", onboardingDTO.getLastName());
    }
}
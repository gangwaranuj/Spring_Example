package com.workmarket.api.v2.worker.service;

import com.workmarket.api.v2.model.ApiBankRoutingDTO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.service.infra.business.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * ACH routing number suggestions
 */
@Service
public class ApiBankRoutingSuggestionService {

    final SuggestionService suggestionService;

    @Autowired
    public ApiBankRoutingSuggestionService(final SuggestionService suggestionService){
        this.suggestionService = suggestionService;
    }

    public List<ApiBankRoutingDTO> suggestBankRouting(final String country, final String search) {
        final List<BankRouting> list = suggestionService.suggestBankRouting(search, country);
        final List<ApiBankRoutingDTO> result = new ArrayList<>();

        for (final BankRouting e : list) {
            final ApiBankRoutingDTO.Builder builder = new ApiBankRoutingDTO.Builder(e);
            final ApiBankRoutingDTO dto = builder.build();

            result.add(dto);
        }

        return result;
    }
}

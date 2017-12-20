package com.workmarket.api.v2.worker.service;

import java.math.BigDecimal;
import java.util.List;

import com.sleepycat.je.tree.LN;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.BankingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain user funds related data and make
 * funds related processing calls. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should
 * give way to service classes that call on microservices for this type of work.
 */
@Service
public class BankAccountsService {

    @Autowired BankingService bankingService;

    public AbstractBankAccount saveBankAccount(final Long userId, final BankAccountDTO bankAccountDTO) {
        return bankingService.saveBankAccount(userId, bankAccountDTO);
    }

    public AbstractBankAccount getBankAccount(final Long bankAccountId) {

        return bankingService.findBankAccount(bankAccountId);
    }

    public List<AbstractBankAccount> getBankAccountsForUser(final Long userId) {

        return (List<AbstractBankAccount>) bankingService.findBankAccounts(userId);
    }

    public AbstractBankAccount deleteBankAccount(final Long bankAccountId, final Long companyId) {
        return bankingService.deactivateBankAccount(bankAccountId, companyId);
    }

    /**
     * @param amount1 Decimal representation of the amount eg .03
     * @param amount2 Decimal representation of the amount
     * @return
     */
    public boolean confirmBankAccount(final Long bankAccountId, final BigDecimal amount1, final BigDecimal amount2, final Long companyId) throws Exception {
        return bankingService.confirmBankAccount(bankAccountId, amount1.movePointRight(2).intValue(), amount2.movePointRight(2).intValue(), companyId);
    }
}

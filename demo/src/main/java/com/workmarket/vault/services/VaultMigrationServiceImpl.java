package com.workmarket.vault.services;

import com.google.common.collect.Lists;
import com.workmarket.common.exceptions.BadRequestException;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.dao.DAOInterface;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.vault.exceptions.VaultRuntimeException;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaultMigrationServiceImpl implements VaultMigrationService {
	private static final Log logger = LogFactory.getLog(VaultMigrationServiceImpl.class);

	@Autowired BankAccountDAO bankAccountDAO;
	@Autowired TaxEntityDAO taxEntityDAO;
	@Autowired VaultHelper vaultHelper;
	@Autowired VaultServerService vaultServerService;

	@Override
	public void migrateBankAccounts(List<Long> bankAccountIds) {
		migrate(bankAccountIds, bankAccountDAO, true, false);
	}

	@Override
	public void migrateTaxEntities(List<Long> taxEntityIds, boolean saveVaultedValues, boolean saveDuplicateTins) {
		migrate(taxEntityIds, taxEntityDAO, saveVaultedValues, saveDuplicateTins);
	}

	private void migrate(List<Long> ids, DAOInterface<? extends AbstractEntity> dao, boolean saveVaultedValues,
	                     boolean saveDuplicateTins) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}

		List<List<Long>> partitions = Lists.partition(ids, 50); // limit in memory list to 50 objects
		for (List<Long> partition : partitions) {
			List<? extends AbstractEntity> entities = dao.get(partition);
			for (AbstractEntity ent : entities) {
				try {
					List<VaultKeyValuePair> pairs = new ArrayList<>();

					if (saveVaultedValues) {
						pairs = vaultHelper.getVaultedValues(ent);
					}

					// Create the duplicate entry if the tax entity is active
					if (saveDuplicateTins && ent instanceof AbstractTaxEntity) {
						AbstractTaxEntity e = (AbstractTaxEntity) ent;
						if (e.getActiveFlag() != null && e.getActiveFlag()) {
							pairs.add(vaultHelper.buildDuplicateTaxNumberCheckPair(e, e.getTaxNumberSanitized()));
						}
					}

					vaultServerService.post(pairs);
				} catch (BadRequestException e) {
					logger.error("Bad Request migrating tax info into vault", e);
				} catch (ServiceUnavailableException e) {
					logger.error("Service unavailable migrating tax info into vault", e);
					throw new VaultRuntimeException("Unavailable vault service migrating bank account info to vault", e);
				}
			}
		}

	}
}

package com.workmarket.service.business.tax;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.dao.tax.TaxVerificationRequestDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationRequest;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.business.event.TaxVerificationEvent;
import com.workmarket.service.exception.tax.TaxVerificationException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static ch.lambdaj.Lambda.forEach;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nick on 11/29/12 3:50 PM
 */
@Service
public class TaxVerificationServiceImpl implements TaxVerificationService {

	private static final Log logger = LogFactory.getLog(TaxVerificationServiceImpl.class);

	@Autowired private TaxVerificationRequestDAO taxVerificationRequestDAO;
	@Autowired private TaxEntityDAO taxEntityDAO;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private UserDAO userDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private MessageSource messageSource;

	@Override
	public boolean isTaxVerificationAvailable() {
		return getUnverifiedEntitiesForVerification().size() > 0;
	}

	@Override
	public List<TaxVerificationRequest> findTaxVerificationRequests() {
		return taxVerificationRequestDAO.findTaxVerificationRequests();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<TaxVerificationRequest> createUsaTaxVerificationBatch(Long userId) {
		checkNotNull(userId);

		List<? extends AbstractTaxEntity> entities = getUnverifiedEntitiesForVerification();

		User user = checkNotNull(userDAO.get(userId));

		TaxVerificationRequest request = null;
		if (CollectionUtils.isNotEmpty(entities)) {
			for (AbstractTaxEntity entity : entities)
				entity.setVerificationPending(true);
			request = new TaxVerificationRequest(entities, Calendar.getInstance(), user);
			taxVerificationRequestDAO.saveOrUpdate(request);
		}
		return Optional.fromNullable(request);
	}

	@Override
	public Optional<TaxVerificationRequest> findTaxEntityValidationRequest(Long requestId) {
		Assert.notNull(requestId);
		TaxVerificationRequest request = taxVerificationRequestDAO.get(requestId);
		if (request != null)
			Hibernate.initialize(request.getTaxEntities()); // force
		return Optional.fromNullable(request);
	}

	@Override
	public void addConfirmationNumberToTaxEntityValidationRequest(Long requestId, String confirmationNumber) {
		Assert.notNull(requestId);
		Assert.hasText(confirmationNumber);
		TaxVerificationRequest request = taxVerificationRequestDAO.get(requestId);
		Assert.notNull(request);
		request.setConfirmationNumber(confirmationNumber);
	}

	@Override
	public long validateRequestFromCsv(Long requestId, Asset asset) throws Exception {
		checkNotNull(asset);

		TaxVerificationRequest request = taxVerificationRequestDAO.get(checkNotNull(requestId));
		Hibernate.initialize(request.getTaxEntities());

		InputStream stream = remoteFileAdapter.getFileStream(RemoteFileType.PRIVATE, asset.getUUID());
		CSVReader csvReader = new CSVReader(new InputStreamReader(stream), ';');
		List<String[]> allLines;
		try {
			allLines = csvReader.readAll();
		} catch (IOException e) {
			throw new TaxVerificationException(messageSource.getMessage(
					"admin.accounting.tin.process_tin_file.exception", null, Locale.getDefault()), e);
		}

		// We only support USA tax entities for now
		List<AbstractTaxEntity> usaEntities = new ArrayList<>();
		for (AbstractTaxEntity ent : request.getTaxEntities()) {
			if (ent instanceof UsaTaxEntity) {
				usaEntities.add(ent);
			}
		}

		if (allLines.size() != usaEntities.size()) {
			throw new TaxVerificationException(
					messageSource.getMessage("admin.accounting.tin.process_tin_file.csv_row_mismatch",
							new Object[]{allLines.size(), request.getTaxEntities().size()}, Locale.getDefault()));
		}

		logger.info(String.format("[irs match] processing file %s", asset.getName()));

		long totalRows = 0L;
		long rowsProcessed = 0L;
		List<Long> entitiesToEmail = Lists.newArrayList();

		for (String[] line : allLines) {

			totalRows++;
			if (line == null || (line.length != 5 && line.length != 6)) {
				continue;
			}

			AbstractTaxEntity entity;
			Integer status;
			Company company;
			try {
				// parse and validate row
				final Long companyId = checkNotNull(Long.parseLong(line[3]));

				status = checkNotNull(Integer.parseInt(line[4]));

				// search for entity by company id from csv
				entity = (Iterables.find(usaEntities, new Predicate<Object>() {
					@Override public boolean apply(@Nullable Object o) {
						AbstractTaxEntity e = (AbstractTaxEntity) o;
						return (e != null && e.getCompany() != null && companyId.equals(e.getCompany().getId()));
					}
				}));
				company = checkNotNull(entity.getCompany());

			} catch (Exception e) {
				logger.error(String.format("[irs match] Error on row %d: ", totalRows), e);
				continue;
			}

			switch (status) {
				case 0: // the name/TIN combination matches IRS records.
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.APPROVED));
					entitiesToEmail.add(entity.getId());
					for (AbstractTaxEntity tin : taxEntityDAO.findAllTaxEntitiesByCompany(company.getId())) {
						tin.setActiveFlag(entity.getId().equals((tin.getId())));
					}
					rowsProcessed++;
					break;
				case 1: // TIN has invalid format
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.INVALID_TIN_FORMAT));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 2: // TIN entered is not currently issued
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.NOT_ISSUED));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 3: // the name/TIN combination do not match IRS records
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.NOT_MATCHED));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 4: // invalid TIN Matching request
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.INVALID_REQUEST));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 5: // indicates a duplicate TIN Matching request
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.DUPLICATE_TIN));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 6: // when the TIN type is unknown and a Matching TIN/name is found in the NAP DM1
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.WRONG_TYPE_SSN));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 7: // when the TIN type is unknown and a Matching TIN/name is found in the EIN/NC DB
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.WRONG_TYPE_EIN));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				case 8: // when the TIN type is unknown and a Matching TIN/name is found in both the NAP DM1 and EIN/NC DBs
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.WRONG_TYPE_BOTH));
					entitiesToEmail.add(entity.getId());
					rowsProcessed++;
					break;
				default:
					line[0] = StringUtilities.showLastNDigits(line[0], '*', 4); // mask EIN/SSN
					entity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.UNVERIFIED));
					logger.error(String.format("[irs match] unexpected response %d from IRS: %s", status, Arrays.toString(line)));
			}
			entity.setVerificationPending(false);
		}

		logger.info(String.format("[irs match] successfully processed %d of %d rows in file %s", rowsProcessed, totalRows, asset.getName()));

		if (!entitiesToEmail.isEmpty()) {
			eventRouter.sendEvent(new TaxVerificationEvent(entitiesToEmail));
			request.setVerificationStatus(VerificationStatus.VERIFIED);
		}

		return entitiesToEmail.size();
	}

	@Override
	public boolean cancelTaxVerificationRequest(Long requestId) {
		TaxVerificationRequest request = taxVerificationRequestDAO.get(checkNotNull(requestId));
		if (request == null)
			return false;

		forEach(request.getTaxEntities()).setVerificationPending(false);
		request.setDeleted(true);

		return true;
	}

	@SuppressWarnings("unchecked")
	private List<? extends AbstractTaxEntity> getUnverifiedEntitiesForVerification() {
		/* 	need to check for two cases:
				1. active and unverified
				2. inactive and unverified AND exists an active approved entity that is older
					this is for the case where a user edits tax info after already being approved
		 */
		List<? extends AbstractTaxEntity> actives = taxEntityDAO.findUnverifiedActiveTaxEntitiesByCountry(AbstractTaxEntity.COUNTRY_USA);
		List<? extends AbstractTaxEntity> inactives = taxEntityDAO.findAllUnverifiedTaxEntitiesWhereActiveOrRejectedExists();
		return ListUtils.union(actives, inactives);
	}
}

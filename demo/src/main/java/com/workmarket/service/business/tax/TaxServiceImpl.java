package com.workmarket.service.business.tax;

import com.google.api.client.util.Maps;
import com.google.common.base.Optional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.workmarket.api.model.CountryEnum;
import com.workmarket.api.model.TaxEntityTypeCodeEnum;
import com.workmarket.api.model.TaxVerificationStatusCodeEnum;
import com.workmarket.api.v2.model.TaxInfoApiDTO;
import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.api.v2.model.W9PdfPreviewApiDTO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.tax.TaxEntityDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.CanadaTaxEntity;
import com.workmarket.domains.model.tax.ForeignTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.factory.TaxEntityFactory;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.services.VaultHelper;

import groovy.lang.Tuple2;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
@SuppressWarnings("unchecked")
public class TaxServiceImpl implements TaxService {

	private static final Log logger = LogFactory.getLog(TaxServiceImpl.class);

	public static final int VAULT_MAX_MULTI_GET = 35;
	public static final String W9_TEMPLATE = "classpath:files/w9.pdf";

	private static Properties w9FieldMap;

	static {
		try {
			w9FieldMap = PropertiesLoaderUtils.loadAllProperties("files/w9.field.properties");
		} catch (IOException e) {
			logger.error("Failed to load assignment detail navigation mappings", e);
		}
	}

	@Autowired private AuthenticationService authenticationService;
	@Autowired private PricingService pricingService;

	@Autowired private WorkDAO workDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private TaxEntityDAO taxEntityDAO;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private TaxEntityFactory taxEntityFactory;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired VaultHelper vaultHelper;
	@Autowired private ResourceLoader resourceLoader;

	@Override
	public BigDecimal calculateTaxAmount(long workId) {
		Assert.notNull(workId);

		Work work = workDAO.get(workId);

		Assert.notNull(work);

		BigDecimal taxAmount = BigDecimal.ZERO;

		if (work.getPricingStrategy().getFullPricingStrategy().getSalesTaxCollectedFlag()) {
			Assert.notNull(work.getPricingStrategy().getFullPricingStrategy().getSalesTaxRate());

			BigDecimal workCost = pricingService.calculateWorkCost(workId);
			Assert.notNull(workCost);

			taxAmount = work.getPricingStrategy().getFullPricingStrategy().getSalesTaxRate().divide(new BigDecimal("100.0000")).multiply(workCost);
		}

		return taxAmount;
	}

	@Override public AbstractTaxEntity findTaxEntityByIdAndCompany(long taxEntityId, long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		return taxEntityDAO.findTaxEntityByIdAndCompany(taxEntityId, companyId);
	}


	@Override
	public AbstractTaxEntity findActiveTaxEntityByCompany(long companyId) {
		return taxEntityDAO.findActiveTaxEntityByCompany(companyId);
	}

	@Override
	public String findTaxEntityCountryId(AbstractTaxEntity taxEntity) {
		if (taxEntity != null) {
			return getCountryIdFromTaxEntityCountry(taxEntity.getCountry());
		}
		return null;
	}

	@Override
	public String getCountryIdFromTaxEntityCountry(String taxCountry) {
		if (AbstractTaxEntity.COUNTRY_USA.equals(taxCountry)) {
			return Country.USA;
		}
		if (AbstractTaxEntity.COUNTRY_CANADA.equals(taxCountry)) {
			return Country.CANADA;
		}
		return null;
	}

	@Override
	public AbstractTaxEntity findActiveTaxEntityByCompanyForEarningReport(long companyId, Calendar activeDateDeadline) {
		AbstractTaxEntity taxEntity = findActiveTaxEntityByCompany(companyId);
		if (taxEntity != null && taxEntity.getActiveDate() != null) {
			if (taxEntity.getActiveDate().after(activeDateDeadline)) {
				return null;
			}
		}
		return taxEntity;
	}

	@Override
	public AbstractTaxEntity findActiveTaxEntity(long userId) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);
		return findActiveTaxEntityByCompany(user.getCompany().getId());
	}

	@Override
	public boolean hasTaxEntityPendingApproval(long userId, String taxNumber) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);

		if (taxNumber != null && taxNumber.trim().length() > 0) {
			return taxEntityDAO.hasTaxEntityPendingApproval(user.getCompany().getId(), taxNumber);
		}
		else {
			return taxEntityDAO.hasTaxEntityPendingApproval(user.getCompany().getId());
		}
	}

	private List<? extends AbstractTaxEntity> findAllTaxEntitiesByCompany(long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		if (company.getTaxEntities() == null) return null;
		taxEntityDAO.initialize(company.getTaxEntities());

		return company.getTaxEntities();
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllTaxEntities(long userId) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);
		return findAllTaxEntitiesByCompany(user.getCompany().getId());
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractTaxEntity> T saveTaxEntityForCompany(long companyId, TaxEntityDTO taxEntityDTO) throws RuntimeException {

		Company company = companyDAO.findCompanyById(companyId);
		User user = authenticationService.getCurrentUser();

		AbstractTaxEntity taxEntity;
		try {
			taxEntity = taxEntityFactory.newInstance(taxEntityDTO);
		} catch (InstantiationException e) {
			throw new RuntimeException("could not create new tax entity", e);
		}
		taxEntity.setSignedBy(user);
		if (taxEntity instanceof UsaTaxEntity) {
			taxEntity.setSignedOn(taxEntityDTO.getSignatureDateAsCalendar());
		}
		taxEntity.setActiveFlag(Boolean.TRUE);
		taxEntity.setCompany(company);

		taxEntity.setTaxNumber(taxEntityDTO.getTaxNumber());
		taxEntity.setTaxNumber(taxEntity.getRawTaxNumber()); // normalize tax number for storage by using getRawTaxNumber
		taxEntity.setDeliveryPolicyFlag(BooleanUtils.isTrue(taxEntityDTO.getDeliveryPolicyFlag()));

		if (StringUtils.isNotBlank(taxEntityDTO.getTaxVerificationStatusCode())) {
			taxEntity.setStatus(TaxVerificationStatusType.newInstance(taxEntityDTO.getTaxVerificationStatusCode()));
		}
		AbstractTaxEntity activeEntity = findActiveTaxEntity(user.getId());

		if (activeEntity == null) {
			taxEntityDAO.saveOrUpdate(taxEntity);
		} else {
			Long savedId;

			// if active entity is different but effective date field not triggered, save the same entity, else create a new one
			if (!areIrsMatchFieldsDifferent(taxEntityDTO, activeEntity)) {
				BeanUtils.copyProperties(taxEntity, activeEntity, new String[]{"id", "activeDate"});
				activeEntity.setBusinessNameFlag(taxEntity.isBusinessNameFlag());
				activeEntity.setBusinessName(taxEntity.getBusinessName());
				taxEntityDAO.saveOrUpdate(activeEntity);
				savedId = activeEntity.getId();

			} else {
				taxEntity.setStatus(TaxVerificationStatusType.newInstance(TaxVerificationStatusType.UNVERIFIED));
				taxEntityDAO.saveOrUpdate(taxEntity);

				savedId = taxEntity.getId();
			}

			// deactivate any other tax entities
			if (company.getTaxEntities() != null) {
				for (AbstractTaxEntity checkTaxEntity : company.getTaxEntities()) {
					if (checkTaxEntity.getActiveFlag() && !checkTaxEntity.getId().equals(savedId)) {
						checkTaxEntity.setActiveFlag(false);
						taxEntityDAO.saveOrUpdate(checkTaxEntity);
					}
				}
			}
		}
		hydratorCache.updateCompanyCache(companyId);
		return (T) taxEntity;
	}

	@Override
	public boolean areIrsMatchFieldsDifferent(TaxEntityDTO dto, AbstractTaxEntity entity) {
		Assert.notNull(dto);
		Assert.notNull(entity);

		try {
			Boolean vaultWrite = featureEvaluator.hasFeature(entity.getCompany().getId(), "vaultWrite");
			Boolean vaultRead = featureEvaluator.hasFeature(entity.getCompany().getId(), "vaultRead");

			String taxNumber = (vaultWrite && vaultRead) ?
				vaultHelper.get(entity, "taxNumber", entity.getTaxNumberSanitized()).getValue()
				: entity.getRawTaxNumber();

			AbstractTaxEntity taxEntity = taxEntityFactory.newInstance(dto);
			taxEntity.setTaxNumber(dto.getTaxNumber());

			String dtoTaxNumber = taxEntity.getRawTaxNumber();

			switch (dto.getTaxCountry()) {
				case AbstractTaxEntity.COUNTRY_USA:
					if (!(entity instanceof UsaTaxEntity))
						return false;

					break;
				case AbstractTaxEntity.COUNTRY_CANADA:
					if (!(entity instanceof CanadaTaxEntity))
						return false;

					break;
				default:
					if (!(entity instanceof ForeignTaxEntity))
						return false;

					break;
			}

			return entity.getBusinessFlag() != dto.getBusinessFlag()
				|| !taxNumber.equals(dtoTaxNumber)
				|| !entity.getTaxName().equalsIgnoreCase(dto.getTaxName());
		}
		catch (Exception e) {
			throw new RuntimeException("could compare tax dto and entity", e);
		}
	}

	@Override
	public List<Long> getAllAccountIdsFromId(final long fromId) {
		return taxEntityDAO.getAccountIdsFromId(fromId);
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllAccountsFromId(long fromId) {
		return taxEntityDAO.findAllAccountsFromId(fromId);
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllTaxEntitiesFromModifiedDate(final Calendar fromModifiedDate) {
		return taxEntityDAO.findAllTaxEntitiesFromModifiedDate(fromModifiedDate);
	}

	@Override
	public List<? extends AbstractTaxEntity> findTaxEntitiesById(List<Long> ids) {
		Assert.notEmpty(ids);
		return taxEntityDAO.get(ids);
	}

	@Override
	public List<? extends AbstractTaxEntity> findTaxEntitiesByTinAndCountry(String tin, String country) {

		if (!AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(country)) { // OTHER does not need to provide TIN
			Assert.isTrue(StringUtils.isNotBlank(tin));
		}

		return taxEntityDAO.findTaxEntitiesByTinAndCountry(tin, country);
	}

	@Override
	public Set<Long> findAllCompaniesWithMultipleApprovedTaxEntities(DateRange dateRange) {
		return taxEntityDAO.findAllCompaniesWithMultipleApprovedTaxEntities(dateRange);
	}

	@Override
	public List<UsaTaxEntity> findAllUsaApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxEntityDAO.findAllUsaApprovedTaxEntitiesByCompanyId(companyId);
	}

	@Override
	public Set<Long> getAllCompaniesWithFirstTaxEntityInPeriod(DateRange dateRange, int taxReportYearToExclude) {
		return taxEntityDAO.getAllCompaniesWithFirstTaxEntityInPeriodAndNoTaxReportForYear(dateRange,
			taxReportYearToExclude);
	}

	@Override
	public List<? extends AbstractTaxEntity> findAllApprovedTaxEntitiesByCompanyId(long companyId) {
		return taxEntityDAO.findAllApprovedTaxEntitiesByCompanyId(companyId);
	}


	@Override
	public AbstractTaxEntity saveTaxEntity(long userId, TaxEntityDTO taxEntityDTO) {
		User user = userDAO.get(userId);
		return saveTaxEntityForCompany(user.getCompany().getId(), taxEntityDTO);
	}

	@Override
	public void saveTaxEntity(AbstractTaxEntity abstractTaxEntity) {
		Assert.notNull(abstractTaxEntity);
		taxEntityDAO.saveOrUpdate(abstractTaxEntity);
	}

	@Override
	public List<Long> getAllIds() {
		return taxEntityDAO.getAllIds();
	}

	@Override
	public List<Long> getAllActivatedAccountIds() {
		return taxEntityDAO.getAllActivatedAccountIds();
	}

	@Override
	public <T extends AbstractTaxReport> Map<Long, String> getTaxIdToTaxNumberMapFromVault(
		final List<? extends AbstractTaxReport> taxReports) {

		// extract tax entities from earning detail reports
		final List<AbstractTaxEntity> taxEntities = new ArrayList<>();
		for (AbstractTaxReport taxReport : taxReports) {
			taxEntities.add(taxReport.getTaxEntity());
		}

		// partition tax entity list so we only attempt to get VAULT_MAX_MULTI_GET tax numbers at a time
		List<List<AbstractTaxEntity>> partitionedTaxEntities = ListUtils.partition(taxEntities, VAULT_MAX_MULTI_GET);

		// get taxEntityId -> taxNumber mappings for each partition
		final Map<Long, String> taxIdToTaxNumberMap = Maps.newHashMap();
		for(List<AbstractTaxEntity> taxEntityList : partitionedTaxEntities) {
			taxIdToTaxNumberMap.putAll(vaultHelper.mapEntityIdToFieldValue(taxEntityList, AbstractTaxEntity.class, "taxNumber"));
		}

		return taxIdToTaxNumberMap;
	}

	@Override
	public String getFormattedTaxNumber(final AbstractTaxEntity taxEntity, final String taxNumber) {

		if(taxEntity instanceof UsaTaxEntity) {
			return taxEntity.getBusinessFlag() ? StringUtilities.formatEin(taxNumber) : StringUtilities.formatSsn(taxNumber);
		}
		else if(taxEntity instanceof CanadaTaxEntity) {
			return taxEntity.getBusinessFlag() ? StringUtilities.formatCanadaBn(taxNumber) : StringUtilities.formatCanadaSin(taxNumber);
		}
		else if(taxEntity instanceof ForeignTaxEntity) {
			return StringUtilities.formatForeignTaxNumber(taxNumber);
		}
		else {
			logger.error("Attempting to format tax number for unknown tax entity type");
			return "";
		}
	}

	@Override
	public Tuple2<File, String> buildPdfForTaxForm(W9PdfPreviewApiDTO dto) throws IOException, DocumentException {
		String filename = String.format("%s_w9_%s.pdf",
			dto.getFullName().replaceAll("\\W+", ""),
			StringUtils.right(dto.getTaxNumber(), 4));

		File file = File.createTempFile(filename, "");
		Resource resource = resourceLoader.getResource(W9_TEMPLATE);
		PdfReader pdfReader = new PdfReader(resource.getFile().getAbsolutePath());
		PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(file));
		AcroFields form = stamper.getAcroFields();

		if (StringUtilities.isUsaTaxIdentificationNumber(dto.getTaxNumber())) {
			if (dto.isBusiness()) {
				form.setField(w9FieldMap.getProperty("EIN1"), dto.getTaxNumber().substring(0, 2));
				form.setField(w9FieldMap.getProperty("EIN2"), dto.getTaxNumber().substring(2));
			} else {
				form.setField(w9FieldMap.getProperty("SSN1"), dto.getTaxNumber().substring(0, 3));
				form.setField(w9FieldMap.getProperty("SSN2"), dto.getTaxNumber().substring(3, 5));
				form.setField(w9FieldMap.getProperty("SSN3"), dto.getTaxNumber().substring(5));
			}
		}
		if (dto.isBusiness()) {
			form.setField(w9FieldMap.getProperty("Name"), StringUtils.upperCase(dto.getCompanyName()));
		} else {
			form.setField(w9FieldMap.getProperty("Name"), StringUtils.upperCase(dto.getFullName()));
		}
		if (dto.getBusinessAsName() != null) {
			form.setField(w9FieldMap.getProperty("BusinessName"), StringUtils.upperCase(dto.getBusinessAsName()));
		}
		form.setField(w9FieldMap.getProperty("Address"), StringUtils.upperCase(dto.getAddress()));
		form.setField(w9FieldMap.getProperty("CityStateZip"), StringUtils.upperCase(String.format("%s %s %s",
			dto.getCity(),
			dto.getState(),
			dto.getPostalCode())));

		String typeCode = dto.getTaxEntityTypeCode().code();

		boolean isLLC = TaxEntityType.LLC_C_CORPORATION.equals(typeCode)
			|| TaxEntityType.LLC_S_CORPORATION.equals(typeCode)
			|| TaxEntityType.LLC_PARTNERSHIP.equals(typeCode);

		setW9CheckboxValue(form, "Individual", TaxEntityType.INDIVIDUAL.equals(typeCode));
		setW9CheckboxValue(form, "CCorporation", (TaxEntityType.CORP.equals(typeCode) || TaxEntityType.C_CORP.equals
			(typeCode)));
		setW9CheckboxValue(form, "SCorporation", TaxEntityType.S_CORP.equals(typeCode));
		setW9CheckboxValue(form, "Partner", TaxEntityType.PARTNER.equals(typeCode));
		setW9CheckboxValue(form, "LLC", isLLC);
		setW9CheckboxValue(form, "TrustEstate", TaxEntityType.TRUST.equals(typeCode));

		if (isLLC) {
			String llcCode = TaxEntityType.LLC_C_CORPORATION.equals(typeCode) ?
				"C" :
				TaxEntityType.LLC_PARTNERSHIP.equals(typeCode) ?
					"P" :
					TaxEntityType.LLC_S_CORPORATION.equals(typeCode) ?
						"S" :
						"";
			form.setField(w9FieldMap.getProperty("LLCType"), llcCode);
		}

		stamper.close();

		return new Tuple2<>(file, filename);
	}

	@Override
	public TaxEntityDTO convert(final TaxInfoSaveApiDTO dto) {
		final TaxEntityDTO result = new TaxEntityDTO();
		if (StringUtils.isNotBlank(dto.getCompanyName())) {
			result.setLastName(dto.getCompanyName());
			result.setTaxName(dto.getCompanyName());
		} else {
			result.setFirstName(dto.getFirstName());
			result.setMiddleName(dto.getMiddleName());
			result.setLastName(dto.getLastName());
			result.setTaxName(dto.getFullName());
		}
		if (dto.getEffectiveDate() != null && dto.getEffectiveDate() > 0) {
			result.setEffectiveDateStringFromCalendar(DateUtilities.getCalendarFromMillis(dto.getEffectiveDate()));
		}
		if (StringUtils.isNotBlank(dto.getSignature())) {
			result.setSignature(dto.getSignature());
			result.setSignatureDateStringFromCalendar(DateUtilities.getCalendarFromMillis(dto.getSignatureDate()));
		}
		if (AbstractTaxEntity.COUNTRY_CANADA.equals(dto.getCountry().code())) {
			result.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.NONE.code());
		} else {
			result.setTaxEntityTypeCode(dto.getTaxEntityTypeCode().code());
		}
		result.setAddress(dto.getAddress());
		result.setBusinessName(dto.getBusinessAsName());
		result.setBusinessNameFlag(StringUtils.isNotBlank(dto.getBusinessAsName()));
		result.setCity(dto.getCity());
		result.setState(dto.getState());
		result.setCountry(dto.getCountry().code());
		result.setTaxCountry(dto.getCountry().code());
		result.setPostalCode(dto.getPostalCode());
		result.setCountryOfIncorporation(dto.getCountryOfIncorporation());
		result.setTaxVerificationStatusCode(TaxVerificationStatusCodeEnum.UNVERIFIED.code());
		result.setTaxNumber(dto.getTaxNumber());
		result.setBusinessFlag(dto.isBusiness());
		result.setDeliveryPolicyFlag(dto.isAgreeToTerms());
		result.setForeignStatusAcceptedFlag(dto.isForeignStatusAccepted());

		return result;
	}

	@Override
	public Optional<TaxInfoApiDTO> convert(AbstractTaxEntity entity) {
		if (entity == null) {
			return Optional.absent();
		}

		final String taxNumber = vaultHelper.get(entity, "taxNumber", entity.getTaxNumber()).getValue();
		final String countryOfIncorporation =
			entity instanceof ForeignTaxEntity ? ((ForeignTaxEntity) entity).getCountryOfIncorporation() : null;
		final boolean foreignStatusAcceptedFlag =
			entity instanceof ForeignTaxEntity
				? BooleanUtils.isTrue(((ForeignTaxEntity) entity).getForeignStatusAcceptedFlag())
				: false;
		final String signature = entity instanceof UsaTaxEntity
			? ((UsaTaxEntity) entity).getSignature() : null;

		return Optional.of(TaxInfoApiDTO.builder()
			.setActive(BooleanUtils.isTrue(entity.getActiveFlag()))
			.setActiveDate(DateUtilities.getUnixTime(entity.getActiveDate()))
			.setAddress(entity.getAddress())
			.setAgreeToTerms(BooleanUtils.isTrue(entity.getDeliveryPolicyFlag()))
			.setBusiness(BooleanUtils.isTrue(entity.getBusinessFlag()))
			.setBusinessAsName(entity.getBusinessName())
			.setCity(entity.getCity())
			.setCompanyName(entity.getTaxName())
			.setCountry(CountryEnum.fromCode(entity.getCountry()))
			.setCountryOfIncorporation(countryOfIncorporation)
			.setEffectiveDate(DateUtilities.getUnixTime(entity.getEffectiveDate()))
			.setFirstName(entity.getFirstName())
			.setForeignStatusAccepted(foreignStatusAcceptedFlag)
			.setInactiveDate(DateUtilities.getUnixTime(entity.getInactiveDate()))
			.setLastName(entity.getLastName())
			.setMiddleName(entity.getMiddleName())
			.setPostalCode(entity.getPostalCode())
			.setSignature(signature)
			.setSignatureDate(DateUtilities.getUnixTime(entity.getSignedOn()))
			.setState(entity.getState())
			.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.fromCode(entity.getTaxEntityType().getCode()))
			.setTaxNumber(StringUtilities.showLastNCharacters(taxNumber, "*", 3))
			.setTaxVerificationStatusCode(TaxVerificationStatusCodeEnum.fromCode(entity.getStatus().getCode()))
			.build());
	}

	@Override
	public Optional<TaxInfoApiDTO> getTaxInfoForUser(long userId) {
		return convert(findActiveTaxEntity(userId));
	}

	@Override
	public Tuple2<File, String> buildPdfForTaxForm(final AbstractTaxEntity taxEntity) throws IOException, DocumentException {
		if (taxEntity == null || !(taxEntity instanceof UsaTaxEntity)) return null;

		final W9PdfPreviewApiDTO.Builder builder = W9PdfPreviewApiDTO.builder();
		TaxEntityDTO dto = TaxEntityDTO.toDTO(taxEntity);
		String taxNumber = vaultHelper.get(taxEntity, "taxNumber", taxEntity.getTaxNumberSanitized()).getValue();
		taxEntity.setTaxNumber(taxNumber);
		builder.setTaxNumber(taxNumber);

		if (StringUtilities.isUsaTaxIdentificationNumber(taxNumber)) {
			if (authenticationService.isMasquerading()) {
				builder.setTaxNumber(taxEntity.getSecureTaxNumber());
			}
		}

		builder.setAddress(dto.getAddress())
			.setBusiness(dto.getBusinessFlag())
			.setBusinessName(dto.getBusinessName())
			.setAddress(dto.getAddress())
			.setCity(dto.getCity())
			.setState(dto.getState())
			.setPostalCode(dto.getPostalCode())
			.setFirstName(dto.getFirstName())
			.setMiddleName(dto.getMiddleName())
			.setLastName(dto.getLastName())
			.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.fromCode(dto.getTaxEntityTypeCode()))
			.setTaxName(dto.getTaxName());

		return buildPdfForTaxForm(builder.build());
	}

	/**
	 *
	 * Sets the Appearance State value for a PDF checkbox (i.e. the off/on value)
	 *
	 * @param form
	 * @param isOn
	 */
	private void setW9CheckboxValue(AcroFields form, String key, boolean isOn) {
		if (!isOn) {
			return; // TODO: clean this up -- doesn't seem to work when you set the "Off" value
		}
		String fieldName = w9FieldMap.getProperty(key);
		if (StringUtils.isEmpty(fieldName)) {
			return;
		}
		String[] states = form.getAppearanceStates(fieldName);
		if (states == null || states.length < 2) return;
		try {
			int onIndex = "Off".equals(states[0]) ? 1 : 0;
			form.setField(fieldName, states[isOn ? onIndex : 1 - onIndex]);
		} catch (IOException e) {
			logger.error(e);
		} catch (DocumentException e) {
			logger.error(e);
		}
	}
}

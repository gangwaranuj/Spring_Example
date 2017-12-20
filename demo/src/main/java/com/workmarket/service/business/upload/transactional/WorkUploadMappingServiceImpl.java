package com.workmarket.service.business.upload.transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.work.dao.WorkUploadMappingDAO;
import com.workmarket.domains.work.dao.WorkUploadMappingGroupDAO;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.model.WorkUploadMapping;
import com.workmarket.domains.work.model.WorkUploadMappingGroup;
import com.workmarket.domains.work.model.WorkUploadMappingGroupPagination;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.work.uploader.DeleteMappingRequest;
import com.workmarket.thrift.work.uploader.FieldMapping;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.thrift.work.uploader.FindMappingsRequest;
import com.workmarket.thrift.work.uploader.FindMappingsResponse;
import com.workmarket.thrift.work.uploader.RenameMappingRequest;
import com.workmarket.thrift.work.uploader.SaveMappingRequest;
import com.workmarket.thrift.work.uploader.WorkUploadDuplicateMappingGroupNameException;
import com.workmarket.thrift.work.uploader.WorkUploadError;
import com.workmarket.thrift.work.uploader.WorkUploadErrorType;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkUploadMappingServiceImpl implements WorkUploadMappingService {

	private static final Log logger = LogFactory.getLog(WorkUploadMappingServiceImpl.class);

	@Autowired private WorkUploadMappingDAO mappingDAO;
	@Autowired private WorkUploadMappingGroupDAO mappingGroupDAO;
	@Autowired private WorkUploadColumnService columnService;
	@Autowired private WorkCustomFieldDAO customFieldDAO;
	@Autowired private AuthenticationService authenticationService;

	@Override
	public void deleteMapping(DeleteMappingRequest request) throws WorkUploadException {
		try {
			authenticationService.setCurrentUser(request.getUserNumber());

			WorkUploadMappingGroup mappingGroup = mappingGroupDAO.get(request.getMappingGroupId());

			Assert.notNull(mappingGroup, "Mapping does not exist.");
			Assert.isTrue(mappingGroup.getCompany().equals(authenticationService.getCurrentUser().getCompany()), "Not authorized.");

			mappingGroup.setDeleted(true);
		} catch (Exception e) {
			logger.error(e);
			throw new WorkUploadException();
		}
	}

	@Override
	public void renameMapping(RenameMappingRequest request) throws WorkUploadException {
		try {
			authenticationService.setCurrentUser(request.getUserNumber());

			WorkUploadMappingGroup mappingGroup = mappingGroupDAO.get(request.getMappingGroupId());

			Assert.notNull(mappingGroup, "Mapping does not exist.");
			Assert.isTrue(mappingGroup.getCompany().equals(authenticationService.getCurrentUser().getCompany()), "Not authorized.");

			mappingGroup.setName(request.getName());
		} catch (Exception e) {
			logger.error(e);
			throw new WorkUploadException();
		}
	}

	@Override
	public FindMappingsResponse findMappings(FindMappingsRequest request) throws WorkUploadException {
		try {
			WorkUploadMappingGroupPagination pagination = new WorkUploadMappingGroupPagination();
			pagination.setStartRow(request.getStartRow());
			pagination.setResultsLimit(request.getResultsLimit());

			pagination = mappingGroupDAO.findByCompanyId(request.getCompanyId(), pagination);

			List<FieldMappingGroup> tgroups = Lists.newArrayList();
			for (WorkUploadMappingGroup mappingGroup : pagination.getResults()) {
				tgroups.add(
					new FieldMappingGroup()
						.setId(mappingGroup.getId())
						.setName(mappingGroup.getName())
				);
			}
			return new FindMappingsResponse()
				.setMappingGroups(tgroups)
				.setNumResults(pagination.getRowCount());
		} catch (Exception e) {
			logger.error(e);
			throw new WorkUploadException();
		}
	}

	@Override
	public FieldMappingGroup saveMapping(SaveMappingRequest request) throws WorkUploadException, WorkUploadDuplicateMappingGroupNameException {
		try {
			authenticationService.setCurrentUser(request.getUserNumber());

			WorkUploadMappingGroup mappingGroup = new WorkUploadMappingGroup();
			mappingGroup.setName(request.getMappingGroup().getName());
			mappingGroup.setCompany(authenticationService.getCurrentUser().getCompany());

			mappingGroupDAO.saveOrUpdate(mappingGroup);

			for (FieldMapping m : request.getMappingGroup().getMappings()) {
				String derivedCode = m.getType().getCode();
				String typeCode = derivedCode;
				WorkCustomField customField = null;

				// TODO Figure out a way to stash this parsing into WorkUploadColumnType
				Pattern pattern = Pattern.compile("(\\w+):(\\d+)");
				Matcher matcher = pattern.matcher(derivedCode);
				if (matcher.matches()) {
					typeCode = matcher.group(1);
					customField = customFieldDAO.get(Long.valueOf(matcher.group(2)));
				}

				WorkUploadMapping mapping = new WorkUploadMapping();
				mapping.setMappingGroup(mappingGroup);
				mapping.setColumnName(m.getColumnName());
				mapping.setColumnIndex(m.getColumnIndex());
				mapping.setColumnType(WorkUploadColumnType.newInstance(typeCode));
				mapping.setCustomField(customField);

				mappingDAO.saveOrUpdate(mapping);
			}

			FieldMappingGroup g = (FieldMappingGroup)SerializationUtilities.clone(request.getMappingGroup());
			g.setId(mappingGroup.getId());

			return g;
		} catch (ConstraintViolationException e) {
			logger.error(e);
			throw new WorkUploadDuplicateMappingGroupNameException();
		} catch (Exception e) {
			logger.error(e);
			throw new WorkUploadException();
		}
	}

	@Override
	public FieldMappingGroup createMappingGroup(List<String[]> assns, Boolean isHeaderProvided) {

		List<String> headers = Arrays.asList(assns.get(0));
		// Get set of sample values to draw from.
		String[] sampleLine = assns.get(isHeaderProvided ? 1 : 0);

		List<FieldMapping> mappings = Lists.newArrayListWithCapacity(headers.size());
		for (int i = 0; i < headers.size(); i++) {
			String code = headers.get(i) == null ? null : headers.get(i).trim();
			FieldType t = new FieldType().setCode(columnService.getColumnTypeCode(code));
			FieldMapping mapping = new FieldMapping()
				.setType(t)
				.setColumnIndex(i)
				.setSampleValue(sampleLine[i]);
			if (isHeaderProvided) {
				mapping.setColumnName(headers.get(i));
			}

			mappings.add(mapping);
		}

		return new FieldMappingGroup().setMappings(mappings);
	}

	@Override
	public ImmutableList<Map> getProjectedMappings(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();

		WorkUploadMappingGroupPagination pagination = new WorkUploadMappingGroupPagination();
		pagination.setProjection(fields);

		mappingGroupDAO.findByCompanyId(companyId, pagination);

		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldMappingGroup getMappingGroupById(WorkUploadRequest request, List<String[]> assns, WorkUploadResponse response) {

		List<String> headers = Arrays.asList(assns.get(0));
		// Get set of sample values to draw from.
		String[] sampleLine = assns.get(request.isHeadersProvided() ? 1 : 0);

		// An existing mapping was provided. Use as reference point.
		List<WorkUploadMapping> existing = mappingDAO.findByMappingGroupId(request.getMappingGroupId());
		List<FieldMapping> mappings = Lists.newArrayList();

		List<String> savedColumnNames = CollectionUtilities.newListPropertyProjection(existing, "columnName");
		List<String> remainingColumnNames = Lists.newArrayList(savedColumnNames);
		if (StringUtilities.any(savedColumnNames)) {
			if (request.isHeadersProvided()) {
				for (int i = 0; i < headers.size(); i++) {
					String colname = headers.get(i);
					FieldMapping mapping = new FieldMapping()
						.setColumnIndex(i)
						.setColumnName(colname);
					String sampleValue = StringUtilities.stripHTML(sampleLine[i]);
					sampleValue = StringUtils.abbreviate(sampleValue, 400);
					mapping.setSampleValue(sampleValue);
					int nameIndex = savedColumnNames.indexOf(colname);
					if (nameIndex == -1) {
						// Column not found - set this column to ignore and remind the user to specify what this column is.
						mappings.add(mapping.setType(new FieldType().setCode("ignore")));
						response.addToWarnings(new WorkUploadError()
							.setErrorType(WorkUploadErrorType.MISSING_DATA)
							.setViolation(new ConstraintViolation()
							.setWhy(colname + " is a new column not saved in this mapping. " +
								"Specify the column type using the dropdowns below.")));
					} else {
						mappings.add(mapping.setType(new FieldType()
							.setCode(existing.get(nameIndex).getDerivedTypeCode())));
						remainingColumnNames.remove(colname);
					}
				}
				for (String columnNotFound : remainingColumnNames) {
					response.addToWarnings(new WorkUploadError()
						.setErrorType(WorkUploadErrorType.MAPPING_COLUMN_NAME_NOT_FOUND)
						.setViolation(new ConstraintViolation()
						.setWhy(columnNotFound + " was not found on your upload spreadsheet.")));
				}
				WorkUploadMappingGroup existingGroup = mappingGroupDAO.findByMappingGroupId(request.getMappingGroupId());
				return new FieldMappingGroup()
					.setId(existingGroup.getId())
					.setName(existingGroup.getName())
					.setMappings(mappings);
			} else {
				response.addToWarnings(new WorkUploadError()
					.setErrorType(WorkUploadErrorType.MAPPING_COLUMN_NAMING_MISMATCH)
					.setViolation(new ConstraintViolation()
					.setWhy("Column names were saved in your mapping, but your spreadsheet has no header row. " +
						"Please be sure the sample values match the column types selected in the dropdown.")));
			}
		}
		// Load saved mapping by column index.
		if (existing.size() != headers.size()) {
			String why;
			if (headers.size() > existing.size())
				why = "There are more columns on your spreadsheet than are saved in your mapping. " +
						"Please remember to select the appropriate column types for these columns from the dropdowns below.";
			else
				why = "WARNING: There are more columns saved in your mapping than there are in your spreadsheet. " +
						"This means that some saved columns WILL BE MISSING from the upload if you proceed. " +
						"Please be sure you have selected the correct mapping for your spreadsheet!";
			response.addToWarnings(new WorkUploadError()
				.setViolation(new ConstraintViolation().setWhy(why))
				.setErrorType(WorkUploadErrorType.MAPPING_COLUMN_LENGTH_MISMATCH));
		}

		for (int i = 0; i < headers.size(); i++) {
			FieldType t = new FieldType()
			.setCode((existing.size() - 1 > i) ?
					"ignore" :
						existing.get(i).getDerivedTypeCode());
			mappings.add(new FieldMapping()
				.setType(t)
				.setColumnIndex(i)
				.setSampleValue(headers.get(i)));
		}
		WorkUploadMappingGroup existingGroup = mappingGroupDAO.findByMappingGroupId(request.getMappingGroupId());
		return new FieldMappingGroup()
			.setId(existingGroup.getId())
			.setName(existingGroup.getName())
			.setMappings(mappings);
	}

	@Override
	public WorkUploadMappingGroup getByMappingGroupId(Long id) {
		WorkUploadMappingGroup mappingGroup = mappingGroupDAO.findBy(
			"id", id,
			"company", authenticationService.getCurrentUser().getCompany()
		);

		if (mappingGroup != null) {
			Hibernate.initialize(mappingGroup.getMappings());
		}

		return mappingGroup;
	}

	@Override
	public WorkUploadMappingGroup saveMappingGroup(WorkUploadMappingGroup mappingGroup) {
		Set<WorkUploadMapping> mappings = mappingGroup.getMappings();

		mappingGroup.setMappings(null);
		mappingGroupDAO.saveOrUpdate(mappingGroup);

		for (WorkUploadMapping mapping : mappings) {
			mapping.setMappingGroup(mappingGroup);
			mappingDAO.saveOrUpdate(mapping);
		}

		mappingGroup.setMappings(mappings);

		return mappingGroup;
	}
}

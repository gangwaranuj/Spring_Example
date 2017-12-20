package com.workmarket.reporting.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.data.export.adapter.CSVAdapter;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.reporting.model.EvidenceReportRow;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Service
public class EvidenceReportServiceImpl implements EvidenceReportService {

	@Autowired EvidenceReportMapperService evidenceReportMapperService;
	@Autowired UserGroupService userGroupService;
	@Autowired ScreeningService screeningService;
	@Autowired PDFTemplateFactory PDFTemplateFactory;
	@Autowired TemplateService templateService;
	@Autowired EventRouter eventRouter;
	@Autowired EventFactory eventFactory;
	@Autowired NotificationDispatcher notificationDispatcher;
	@Autowired NotificationTemplateFactory notificationTemplateFactory;
	@Autowired AuthenticationService authenticationService;
	@Autowired AssetBundlerQueue assetBundlerQueue;
	@Autowired UserService userService;



	@Override
	public List<EvidenceReport> fetchDrugTestByGroupId(Long groupId){
		return fetchEvidenceReportByGroupId(groupId,Screening.DRUG_TEST_TYPE);

	}

	@Override
	public List<EvidenceReport> fetchBackgroundCheckByGroupId(Long groupId){
		return fetchEvidenceReportByGroupId(groupId, Screening.BACKGROUND_CHECK_TYPE);
	}

	@Override
	public List<EvidenceReport> fetchEvidenceReportByGroupId(Long groupId,String screeningType){
		Assert.notNull(groupId);
		List<EvidenceReport> evidenceReports = Lists.newArrayList();
		List<Long> userIds = userGroupService.getAllActiveGroupMemberIds(groupId);

		if (CollectionUtils.isEmpty(userIds)) {
			return evidenceReports;
		}

		evidenceReports = screeningService.findBulkMostRecentEvidenceReport(userIds,screeningType);

		if (evidenceReports == null) {
			return new ArrayList<EvidenceReport>();
		}

		return evidenceReports;
	}

	@Override
	public List<EvidenceReportRow> generateEvidenceReportByGroup(Long groupId,String screeningType) {
		Assert.notNull(groupId);
		return evidenceReportMapperService.mapEvidenceReportToDataTable(groupId,
				fetchEvidenceReportByGroupId(groupId,screeningType),
				screeningType);
	}

	@Override
	public Optional<String> generateBackgroundCheckPDF(Long userId, boolean generatePDF) {
		Assert.notNull(userId);
		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentBackgroundCheck(userId));
		User u = userService.getUser(userId);
		backgroundCheck.setUser(u);
		if(backgroundCheck == null) return Optional.absent();
		if(generatePDF==true){
			return Optional.of(templateService.renderPDFTemplate(
					PDFTemplateFactory.newBackgroundEvidenceReportPDFTemplate(backgroundCheck,
							"evidence_report_" + backgroundCheck.getId())));
		}
		return Optional.absent();
	}

	//send an email with all the background checks for the members of the group
	@Override
	public void bulkDownloadEvidenceReport(String toEmail,Long groupId,String screeningType){
		Assert.hasText(toEmail);
		Assert.notNull(groupId);
		eventRouter.sendEvent(eventFactory.buildDownloadCertificatesEvent(
				toEmail,
				groupId,
				screeningType
		));
	}

	//handler for async call from above from queue
	//fetches background checks, builds them in pdf template and emails to user
	@Override
	public void bulkDownloadEvidenceReportHandler(String toEmail,Long groupId,String screeningType) {
		Assert.notNull(groupId);
		Assert.hasText(toEmail);
		List<? extends EvidenceReport> reports = null;
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		switch(screeningType){
			case "background":
				List<EvidenceReport> evidenceReports = fetchEvidenceReportByGroupId(groupId,screeningType);
				PDFTemplate  pdfTemplate = PDFTemplateFactory.newBatchBackgroundEvidenceReportPDFTemplate(evidenceReports,
						groupId);
				notificationDispatcher.dispatchEmail(
						notificationTemplateFactory.buildBatchEvidenceReportTemplate(toEmail,
								pdfTemplate));
				return;
		}

		List<String> uuids = Lists.newArrayList();
		for(EvidenceReport report : reports){
			uuids.add(report.getUuid());
		}
		assetBundlerQueue.bundleAssetsForUser(uuids,userService.findUserIdByEmail(toEmail));
	}

	//send a csv in email with the list of users that have background checks
	@Override
	public void exportToCSV(String toEmail,Long groupId,String screeningType){
		Assert.notNull(groupId);
		Assert.hasText(toEmail);
		eventRouter.sendEvent(eventFactory.buildExportEvidenceReportEvent(toEmail,groupId,screeningType));
	}


	@Override
	public void exportToCSVHandler(String toEmail, Long groupId,String screeningType){
		Assert.notNull(groupId);
		Assert.hasText(toEmail);
		String filename = null;
		switch(screeningType){
			case Screening.BACKGROUND_CHECK_TYPE:
				filename = generateBgCheckCSV(groupId, screeningType);
				break;
		}

		if(StringUtils.isEmpty(filename))  return; //should we send an email notifying user that we could not send email?
		FileDTO attachment = new FileDTO();
		attachment.setMimeType(MimeType.TEXT_CSV.getMimeType());
		attachment.setSourceFilePath(filename);
		attachment.setName(new File(filename).getName());
		notificationDispatcher.dispatchEmail(
				notificationTemplateFactory.buildEvidenceReportCSVTemplate(toEmail,attachment)
		);
	}

	private String generateBgCheckCSV(long groupId, String screeningType){
		List<EvidenceReport> evidenceReports = fetchEvidenceReportByGroupId(groupId,screeningType);
		return createFileFromRows(groupId,
				evidenceReportMapperService.mapEvidenceReportToCSV(evidenceReports));
	}

	private String createFileFromRows(Long groupId, List<String[]> rows) {
		Assert.notNull(groupId);
		Assert.notNull(rows);
		String filename = Constants.TEMPORARY_FILE_DIRECTORY +  "evidence_report_" + groupId + ".csv";
		try{
			Writer writer = new FileWriter(filename);
			CSVAdapter adapter = new CSVAdapter();
			adapter.export(writer, rows);
		}catch(Exception e){
			return new String();
		}
		return filename;
	}
}

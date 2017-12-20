package com.workmarket.web.controllers.reports;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.reporting.model.EvidenceReportGroupFilter;
import com.workmarket.reporting.model.EvidenceReportRow;
import com.workmarket.reporting.service.EvidenceReportService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports/evidence")
@PreAuthorize("hasAnyRole('PERMISSION_REPORTMYWORK', 'PERMISSION_REPORTCOWORK') AND !principal.companyIsLocked")
public class EvidenceReportsController extends BaseController {

	@Autowired EvidenceReportService evidenceReportService;
	@Autowired UserGroupService groupService;
	@Autowired AuthenticationService authenticationService;
	@Autowired AuthorizationService authorizationService;

	@RequestMapping(value = "/fetch_groups.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getGroups(Model model) {
		UserGroupPagination pagination = new UserGroupPagination(true);
		pagination = groupService.findAllGroupsByCompanyId(authenticationService.getCurrentUser().getCompany().getId(),pagination);
		List<EvidenceReportGroupFilter> groups = Lists.newArrayList();
		for (UserGroup g : pagination.getResults()) {
			groups.add(new EvidenceReportGroupFilter(g.getId(),g.getName()));
		}
		model.addAttribute("response",groups);
	}

	@RequestMapping(value="/backgroundcheck", method = RequestMethod.GET)
	public String getBackgroundCheckEvidenceReport(Model model) {
		model.addAttribute("recipientEmail", getEmail(getCurrentUser()));
		model.addAttribute("screeningType", Screening.BACKGROUND_CHECK_TYPE);
		model.addAttribute("reportTitle","Background Check");
		return "web/pages/reports/evidence/evidencereport";
	}

	@RequestMapping(value="/drugtest", method= RequestMethod.GET)
	public String getDrugTestEvidenceReport(Model model) {
		model.addAttribute("recipientEmail", getEmail(getCurrentUser()));
		model.addAttribute("screeningType", Screening.DRUG_TEST_TYPE);
		model.addAttribute("reportTitle","Drug Test");
		return "web/pages/reports/evidence/evidencereport";
	}

	@RequestMapping(value="/certificationreport", method= RequestMethod.GET)
	public String getCertificationEvidenceReport(Model model){
		model.addAttribute("recipientEmail", getEmail(getCurrentUser()));
		model.addAttribute("reportTitle","Certifications");
		model.addAttribute("screeningType", Screening.CERTIFICATION);
		return "web/pages/reports/evidence/certificationreport";
	}

	@RequestMapping(value="/licensereport", method= RequestMethod.GET)
	public String getLicenseEvidenceReport(Model model){
		model.addAttribute("recipientEmail", getEmail(getCurrentUser()));
		model.addAttribute("reportTitle","Licenses");
		model.addAttribute("screeningType", Screening.LICENSE);
		return "web/pages/reports/evidence/licensereport";
	}

	@RequestMapping(value="/insurancereport", method= RequestMethod.GET)
	public String getInsuranceEvidenceReport(Model model){
		model.addAttribute("recipientEmail", getEmail(getCurrentUser()));
		model.addAttribute("reportTitle","Insurance");
		model.addAttribute("screeningType", Screening.INSURANCE);
		return "web/pages/reports/evidence/insurancereport";
	}

	@RequestMapping(value = "/report_data.json",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getEvidenceReportData(@RequestParam Long groupId, @RequestParam String screeningType, HttpServletRequest request, Model model) {
		checkGroupBelongsToCurrentUser(groupId);
		DataTablesRequest dataTablesRequest = DataTablesRequest.newInstance(request);
		List<EvidenceReportRow> evidenceReport = evidenceReportService.generateEvidenceReportByGroup(groupId, screeningType);
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(dataTablesRequest);
		for(EvidenceReportRow row : evidenceReport){
			response.addRow(row.getRow());
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(value="/generatepdf", method=RequestMethod.GET)
	public ModelAndView printBackgroundEvidenceReport(@RequestParam Long userId, @RequestParam Long groupId) {
		checkGroupBelongsToCurrentUser(groupId);
		checkIfResourceIsInGroup(userId, groupId);
		HTML2PDFView view = new HTML2PDFView(StringUtils.EMPTY);
		Optional<String> evidenceReport = evidenceReportService.generateBackgroundCheckPDF(userId, true);
		if (evidenceReport.isPresent()){
			view.setHtml(evidenceReport.get());
		}
		return new ModelAndView(view);
	}

	@RequestMapping(value="/exporttocsv.json", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void exportToCSV(@RequestParam Long groupId, @RequestParam String screeningType) {
		checkGroupBelongsToCurrentUser(groupId);
		evidenceReportService.exportToCSV(getEmail(getCurrentUser()), groupId,screeningType);
	}
	
	@RequestMapping(value="/downloadcertificates.json", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadCertificates(@RequestParam Long groupId, @RequestParam String screeningType) {
		checkGroupBelongsToCurrentUser(groupId);
		evidenceReportService.bulkDownloadEvidenceReport(getEmail(getCurrentUser()), groupId,screeningType);
	}

	private String getEmail(ExtendedUserDetails user) throws HttpException401{
		if(user == null){
			throw new HttpException401();
		}
		return user.isMasquerading() ? user.getMasqueradeUser().getEmail() : user.getEmail();
	}

	//Authorization Methods, so user's can only see group details for which they are owners
	//and can only see background checks for resources that belong to their groups
	private void checkIfResourceIsInGroup(Long userId, Long groupId) throws HttpException401{
		if (groupService.isUserMemberOfGroup(groupId, userId)) {
			return;
		}
		throw new HttpException401();
	}

	private void checkGroupBelongsToCurrentUser(Long groupId) throws HttpException401 {
		User user = authenticationService.getCurrentUser();
		UserGroup group =groupService.findGroupById(groupId);
		if(group == null) throw new HttpException401();
		if(user.getCompany().getId().equals(group.getCompany().getId())){
			return;
		}
		throw new HttpException401();
	}
}

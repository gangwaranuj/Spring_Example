package com.workmarket.domains.work.service.validator;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeAssociationDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by nick on 1/24/14 2:17 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WorkSaveRequestValidatorIT extends BaseServiceIT {

	@Autowired WorkSubStatusService workSubStatusService;
	@Autowired TWorkFacadeService tWorkFacadeService;
	@Autowired WorkSubStatusTypeAssociationDAO workSubStatusTypeAssociationDAO;
	@Autowired WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired MessageBundleHelper messageHelper;

	@Test
	@Transactional
	public void validateWork_WorkWithValidLabelScopes_Success() throws Exception {

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		assertNotNull(workSubStatusType);
		workSubStatusType.setWorkScope(Sets.newHashSet(
				WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT),
				WorkStatusType.newWorkStatusType(WorkStatusType.SENT),
				WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE)));

		User employee = newWMEmployee();
		Work work = newThriftWork(employee);
		assertNotNull(work);

		work = tWorkFacadeService.findWorkDetail(new WorkRequest()
				.setWorkNumber(work.getWorkNumber())
				.setUserId(employee.getId()))
				.getWork();
		workSaveRequestValidator.validateWork(new WorkSaveRequest(employee.getId(), work));
	}

	@Test
	@Transactional
	public void validateWork_WorkWithInvalidLabelScopes_Success() throws Exception {

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		assertNotNull(workSubStatusType);
		workSubStatusType.setWorkScope(Sets.newHashSet(
				WorkStatusType.newWorkStatusType(WorkStatusType.SENT),
				WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE)));

		User employee = newWMEmployee();
		Work work = newThriftWork(employee);
		assertNotNull(work);

		work = tWorkFacadeService.findWorkDetail(new WorkRequest()
				.setWorkNumber(work.getWorkNumber())
				.setUserId(employee.getId()))
				.getWork();

		try {
			workSaveRequestValidator.validateWork(new WorkSaveRequest(employee.getId(), work)
				.setLabelId(workSubStatusType.getId()));
			fail();
		} catch (ValidationException e) {
			assertEquals("label", e.getErrors().get(0).getProperty());
		}
	}

	@Test
	public void validateWork_NoTransaction_Success() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work = newThriftWork(employee);
		assertNotNull(work);

		work = tWorkFacadeService.findWorkDetail(new WorkRequest()
			.setWorkNumber(work.getWorkNumber())
			.setUserId(employee.getId()))
			.getWork();

		workSaveRequestValidator.validateWork(new WorkSaveRequest(employee.getId(), work));
	}

	@Test
	public void validateWorkWithMissingRequiredUniqueId_NoTransaction_FailsValidation() throws Exception {

		User employee = newEmployeeWithCashBalance();

		String uniqueExternalIdDisplayName = "ticket number";

		CompanyPreference companyPreference = companyService.getCompanyPreference(employee.getCompany().getId());
		companyPreference.setExternalIdActive(true);
		companyPreference.setExternalIdDisplayName(uniqueExternalIdDisplayName);
		companyPreference.setExternalIdVersion(1);
		companyService.updateCompanyPreference(companyPreference);

		Work work = newThriftWork(employee);
		assertNotNull(work);

		work = tWorkFacadeService.findWorkDetail(new WorkRequest()
			.setWorkNumber(work.getWorkNumber())
			.setUserId(employee.getId()))
			.getWork();

		try {
			workSaveRequestValidator.validateWork(new WorkSaveRequest(employee.getId(), work));
			fail();
		} catch (ValidationException e) {
			assertEquals(uniqueExternalIdDisplayName, e.getErrors().get(0).getProperty());
		}
	}

	@Test
	public void validateWorkWithRequiredUniqueId_NoTransaction_Success() throws Exception {

		User employee = newEmployeeWithCashBalance();

		String uniqueExternalIdDisplayName = "ticket number";
		String uniqueExternalIdValue = "uniquevalue";

		CompanyPreference companyPreference = companyService.getCompanyPreference(employee.getCompany().getId());
		companyPreference.setExternalIdActive(true);
		companyPreference.setExternalIdDisplayName(uniqueExternalIdDisplayName);
		companyPreference.setExternalIdVersion(1);
		companyService.updateCompanyPreference(companyPreference);

		WorkSaveRequest workSaveRequest = newWorkSaveRequest(employee);
		workSaveRequest.getWork().setUniqueExternalIdValue(uniqueExternalIdValue);
		Work work = tWorkService.saveOrUpdateWorkDraft(workSaveRequest).getWork();

		assertNotNull(work);

		work = tWorkFacadeService.findWorkDetail(new WorkRequest()
			.setWorkNumber(work.getWorkNumber())
			.setUserId(employee.getId()))
			.getWork();

		assertEquals(uniqueExternalIdValue, work.getUniqueExternalIdValue());
	}

	@Test
	public void validateWork_withInvalidPaymentTerms_FailsValidation() throws Exception {
		User employee = newEmployeeWithCashBalance();
		WorkSaveRequest workSaveRequest = newWorkSaveRequest(employee);
		workSaveRequest.getWork().getConfiguration().setPaymentTermsDays(Constants.MAX_PAYMENT_TERMS_DAYS + 1);

		try {
			workSaveRequestValidator.validateWork(workSaveRequest);
			fail();
		} catch (ValidationException e) {
			assertEquals("paymentTermsDays", e.getErrors().get(0).getProperty());
		}
	}
}

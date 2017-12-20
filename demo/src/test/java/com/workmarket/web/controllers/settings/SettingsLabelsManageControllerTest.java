package com.workmarket.web.controllers.settings;

import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import com.workmarket.web.forms.mmw.LabelsManageForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsLabelsManageControllerTest extends BaseControllerUnitTest {

	private static final String OTHER = "other";
	private static final boolean RESOURCE_VISIBLE = true;
	private static final boolean RESOURCE_NOT_VISIBLE = true;
	private static final boolean RESOURCE_EDITABLE = true;
	private static final boolean RESOURCE_NOT_EDITABLE = true;
	private static final boolean NOTIFY_CLIENT_ENABLED = true;
	private static final boolean NOTIFY_CLIENT_DISABLED = false;
	private static final boolean NOTIFY_RESOURCE_ENABLED = true;
	private static final boolean NOTIFY_RESOURCE_DISABLED = false;

	@Mock private MessageBundleHelper messageHelper;
	@Mock private WorkSubStatusService workSubStatusService;
	@Mock protected SecurityContextFacade securityContextFacade;

	@InjectMocks SettingsLabelsManageController controller;

	final private static long WORK_SUB_STATUS_ID = 1L;

	@Before
	public void setup() {
		init(securityContextFacade);
	}

	@Test
	public void populateLabelsManageForm_nullId_returnsNewForm() {
		LabelsManageForm result = controller.populateLabelsManageForm(null);

		assertNull(result.getWorkSubStatusTypeId());
	}

	@Test
	public void populateLabelsManageForm_nullWorkSubStatus_returnsNull() {
		when(workSubStatusService.findCustomWorkSubStatusByCompany(anyLong(), anyLong())).thenReturn(null);

		assertNull(controller.populateLabelsManageForm(WORK_SUB_STATUS_ID));
	}

	@Test
	public void resourceEditableTrue_ifViewEditResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_NOT_EDITABLE, LabelsManageForm.VIEW_EDIT);

		controller.saveLabelsManage(form);

		assertTrue(form.isResourceEditable());
	}

	@Test
	public void resourceEditableFalse_ifViewResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_NOT_EDITABLE, LabelsManageForm.VIEW);

		controller.saveLabelsManage(form);

		assertFalse(form.isResourceEditable());
	}

	@Test
	public void resourceEditableFalse_ifOtherResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_EDITABLE, OTHER);

		controller.saveLabelsManage(form);

		assertFalse(form.isResourceEditable());
	}

	@Test
	public void resourceVisibleTrue_ifViewEditResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_NOT_VISIBLE, LabelsManageForm.VIEW_EDIT);

		controller.saveLabelsManage(form);

		assertTrue(form.getResourceVisible());
	}

	@Test
	public void resourceVisibleTrue_ifViewResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_NOT_VISIBLE, LabelsManageForm.VIEW);

		controller.saveLabelsManage(form);

		assertTrue(form.getResourceVisible());
	}

	@Test
	public void resourceVisibleFalse_ifOtherResourceAccess() {
		LabelsManageForm form = initFormForResourceEditable(RESOURCE_VISIBLE, OTHER);

		controller.saveLabelsManage(form);

		assertFalse(form.getResourceVisible());
	}

	@Test
	public void ifNotifyIo_notifyClientEnabled_notifyResourceDisabled() {
		LabelsManageForm form =
			initFormForNotify(LabelsManageForm.IO, NOTIFY_CLIENT_DISABLED, NOTIFY_RESOURCE_ENABLED);

		controller.saveLabelsManage(form);

		assertTrue(form.isNotifyClientEnabled());
		assertFalse(form.isNotifyResourceEnabled());
	}

	@Test
	public void ifNotifyR_notifyClientDisabled_notifyResourceEnabled() {
		LabelsManageForm form =
			initFormForNotify(LabelsManageForm.R, NOTIFY_CLIENT_ENABLED, NOTIFY_RESOURCE_DISABLED);

		controller.saveLabelsManage(form);

		assertFalse(form.isNotifyClientEnabled());
		assertTrue(form.isNotifyResourceEnabled());
	}

	@Test
	public void ifNotifyIor_notifyClientEnabled_notifyResourceEnabled() {
		LabelsManageForm form =
			initFormForNotify(LabelsManageForm.IO_R, NOTIFY_CLIENT_DISABLED, NOTIFY_RESOURCE_DISABLED);

		controller.saveLabelsManage(form);

		assertTrue(form.isNotifyClientEnabled());
		assertTrue(form.isNotifyResourceEnabled());
	}

	@Test
	public void ifNotifyOther_notifyClientDisabled_notifyResourceDisabled() {
		LabelsManageForm form = initFormForNotify(OTHER, NOTIFY_CLIENT_ENABLED, NOTIFY_RESOURCE_ENABLED);

		controller.saveLabelsManage(form);

		assertFalse(form.isNotifyClientEnabled());
		assertFalse(form.isNotifyResourceEnabled());
	}

	private LabelsManageForm initFormForResourceEditable(boolean resourceEditable, String viewEdit) {
		LabelsManageForm form = initForm();
		form.setResourceEditable(resourceEditable);
		form.setResourceAccess(viewEdit);
		return form;
	}

	private LabelsManageForm initFormForNotify(String notify, boolean notifyClientEnabled, boolean notifyResourceEnabled) {
		LabelsManageForm form = initForm();
		form.setNotify(notify);
		form.setNotifyClientEnabled(notifyClientEnabled);
		form.setNotifyResourceEnabled(notifyResourceEnabled);
		return form;
	}

	private LabelsManageForm initForm() {
		LabelsManageForm form = new LabelsManageForm();
		form.setDescription("some description");
		return form;
	}


}

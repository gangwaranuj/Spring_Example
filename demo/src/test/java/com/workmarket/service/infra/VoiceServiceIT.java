package com.workmarket.service.infra;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.VoiceResponseDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.VoiceService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.utility.StringUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VoiceServiceIT extends BaseServiceIT {

	@Autowired private VoiceService voiceService;
	@Autowired private LaneService laneService;
	@Autowired private WorkService workService;
	@Autowired private ProfileService profileService;

	// Checkin

	@Test
	public void workIVRFromUnknownNumberRedirectToClientServicesFromUserIDPrompt() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work1 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		Work work2 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work1.getBuyer().getCompany().getId());
		laneService.addUserToCompanyLane2(resource.getId(), work2.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work1.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));
		workRoutingService.addToWorkResources(work2.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));

		workService.acceptWork(resource.getId(), work1.getId());
		workService.acceptWork(resource.getId(), work2.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);

		assertEquals(true, response.contains("enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		responseDTO.setMsg("0");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Transferring to a client service representative."));
	}

	@Test
	public void workIVRFromUnknownNumberRedirectToClientServicesFromWorkIDPrompt() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work1 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		Work work2 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work1.getBuyer().getCompany().getId());
		laneService.addUserToCompanyLane2(resource.getId(), work2.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work1.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));
		workRoutingService.addToWorkResources(work2.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));

		workService.acceptWork(resource.getId(), work1.getId());
		workService.acceptWork(resource.getId(), work2.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("enter an active assignment ID"));

		responseDTO.setMsg("0");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Transferring to a client service representative."));
	}

	@Test
	public void workIVRFromUnknownNumberRedirectToClientServicesFromMenuPrompt() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work1 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		Work work2 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work1.getBuyer().getCompany().getId());
		laneService.addUserToCompanyLane2(resource.getId(), work2.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work1.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));
		workRoutingService.addToWorkResources(work2.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));

		workService.acceptWork(resource.getId(), work1.getId());
		workService.acceptWork(resource.getId(), work2.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());
		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("enter an active assignment ID"));

		responseDTO.setMsg(work1.getWorkNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Please listen to the following options"));

		responseDTO.setMsg("0");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Transferring to a client service representative."));
	}

	@Test
	public void workIVRCheckInFromUnknownNumberWithSingleWork() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("listen to the following options"));

		responseDTO.setMsg("11");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("I'm sorry, that status code isn't available."));
		//assertEquals(true, response.contains("You have no active WorkMarket assignments."));
	}

	@Test
	public void workIVRCheckOutFromUnknownNumberWithSingleWork() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("listen to the following options"));

		responseDTO.setMsg("22");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("I'm sorry, that status code isn't available."));
	}

	@Test
	public void workIVRAbandonFromUnknownNumberWithSingleWork() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("listen to the following options"));

		responseDTO.setMsg("33");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("I'm sorry, that status code isn't available."));
	}

	@Test
	public void workIVRInvalidOptionFromUnknownNumberWithSingleWork() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("listen to the following options"));

		responseDTO.setMsg("999");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("I'm sorry, that status code isn't available."));
	}

	@Test
	public void workIVRCheckInFromUnknownNumberWithMultipleWork() throws Exception {

		User employee = newEmployeeWithCashBalance();
		Work work1 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		Work work2 = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(resource.getId(), work1.getBuyer().getCompany().getId());
		laneService.addUserToCompanyLane2(resource.getId(), work2.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work1.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));
		workRoutingService.addToWorkResources(work2.getWorkNumber(), Sets.newHashSet(resource.getUserNumber(), resource.getUserNumber()));

		workService.acceptWork(resource.getId(), work1.getId());
		workService.acceptWork(resource.getId(), work2.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Please enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("enter an active assignment ID"));

		responseDTO.setMsg(work1.getWorkNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Please listen to the following options"));

		// this status code is removed from IVR
		responseDTO.setMsg("11");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("I'm sorry, that status code isn't available."));
	}

	@Test
	public void workIVRCheckInFromUnknownNumberWithoutWork() throws Exception {
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);

		assertEquals(true, response.contains("enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("You have no active WorkMarket assignments."));
	}

	@Test
	public void workIVRCheckInFromKnownNumberWithSingleWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();
		profileService.updateProfileProperties(resource.getId(), workPhoneParams());

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback
		Profile profile = profileService.findProfile(resource.getId());
		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber(StringUtilities.formatE164PhoneNumber(profile.getWorkPhone()));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Hello " + resource.getFirstName()));

		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("listen to the following options"));

		responseDTO.setMsg("11");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("that status code isn't available"));
		//assertEquals(true, response.contains("assignment status has been updated"));
	}

	@Test
	public void workIVRCheckInFromKnownNumberWithoutWork() throws Exception {
		User resource = newContractorIndependentLane4ReadyWithCashBalance();
		profileService.updateProfileProperties(resource.getId(), workPhoneParams());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback
		Profile profile = profileService.findProfile(resource.getId());
		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber(StringUtilities.formatE164PhoneNumber(profile.getWorkPhone()));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Hello " + resource.getFirstName()));


		responseDTO.setMsg(resource.getUserNumber());

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("You have no active WorkMarket assignments."));
	}

	private Map<String, String> workPhoneParams() {
		Map<String, String> params = Maps.newHashMap();
		params.put("workPhone", "2" + RandomUtilities.generateNumericString(9));
		return params;
	}

	@Test
	public void workIVRCheckInFromKnownNumberFailsPromptsHangup() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		profileService.updateProfileProperties(resource.getId(), workPhoneParams());

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback
		Profile profile = profileService.findProfile(resource.getId());
		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber(StringUtilities.formatE164PhoneNumber(profile.getWorkPhone()));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Hello " + resource.getFirstName()));

		for (int i = 0; i < Constants.VOICE_CALL_FAILED_PROMPTS_CUTOFF; i++) {
			responseDTO.setMsg(resource.getUserNumber() + "1234");

			response = voiceService.respond(responseDTO);

			assertEquals(true, response.contains("Please try again."));
		}

		responseDTO.setMsg(resource.getUserNumber() + "1234");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("that's not a valid or active ID"));
	}

	@Test
	public void workIVRCheckInFromUnknownNumberFailsOnceThenSucceeds() throws Exception {

		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber("2" + RandomUtilities.generateNumericString(9));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("enter your WorkMarket user ID"));

		responseDTO.setMsg(resource.getUserNumber() + "1234");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Please try again."));

		responseDTO.setMsg(resource.getUserNumber());
		responseDTO.setRedirectToSubStatus("start");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("have no active WorkMarket assignments"));
	}

	@Test
	public void workIVRCheckInFromKnownNumberTimeoutPromptsHangup() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());
		User resource = newContractorIndependentLane4ReadyWithCashBalance();

		profileService.updateProfileProperties(resource.getId(), workPhoneParams());

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource.getUserNumber()));
		workService.acceptWork(resource.getId(), work.getId());

		// Mock call initiation - would be coming via (HTTP in the case of Twilio) callback

		Profile profile = profileService.findProfile(resource.getId());
		VoiceResponseDTO responseDTO = new VoiceResponseDTO();
		responseDTO.setFromNumber(StringUtilities.formatE164PhoneNumber(profile.getWorkPhone()));
		responseDTO.setToNumber("+12125551212");
		responseDTO.setCallId(RandomUtilities.generateNumericString(17) + RandomUtilities.generateAlphaString(17));
		responseDTO.setCallStatus("inprogress");

		String response = voiceService.respond(responseDTO);

		assertNotNull(response);
		assertEquals(true, response.contains("Hello " + resource.getFirstName()));

		for (int i = 0; i < Constants.VOICE_CALL_FAILED_PROMPTS_CUTOFF; i++) {
			responseDTO.setMsg("TIMEOUT");
			responseDTO.setRedirectToSubStatus("start");

			response = voiceService.respond(responseDTO);

			assertEquals(true, response.contains("Please try again."));
		}

		responseDTO.setMsg("TIMEOUT");
		responseDTO.setRedirectToSubStatus("start");

		response = voiceService.respond(responseDTO);

		assertEquals(true, response.contains("Thank you. Good Bye."));
	}

}

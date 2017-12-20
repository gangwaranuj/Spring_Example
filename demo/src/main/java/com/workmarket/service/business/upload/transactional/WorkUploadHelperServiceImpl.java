package com.workmarket.service.business.upload.transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.LocationService;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.service.business.upload.parser.WorkUploadLocation;
import com.workmarket.service.business.upload.parser.WorkUploadLocationContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map.Entry;

@Service
public class WorkUploadHelperServiceImpl implements WorkUploadHelperService {
	@Autowired private CRMService crmService;
	@Autowired private DirectoryService directoryService;
	@Autowired private TWorkService workService;
	@Autowired private CompanyService companyService;
	@Autowired private LocationService locationService;
	private static final Logger logger = LoggerFactory.getLogger(WorkUploadHelperServiceImpl.class);

	@Override
	public void saveUpload(
			final List<WorkSaveRequest> requests,
			final Multimap<WorkUploadLocation, Integer> newLocations,
			final Multimap<WorkUploadLocationContact, Integer> newContacts,
			final Long companyId
	) throws ValidationException {

		StopWatch timer = new StopWatch("saveUpload");

		timer.start("WorkUploadHelperServiceImp.saveUpload() -- begin");

		timer.stop();

		timer.start("saveLocations");

		List<WorkUploadLocation> savedLocations = Lists.newArrayList();

		final long throttleDelay = locationService.getGeocodeDelay();
		for (WorkUploadLocation location : newLocations.keySet()) {
			boolean isAlreadySaved = savedLocations.contains(location);
			Location saved = null;
			if (!isAlreadySaved) {

				// Slowing bulk uploads down a bit to avoid indigestion
				try {
					Thread.sleep(throttleDelay);
				} catch (final InterruptedException e) {
					logger.error("[geo] Interrupted", e);
					Thread.currentThread().interrupt();
				}
				saved = saveLocation(location);
				savedLocations.add(location);
			}
			if (saved != null || isAlreadySaved) {
				for (Integer line : newLocations.get(location)) {
					requests.get(line).getWork().getLocation().setId(saved.getId());
				}
			}
		}
		timer.stop();

		List<WorkUploadLocationContact> savedContacts = Lists.newArrayList();
		timer.start("saveContacts");
		Company company = companyService.findById(companyId);
		for (Entry<WorkUploadLocationContact, Integer> entry : newContacts.entries()) {

			if (!savedContacts.contains(entry.getKey())) {
				WorkUploadLocationContact uploadContact = entry.getKey();
				Integer line = entry.getValue();
				Work work = requests.get(line).getWork();

				Long locId = null;
				if (work.isSetLocation() && work.getLocation().isSetId()) {
					locId = work.getLocation().getId();
				}

				ClientContact saved = saveContact(entry.getKey(), locId, company);

				if (saved != null) {
					if (uploadContact.isPrimary()) {
						work.getLocationContact().setId(saved.getId());
					} else {
						work.getSecondaryLocationContact().setId(saved.getId());
					}
					crmService.addPhonesToClientContact(saved.getId(), entry.getKey().toDTO().getPhoneNumbers());
					crmService.addEmailsToClientContact(saved.getId(), entry.getKey().toDTO().getEmails());

					// If work has a location and client company, the location will have been saved as a client location
					// If so, associate the contact with the location as well
					if (crmService.findClientLocationById(locId) != null) {
						crmService.addLocationToClientContact(saved.getId(), locId);
					}
				}
				savedContacts.add(entry.getKey());
			}
		}
		timer.stop();

		timer.start("uploadWork");
		workService.uploadWorkAsync(requests);
		timer.stop();

		logger.debug(timer.prettyPrint());
	}

	private Location saveLocation(WorkUploadLocation wul) {
		LocationDTO dto = wul.toDTO();

		if (wul.hasClientCompany()) {
			return crmService.saveOrUpdateClientLocation(wul.getClientCompanyId(), dto, null);
		} else {
			return directoryService.saveOrUpdateLocation(dto);
		}
	}

	private ClientContact saveContact(WorkUploadLocationContact contact, Long locationId, Company company) {
		ClientContactDTO dto = contact.toDTO();
		if (contact.getLocation() != null) {
			dto.setClientLocationId(locationId);
		}
		return crmService.saveOrUpdateClientContact(company, dto);
	}
}

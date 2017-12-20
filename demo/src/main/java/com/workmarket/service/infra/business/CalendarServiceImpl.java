package com.workmarket.service.infra.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.business.dto.CalendarDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.FileUtilities;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CalendarServiceImpl implements CalendarService{

	@Autowired private WorkDAO workDAO;
	@Autowired private UserDAO userDAO;

	@Autowired private TemplateService templateService;


	@Value("${baseurl}")
	public String BASEURL;

	@Value("${assignment.details.url}")
	private String ASSIGNMENT_DETAILS_URL;

	@Value("${calendar.path}")
	private String CALENDAR_DIRECTORY_PATH;

	/**
	 * Creates a Calendar Object
	 *
	 * @params CalendarDTO
	 * @return A String with the absolute file path of the created object
	 * @throws ValidationException
	 * @throws IOException
	 * @see http://m2.modularity.net.au/projects/ical4j/apidocs/index.html
	 *
	 */
	/* TODO: Implement more advanced settings like the use of different timezones,
	 * add multiple users as recipients, option for Full day instead
	 * of specific date-time, etc.
	 *
	 */
	@Override
	public String createCalendar(CalendarDTO calendarDTO) throws IOException, ValidationException {
		Assert.notNull(calendarDTO);
		Assert.notNull(calendarDTO.getToEmail());
		Assert.notNull(calendarDTO.getFromDate());

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		Calendar calendar = new Calendar();
		VEvent event;

		java.util.Calendar dateTo = calendarDTO.getToDate();

		if (dateTo != null) {
			event = new VEvent(new DateTime(calendarDTO.getFromDate().getTime()), new DateTime(dateTo.getTime()),  calendarDTO.getTitle());
			event.getProperties().getProperty(Property.DTEND).getParameters().add(net.fortuna.ical4j.model.parameter.Value.DATE_TIME);
		}
		else {
			event = new VEvent(new DateTime(calendarDTO.getFromDate().getTime()), calendarDTO.getTitle());
		}

		event.getProperties().getProperty(Property.DTSTART).getParameters().add(net.fortuna.ical4j.model.parameter.Value.DATE_TIME);
		event.getProperties().add(new Description(calendarDTO.getDescription()));
		event.getProperties().add(new Location(calendarDTO.getLocation()));

		// Add attendees
		//TODO: Implement multiple attendees
		Attendee attendee = new Attendee(URI.create("mailto:"+ calendarDTO.getToEmail()));
		attendee.getParameters().add(Role.REQ_PARTICIPANT);
		attendee.getParameters().add(new Cn( calendarDTO.getToName() ));
		event.getProperties().add(attendee);

		UidGenerator ug = new UidGenerator("1");
		event.getProperties().add(ug.generateUid());

		calendar.getProperties().add(new ProdId("Work Market"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getComponents().add(event);

		//TODO: Check how are we going to handle these files and delete them afterwards
		FileUtilities.createFolder(CALENDAR_DIRECTORY_PATH);

		String outputFilePath = Constants.CALENDAR_DIRECTORY_PATH + File.pathSeparator + Constants.DEFAULT_CALENDAR_FILE_NAME
			+ UUID.randomUUID().toString() + Constants.CALENDAR_EXTENSION;

		FileOutputStream output = new FileOutputStream(outputFilePath);
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, output);

		return outputFilePath;
	}

	@Override
	public String createWorkCalendar(Long userId, Long workId) throws IOException, ValidationException  {
		Assert.notNull(workId);
		Assert.notNull(userId);

		Work work = workDAO.get(workId);
		User user = userDAO.get(userId);
		Assert.notNull(work);
		Assert.notNull(user);

		CalendarDTO calendarDTO = new CalendarDTO();
		calendarDTO.setFromDate(work.getScheduleFrom());

		if (work.getIsScheduleRange()) {
			calendarDTO.setToDate(work.getScheduleThrough());
		}
		if (work.getAddress() != null) {
			calendarDTO.setLocation(work.getAddress().getFullAddress());
		}

		calendarDTO.setTitle(work.getTitle());
		calendarDTO.setToEmail(user.getEmail());
		calendarDTO.setToName(user.getFullName());
		calendarDTO.setDescription(templateService.renderWorkCalendarTemplate(work));
		calendarDTO.setLocation(BASEURL + ASSIGNMENT_DETAILS_URL + work.getWorkNumber());

		return createCalendar(calendarDTO);
	}

	public WorkDAO getWorkDAO() {
		return workDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public TemplateService getTemplateService() {
		return templateService;
	}

	public String getBASEURL() {
		return BASEURL;
	}

	public String getASSIGNMENT_DETAILS_URL() {
		return ASSIGNMENT_DETAILS_URL;
	}

	public String getCALENDAR_DIRECTORY_PATH() {
		return CALENDAR_DIRECTORY_PATH;
	}

	public void setWorkDAO(WorkDAO workDAO) {
		this.workDAO = workDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public void setBASEURL(String bASEURL) {
		BASEURL = bASEURL;
	}

	public void setASSIGNMENT_DETAILS_URL(String aSSIGNMENT_DETAILS_URL) {
		ASSIGNMENT_DETAILS_URL = aSSIGNMENT_DETAILS_URL;
	}

	public void setCALENDAR_DIRECTORY_PATH(String cALENDAR_DIRECTORY_PATH) {
		CALENDAR_DIRECTORY_PATH = cALENDAR_DIRECTORY_PATH;
	}

}

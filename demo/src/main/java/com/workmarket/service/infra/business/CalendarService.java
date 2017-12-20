package com.workmarket.service.infra.business;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

import net.fortuna.ical4j.model.ValidationException;

import com.workmarket.service.business.dto.CalendarDTO;

public interface CalendarService {

	public String createCalendar(CalendarDTO calendarDTO) throws FileNotFoundException, SocketException, IOException, ValidationException ;
	
	public String createWorkCalendar(Long userId, Long workId) throws IOException, ValidationException ;
}

package com.workmarket.service.infra.business;

import com.workmarket.service.business.dto.VoiceResponseDTO;
import com.workmarket.service.exception.IllegalWorkAccessException;

import javax.naming.OperationNotSupportedException;

public interface VoiceService {
	/**
	 * Respond to a call or call command.
	 * @param dto Details of the call or call command
	 * @return Voice response
	 * @throws OperationNotSupportedException 
	 * @throws IllegalWorkAccessException 
	 */
	String respond(VoiceResponseDTO dto) throws OperationNotSupportedException, IllegalWorkAccessException ;
}
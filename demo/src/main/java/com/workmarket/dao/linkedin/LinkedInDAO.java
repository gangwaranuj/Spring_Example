package com.workmarket.dao.linkedin;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.linkedin.LinkedInEducation;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.linkedin.LinkedInPhoneNumber;
import com.workmarket.domains.model.linkedin.LinkedInPosition;


public interface LinkedInDAO extends DAOInterface<LinkedInPerson>
{
	public enum LinkedInRestriction {
		WITH_USER, WITHOUT_USER
	}

	LinkedInPerson saveOrUpdateLinkedInPerson(LinkedInPerson linkedInPerson);

	LinkedInPosition saveOrUpdateLinkedInPosition(LinkedInPosition linkedInPosition);

	LinkedInEducation saveOrUpdateLinkedInEducation(LinkedInEducation linkedInEducation);

	LinkedInPhoneNumber saveOrUpdateLinkedInPhoneNumber(LinkedInPhoneNumber linkedInPhoneNumber);

	LinkedInPerson findMostRecentLinkedInPerson(Long userId);

	LinkedInPerson findMostRecentLinkedInPersonByLinkedInId(String linkedInId, LinkedInRestriction restriction);
}

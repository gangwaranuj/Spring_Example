package com.workmarket.dao.qualification;

import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;

import java.util.List;

/**
 * User to qualification association DAO.
 */
public interface UserToQualificationDAO {

    /**
     * Saves or updates a user to qualification association.
     * The interface for single user to qualification save or update is needed for single job title requirement.
     *
     * @param userToQualification user to qualification association.
     */
    void saveOrUpdate(UserToQualification userToQualification);

    /**
     * Finds a list of qualifications given user id and qualification type.
     * If includedDeleted flag is set to true, deleted qualifications will be included in the results.
     *
     * @param userId             user id
     * @param qualificationType  qualification type
     * @param includeDeleted     flag to include deleted association
     * @return List
     */
    List<UserToQualification> findQualifications(Long userId, QualificationType qualificationType, boolean includeDeleted);

    /**
     * Sets a list of qualifications with the same type for the user. It supports non job title types only.
     *
     * @param userId            user id
     * @param qualificationType qualification type
     * @param qualifications    a list of qualifications
     * @return List
     */
    List<UserToQualification> setUserQualificationsByType(Long userId, QualificationType qualificationType, List<Qualification> qualifications);
}

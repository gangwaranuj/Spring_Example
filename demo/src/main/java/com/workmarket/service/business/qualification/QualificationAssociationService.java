package com.workmarket.service.business.qualification;

import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;

import java.util.List;

/**
 * Qualification association service.
 */
public interface QualificationAssociationService {
    /**
     * Saves or updates a user to qualification association.
     *
     * @param userToQualification user to qualification association
     * @return UserToQualification
     */
    UserToQualification saveOrUpdate(UserToQualification userToQualification);

    /**
     * Sets a list of qualifications for the user given a qualification type.
     * All qualifications in the list should have the same qualification type.
     * This method supports non-job-title qualifications set since a user can have only one job title.
     *
     * @param userId            user id
     * @param qualificationType qualification type
     * @param qualifications    a list of qualifications
     * @return List
     */
    List<UserToQualification> setUserQualifications(Long userId, QualificationType qualificationType, List<Qualification> qualifications);

    /**
     * Finds a list of qualification associations given the user id and qualification type.
     * @param userId            user id
     * @param qualificationType qualification type
     * @param includeDeleted    flag to indicate whether or not include deleted qualifications
     * @return List of user to qualification associations.
     */
    List<UserToQualification> findUserQualifications(Long userId, QualificationType qualificationType, boolean includeDeleted);

    /**
     * Sets a list of qualifications for the assignment given a qualification type.
     * All qualifications in the list should have the same qualification type.
     *
     * @param workId            work id
     * @param qualificationType qualification type
     * @param qualifications    a list of qualifications
     * @return List
     */
    List<WorkToQualification> setWorkQualifications(Long workId, Long userId, QualificationType qualificationType, List<Qualification> qualifications);

    /**
     * Finds a list of qualification associations given the work id and qualification type.
     * @param workId            work id
     * @param qualificationType qualification type
     * @param includeDeleted    flag to indicate whether or not include deleted qualifications
     * @return List of work to qualification associations.
     */
    List<WorkToQualification> findWorkQualifications(Long workId, QualificationType qualificationType, boolean includeDeleted);
}

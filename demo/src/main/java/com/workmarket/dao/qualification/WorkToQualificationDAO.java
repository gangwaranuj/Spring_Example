package com.workmarket.dao.qualification;

import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;

import java.util.List;

/**
 * Work to qualification association DAO.
 */
public interface WorkToQualificationDAO {

    /**
     * Finds a list of qualifications given work id and qualification type.
     * If includedDeleted flag is set to true, deleted qualifications will be included in the results.
     *
     * @param workId             work id
     * @param qualificationType  qualification type
     * @param includeDeleted     flag to include deleted association
     * @return List
     */
    List<WorkToQualification> findQualifications(Long workId, QualificationType qualificationType, boolean includeDeleted);

    /**
     * Sets a list of qualifications with the same type for the assignment.
     * The interface supports multiple job functions for a work.
     *
     * @param workId            work id
     * @param userId            user id of the work
     * @param qualificationType qualification type
     * @param qualifications    a list of qualifications
     * @return List
     */
    List<WorkToQualification> setWorkQualificationsByType(Long workId, Long userId, QualificationType qualificationType, List<Qualification> qualifications);
}

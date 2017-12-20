package com.workmarket.service.business.qualification;

import com.workmarket.dao.qualification.UserToQualificationDAO;
import com.workmarket.dao.qualification.WorkToQualificationDAO;
import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implementation of qualification association service.
 */
@Service
public class QualificationAssociationServiceImpl implements QualificationAssociationService {

    private final UserToQualificationDAO userToQualificationDAO;
    private final WorkToQualificationDAO workToQualificationDAO;

    @Autowired
    public QualificationAssociationServiceImpl(final UserToQualificationDAO userToQualificationDAO,
                                               final WorkToQualificationDAO workToQualificationDAO) {
        this.userToQualificationDAO = userToQualificationDAO;
        this.workToQualificationDAO = workToQualificationDAO;
    }

    @Override
    public UserToQualification saveOrUpdate(final UserToQualification userToQualification) {
        Assert.notNull(userToQualification);
        userToQualificationDAO.saveOrUpdate(userToQualification);
        return userToQualification;
    }

    @Override
    public List<UserToQualification> setUserQualifications(final Long userId,
                                                           final QualificationType qualificationType,
                                                           final List<Qualification> qualifications) {
        Assert.notNull(userId);
        Assert.notEmpty(qualifications);
        Assert.isTrue(!qualificationType.equals(QualificationType.job_title));
        for (final Qualification qualification: qualifications) {
            Assert.isTrue(qualificationType.equals(qualification.getQualificationType()));
        }
        return userToQualificationDAO.setUserQualificationsByType(userId, qualificationType, qualifications);
    }

    @Override
    public List<UserToQualification> findUserQualifications(final Long userId,
                                                            final QualificationType qualificationType,
                                                            final boolean includeDeleted) {
        Assert.notNull(userId);
        return userToQualificationDAO.findQualifications(userId, qualificationType, includeDeleted);
    }

    @Override
    public List<WorkToQualification> setWorkQualifications(final Long workId,
                                                           final Long userId,
                                                           final QualificationType qualificationType,
                                                           final List<Qualification> qualifications) {
        Assert.notNull(workId);
        Assert.notNull(userId);
        Assert.notEmpty(qualifications);
        for (final Qualification qualification: qualifications) {
            Assert.isTrue(qualificationType.equals(qualification.getQualificationType()));
        }
        return workToQualificationDAO.setWorkQualificationsByType(workId, userId, qualificationType, qualifications);
    }

    @Override
    public List<WorkToQualification> findWorkQualifications(final Long workId,
                                                            final QualificationType qualificationType,
                                                            final boolean includeDeleted) {
        Assert.notNull(workId);
        return workToQualificationDAO.findQualifications(workId, qualificationType, includeDeleted);
    }
}

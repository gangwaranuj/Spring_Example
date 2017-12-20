package com.workmarket.domains.model.qualification;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.search.qualification.QualificationType;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User to qualification association.
 */
@Entity(name = "userToQualification")
@Table(name = "user_to_qualification")
@AuditChanges
public class UserToQualification extends DeletableEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String qualificationUuid;
    private QualificationType qualificationType;

    /**
     * Constructor.
     *
     * @param userId user id
     * @param qualificationUuid qualification uuid
     */
    public UserToQualification(final Long userId,
                               final String qualificationUuid,
                               final QualificationType qualificationType) {
        this.userId = userId;
        this.qualificationUuid = qualificationUuid;
        this.qualificationType = qualificationType;
    }

    /**
     * Gets the user id.
     *
     * @return Long
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId user id
     */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    /**
     * Gets qualification uuid.
     *
     * @return String
     */
    public String getQualificationUuid() {
        return qualificationUuid;
    }

    /**
     * Sets qualification uuid.
     *
     * @param qualificationUuid qualification uuid
     */
    public void setQualificationUuid(final String qualificationUuid) {
        this.qualificationUuid = qualificationUuid;
    }

    /**
     * Gets qualification type.
     *
     * @return QualificationType.
     */
    public QualificationType getQualificationType() {
        return qualificationType;
    }

    /**
     * Sets qualification type.
     *
     * @param qualificationType Qualification type.
     */
    public void setQualificationType(final QualificationType qualificationType) {
        this.qualificationType = qualificationType;
    }
}

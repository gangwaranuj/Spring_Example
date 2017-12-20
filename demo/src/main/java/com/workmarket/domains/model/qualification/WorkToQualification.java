package com.workmarket.domains.model.qualification;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.search.qualification.QualificationType;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Work to qualification association.
 */
@Entity(name = "workToQualification")
@Table(name = "work_to_qualification")
@AuditChanges
public class WorkToQualification extends DeletableEntity {

    private static final long serialVersionUID = 1L;

    private Long workId;
    private String qualificationUuid;
    private QualificationType qualificationType;

    /**
     * Constructor.
     *
     * @param workId work id.
     * @param qualificationUuid qualification uuid.
     * @param qualificationType qualification type.
     */
    public WorkToQualification(final Long workId,
                               final String qualificationUuid,
                               final QualificationType qualificationType) {
        this.workId = workId;
        this.qualificationUuid = qualificationUuid;
        this.qualificationType = qualificationType;
    }

    /**
     * Gets the work id.
     *
     * @return Long.
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * Sets the work id.
     *
     * @param workId work id.
     */
    public void setWorkId(final Long workId) {
        this.workId = workId;
    }

    /**
     * Gets the qualification uuid.
     *
     * @return String
     */
    public String getQualificationUuid() {
        return qualificationUuid;
    }

    /**
     * Sets the qualification uuid.
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
     * @param qualificationType qualification type.
     */
    public void setQualificationType(final QualificationType qualificationType) {
        this.qualificationType = qualificationType;
    }
}

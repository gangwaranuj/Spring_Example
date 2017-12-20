package com.workmarket.service.business.dto;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.utility.BeanUtilities;

public class AssessmentDTO {
	// Assignment properties
	private Long id;
	private String name;
	private String description;
	private String type = AbstractAssessment.GRADED_ASSESSMENT_TYPE;
	private String assessmentStatusTypeCode = AssessmentStatusType.DRAFT;
	private Integer approximateDurationMinutes;
	private Long industryId;

	// Assignment configuration properties
	private Double passingScore = 100.0;
	private Boolean passingScoreShared = Boolean.TRUE;
	private Integer retakesAllowed;
	private Integer durationMinutes;
	private Boolean resultsSharedWithPassers = Boolean.FALSE;
	private Boolean resultsSharedWithFailers = Boolean.FALSE;
	private Boolean statisticsShared = Boolean.TRUE;
	private boolean featured;
	private boolean isRequired;

	public static AssessmentDTO newDTO(AbstractAssessment assessment) {
		AssessmentDTO dto = new AssessmentDTO();
		BeanUtilities.copyProperties(dto, assessment);
		dto.setId(assessment.getId());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssessmentStatusTypeCode() {
		return assessmentStatusTypeCode;
	}

	public void setAssessmentStatusTypeCode(String assessmentStatusTypeCode) {
		this.assessmentStatusTypeCode = assessmentStatusTypeCode;
	}

	public Integer getApproximateDurationMinutes() {
		return approximateDurationMinutes;
	}

	public void setApproximateDurationMinutes(Integer approximateDurationMinutes) {
		this.approximateDurationMinutes = approximateDurationMinutes;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Double getPassingScore() {
		return passingScore;
	}

	public void setPassingScore(Double passingScore) {
		this.passingScore = passingScore;
	}

	public Boolean getPassingScoreShared() {
		return passingScoreShared;
	}

	public void setPassingScoreShared(Boolean passingScoreShared) {
		this.passingScoreShared = passingScoreShared;
	}

	public Integer getRetakesAllowed() {
		return retakesAllowed;
	}

	public void setRetakesAllowed(Integer retakesAllowed) {
		this.retakesAllowed = retakesAllowed;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Boolean getResultsSharedWithPassers() {
		return resultsSharedWithPassers;
	}

	public void setResultsSharedWithPassers(Boolean resultsSharedWithPassers) {
		this.resultsSharedWithPassers = resultsSharedWithPassers;
	}

	public Boolean getResultsSharedWithFailers() {
		return resultsSharedWithFailers;
	}

	public void setResultsSharedWithFailers(Boolean resultsSharedWithFailers) {
		this.resultsSharedWithFailers = resultsSharedWithFailers;
	}

	public Boolean getStatisticsShared() {
		return statisticsShared;
	}

	public void setStatisticsShared(Boolean statisticsShared) {
		this.statisticsShared = statisticsShared;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public boolean isFeatured() {
		return featured;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
}

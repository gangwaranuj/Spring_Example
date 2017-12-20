package com.workmarket.domains.model.qualification;

import java.util.List;

/**
 * Created by bruno on 8/22/16.
 */
public class SkillRecommenderDTO {
    private String jobTitle;
    private List<String> industries;
    private List<String> removedSkills;
    private List<String> definedSkills;
    private List<String> selectedSkills;
    private Long offset;
    private Long limit;
    private String assignmentTitle;
    private String assignmentDescription;

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public String getAssignmentDescription() {
        return assignmentDescription;
    }

    public void setAssignmentDescription(String assignmentDescription) {
        this.assignmentDescription = assignmentDescription;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public List<String> getRemovedSkills() {
        return removedSkills;
    }

    public void setRemovedSkills(List<String> removedSkills) {
        this.removedSkills = removedSkills;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<String> getDefinedSkills() {
        return definedSkills;
    }

    public void setDefinedSkills(List<String> definedSkills) {
        this.definedSkills = definedSkills;
    }

    public List<String> getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(List<String> selectedSkills) {
        this.selectedSkills = selectedSkills;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }
}

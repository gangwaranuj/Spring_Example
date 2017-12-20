package com.workmarket.domains.onboarding.model;

/**
 * Created by bruno on 7/19/16.
 */
public class OnboardingSkillDTO {
    private Long id;
    private String name;
    private Qualification.Type type;

    public OnboardingSkillDTO() {}

    public OnboardingSkillDTO(Long id, String name, Qualification.Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Qualification.Type getType() { return type; }

    public void setType(Qualification.Type type) { this.type = type; }

}

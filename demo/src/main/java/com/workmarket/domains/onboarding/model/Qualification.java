package com.workmarket.domains.onboarding.model;

/**
 * Created by bruno on 8/9/16.
 */
public class Qualification {
    public enum Type { SKILL, SPECIALTY, JOBTITLE }
    private Long id;
    private String name;
    private Double score;
    private Type type;

    public Qualification() {}

    public Qualification(final Long id, final String name, final Double score, final Type type) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() { return name; }

    public Double getScore() {
        return score;
    }

    public Type getType() {
        return type;
    }

}

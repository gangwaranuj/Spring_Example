package com.workmarket.domains.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="objectiveType")
@Table(name="objective_type")
@AttributeOverrides({
	@AttributeOverride(name="code", column=@Column(length=15))
})
public class ObjectiveType extends LookupEntity{
	
	private static final long serialVersionUID = 1L;

	public static final String WORK = "work";
	public static final String SOCIAL = "social";
	public static final String PROFESSIONAL = "professional";
	public static final String OTHER = "other";
	
	public ObjectiveType(){
		super();
	}

	public ObjectiveType(String code) {
		super(code);
	}

	public static ObjectiveType newObjectiveType(String code) {
        return new ObjectiveType(code);
    }
}
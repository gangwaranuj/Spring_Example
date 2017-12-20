package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity(name="language_proficiency_type")
@Table(name="language_proficiency_type")
public class LanguageProficiencyType extends LookupEntity{
	
	private static final long serialVersionUID = 1L;
	
	
	public static String NATIVE = "native";
	public static String FLUENT = "fluent";
	public static String CONVERSATIONAL = "convers";
	public static String BASIC = "basic";
	
	public LanguageProficiencyType(){
		
	}
	
	public LanguageProficiencyType(String code){
		super(code);
	}

}

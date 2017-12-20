package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="profile_lanaguge")
@Table(name="profile_language")
public class ProfileLanguage extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
	private Language language;
	private Profile profile;
	private LanguageProficiencyType languageProficiencyType;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="id")
	public Language getLanguage() {
		return language;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="profile_id", referencedColumnName="id")
	public Profile getProfile() {
		return profile;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_proficiency_type_code", referencedColumnName="code")
	public LanguageProficiencyType getLanguageProficiencyType() {
		return languageProficiencyType;
	}

	
	public void setLanguage(Language language) {
		this.language = language;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public void setLanguageProficiencyType(LanguageProficiencyType proficiencyType){
		this.languageProficiencyType = proficiencyType;		
	}
}
package com.workmarket.domains.model.recruiting;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name = "recruitingVendor")
@Table(name = "recruiting_vendor")
public class RecruitingVendor extends LookupEntity {

	private static final long serialVersionUID = 8189895687142750327L;

	public String OTHER = "other";
	public String CRAIGSLIST = "craigslist";
	public String CAREERBUILDER = "careerbuilder";
	public String DICE = "dice";
	public String INDEED = "indeed";
	public String LINKEDIN = "linkedin";
	public String MONSTER = "monster";
	public String SIMPLYHIRED = "simplyhired";
	public String YAHOO_HOTJOBS = "hotjobs";

	public RecruitingVendor() {}

	public RecruitingVendor(String code) {
		super(code);
	}
}

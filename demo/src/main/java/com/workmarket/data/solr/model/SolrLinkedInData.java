package com.workmarket.data.solr.model;

import com.google.common.collect.Lists;

import java.util.List;

public class SolrLinkedInData {
	private List<LinkedInCompanyData> linkedInCompanies;
	private List<LinkedInSchoolData> linkedInSchools;
	
	public SolrLinkedInData() {
		List<LinkedInCompanyData> companies = Lists.newArrayList();
		List<LinkedInSchoolData> education = Lists.newArrayList();
		
		setLinkedInCompanies(companies);
		setLinkedInSchools(education);
	}
	
	public List<LinkedInCompanyData> getLinkedInCompanies() {
		return linkedInCompanies;
	}
	public void setLinkedInCompanies(List<LinkedInCompanyData> linkedInCompanies) {
		this.linkedInCompanies = linkedInCompanies;
	}
	public List<LinkedInSchoolData> getLinkedInSchools() {
		return linkedInSchools;
	}
	public void setLinkedInSchools(List<LinkedInSchoolData> linkedInSchools) {
		this.linkedInSchools = linkedInSchools;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((linkedInCompanies == null) ? 0 : linkedInCompanies
						.hashCode());
		result = prime
				* result
				+ ((linkedInSchools == null) ? 0 : linkedInSchools
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrLinkedInData other = (SolrLinkedInData) obj;
		if (linkedInCompanies == null) {
			if (other.linkedInCompanies != null)
				return false;
		} else if (!linkedInCompanies.equals(other.linkedInCompanies))
			return false;
		if (linkedInSchools == null) {
			if (other.linkedInSchools != null)
				return false;
		} else if (!linkedInSchools.equals(other.linkedInSchools))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "LinkedInData [linkedInCompanies=" + linkedInCompanies
				+ ", linkedInSchools=" + linkedInSchools + "]";
	}
		
	
	public static class LinkedInCompanyData {
		private String companyName;
		private String companyTitle;
		
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getCompanyTitle() {
			return companyTitle;
		}
		public void setCompanyTitle(String companyTitle) {
			this.companyTitle = companyTitle;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((companyName == null) ? 0 : companyName.hashCode());
			result = prime * result
					+ ((companyTitle == null) ? 0 : companyTitle.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LinkedInCompanyData other = (LinkedInCompanyData) obj;
			if (companyName == null) {
				if (other.companyName != null)
					return false;
			} else if (!companyName.equals(other.companyName))
				return false;
			if (companyTitle == null) {
				if (other.companyTitle != null)
					return false;
			} else if (!companyTitle.equals(other.companyTitle))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "LinkedInCompanyData [companyName=" + companyName
					+ ", companyTitle=" + companyTitle + "]";
		}
		
	}
	public static class LinkedInSchoolData {
		private String schoolName;
		private String fieldOfStudy;
		public String getSchoolName() {
			return schoolName;
		}
		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}
		public String getFieldOfStudy() {
			return fieldOfStudy;
		}
		public void setFieldOfStudy(String fieldOfStudy) {
			this.fieldOfStudy = fieldOfStudy;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fieldOfStudy == null) ? 0 : fieldOfStudy.hashCode());
			result = prime * result
					+ ((schoolName == null) ? 0 : schoolName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LinkedInSchoolData other = (LinkedInSchoolData) obj;
			if (fieldOfStudy == null) {
				if (other.fieldOfStudy != null)
					return false;
			} else if (!fieldOfStudy.equals(other.fieldOfStudy))
				return false;
			if (schoolName == null) {
				if (other.schoolName != null)
					return false;
			} else if (!schoolName.equals(other.schoolName))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "LinkedInSchoolData [schoolName=" + schoolName
					+ ", fieldOfStudy=" + fieldOfStudy + "]";
		}
		
	}
	


}

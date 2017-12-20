package com.workmarket.service.business.dto;

import com.workmarket.service.business.feed.FeedItem;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "source")
@XmlType(propOrder = {"publisher", "publisherUrl", "lastBuildDate", "jobs"})
public class IndeedFeedDTO {
	private Date lastBuildDate = new Date();
	private List<PublicWorkDTO> jobs = new LinkedList<>();

	public IndeedFeedDTO(List<FeedItem> items){
		for(FeedItem item : items){
			jobs.add(new PublicWorkDTO(item));
		}
	}
	public IndeedFeedDTO() {
	}

	@XmlElement
	public String getPublisher() {
		return Constants.WM_NAME;
	}

	@XmlElement(name = "publisherurl")
	public String getPublisherUrl() {
		return Constants.PROD_PUBLIC_BASE_URL;
	}

	@XmlElement(name = "lastbuilddate")
	public String getLastBuildDate() {
		return DateUtilities.format("EEE, d MMM y h:mmaa", lastBuildDate);
	}

	public void setLastBuildDate(Date lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	@XmlElementRef
	public List<PublicWorkDTO> getJobs() {
		return jobs;
	}

	public void setJobs(List<PublicWorkDTO> jobs) {
		this.jobs = jobs;
	}
}

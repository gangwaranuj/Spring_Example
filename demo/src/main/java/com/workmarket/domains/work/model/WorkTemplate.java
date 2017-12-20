package com.workmarket.domains.work.model;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Formula;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.validation.constraints.Size;
import java.util.Calendar;

@Entity(name="workTemplate")
@DiscriminatorValue("WT")
@NamedQueries({})
@AuditChanges
@Access(AccessType.FIELD)
public class WorkTemplate extends AbstractWork {

	private static final long serialVersionUID = 1L;

	@Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH)
	@Column(name = "template_name", length = Constants.NAME_MAX_LENGTH, nullable = false)
	private String templateName;

	@Size(min = Constants.TEXT_MIN_LENGTH, max = Constants.TEXT_MAX_LENGTH)
	@Column(name = "template_description", length = Constants.NAME_MAX_LENGTH)
	private String templateDescription;

	@Formula(value="(select count(w.id) from work w where w.work_template_id = id)")
	private Integer workCount;
	@Formula(value="(select max(w.created_on) from work w where w.work_template_id = id) ")
	private Calendar latestCreatedWork;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	public Integer getWorkCount() {
		return workCount;
	}

	public void setWorkCount(Integer workCount) {
		this.workCount = workCount;
	}

	public Calendar getLatestCreatedWork() {
		return latestCreatedWork;
	}

	public void setLatestCreatedWork(Calendar latestCreatedWork) {
		this.latestCreatedWork = latestCreatedWork;
	}

}

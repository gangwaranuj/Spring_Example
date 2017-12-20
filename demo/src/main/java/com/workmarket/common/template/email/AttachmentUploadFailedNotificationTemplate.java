package com.workmarket.common.template.email;

import com.workmarket.domains.work.model.AbstractWork;

public class AttachmentUploadFailedNotificationTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8773907212118314979L;
	private String fileName;
	private AbstractWork work;

	public AttachmentUploadFailedNotificationTemplate(String fileName,AbstractWork work) {
		this.fileName = fileName;
		this.work = work;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}
}

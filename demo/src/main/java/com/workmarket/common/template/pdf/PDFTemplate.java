package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.MimeType;
import com.workmarket.common.template.Template;
import com.workmarket.configuration.Constants;
import org.apache.commons.lang.StringUtils;

public abstract class PDFTemplate extends Template {

	private static final long serialVersionUID = -292993740361840180L;
	private String outputFileName;

	protected PDFTemplate() {
	}

	@Override
	public String getSubjectTemplate() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getSubjectTemplatePath() {
		return StringUtils.EMPTY;
	}

	@Override
	public String getPath() {
		return "/template/pdf/";
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public String getMimeType() {
		return MimeType.PDF.getMimeType();
	}

	protected void setOutputFileName(String outputFileName) {
		if (StringUtils.isNotBlank(outputFileName)) {
			this.outputFileName = outputFileName + Constants.PDF_EXTENSION;
		}
	}
}

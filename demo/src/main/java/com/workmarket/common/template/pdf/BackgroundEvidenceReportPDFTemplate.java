package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.screening.BackgroundCheck;
import org.springframework.util.ClassUtils;

public class BackgroundEvidenceReportPDFTemplate  extends PDFTemplate{

	private static final long serialVersionUID = 1L;

	private BackgroundCheck backgroundCheck;
	private String sterlingLogoUrl;

	private static final String srcSterlingImageLogo = "images/sterling-logo.png";

	public BackgroundEvidenceReportPDFTemplate(BackgroundCheck backgroundCheck, String filename) {
		super();
		this.backgroundCheck = backgroundCheck;
		this.sterlingLogoUrl = ClassUtils.getDefaultClassLoader().getResource(srcSterlingImageLogo).getPath();
		setOutputFileName(filename);
	}

	public BackgroundCheck getBackgroundCheck() {
		return backgroundCheck;
	}

	public String getSterlingLogoUrl() {
		return sterlingLogoUrl;
	}
}

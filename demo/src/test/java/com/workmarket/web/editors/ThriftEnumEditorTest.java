package com.workmarket.web.editors;

import com.workmarket.thrift.assessment.AssessmentRequestInfo;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ThriftEnumEditorTest  {

	@Test
	public void setAsText() throws Exception {
		ThriftEnumEditor editor = new ThriftEnumEditor(AssessmentRequestInfo.class);
		editor.setAsText(String.valueOf(AssessmentRequestInfo.ITEM_INFO.getValue()));
		assertEquals(AssessmentRequestInfo.ITEM_INFO, editor.getValue());
	}

	@Test
	public void getAsText() throws Exception {
		ThriftEnumEditor editor = new ThriftEnumEditor(AssessmentRequestInfo.class);
		editor.setValue(AssessmentRequestInfo.ITEM_INFO);
		assertEquals(String.valueOf(AssessmentRequestInfo.ITEM_INFO.getValue()), editor.getAsText());
	}
}

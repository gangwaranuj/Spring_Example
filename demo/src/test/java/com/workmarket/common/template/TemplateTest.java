package com.workmarket.common.template;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateTest {
    @Test
    public void canonicalizeClassName_JSONName() {
        final String bareClassName = "MyClassName";
        final String className = String.format("%sWithJSONObjects", bareClassName);
        assertEquals(bareClassName, Template.canonicalizeClassName(className));
    }

    @Test
    public void canonicalizeClassName_JavaName() {
        final String bareClassName = "MyClassName";
        final String className = String.format("%sWithJavaObjects", bareClassName);
        assertEquals(bareClassName, Template.canonicalizeClassName(className));
    }

    @Test
    public void canonicalizeClassName_NoChange() {
        final String bareClassName = "MyClassName";
        assertEquals(bareClassName, Template.canonicalizeClassName(bareClassName));
    }

    @Test
    public void canonicalizeClassName_DontChangeJSONInTheMiddleOfTheClassName() {
        final String bareClassName = "MyClassNameWithJSONObjectsInTheMiddle";
        assertEquals(bareClassName, Template.canonicalizeClassName(bareClassName));
    }

    @Test
    public void canonicalizeClassName_DontChangeJavaInTheMiddleOfTheClassName() {
        final String bareClassName = "MyClassNameWithJavaObjectsInTheMiddle";
        assertEquals(bareClassName, Template.canonicalizeClassName(bareClassName));
    }

    @Test
    public void testMakeEmailPath() {
        final String path = Template.makeEmailPath("HeaderSMSTemplate");
        assertEquals("/template/email/HeaderSMSTemplate.vm", path);
    }

    @Test
    public void testMakeEmailSubjectPath() {
        final String path = Template.makeEmailSubjectPath("HeaderSMSTemplate");
        assertEquals("/template/email/subject/HeaderSMSTemplate.vm", path);
    }
}

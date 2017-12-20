package com.workmarket.common.template;

import com.workmarket.configuration.Constants;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Template implements Serializable {
    private static final Pattern classSanitizerPattern = Pattern.compile("^(.*)With(?:JSON|Java)Objects$");

    /**
     *
     */
    private static final long serialVersionUID = 9180448606077442365L;

    private final String subjectTemplateName = canonicalizeClassName(this.getClass().getSimpleName());
    private final String templateTemplateName = canonicalizeClassName(this.getClass().getSimpleName());
    private final String templateTemplatePath = makeEmailPath(getTemplateTemplate());

    private final String headerTemplatePath = makeEmailPath(getHeaderTemplate());
    private final String footerTemplatePath = makeEmailPath(getFooterTemplate());
    private final String subjectTemplatePath = makeEmailSubjectPath(getSubjectTemplate());

    public String getHeaderTemplate() {
        return Constants.EMAIL_HEADER_TEMPLATE;
    }

    public String getTemplateTemplate() {
        return templateTemplateName;
    }

    public String getFooterTemplate() {
        return Constants.EMAIL_FOOTER_TEMPLATE;
    }

    public String getSubjectTemplate() {
        return subjectTemplateName;
    }

    public String getHeaderTemplatePath() {
        return headerTemplatePath;
    }

    public String getTemplateTemplatePath() {
        return templateTemplatePath;
    }

    public String getFooterTemplatePath() {
        return footerTemplatePath;
    }

    public String getSubjectTemplatePath() {
        return subjectTemplatePath;
    }

    public Object getModel() {
        return this;
    }

    public abstract String getPath();

    /**
     * Remove the part of the class name that differentiates between JSON-based and Java object-based templates.
     * Because template files are programatically looked up based on the class name, we must remove the part of the
     * name that indicates if it's a JSON or Java object class.
     * <p/>
     * For example, calling this function with "ForumCommentAddedNotificationTemplateWithJavaObjects"
     * will return "ForumCommentAddedNotificationTemplate".
     *
     * @param className
     * @return
     */
    public static String canonicalizeClassName(final String className) {
        final Matcher matcher = classSanitizerPattern.matcher(className);
        if ((matcher == null) || !matcher.find()) {
            return className;
        } else {
            return matcher.group(1);
        }
    }

    /**
     * Make the email path for the given filename. The filename should not include the extension
     *
     * @param fileName
     * @return
     */
    public static String makeEmailPath(final String fileName) {
        return Constants.EMAIL_TEMPLATE_DIRECTORY_PATH +
            "/" +
            fileName +
            Constants.EMAIL_TEMPLATE_EXTENSION;
    }

    /**
     * Make the email subject path for the given filename. The filename should not include the extension
     *
     * @param fileName
     * @return
     */
    public static String makeEmailSubjectPath(final String fileName) {
        return Constants.EMAIL_SUBJECT_TEMPLATE_DIRECTORY_PATH +
            "/" +
            fileName +
            Constants.EMAIL_TEMPLATE_EXTENSION;
    }
}

package com.workmarket.web.editors;

import com.workmarket.domains.model.Industry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component("industryEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndustryEditor extends PropertyEditorSupport
{
    public void setAsText(String text) throws IllegalArgumentException {
        Industry industry = null;

        if (StringUtils.isNotEmpty(text)) {
            industry = new Industry(Long.valueOf(text));
        }

        setValue(industry);
    }

    @Override
    public String getAsText() {
        Industry industry = (Industry) getValue();
        return industry == null ? null : String.valueOf(industry.getId());
    }
}

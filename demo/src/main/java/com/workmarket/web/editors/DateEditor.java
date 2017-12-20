package com.workmarket.web.editors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("dateEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateEditor extends PropertyEditorSupport{
    private String pattern="MM/dd/yyyy";

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


    protected DateFormat getFormatter() {
        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getInstance();
        sdf.applyPattern(pattern);
        return sdf;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Date value = null;

        if (StringUtils.hasText(text)) {
            try {
                DateFormat dateFormat = getFormatter();
                value = dateFormat.parse(text);
            } catch (ParseException ex) {
                throw new IllegalArgumentException(text, ex);
            }
        }

        setValue(value);
    }

    @Override
    public String getAsText() {
        Date date = (Date)getValue();

        if (date == null) {
            return "";
        }
        else {
            DateFormat dateFormat = getFormatter();
            return dateFormat.format(date);
        }
    }
}

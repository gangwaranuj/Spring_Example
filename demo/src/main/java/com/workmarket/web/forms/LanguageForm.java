package com.workmarket.web.forms;

import com.workmarket.domains.model.CallingCode;
import com.workmarket.web.forms.base.AddressForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class LanguageForm {

    String format;

    String locale;


    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

}

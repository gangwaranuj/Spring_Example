package com.workmarket.service.locale;

import com.workmarket.biz.gen.Messages.WMFormat;
import com.workmarket.biz.gen.Messages.WMLocale;

import java.util.List;

public interface LocaleService {
    WMFormat getPreferredFormat(String userUuid);
    WMLocale getPreferredLocale(String userUuid);
    List<WMFormat> getSupportedFormats();
    List<WMLocale> getSupportedLocale();
    void setPreferredFormat(String userUuid, String code);
    void setPreferredLocale(String userUuid, String code);
    String getValidLocaleCode(final String code);
    String getValidFormatCode(final String code);
}

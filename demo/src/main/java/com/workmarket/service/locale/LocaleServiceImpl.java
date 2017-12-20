package com.workmarket.service.locale;

import com.workmarket.biz.gen.Messages.*;
import com.workmarket.biz.LocaleClient;
import com.workmarket.service.orgstructure.OrgStructureServiceImpl;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocaleServiceImpl implements LocaleService {
    private static final Log logger = LogFactory.getLog(LocaleServiceImpl.class);
    private static final String DEFAULT_LOCALE_CODE = "en_US";
    private static final String DEFAULT_FORMAT_CODE = "US";
    @Autowired
    private WebRequestContextProvider webRequestContextProvider;
    @Autowired
    @Qualifier("LocaleClient")
    LocaleClient localeClient;

    public WMFormat getPreferredFormat(final String userUuid) {
        try {
            final FormatResponse prefferedFormat = localeClient.getPreferredFormat(
                    GetPreferredFormatRequest.newBuilder().setUserUuid(userUuid).build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!prefferedFormat.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + prefferedFormat.getStatus().getMessage(0));
                throw new RuntimeException("Error getting preferred format");
            }

            return prefferedFormat.getFormat();

        } catch (Exception e) {
            throw new RuntimeException("Error getting preferred format", e);
        }
    }

    public WMLocale getPreferredLocale(final String userUuid) {
        try {
            final LocaleResponse preferredLocale = localeClient.getPreferredLocale(
                    GetPreferredLocaleRequest.newBuilder().setUserUuid(userUuid).build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!preferredLocale.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + preferredLocale.getStatus().getMessage(0));
                throw new RuntimeException("Error getting preferred locale");
            }

            return preferredLocale.getLocale();
        } catch (Exception e) {
            throw new RuntimeException("Error getting preferred locale", e);
        }
    }

    public List<WMFormat> getSupportedFormats() {
        try {
            final SupportedFormatsResponse supportedFormats = localeClient.getSupportedFormats(
                    GetSupportedFormatsRequest.newBuilder().build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!supportedFormats.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + supportedFormats.getStatus().getMessage(0));
                throw new RuntimeException("Error getting supported formats");
            }

            return supportedFormats.getFormatList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting supported formats", e);
        }
    }

    public List<WMLocale> getSupportedLocale() {
        try {
            final SupportedLocalesResponse supportedLocale = localeClient.getSupportedLocale(
                    GetSupportedLocalesRequest.newBuilder().build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!supportedLocale.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + supportedLocale.getStatus().getMessage(0));
                throw new RuntimeException("Error getting supported locales");
            }

            return supportedLocale.getLocaleList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting supported locales", e);
        }
    }
    
    public void setPreferredFormat(final String userUuid, final String code) {
        final String validCode = getValidFormatCode(code);

        try {
            final FormatResponse formatResponse = localeClient.setPreferredFormat(
                    SetPreferredFormatRequest.newBuilder().setUserUuid(userUuid).setCode(validCode).build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!formatResponse.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + formatResponse.getStatus().getMessage(0));
                throw new RuntimeException("Error setting preferred formats");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting preferred formats", e);
        }
    }

    public void setPreferredLocale(final String userUuid, final String code) {
        final String validCode = getValidLocaleCode(code);

        try {
            final LocaleResponse localeResponse = localeClient.setPreferredLocale(
                    SetPreferredLocaleRequest.newBuilder().setUserUuid(userUuid).setCode(validCode).build(),
                    webRequestContextProvider.getRequestContext())
                    .toBlocking()
                    .single();

            if (!localeResponse.getStatus().getSuccess()) {
                logger.info("Request returned with failed status: " + localeResponse.getStatus().getMessage(0));
                throw new RuntimeException("Error setting preferred locale");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting preferred locale", e);
        }
    }

    public String getValidLocaleCode(final String code) {
        if (code == null) {
            return DEFAULT_LOCALE_CODE;
        }

        final List<WMLocale> locales = getSupportedLocale();

        for (final WMLocale locale : locales) {
            if (code.equalsIgnoreCase(locale.getCode())) {
                return locale.getCode();
            }
        }

        return DEFAULT_LOCALE_CODE;
    }

    public String getValidFormatCode(final String code) {
        if (code == null) {
            return DEFAULT_FORMAT_CODE;
        }

        final List<WMFormat> formats = getSupportedFormats();

        for (final WMFormat format : formats) {
            if (code.equalsIgnoreCase(format.getCode())) {
                return format.getCode();
            }
        }

        return DEFAULT_FORMAT_CODE;
    }

}

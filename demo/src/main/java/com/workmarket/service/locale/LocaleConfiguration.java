package com.workmarket.service.locale;

import com.workmarket.biz.LocaleClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LocaleConfiguration {

    /**
     * Wire LocaleClient.
     *
     * @return LocaleClient.
     */
    @Bean(name = "LocaleClient")
    protected LocaleClient getLocaleClient() throws IOException {
        return new LocaleClient();
    }
}

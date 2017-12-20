package com.workmarket.web.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.model.resolver.AssignmentsRequestDTOArgumentResolver;
import com.workmarket.api.model.resolver.CustomFieldGroupsArgumentResolver;
import com.workmarket.api.model.resolver.EmailAddressArgumentResolver;
import com.workmarket.api.model.resolver.FeedRequestDTOArgumentResolver;
import com.workmarket.api.model.resolver.LocationContactsArgumentResolver;
import com.workmarket.api.model.resolver.PartArgumentResolver;
import com.workmarket.api.model.resolver.PhoneNumberArgumentResolver;
import com.workmarket.api.model.resolver.SendToGroupsArgumentResolver;
import com.workmarket.api.model.resolver.UserNotificationSearchRequestArgumentResolver;
import com.workmarket.api.model.resolver.WebsiteArgumentResolver;
import com.workmarket.api.model.resolver.WorkersSearchRequestArgumentResolver;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.ApiV1ResponseToHtmlHttpMessageConverter;
import com.workmarket.api.v1.MappingJacksonJsonpHttpMessageConverter;
import com.workmarket.api.model.resolver.ApiArgumentResolverImpl;
import com.workmarket.api.v1.xstream.ApiV1ResponseMetaXMLConverter;
import com.workmarket.api.v1.xstream.ApiV1ResponseXMLConverter;
import com.workmarket.api.v1.xstream.BooleanConverter;
import com.workmarket.api.v1.xstream.ListConverter;
import com.workmarket.api.v1.xstream.MapConverter;
import com.workmarket.api.v1.xstream.NoopXmlFriendlyReplacer;
import com.workmarket.api.v1.xstream.StreamDriver;
import com.workmarket.common.util.proto.JacksonProtoModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.site.SitePreferenceWebArgumentResolver;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	private static final Log logger = LogFactory.getLog(WebConfig.class);
	private static final String LOCALE_LANG_PARAM_NAME = "lang";

	@Bean
	public XStreamMarshaller getApiXmlMarshaller() {
		XStreamMarshaller apiXmlMarshaller = new XStreamMarshaller();
		apiXmlMarshaller.setStreamDriver(new StreamDriver(new NoopXmlFriendlyReplacer(null, "_")));
		apiXmlMarshaller.setMode(1001);
		apiXmlMarshaller.setAliases(ImmutableMap.of("envelope", ApiV1Response.class));
		apiXmlMarshaller.setConverters(new ApiV1ResponseXMLConverter(),
				new ApiV1ResponseMetaXMLConverter(),
				new MapConverter(),
				new ListConverter(),
				getBooleanConverter());
		return apiXmlMarshaller;
	}

	@Bean
	public BooleanConverter getBooleanConverter() {
		return BooleanConverter.getInstance();
	}

	@Bean
	public HttpMessageConverter getMarshallingHttpMessageConverter() {
		MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
		converter.setMarshaller(getApiXmlMarshaller());
		converter.setUnmarshaller(getApiXmlMarshaller());
		return converter;
	}

	@Bean
	@Qualifier("jsonHttpMessageConverter")
	public ApiBaseHttpMessageConverter getApiBaseHttpMessageConverter() {
		ApiBaseHttpMessageConverter converter = new ApiBaseHttpMessageConverter();
		converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
		// set up ObjectMapper to use all fields
		// TODO API - make this JsonAutoDetect.Visibility.PUBLIC_ONLY
		converter.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		converter.getObjectMapper().registerModule(new JacksonProtoModule());
		return converter;
	}

	@Bean
	public HttpMessageConverter getMappingJacksonJsonpHttpMessageConverter() {
		MappingJacksonJsonpHttpMessageConverter converter = new MappingJacksonJsonpHttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/javascript"));
		return converter;
	}

	@Bean
	public HttpMessageConverter getApiV1ResponseToHtmlMessageConverter() {
		ApiV1ResponseToHtmlHttpMessageConverter converter = new ApiV1ResponseToHtmlHttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/html"));
		return converter;
	}


	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		converters.add(new StringHttpMessageConverter());
		converters.add(getMarshallingHttpMessageConverter());
		converters.add(getApiBaseHttpMessageConverter());
		converters.add(getMappingJacksonJsonpHttpMessageConverter());
		converters.add(getApiV1ResponseToHtmlMessageConverter());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
		argumentResolvers.add(new ServletWebArgumentResolverAdapter(new DeviceWebArgumentResolver()));
		argumentResolvers.add(new ServletWebArgumentResolverAdapter(new SitePreferenceWebArgumentResolver()));
		argumentResolvers.add(new UserNotificationSearchRequestArgumentResolver());
		argumentResolvers.add(getApiArgumentResolver());
	}

	@Bean
	public ApiArgumentResolverImpl getApiArgumentResolver() {
		return new ApiArgumentResolverImpl(Lists.newArrayList(
				new CustomFieldGroupsArgumentResolver(),
				new PartArgumentResolver(),
				new SendToGroupsArgumentResolver(),
				new LocationContactsArgumentResolver(),
				new PhoneNumberArgumentResolver(),
				new WebsiteArgumentResolver(),
				new EmailAddressArgumentResolver(),
				new WorkersSearchRequestArgumentResolver(),
				new FeedRequestDTOArgumentResolver(),
				new AssignmentsRequestDTOArgumentResolver()
		));
	}

	/* Localization section begins. */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor(){
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(LOCALE_LANG_PARAM_NAME);
		return localeChangeInterceptor;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver getLocaleResolver(){
		CookieLocaleResolver localeContextResolver = new CookieLocaleResolver();
		localeContextResolver.setDefaultLocale(Locale.ENGLISH);
		localeContextResolver.setCookieName(LOCALE_LANG_PARAM_NAME);
		return localeContextResolver;
	}
	/* Localization section ends. */
}

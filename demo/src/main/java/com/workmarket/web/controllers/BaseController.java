package com.workmarket.web.controllers;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.PrivacyEvaluator;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.model.InternalPrivateNetworkAdmitted;
import com.workmarket.domains.velvetrope.rope.InternalPrivateNetworkAdmittedRope;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.LocaleSerializationService;
import com.workmarket.service.business.OrgSerializationService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNavService;
import com.workmarket.service.configuration.MobileConstants;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.web.cachebusting.CacheBusterService;
import com.workmarket.biz.gen.Messages.WMLocale;
import com.workmarket.biz.gen.Messages.WMFormat;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.filter.LocaleFilter;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import static com.workmarket.utility.WebUtilities.isPageRequest;

@Controller
public class BaseController {
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	private static Map<String, String> manifestMap;

	@Autowired @Qualifier("accountRegisterServicePrefundImpl") protected AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired protected PricingService pricingService;
	@Autowired protected CompanyService companyService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private PrivacyEvaluator privacyEvaluator;
	@Autowired private UserNavService userNavService;
	@Autowired private CacheBusterService cacheBusterService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private ResourceLoader resourceLoader;
	@Autowired @Qualifier("internalPrivateNetworkDoorman") private Doorman internalPrivateNetworkRope;
	@Autowired private LocaleService localeService;
	@Autowired private LocaleSerializationService localeJsonService;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private OrgSerializationService orgSerializationService;

	@Value("${sugar.client.base_url}")
	protected String SUGAR_CLIENT_URL;

	@Value("${google.analytics.propertyId}")
	protected String GOOGLE_ANALYTICS_PROPERTY_ID;

	@Value("${segment.writekey}")
	protected String SEGMENT_WRITE_KEY;

	@Value("${google.recaptcha.site.key}")
	protected String GOOGLE_RECAPTCHA_SITE_KEY;

	@Value("${manifest.build.path}")
	protected String MANIFEST_PATH;

	@Value("${cache.build.path}")
	protected String CACHE_PATH;

	@InitBinder
	public void initialBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
	}

	@ModelAttribute("mediaPrefix")
	public String initCacheBuster() {
		return cacheBusterService.getMediaPrefix();
	}

	protected Authentication getAuthentication() {
		return securityContextFacade.getSecurityContext().getAuthentication();
	}

	protected SecurityContext getSecurityContext() {
		return securityContextFacade.getSecurityContext();
	}

	protected boolean isAuthenticated() {
		return getCurrentUser() != null;
	}

	public boolean hasFeature(String feature) {
		return featureEvaluator.hasFeature(getAuthentication(), feature);
	}

	protected boolean hasFeature(Long companyId, String feature) {
		return featureEvaluator.hasFeature(companyId, feature);
	}

	protected boolean hasFeature(UserDetails user, Object feature) {
		return featureEvaluator.hasFeature(user, feature);
	}

	protected boolean hasGlobalFeature(String feature) {
		return featureEvaluator.hasGlobalFeature(feature);
	}

	protected boolean isProtected(String profileProperty) {
		return privacyEvaluator.isProtected(getAuthentication(), profileProperty);
	}

	@ModelAttribute("sugarUrl")
	public String getSugarUrl() {
		return SUGAR_CLIENT_URL;
	}

	@ModelAttribute("googleAnalyticsPropertyId")
	public String getGoogleAnalyticsPropertyId() {
		return GOOGLE_ANALYTICS_PROPERTY_ID;
	}

	@ModelAttribute("googleRecaptchaSiteKey")
	public String getGoogleRecaptchaSiteKey() {
		return GOOGLE_RECAPTCHA_SITE_KEY;
	}

	@ModelAttribute("segmentWriteKey")
	public String getSegmentWriteKey() {
		return SEGMENT_WRITE_KEY;
	}

	@ModelAttribute("currentUser")
	protected ExtendedUserDetails getCurrentUser() {
		return securityContextFacade == null ? null : securityContextFacade.getCurrentUser();
	}

	@ModelAttribute("isNavPinnedOpen")
	protected boolean isNavPinnedOpen() {
		if (!isAuthenticated()) {
			return false;
		}
		Long userId = getCurrentUser().getId();
		Map<String, String> returnedRedis = userNavService.get(userId);
		// check for false, that way if the value isn't set in redis, it will default to open
		return !"false".equals(returnedRedis.get("open"));
	}

	@ModelAttribute("currentLocale")
	protected String currentLocale(final HttpServletRequest request) {
		return (String) request.getSession().getAttribute(LocaleFilter.LOCALE_SESSION_ID);
	}

	@ModelAttribute("currentFormat")
	protected String currentFormat(final HttpServletRequest request) {
		return (String) request.getSession().getAttribute(LocaleFilter.FORMAT_SESSION_ID);
	}

	@ModelAttribute("preferredLocale")
	protected String preferredLocale(final HttpServletRequest request) {
		final boolean hasLocaleFeature = isAuthenticated() && featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");
		if (hasLocaleFeature) {
			return localeService.getPreferredLocale(getCurrentUser().getUuid()).getCode();
		} else {
			return (String) request.getSession().getAttribute(LocaleFilter.LOCALE_SESSION_ID);
		}
	}

	@ModelAttribute("preferredFormat")
	protected String preferredFormat(final HttpServletRequest request) {
		final boolean hasLocaleFeature = isAuthenticated() && featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");
		if (hasLocaleFeature) {
			return localeService.getPreferredFormat(getCurrentUser().getUuid()).getCode();
		} else {
			return (String) request.getSession().getAttribute(LocaleFilter.FORMAT_SESSION_ID);
		}
	}

	@ModelAttribute("supportedLocalesList")
	protected String supportedLocalesList(final HttpServletRequest request) {
		final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(Constants.WORKMARKET_SYSTEM_USER_ID, "locale");
		return hasLocaleFeature ? localeJsonService.toJson(localeService.getSupportedLocale()) : "[]";
	}

	@ModelAttribute("companyLogoUri")
	protected String companyLogoUri(HttpServletRequest request) {
		if (isAuthenticated() && isPageRequest(request)) {
			CompanyAssetAssociation avatar = companyService.findCompanyAvatars(getCurrentUser().getCompanyId());

			if (avatar == null || avatar.getTransformedLargeAsset() == null) {
				return null;
			}
			return avatar.getTransformedLargeAsset().getCdnUri();
		}
		return null;
	}

	@ModelAttribute("availableFunds")
	protected BigDecimal availableFunds(HttpServletRequest request) {
		if (isAuthenticated() && isPageRequest(request)) {
			AccountRegisterSummaryFields register = accountRegisterServicePrefundImpl.getAccountRegisterSummaryFields(getCurrentUser().getCompanyId());
			if (register != null) {
				return register.getWithdrawableCash();
			}
		}
		return BigDecimal.ZERO;
	}

	@ModelAttribute("uploadStatus")
	protected double uploadStatus() {
		if (isAuthenticated() && redisAdapter != null) {
			String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(getCurrentUser().getId());
			double progress = Double.valueOf((String) redisAdapter.get(uploadProgressKey).or("1"));
			return progress;
		}
		return 1;
	}

	@ModelAttribute("manifestMap")
	protected Map<String, String> getManifestMap() {
		try {
			final Map<Object, Object> manifestCacheMap = redisAdapter.getAllForHash(RedisFilters.webpackManifestKey());
			final Optional<Object> manifestCacheHash = redisAdapter.get(RedisFilters.webpackManifestHashKey());
			final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			final String currentHash =
					resolver.getResources(MANIFEST_PATH)[0]
					.getFilename()
					.split(Pattern.quote("."))[0];

			if (!manifestCacheMap.isEmpty() && manifestCacheHash.isPresent()) {
				final String cacheHashId = manifestCacheHash.get().toString();

				if (!cacheHashId.equals(currentHash)) {
					manifestMap = loadManifestMap();
					redisAdapter.set(RedisFilters.webpackManifestKey(), manifestMap);
					redisAdapter.set(RedisFilters.webpackManifestHashKey(), currentHash);
				} else {
					ObjectMapper mapper = new ObjectMapper();
					manifestMap = mapper.convertValue(manifestCacheMap, Map.class);
				}
			} else {
				manifestMap = loadManifestMap();
				redisAdapter.set(RedisFilters.webpackManifestKey(), manifestMap);
				redisAdapter.set(RedisFilters.webpackManifestHashKey(), currentHash);
			}
			return manifestMap;
		} catch (Exception error) {
			logger.error("Webpack manifest file not found.", error);
			return Collections.emptyMap();
		}
	}

	@ModelAttribute("isWorkerCompany")
	public boolean isWorkerCompany() {
		boolean isResourceCustomer = false;
		if (isAuthenticated()) {
			String customerType = companyService.getCustomerType(getCurrentUser().getCompanyId());
			if (CustomerType.RESOURCE.value().equals(customerType)) {
				isResourceCustomer = true;
			}
		}
		return isResourceCustomer;
	}

	@ModelAttribute("orgStructuresData")
	public Map<String, Object> getOrgStructuresData(HttpServletRequest request) {
		if (isAuthenticated() && isPageRequest(request) &&
				featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "org_structures")) {
			final Long userId = getCurrentUser().getId();
			return CollectionUtilities.newObjectMap(
					"isEnabled", true,
					"orgModesJson", orgSerializationService.toJson(orgStructureService.getOrgModeOptions(userId)),
					"activeOrgMode", orgStructureService.getOrgModeSetting(userId)
			);
		} else {
			return CollectionUtilities.newObjectMap("isEnabled", false);
		}
	}

	@ModelAttribute("isLocaleEnabled")
	public boolean isLocaleEnabled(HttpServletRequest request) {
		return isAuthenticated() && isPageRequest(request) && featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");
	}

	protected BigDecimal getGeneralCash() {
		if (isAuthenticated()) {
			return accountRegisterServicePrefundImpl.calculateGeneralCashByCompany(getCurrentUser().getCompanyId());
		}
		return BigDecimal.ZERO;
	}

	protected BigDecimal getAPLimit() {
		if (isAuthenticated() && companyService.hasPaymentTermsEnabled(getCurrentUser().getCompanyId())) {
			return pricingService.calculateRemainingAPBalance(getCurrentUser().getCompanyId());
		}
		return BigDecimal.ZERO;
	}

	protected BigDecimal getSpendLimit() {
		if (isAuthenticated()) {
			return accountRegisterServicePrefundImpl.calcSufficientBuyerFundsByCompany(getCurrentUser().getCompanyId());
		}
		return BigDecimal.ZERO;
	}

	protected boolean isMobile(HttpServletRequest request, SitePreference site) {
		if (request != null && !StringUtils.containsIgnoreCase(request.getQueryString(), "site_preference=normal")) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie c : cookies) {
					if (MobileConstants.APP_PLATFORM_ATTR.equals(c.getName())) {
						return true;
					}
				}
			}
		}

		return (site != null && site.isMobile());
	}

	protected AjaxResponseBuilder getAjaxResponseWithError(MessageBundle messages) {
		return AjaxResponseBuilder.fail().setMessages(messages.getErrors());
	}

	protected boolean isUserPrivateEmployee() {
		ExtendedUserDetails userDetails = getCurrentUser();
		InternalPrivateNetworkAdmitted internalPrivateNetworkAdmitted = new InternalPrivateNetworkAdmitted();

		if (userDetails.isInSearch() || userDetails.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER")) {
			return false;
		}

		internalPrivateNetworkRope.welcome(
			new WebGuest(userDetails),
			new InternalPrivateNetworkAdmittedRope(internalPrivateNetworkAdmitted)
		);

		return internalPrivateNetworkAdmitted.getAdmitted();
	}

	private Map<String, String> loadManifestMap() {
		try {
			final Gson gson = new Gson();
			final Type mapType = new TypeToken<Map<String, String>>() {}.getType();
			final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			final URL manifestPath = resolver.getResources(MANIFEST_PATH)[0].getURL();

			final FileReader reader = new FileReader(new File(manifestPath.getPath()));

			return gson.fromJson(reader, mapType);
		} catch (Exception error) {
			logger.error("Webpack manifest file not found.", error);
			return Collections.emptyMap();
		}
	}
}

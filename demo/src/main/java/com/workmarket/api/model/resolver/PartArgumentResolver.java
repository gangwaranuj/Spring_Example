package com.workmarket.api.model.resolver;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.PartDistributionMethodType;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PartArgumentResolver implements HandlerMethodArgumentResolver {

	private static final List<String> EMPTY_PROPERTIES = Lists.newArrayList(null, "");

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (supportsParameter(methodParameter)) {
			HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
			Map<String, String> parts = populateParts(request);

			return evaluateArgument(parts);
		}
		return null;
	}

	public boolean supportsParameter(MethodParameter methodParameter) {
		return (methodParameter.hasParameterAnnotation(ApiArgumentResolver.class) && (PartGroupDTO.class == methodParameter.getParameterType()));
	}

	protected PartGroupDTO evaluateArgument(Map<String, String> parts) {
		if (parts.isEmpty()) {
			return null;
		}

		PartGroupDTO partGroupDTO = new PartGroupDTO();

		partGroupDTO.setSuppliedByWorker("1".equals(parts.get("supplied_by_resource")));
		partGroupDTO.setReturnRequired("1".equals(parts.get("return_required")));

		if (parts.get("distribution_method") != null) {
			try {
				partGroupDTO.setShippingDestinationType(
					ShippingDestinationType.convertFromDistributionMethod(
						PartDistributionMethodType.valueOf(parts.get("distribution_method").toUpperCase())
					)
				);
			} catch (IllegalArgumentException e) {
				partGroupDTO.setShippingDestinationType(null);
			}
		}

		if (areAnyLocationPropertiesPresent(
			parts.get("pickup_location_id"),
			parts.get("pickup_location_name"),
			parts.get("pickup_location_number"),
			parts.get("pickup_location_address1"),
			parts.get("pickup_location_address2"),
			parts.get("pickup_location_city"),
			parts.get("pickup_location_state"),
			parts.get("pickup_location_zip"),
			parts.get("pickup_location_type"),
			parts.get("pickup_location_country")
		)) {
			partGroupDTO.setShipToLocation(new LocationDTO(
				NumberUtils.createLong(parts.get("pickup_location_id")),
				parts.get("pickup_location_name"),
				parts.get("pickup_location_number"),
				parts.get("pickup_location_address1"),
				parts.get("pickup_location_address2"),
				parts.get("pickup_location_city"),
				parts.get("pickup_location_state"),
				parts.get("pickup_location_zip"),
				parts.get("pickup_location_type"),
				parts.get("pickup_location_country")
			));
		}

		if (areAnyLocationPropertiesPresent(
			parts.get("return_location_id"),
			parts.get("return_location_name"),
			parts.get("return_location_number"),
			parts.get("return_location_address1"),
			parts.get("return_location_address2"),
			parts.get("return_location_city"),
			parts.get("return_location_state"),
			parts.get("return_location_zip"),
			parts.get("return_location_type"),
			parts.get("return_location_country")
		)) {
			partGroupDTO.setReturnToLocation(new LocationDTO(
				NumberUtils.createLong(parts.get("return_location_id")),
				parts.get("return_location_name"),
				parts.get("return_location_number"),
				parts.get("return_location_address1"),
				parts.get("return_location_address2"),
				parts.get("return_location_city"),
				parts.get("return_location_state"),
				parts.get("return_location_zip"),
				parts.get("return_location_type"),
				parts.get("return_location_country")
			));
		}

		partGroupDTO.addPart(
			parsePart(
				parts.get("pickup_shipping_provider"), parts.get("pickup_tracking_number"), NumberUtils.createBigDecimal(parts.get("pickup_part_value")), false
			)
		);
		partGroupDTO.addPart(
			parsePart(
				parts.get("return_shipping_provider"), parts.get("return_tracking_number"), NumberUtils.createBigDecimal(parts.get("return_part_value")), true
			)
		);

		return partGroupDTO;
	}

	protected Map<String,String> populateParts(HttpServletRequest request) {
		Map<String, String> parts = new HashMap<>();
		Pattern pattern = Pattern.compile("parts\\[(.+)\\]");

		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String)e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				String key = matcher.group(1);
				parts.put(key, request.getParameter(paramName));
			}
		}

		return parts;
	}

	private static PartDTO parsePart(String shippingProvider, String trackingNumber, BigDecimal value, boolean isReturn) {
		if (isNotBlank(shippingProvider) || isNotBlank(trackingNumber) || value != null) {
			return new PartDTO(
				ShippingProvider.getShippingProvider(shippingProvider),
				trackingNumber,
				value,
				isReturn,
				isReturn ? "Return Part" : "Part");
		}
		return null;
	}

	private static boolean areAnyLocationPropertiesPresent(String... locationProperties) {
		List<String> propertiesAsList = Lists.newArrayList(locationProperties);
		propertiesAsList.removeAll(EMPTY_PROPERTIES);
		return !CollectionUtils.isEmpty(propertiesAsList);
	}
}

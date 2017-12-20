package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.pricing.*;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.thrift.work.exception.WorkRowParseException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PricingStrategyParserImpl implements PricingStrategyParser {

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		Map<String,String> types = buildData.getTypes();

		PricingStrategyType type;
		try {
			type = calculatePricingStrategyType(types);

			if (type == null) { return; }

			final PricingStrategy strategy;
			final Work work = response.getWork();
			switch (type) {
				case FLAT: {
					strategy = parseFlatPricing(work, types);
					break;
				}
				case BLENDED_PER_HOUR: {
					strategy = parseBlendedPerHourPricing(work, types);
					break;
				}
				case PER_HOUR: {
					strategy = parsePerHourPricing(work, types);
					break;
				}
				case PER_UNIT: {
					strategy = parsePerUnitPricing(work, types);
					break;
				}
				default: {
					throw throwUnsupportedPricingStrategyException(type);
				}
			}
			response.getWork().setPricing(strategy);
		} catch (WorkRowParseException e) {
			for (WorkRowParseError error : e.getErrors()) {
				response.addToRowParseErrors(error);
			}
		}
	}

	private static PricingStrategy parsePerUnitPricing(Work work, Map<String, String> types) throws WorkRowParseException {
		PricingStrategy strategy = new PricingStrategy().setId(new PerUnitPricingStrategy().getId());
		Float perUnitPrice = null;
		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE)) {
			try {
				perUnitPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE);
			}
			setDisplayMode(work, true);
		} else {
			try {
				perUnitPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE);
			}
			setDisplayMode(work, false);
		}

		if (perUnitPrice != null)
			strategy.setPerUnitPrice(perUnitPrice);

		try {
			final Float maxNumberOfUnits = WorkUploadColumn.parseFloat(types, WorkUploadColumn.MAX_NUMBER_OF_UNITS);
			if (maxNumberOfUnits != null) {
				strategy.setMaxNumberOfUnits(maxNumberOfUnits);
			}
		} catch (NumberFormatException e) {
			throwDataParseException(WorkUploadColumn.MAX_NUMBER_OF_UNITS);
		}

		return strategy;
	}

	private static PricingStrategy parsePerHourPricing(Work work, Map<String, String> types) throws WorkRowParseException {
		PricingStrategy strategy = new PricingStrategy().setId(new PerHourPricingStrategy().getId());
		Float perHourPrice = null;
		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE)) {
			try {
				perHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE);
			}
			setDisplayMode(work, true);
		} else {
			try {
				perHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE);
			}
			setDisplayMode(work, false);
		}

		if (perHourPrice != null) {
			strategy.setPerHourPrice(perHourPrice);
		}

		try {
			final Float maxNumberOfHours = WorkUploadColumn.parseFloat(types, WorkUploadColumn.MAX_NUMBER_OF_HOURS);
			if (maxNumberOfHours != null) {
				strategy.setMaxNumberOfHours(maxNumberOfHours);
			}
		} catch (NumberFormatException e) {
			throwDataParseException(WorkUploadColumn.MAX_NUMBER_OF_HOURS);
		}

		return strategy;
	}

	private static PricingStrategy parseBlendedPerHourPricing(Work work, Map<String, String> types) throws WorkRowParseException {
		PricingStrategy strategy = new PricingStrategy().setId(new BlendedPerHourPricingStrategy().getId());
		// case where client fee and resource fee differ?

		Float initialPerHourPrice = null;
		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE)) {
			try {
				initialPerHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE);
			}
			setDisplayMode(work, true);
		} else {
			try {
				initialPerHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE);
			}
			setDisplayMode(work, false);
		}

		Float additionalPerHourPrice = null;
		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE)) {
			try {
				additionalPerHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE);
			}
		} else {
			try {
				additionalPerHourPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE);
			}
		}

		Float maxHoursAtInitialPrice = null;
		Float maxBlendedHours = null;

		try {
			maxHoursAtInitialPrice = WorkUploadColumn.parseFloat(types, WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE);
		} catch (NumberFormatException e) {
			throwDataParseException(WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE);
		}

		try {
			maxBlendedHours = WorkUploadColumn.parseFloat(types, WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE);
		} catch (NumberFormatException e) {
			throwDataParseException(WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE);
		}

		if (initialPerHourPrice != null) {
			strategy.setInitialPerHourPrice(initialPerHourPrice);
		}
		if (additionalPerHourPrice != null) {
			strategy.setAdditionalPerHourPrice(additionalPerHourPrice);
		}
		if (maxHoursAtInitialPrice != null) {
			strategy.setInitialNumberOfHours(maxHoursAtInitialPrice);
		}
		if (maxBlendedHours != null) {
			strategy.setMaxBlendedNumberOfHours(maxBlendedHours);
		}

		return strategy;
	}

	private static PricingStrategy parseFlatPricing(Work work, Map<String, String> types) throws WorkRowParseException {
		PricingStrategy strategy = new PricingStrategy().setId(new FlatPricePricingStrategy().getId());
		Float flatPrice = null;
		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.FLAT_PRICE_CLIENT_FEE)) {
			try {
				flatPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.FLAT_PRICE_CLIENT_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.FLAT_PRICE_CLIENT_FEE);
			}
			setDisplayMode(work, true);
		} else {
			try {
				flatPrice = WorkUploadColumn.parsePrice(types, WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE);
			} catch (NumberFormatException e) {
				throwDataParseException(WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE);
			}
			setDisplayMode(work, false);
		}

		if (flatPrice != null) {
			strategy.setFlatPrice(flatPrice);
		}

		return strategy;
	}

	private static PricingStrategyType calculatePricingStrategyType(Map<String, String> types) throws WorkRowParseException {
		PricingStrategyType typeToReturn = null;

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.FLAT_PRICE_CLIENT_FEE, WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE)) {
			typeToReturn = PricingStrategyType.FLAT;
			//make sure one or the other but not both are there
			if (WorkUploadColumn.containsAll(types, WorkUploadColumn.FLAT_PRICE_CLIENT_FEE, WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE)) {
				throwInvalidDataException(typeToReturn);
			}
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE, WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE)) {
			typeToReturn = multipleStrategyCheck(typeToReturn, PricingStrategyType.BLENDED_PER_HOUR);
			if (WorkUploadColumn.containsAll(types, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE)) {
				throwInvalidDataException(typeToReturn);
			}
			if (WorkUploadColumn.containsAll(types, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE)) {
				throwInvalidDataException(typeToReturn);
			}
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.MAX_NUMBER_OF_HOURS, WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE)) {
			typeToReturn = multipleStrategyCheck(typeToReturn, PricingStrategyType.PER_HOUR);
			if (WorkUploadColumn.containsAll(types, WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE, WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE)) {
				throwInvalidDataException(typeToReturn);
			}
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.MAX_NUMBER_OF_UNITS, WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE, WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE)) {
			typeToReturn = multipleStrategyCheck(typeToReturn, PricingStrategyType.PER_UNIT);
			if (WorkUploadColumn.containsAll(types, WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE, WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE)) {
				throwInvalidDataException(typeToReturn);
			}
		}

		return typeToReturn;
	}

	private static void setDisplayMode(Work work, boolean maxSpendMode) {
		if (!work.isSetConfiguration()) {
			work.setConfiguration(new ManageMyWorkMarket());
		}
		work.getConfiguration().setUseMaxSpendPricingDisplayModeFlag(maxSpendMode);
	}

	private static void throwDataParseException(WorkUploadColumn column) throws WorkRowParseException {
		WorkRowParseError error = new WorkRowParseError();
		error.setColumn(column);
		error.setMessage("Invalid data format for " + column.getUploadColumnDescription());
		error.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		throw new WorkRowParseException(Lists.newArrayList(error));
	}

	private static void throwInvalidDataException(PricingStrategyType pricingStrategyType) throws WorkRowParseException {
		WorkRowParseError error = new WorkRowParseError();
		error.setMessage("Invalid data for the pricing strategy was passed. Please validate the rules to create a strategy for type: " + pricingStrategyType.getDescription());
		error.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		throw new WorkRowParseException(Lists.newArrayList(error));
	}

	private static PricingStrategyType multipleStrategyCheck(
			PricingStrategyType previousCheck, PricingStrategyType pricingStrategyType) throws WorkRowParseException {

		if (previousCheck == null) {
			return pricingStrategyType;
		}
		//throw exception - more than one strategy detected!!
		WorkRowParseError error = new WorkRowParseError();
		error.setColumn(WorkUploadColumn.FLAT_PRICE_CLIENT_FEE);
		error.setMessage("More than one type of pricing strategy provided: " + pricingStrategyType.getDescription() + " and " + previousCheck.getDescription() + ".");
		error.setErrorType(WorkRowParseErrorType.MULTIPLE_STRATEGIES_INFERRED);
		throw new WorkRowParseException(Lists.newArrayList(error));
	}

	private static WorkRowParseException throwUnsupportedPricingStrategyException(PricingStrategyType pricingStrategyType) throws WorkRowParseException {
		WorkRowParseError parseError = new WorkRowParseError();
		parseError.setErrorType(WorkRowParseErrorType.INVALID_DATA);
		parseError.setMessage("Unsupported pricing strategy detected " + pricingStrategyType.getDescription());
		return new WorkRowParseException(Lists.newArrayList(parseError));
	}
}

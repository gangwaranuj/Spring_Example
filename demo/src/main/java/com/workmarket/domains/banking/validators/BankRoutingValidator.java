package com.workmarket.domains.banking.validators;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by ianha on 4/27/15.
 */
public class BankRoutingValidator {
    public static boolean isValidRoutingNumber(String routingNumber) {
        return NumberUtils.isDigits(routingNumber) && routingNumber.length() == 9;
    }

    public static boolean isValidBranchNumber(String branchNumber) {
        return NumberUtils.isDigits(branchNumber) && branchNumber.length() == 5;
    }

    public static boolean isValidInstitutionNumber(String institutionNumber) {
        return NumberUtils.isDigits(institutionNumber) && institutionNumber.length() == 3;
    }
}

package com.workmarket.domains.banking.util;

import com.workmarket.domains.banking.validators.BankRoutingValidator;

/**
 * Created by ianha on 4/27/15.
 */
public class BankRoutingUtil {
    public static String buildRoutingNumber(String branchNumber, String institutionNumber) {
        if (!BankRoutingValidator.isValidBranchNumber(branchNumber) || !BankRoutingValidator.isValidInstitutionNumber(institutionNumber)) {
            return "";
        }

        return "0" + institutionNumber + branchNumber;
    }

    public static String getBranchNumber(String routingNumber) {
        if (BankRoutingValidator.isValidRoutingNumber(routingNumber)) {
            return routingNumber.substring(4);
        }

        return "";
    }

    public static String getInstitutionNumber(String routingNumber) {
        if (BankRoutingValidator.isValidRoutingNumber(routingNumber)) {
            return routingNumber.substring(1, 4);
        }

        return "";
    }
}

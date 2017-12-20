package com.workmarket.common.utils;

import com.workmarket.splitter.FeatureDomain;
import com.workmarket.splitter.WorkmarketComponent;

import java.util.Calendar;

@WorkmarketComponent(FeatureDomain.UTILS)
public class ConverterUtilities {
    public static String toString(Object o) {
        if(o == null) {
            return "";
        }

        if(o instanceof Calendar) {
            return "" + ((Calendar)o).getTimeInMillis();
        }
        return o.toString();
    }
}
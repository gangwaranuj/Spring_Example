package com.workmarket.utility;

import java.util.Calendar;

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
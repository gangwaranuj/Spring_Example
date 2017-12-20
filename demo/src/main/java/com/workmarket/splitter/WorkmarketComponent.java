package com.workmarket.splitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ant on 10/14/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkmarketComponent {

	FeatureDomain [] value();

}

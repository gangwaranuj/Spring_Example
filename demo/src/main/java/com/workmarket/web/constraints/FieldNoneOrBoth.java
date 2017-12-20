package com.workmarket.web.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by nick on 9/26/12 6:46 PM
 * A validator to verify that either 1) both fields are null or 2) both fields are not-null
 */

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldNoneOrBothValidator.class)
@Documented
public @interface FieldNoneOrBoth {
	String message() default "{validator.fieldNoneOrBoth}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The first field
	 */
	String first();

	/**
	 * @return The second field
	 */
	String second();

	/**
	 * Defines several <code>@FieldNoneOrBoth</code> annotations on the same element
	 *
	 * @see FieldNoneOrBoth
	 */
	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented @interface List {
		FieldNoneOrBoth[] value();
	}
}

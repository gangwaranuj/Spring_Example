package com.workmarket.domains.model.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation
 * <p/>
 * All entities marked with this annotation will have their
 * <p/>
 * created_on
 * modified_on
 * created_by
 * modified_by
 * <p/>
 * updated by AuditListened. All these fields will be managed for you.
 *
 * NOTE: this cannot be used when DAO does not have access to SecurityContext (i.e. async queue events not in ThreadLocal)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface AuditChanges {

}

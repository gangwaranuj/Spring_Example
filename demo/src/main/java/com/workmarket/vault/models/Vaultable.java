package com.workmarket.vault.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level annotation marking a given type as having fields that should be saved to the Vault.
 * Those fields MUST be marked with the {@link Vaulted} annotation. Classes with a {@link Vaultable}
 * annotation but without {@link Vaulted} fields does not get saved in any way.
 *
 * Reminder: Class annotations are NOT inheritable, so be sure to include this annotation
 * at every class level wherever {@link Vaulted} fields exist. Otherwise, fields marked
 * with {@link Vaulted} will not be saved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Vaultable {
}

package dev.truewinter.snowmail.api;

import java.lang.annotation.*;

/**
 * Fields annotated with NullableField may appear as null in the API responses
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface NullableField {
    // The TypeScript generator plugin requires a runtime annotation for nullable fields
}

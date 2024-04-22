package dev.truewinter.snowmail;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface NullableField {
    // The TypeScript generator plugin requires a runtime annotation for nullable fields
}

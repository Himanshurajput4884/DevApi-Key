package com.himanshu.kumar.LaughApi.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Annotation for excluding specific private API endpoints from rate limit
 * allowing them to be accessed without restrictions regardless of the user's current rate limit plan
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByPassRateLimit {

}

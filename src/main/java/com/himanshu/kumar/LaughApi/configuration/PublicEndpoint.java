package com.himanshu.kumar.LaughApi.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/***
 * Annotation to declare API endpoints as public i.e non-secured, allowing then to be
 * accessed without a valid Authorization header in HTTP request.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {

}

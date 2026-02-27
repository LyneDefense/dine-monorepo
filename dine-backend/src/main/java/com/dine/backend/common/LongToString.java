package com.dine.backend.common;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serialize Long to String to prevent JavaScript precision loss.
 * <p>
 * Usage:
 * <pre>
 * public class UserDTO {
 *     &#64;LongToString
 *     private Long id;
 *
 *     &#64;LongToString
 *     private Long restaurantId;
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ToStringSerializer.class)
public @interface LongToString {
}

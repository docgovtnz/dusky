package com.fronde.server.services.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the CheckPermission annotation. Add this to any method where you want a method level
 * security done. The preference would be to perform these checks at the Controller layer rather
 * than the Service layer, and certainly not the Repository layer.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {

  Permission value();

}

package com.hackathon.tichetelefonovani.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by povolny on 1.12.2017.
 */

@Target(PARAMETER)
@Retention(RUNTIME)
@interface Chunked {
}

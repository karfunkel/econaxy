package de.econaxy.server.domain

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * This annotates a groovy.util.ObservableSet, groovy.util.ObservableList or groovy.util.ObservableMap
 * as a Container for instances of the specified class inherited from DomainTrait.
 *
 * Parent child relations will updated automatically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface Container {
    Class value() default Object
}

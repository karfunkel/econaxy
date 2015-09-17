package de.econaxy.server.domain

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * This annotates a Collection to contain all instances based on the given path. <br/>
 * It listens to the DomainBus and detects all changes to Containers relevant in the path.
 *
 * The Path will be defined as a Closure for containing all Instances of that class or a Closure
 * receiving and returning a de.econaxy.server.domain.Path instance based on the DomainTrait where
 * the Collector is defined.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface Collector {
    Class value()
}

package de.econaxy.shared

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation defining the LABEL @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Label {
    String value()
}

/**
 * Annotation defining the WIDGETHINT @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface WidgetHint {
    String value()
}

/**
 * Annotation defining the REGEX @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Regex {
    String value()
}

/**
 * Annotation defining the HELPURL @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface HelpUrl {
    String value()
}

/**
 * Annotation defining the TOOLTIP @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Tooltip {
    String value()
}

/**
 * Annotation defining the MANDATORY @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Mandatory {
    boolean value() default true
}

/**
 * Annotation defining the VISIBLE @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Visible {
    boolean value() default true
}

/**
 * Annotation defining the ENABLED @org.opendolpin.core.Tag for this property.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Enabled {
    boolean value() default true
}

/**
 * Annotation defining a Closure creating the initial value for this property.
 *
 * e.g. <br/>
 * <pre>
 * <code>
 * {@literal @}InitialValue({ UUID.randomUUID() })
 *    String id
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface InitialValue {
    Class value()
}

/**
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
public @interface Parent {
}


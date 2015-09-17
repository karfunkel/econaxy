package de.econaxy.shared

import de.econaxy.shared.converter.Converters
import spock.lang.Specification
import spock.lang.Unroll

class ConverterTest extends Specification {
    def "Test INVERTER"() {
        expect:
        Converters.INVERTER.convert(true) == false
        Converters.INVERTER.convert(false) == true
    }

    @Unroll
    def "Test Converting from #from to #toType"() {
        def result
        when:
        result = Converters.convert(from, toType)

        then:
        result == to
        if(result != null)
            toType.isAssignableFrom(to.getClass())

        where:
        from | toType | to
        "Test" | String | "Test"
        9223372036854775807 | String | "9223372036854775807"
        "9223372036854775807" | Long | 9223372036854775807
        32767 | String | "32767"
        "32767" | Short | (short) 32767
        2147483647 | String | "2147483647"
        "2147483647" | Integer | 2147483647
        9223372036854775808 | String | "9223372036854775808"
        "9223372036854775808" | BigInteger | 9223372036854775808
        3.4028235E38 | String | "3.4028235E+38"
        "3.4028235E38" | Float | (float) 3.4028235E38
        3.4028235 | String | "3.4028235"
        "3.4028235" | Float | (float) 3.4028235
        1.7976931348623157E308 | String | "1.7976931348623157E+308"
        "1.7976931348623157E308" | Double | 1.7976931348623157E308d
        1.7976931348623157 | String | "1.7976931348623157"
        "1.7976931348623157" | Double | 1.7976931348623157d
        2.7976931348623157E308 | String | "2.7976931348623157E+308"
        "2.7976931348623157E308" | BigDecimal | 2.7976931348623157E308g
        true | String | "true"
        "true" | Boolean | true
        false | String | "false"
        "false" | Boolean | false
        "abc".charAt(0) | String | "a"
        "a" | Character | "abc".charAt(0)
        new Date(100, 4, 16, 21, 47, 59) | String | "2000-05-16 21:47:59.0"
        "2000-05-16 21:47:59.0" | Date | new Date(100, 4, 16, 21, 47, 59)
        new Date(100, 4, 16, 21, 47, 59) | Long | 958506479000l
        958506479000l | Date | new Date(100, 4, 16, 21, 47, 59)
        [1, "abc", 1.02d, 2.45, true] | String | "java.lang.Integer#~#1#,#java.lang.String#~#abc#,#java.lang.Double#~#1.02#,#java.math.BigDecimal#~#2.45#,#java.lang.Boolean#~#true"
        "java.lang.Integer#~#1#,#java.lang.String#~#abc#,#java.lang.Double#~#1.02#,#java.math.BigDecimal#~#2.45#,#java.lang.Boolean#~#true" | List | [1, "abc", 1.02d, 2.45, true]
        [key1: 1, (123): "abc", key2: 1.02d, (456d): 2.45, (789g): true] | String | "java.lang.String#~#key1#:#java.lang.Integer#~#1#,#java.lang.Integer#~#123#:#java.lang.String#~#abc#,#java.lang.String#~#key2#:#java.lang.Double#~#1.02#,#java.lang.Double#~#456.0#:#java.math.BigDecimal#~#2.45#,#java.math.BigInteger#~#789#:#java.lang.Boolean#~#true"
        "java.lang.String#~#key1#:#java.lang.Integer#~#1#,#java.lang.Integer#~#123#:#java.lang.String#~#abc#,#java.lang.String#~#key2#:#java.lang.Double#~#1.02#,#java.lang.Double#~#456.0#:#java.math.BigDecimal#~#2.45#,#java.math.BigInteger#~#789#:#java.lang.Boolean#~#true" | Map | [key1: 1, (123): "abc", key2: 1.02d, (456d): 2.45, (789g): true]
        null | String | "null"
        "null" | Integer | null
        "null" | Short | null
        "null" | Long | null
        "null" | BigInteger | null
        "null" | Double | null
        "null" | Float | null
        "null" | BigDecimal | null
        "null" | Boolean | null
        "null" | Character | null
        "null" | Date | null
        "null" | List | null
        "null" | Map | null

        // TODO: NULL Test

    }
}





package de.econaxy.shared.converter

import de.econaxy.shared.Constants
import org.codehaus.groovy.runtime.NullObject
import org.opendolphin.binding.Converter

class Converters {
    static private Map<String, Converter> converters = [:]

    static final Converter INVERTER = { def val -> !val }

    static {
        register(NullObject, String) { def val -> 'null' }
        register(String, String) { String val -> val == "null" ? null : val }
        register(Long, String) { Long val -> val?.toString() ?: "null" }
        register(String, Long) { String val -> val != 'null' ? Long.parseLong(val) : null }
        register(Short, String) { Short val -> val?.toString() ?: "null" }
        register(String, Short) { String val -> val != 'null' ? Short.parseShort(val) : null }
        register(Integer, String) { Integer val -> val?.toString() ?: "null" }
        register(String, Integer) { String val -> val != 'null' ? Integer.parseInt(val) : null }
        register(BigInteger, String) { BigInteger val -> val?.toString() ?: "null" }
        register(String, BigInteger) { String val -> val != 'null' ? new BigInteger(val) : null }

        register(Float, String) { Float val -> val?.toString() ?: "null" }
        register(String, Float) { String val -> val != 'null' ? Float.parseFloat(val) : null }
        register(Double, String) { Double val -> val?.toString() ?: "null" }
        register(String, Double) { String val -> val != 'null' ? Double.parseDouble(val) : null }
        register(BigDecimal, String) { BigDecimal val -> val?.toString() ?: "null" }
        register(String, BigDecimal) { String val -> val != 'null' ? new BigDecimal(val) : null }

        register(Boolean, String) { Boolean val -> val?.toString() ?: "null" }
        register(String, Boolean) { String val -> val != 'null' ? Boolean.parseBoolean(val) : null }

        register(Character, String) { Character val -> val?.toString() ?: "null" }
        register(String, Character) { String val -> val != 'null' ? val.charAt(0) : null }

        register(Date, Long) { Date val -> val?.time ?: 0 }
        register(Long, Date) { Long val -> val != 'null' ? new Date(val) : null }
        register(Date, String) { Date val -> val?.format(Constants.dateFormat) ?: '' }
        register(String, Date) { String val -> val != 'null' ? Date.parse(Constants.dateFormat, val) : null }

        register(Collection, String) { Collection val ->
            val?.collect { toValueString(it) }?.join(Constants.collectionSeparator) ?: "null"
        }
        register(Map, String) { Map val ->
            val?.collect {
                def key = toValueString(it.key)
                def value = toValueString(it.value)
                return "$key${Constants.mapKeyValueSeparator}$value"
            }?.join(Constants.mapSeparator) ?: "null"
        }

        register(String, Collection) { String val ->
            if (val == 'null')
                return null
            return val.split(Constants.collectionSeparator).collect {
                def parts = it.split(Constants.typeSeparator)
                return convert(parts[1], Class.forName(parts[0]))
            }
        }

        register(String, Map) { String val ->
            if (val == 'null')
                return null
            return val.split(Constants.mapSeparator).collect {
                it.split(Constants.mapKeyValueSeparator).collect {
                    def parts = it.split(Constants.typeSeparator)
                    return convert(parts[1], Class.forName(parts[0]))
                }
            }.collectEntries()
        }
    }

    private static String toValueString(Object value) {
        String to = value.getClass().name
        return "$to${Constants.typeSeparator}${convert(value.hasProperty('uid') ? value.uid : value, String)}"
    }

    static void register(String name, Closure converter) {
        register(name, converter as Converter)
    }

    static void register(String name, Converter converter) {
        if (!name)
            throw new IllegalArgumentException("Converter name '$name' is invalid")
        if (converters.containsKey(name))
            throw new IllegalArgumentException("Converter with name $name already registered")
        converters[name] = converter
    }

    static void register(Class from, Class to, Closure converter) {
        register(from, to, converter as Converter)
    }

    static void register(Class from, Class to, Converter converter) {
        String name = "${from.name}_${to.name}"
        if (converters.containsKey(name))
            throw new IllegalArgumentException("Converter from $from to $to already registered")
        converters[name] = converter
    }

    static Converter get(Class from, Class to) {
        Converter converter
        List<Class> fromHistory = getClassHistory(from)
        List<Class> toHistory = getClassHistory(to)
        for (Class f : fromHistory) {
            if (converter != null)
                break
            for (Class t : toHistory) {
                if (converter != null)
                    break
                converter = converters["${f.name}_${t.name}"]
            }
        }
        if (!converter)
            throw new IllegalArgumentException("Converter from $from to $to not found")
        return converter
    }

    private static List<Class> getClassHistory(Class clazz) {
        List<Class> clazzes = [clazz]
        def cls = clazz
        clazzes.addAll(cls.interfaces)
        while (cls.superclass) {
            cls = cls.superclass
            clazzes << cls
            clazzes.addAll(cls.interfaces)
        }
        return clazzes.unique { a, b -> a.name <=> b.name }
    }

    static Converter get(String name) {
        def converter = converters[name]
        if (!converter)
            throw new IllegalArgumentException("Converter with name $name not found")
        return converter
    }

    static Converter chain(Converter... converters) {
        return { value ->
            converters.inject(value) { val, converter ->
                return converter.convert(val)
            }
        } as Converter
    }

    static <F, T> T convert(F value, Class<T> to) {
        ((Converter) get(value.getClass(), to)).convert(value)
    }

    static def convert(def value, String name) {
        get(name).convert(value)
    }
}

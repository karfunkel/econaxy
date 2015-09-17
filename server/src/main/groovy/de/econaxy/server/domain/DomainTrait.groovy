package de.econaxy.server.domain

import java.lang.ref.WeakReference

trait DomainTrait {
    final DomainBus bus = DomainBus.instance
    String uid = UUID.randomUUID().toString()

    final static Map<Class, Map<String, WeakReference<? extends DomainTrait>>> instanceMap = [:].withDefault { key -> [:] }

    protected static Map<Class, String> parentPropertyMap = [:]
    protected final static String NO_PARENT = '$noparent$'

    String getParentProperty() {
        parentPropertyMap[this.getClass()]
    }

    String setParentProperty(String parentProperty) {
        parentPropertyMap[this.getClass()] = parentProperty
    }

    DomainTrait getParent() {
        parentProperty ? this.getProperty(parentProperty) : null
    }

    void setParent(DomainTrait parent) {
        if (parentProperty)
            this.setProperty(parentProperty, parent)
    }

    Path getPath() {
        return new Path(this)
    }

    static DomainTrait getById(Class cls, String uid) {
        return instanceMap[cls][uid].get()
    }

    static DomainTrait getById(String uid) {
        for(Class cls: instanceMap.keySet()) {
            def domain = getById(cls, uid)
            if(domain)
                return domain
        }
        return null
    }

    void init() {
        WeakReference ref = instanceMap[this.getClass()].put(this.uid, new WeakReference<DomainTrait>(this))
        if(ref?.get()) {
            throw new Exception("Instance-Map for class ${this.getClass()} already contained a value - something strange occurred")
        }
        bus.register(this)
        if (!getParentProperty()) {
            DomainUtil.withParent(this) { String parentPropertyName, Class childType ->
                parentProperty = parentPropertyName
            }
        }
        DomainUtil.withContainer(this) { Object container, String containerName, Class childType, String parentProperty ->
            bus.register(this, containerName)
            DomainUtil.registerContainerListener(container, this)
        }
        DomainUtil.withCollector(this) { Object collector, String collectorName, Path filter ->
            DomainUtil.registerCollectorListener(collector, this, filter)
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof DomainTrait)) return false
        DomainTrait domain = (DomainTrait) o
        if (uid != domain.uid) return false
        return true
    }

    int hashCode() {
        uid.hashCode()
    }

}

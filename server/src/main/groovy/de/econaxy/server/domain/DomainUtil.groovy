package de.econaxy.server.domain

import java.lang.reflect.Field

class DomainUtil {
    static void registerContainerListener(Object container, DomainTrait domain) {
        throw new RuntimeException("The method registerContainerListener may only be called with ObservableSet, ObservableList or ObservableMap")
    }

    static void registerContainerListener(ObservableSet container, DomainTrait domain) {
        container.addPropertyChangeListener { evt ->
            switch (evt) {
                case ObservableSet.ElementAddedEvent:
                    DomainTrait newElement = evt.newValue
                    newElement.parent = domain
                    break
                case ObservableSet.MultiElementAddedEvent:
                    evt.values.each { DomainTrait newElement ->
                        newElement.parent = domain
                    }
                    break
                case ObservableSet.ElementRemovedEvent:
                    DomainTrait oldElement = evt.oldValue
                    oldElement.parent = null
                    break
                case ObservableSet.MultiElementRemovedEvent:
                case ObservableSet.ElementClearedEvent:
                    evt.values.each { DomainTrait oldElement ->
                        oldElement.parent = null
                    }
                    break
            }
        }
    }

    static void registerContainerListener(ObservableList container, DomainTrait domain) {
        container.addPropertyChangeListener { evt ->
            switch (evt) {
                case ObservableList.ElementAddedEvent:
                    DomainTrait newElement = evt.newValue
                    newElement.parent = domain
                    break
                case ObservableList.MultiElementAddedEvent:
                    evt.values.each { DomainTrait newElement ->
                        newElement.parent = domain
                    }
                    break
                case ObservableList.ElementRemovedEvent:
                    DomainTrait oldElement = evt.oldValue
                    oldElement.parent = null
                    break
                case ObservableList.MultiElementRemovedEvent:
                case ObservableList.ElementClearedEvent:
                    evt.values.each { DomainTrait oldElement ->
                        oldElement.parent = null
                    }
                    break
                case ObservableList.ElementUpdatedEvent:
                    evt.oldValue.parent = null
                    evt.newValue.parent = this
                    break
            }
        }
    }

    static void registerContainerListener(ObservableMap container, DomainTrait domain) {
        container.addPropertyChangeListener { evt ->
            switch (evt) {
                case ObservableMap.PropertyAddedEvent:
                    DomainTrait newElement = evt.newValue
                    newElement.parent = domain
                    break
                case ObservableMap.MultiPropertyEvent:
                    evt.events.each { ObservableMap.PropertyEvent event ->
                        if (event instanceof ObservableMap.PropertyAddedEvent)
                            event.newValue.parent = domain
                        else if (event instanceof ObservableMap.PropertyRemovedEvent)
                            event.oldValue.parent = domain
                    }
                    break
                case ObservableMap.PropertyRemovedEvent:
                    DomainTrait oldElement = evt.oldValue
                    oldElement.parent = null
                    break
                case ObservableMap.PropertyClearedEvent:
                    evt.values.each { k, DomainTrait oldElement ->
                        oldElement.parent = null
                    }
                    break
                case ObservableMap.PropertyUpdatedEvent:
                    evt.oldValue.parent = null
                    evt.newValue.parent = domain
                    break
            }
        }
    }

    static void registerCollectorListener(Object collector, DomainTrait domain, Path filter) {
        throw new RuntimeException("The method registerCollectorListener may only be called with a Collection")
    }

    static void registerCollectorListener(Collection collector, DomainTrait domain, Path filter) {
        DomainBus.instance.subscribe(filter) { DomainBus.DomainEvent evt ->
            switch (evt.type) {
                case DomainBus.ChangeType.ADDED:
                    collector.add(evt.newValue)
                    break
                case DomainBus.ChangeType.MULTI_ADD:
                    collector.addAll(evt.values)
                    break
                case DomainBus.ChangeType.REMOVED:
                    collector.remove(evt.oldValue)
                    break
                case DomainBus.ChangeType.MULTI_REMOVE:
                    collector.removeAll(evt.values)
                    break
                case DomainBus.ChangeType.CLEARED:
                    collector.clear()
                    break
            }
        }
    }

    static void withParent(DomainTrait domain, Closure closure) {
        domain.getClass().declaredFields.findAll { it.isAnnotationPresent(Parent) }.each { parent ->
            closure(parent.name, parent.type)
        }
    }

    static void withContainer(DomainTrait domain, Closure closure) {
        domain.getClass().declaredFields.findAll { it.isAnnotationPresent(Container) }.each { container ->
            if ([ObservableList, ObservableSet, ObservableMap].any { it.isAssignableFrom(container.type) }) {
                def annotation = container.getAnnotation(Container)
                Class childType = annotation.value()
                if(childType == Object)
                    throw new IllegalArgumentException("$Container must define a type if used with an $ObservableList, $ObservableSet or $ObservableMap")
                Field[] childField = childType.declaredFields.findAll { it.isAnnotationPresent(Parent) }
                if (childField.size() == 0)
                    throw new IllegalArgumentException("Type $childType has no property annotated with $Parent")
                else if (childField.size() > 1)
                    throw new IllegalArgumentException("Type $childType has multiple properties annotated with $Parent")
                else {
                    def containerObj = domain.getProperty(container.name)
                    closure(containerObj, container.name, childType, childField.first().getName())
                }
            } else if(DomainTrait.isAssignableFrom(container.type)) {
                domain.addPropertyChangeListener(container.name) { evt ->
                    if (evt.oldValue) {
                        evt.oldValue.parent = null
                    }
                    if (evt.newValue) {
                        evt.newValue.parent = domain
                    }
                }
            } else
                throw new IllegalArgumentException("Property $container.name annotated with $Container has to be an $ObservableList, $ObservableSet or $ObservableMap")
        }
    }

    static void withCollector(DomainTrait domain, Closure closure) {
        domain.getClass().declaredFields.findAll {
            it.isAnnotationPresent(Collector)
        }.each { collector ->
            def annotation = collector.getAnnotation(Collector)
            Class value = annotation.value()
            Path path = new Path(domain)
            if (Closure.isAssignableFrom(value)) {
                Closure pathClosure = value.newInstance(null, null)
                path = pathClosure(path)
            } else {
                path >> value >>> collector.name
            }
            def collectorObj = domain.getProperty(collector.name)
            closure(collectorObj, collector.name, path)
        }
    }
}

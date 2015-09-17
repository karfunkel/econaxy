package de.econaxy.server.domain

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@Singleton
class DomainBus {
    private final Map<Class, List<Map<String, Object>>> listenerMap = [:].withDefault { [] }

    protected static final Closure ALL_FILTER = { true }

    protected static final Closure PATH_FILTER_FACTORY = { Path filter, DomainBus.DomainEvent event -> event.path.matches(filter) }

    void subscribe(Class sourceClass = null, Path filter, Closure listener) {
        subscribe(sourceClass, PATH_FILTER_FACTORY.curry(filter), listener as DomainBus.DomainListener)
    }

    void subscribe(Class sourceClass = null, Path filter, DomainBus.DomainListener listener) {
        subscribe(sourceClass, PATH_FILTER_FACTORY.curry(filter), listener)
    }

    void subscribe(Class sourceClass = null, Closure filter = ALL_FILTER, Closure listener) {
        subscribe(sourceClass, filter, listener as DomainBus.DomainListener)
    }

    void subscribe(Class sourceClass = null, Closure filter = ALL_FILTER, DomainBus.DomainListener listener) {
        listenerMap[sourceClass] << [filter: filter, listener: listener]
    }

    void register(final DomainTrait domain, final String targetProperty = null, final List<String> properties = null) {
        PropertyChangeListener listener = { PropertyChangeEvent evt ->
            fireDomainEvent(evt, targetProperty ? domain.path.withContainer(targetProperty) : domain.path)
        }
        def target = targetProperty ? domain."$targetProperty" : domain
        if (properties) {
            properties.each { property ->
                target.addPropertyChangeListener property, listener
            }
        } else
            target.addPropertyChangeListener listener
    }

    protected fireDomainEvent(ObservableSet.ElementAddedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainAddedEvent(evt.source, path, evt.newValue))
    }

    protected fireDomainEvent(ObservableSet.ElementRemovedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainRemovedEvent(evt.source, path, evt.oldValue))
    }

    protected fireDomainEvent(ObservableSet.ElementClearedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainClearedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableSet.MultiElementAddedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainMultiAddedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableSet.MultiElementRemovedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainMultiRemovedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableList.ElementAddedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainAddedEvent(evt.source, path, evt.newValue))
    }

    protected fireDomainEvent(ObservableList.ElementRemovedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainRemovedEvent(evt.source, path, evt.oldValue))
    }

    protected fireDomainEvent(ObservableList.ElementClearedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainClearedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableList.MultiElementAddedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainMultiAddedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableList.MultiElementRemovedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainMultiRemovedEvent(evt.source, path, evt.values))
    }

    protected fireDomainEvent(ObservableMap.PropertyAddedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainAddedEvent(evt.source, path, evt.propertyName, evt.newValue))
    }

    protected fireDomainEvent(ObservableMap.PropertyRemovedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainRemovedEvent(evt.source, path, evt.propertyName, evt.oldValue))
    }

    protected fireDomainEvent(ObservableMap.PropertyClearedEvent evt, Path path) {
        evt.values.each { k, v ->
            fireDomainEvent(new DomainBus.DomainEvent(evt.source, path, evt.propertyName, evt.oldValue, evt.newValue, DomainBus.ChangeType.REMOVED))
        }
    }

    protected fireDomainEvent(ObservableMap.MultiPropertyEvent evt, Path path) {
        evt.events.each { fireDomainEvent(evt, path) }
    }

    protected fireDomainEvent(ObservableMap.PropertyUpdatedEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainChangedEvent(evt.source, path, evt.propertyName, evt.oldValue, evt.newValue))
    }

    protected fireDomainEvent(PropertyChangeEvent evt, Path path) {
        fireDomainEvent(new DomainBus.DomainChangedEvent(evt.source, path, evt.propertyName, evt.oldValue, evt.newValue))
    }

    protected fireDomainEvent(DomainBus.DomainEvent evt) {
        (listenerMap[evt.source.getClass()] + listenerMap[null]).each {
            if (it.filter) {
                if (it.filter(evt))
                    it.listener.domainChange(evt)

            } else
                it.listener.domainChange(evt)
        }
    }

    static interface DomainListener extends EventListener {
        void domainChange(DomainBus.DomainEvent evt)
    }

    static enum ChangeType {
        ADDED, REMOVED, CLEARED, MULTI_ADD, MULTI_REMOVE, CHANGED

        public static final Object oldValue = new Object()
        public static final Object newValue = new Object()
    }

    static class DomainEvent extends EventObject {
        final DomainBus.ChangeType type
        final Object oldValue
        final Object newValue
        final String propertyName
        final Path path

        public DomainEvent(Object source, Path path, Object oldValue, Object newValue, DomainBus.ChangeType type) {
            this(source, path, 'content', oldValue, newValue, type)
        }

        public DomainEvent(Object source, Path path, String propertyName, Object oldValue, Object newValue, DomainBus.ChangeType type) {
            super(source)
            this.oldValue = oldValue
            this.newValue = newValue
            this.propertyName = propertyName
            this.type = type
            this.path = path
        }

        public String getTypeAsString() {
            return type.name().toUpperCase()
        }

        boolean isCase(DomainBus.ChangeType type) {
            this.type == type
        }
    }

    static class DomainChangedEvent extends DomainBus.DomainEvent {
        public DomainChangedEvent(Object source, Path path, String propertyName = 'content', Object oldValue, Object newValue) {
            super(source, path, propertyName, oldValue, newValue, DomainBus.ChangeType.CHANGED)
        }
    }

    static class DomainAddedEvent extends DomainBus.DomainEvent {
        public DomainAddedEvent(Object source, Path path, String propertyName = 'content', Object newValue) {
            super(source, path, propertyName, null, newValue, DomainBus.ChangeType.ADDED)
        }
    }

    static class DomainRemovedEvent extends DomainBus.DomainEvent {
        public DomainRemovedEvent(Object source, Path path, String propertyName = 'content', Object value) {
            super(source, path, propertyName, value, null, DomainBus.ChangeType.REMOVED)
        }
    }

    static class DomainClearedEvent extends DomainBus.DomainEvent {
        private List values = []

        public DomainClearedEvent(Object source, Path path, List values) {
            super(source, path, DomainBus.ChangeType.oldValue, DomainBus.ChangeType.newValue, DomainBus.ChangeType.CLEARED)
            this.values?.addAll(values)
        }

        public List getValues() {
            return values.asImmutable()
        }
    }

    static class DomainMultiAddedEvent extends DomainBus.DomainEvent {
        private List values = []

        public DomainMultiAddedEvent(Object source, Path path, List values) {
            super(source, path, DomainBus.ChangeType.oldValue, DomainBus.ChangeType.newValue, DomainBus.ChangeType.MULTI_ADD)
            this.values?.addAll(values);
        }

        public List getValues() {
            return values.asImmutable()
        }
    }

    static class DomainMultiRemovedEvent extends DomainBus.DomainEvent {
        private List values = []

        public DomainMultiRemovedEvent(Object source, Path path, List values) {
            super(source, path, DomainBus.ChangeType.oldValue, DomainBus.ChangeType.newValue, DomainBus.ChangeType.MULTI_REMOVE)
            this.values?.addAll(values);
        }

        public List getValues() {
            return values.asImmutable()
        }
    }
}

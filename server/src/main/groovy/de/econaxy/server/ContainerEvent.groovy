package de.econaxy.server


import java.beans.PropertyChangeEvent

class ContainerEvent<I> {
    enum Type {
        PROPERTY(PropertyChangeEvent),
        ADD(ObservableList.ElementAddedEvent),
        MULTI_ADD(ObservableList.MultiElementAddedEvent),
        UPDATE(ObservableList.ElementUpdatedEvent),
        REMOVE(ObservableList.ElementRemovedEvent),
        MULTI_REMOVE(ObservableList.MultiElementRemovedEvent),
        CLEAR(ObservableList.ElementClearedEvent)

        Class<? extends PropertyChangeEvent> eventType

        Type(Class<? extends PropertyChangeEvent> eventType) {
            this.eventType = eventType
        }

        static Type byEvent(PropertyChangeEvent event) {
            values().find { it.eventType == event }
        }
    }

    Type type
    CrossSessionContainer container
    I index
    PropertyChangeEvent changeEvent

    public ContainerEvent(CrossSessionContainer container, I index, PropertyChangeEvent event) {
        this.container = container
        this.index = index
        this.changeEvent = event
        this.type = Type.byEvent(event)
    }
}

package de.econaxy.server.domain

import groovy.transform.EqualsAndHashCode

import java.beans.PropertyChangeListener

@EqualsAndHashCode(includes = ['source', 'target'])
class ObservableSetBinding {
    ObservableSet source
    PropertyChangeListener sourceListener
    ObservableSet target
    PropertyChangeListener targetListener
    boolean bidirectional

    private boolean inChange = false
    def listenerFactory = { target, evt ->
        switch (evt) {
            case ObservableSet.ElementAddedEvent:
                DomainTrait newElement = evt.newValue
                if (!inChange) {
                    inChange = true
                    target.add(newElement)
                    inChange = false
                }
                break
            case ObservableSet.MultiElementAddedEvent:
                evt.values.each { DomainTrait newElement ->
                    if (!inChange) {
                        inChange = true
                        target.add(newElement)
                        inChange = false
                    }
                }
                break
            case ObservableSet.ElementRemovedEvent:
                DomainTrait oldElement = evt.oldValue
                if (!inChange) {
                    inChange = true
                    target.remove(oldElement)
                    inChange = false
                }
                break
            case ObservableSet.MultiElementRemovedEvent:
            case ObservableSet.ElementClearedEvent:
                evt.values.each { DomainTrait oldElement ->
                    if (!inChange) {
                        inChange = true
                        target.remove(oldElement)
                        inChange = false
                    }
                }
                break
        }
    }

    ObservableSetBinding(ObservableSet source, ObservableSet target, boolean bidirectional) {
        this.source = source
        this.target = target
        this.bidirectional = bidirectional
        this.sourceListener = listenerFactory.curry(target)
        if (bidirectional)
            this.targetListener = listenerFactory.curry(source)
    }

    void bind() {
        source.addPropertyChangeListener sourceListener
        if (bidirectional)
            target.addPropertyChangeListener targetListener
    }

    void unbind() {
        source.removePropertyChangeListener sourceListener
        if (bidirectional)
            target.removePropertyChangeListener targetListener
    }
}

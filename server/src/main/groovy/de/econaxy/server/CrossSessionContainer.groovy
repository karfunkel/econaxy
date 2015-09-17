package de.econaxy.server

import groovy.transform.EqualsAndHashCode
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.server.EventBus

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * Container for session data.
 * This object is shared by all sessions.
 * Data can be stored globally and will be shared/copied between all sessions, or individually for one session.
 * The used id is usually the session id.
 *
 * The container per session is an ObservableList and changes to it will be sent to the EventBus.
 *
 * @param < V >      Type of the stored value
 * @param < I >      Type of the id to store the values in this Container. This was has to be aggregable from the value via the containerIdReader/containerIdWriter.
 */
@EqualsAndHashCode(excludes = ['bus', 'elementListener'], includeFields = true)
class CrossSessionContainer<V extends Cloneable & PropertyChangeSupport, I> {
    protected final EventBus bus = Econaxy.bus

    @Delegate
    final protected ObservableMap container = [:]
    final protected ObservableList globals = []
    final protected Map<I, PropertyChangeListener> listeners = [:]

    protected Closure<I> containerIdReader
    protected Closure<I> containerIdWriter

    protected PropertyChangeListener elementListener = { evt ->
        // TODO:
    }

    CrossSessionContainer(Closure<I> containerIdReader, Closure<I> containerIdWriter) {
        this.containerIdReader = containerIdReader
        this.containerIdWriter = containerIdWriter
    }

    CrossSessionContainer(String idProperty) {
        this({ obj -> obj."$idProperty" }, { obj, val -> obj."$idProperty" = val })
    }

    /**
     * Add an element to the container
     * @param obj Element to add
     * @param future If true, all future added containers will get it aswell
     */
    void addGlobal(V obj, boolean future = false) {
        def clones = container.keySet().collect { id ->
            def clone = registerListeners(obj.clone())
            containerIdWriter(clone, id)
            container[id] << clone
            return clone
        }
        obj.addPropertyChangeListener { evt ->
            clones.each { clone ->
                clone."$evt.propertyName" = evt.newValue
            }
        }
        if (future) {
            globals << obj
        }
    }

    void add(V obj) {
        I id = containerIdReader(obj)
        if (!id) {
            addGlobal(obj)
        } else {
            ObservableList list = container[id]
            if (list == null) {
                // add all existing future global messages
                list = globals.collect {
                    def clone = registerListeners(it.clone())
                    containerIdWriter(clone, id)
                    return clone
                }
                container[id] = list as ObservableList
            }
            list << registerListeners(obj)
        }
    }

    protected V registerListeners(V obj) {
        obj.removePropertyChangeListener(elementListener)
        obj.addPropertyChangeListener(elementListener)
        return obj
    }

    // TODO: remove listeners
    boolean removeGlobal(V obj) {
        container.each { k, v ->
            v.remove(obj)
        }
        return globals.remove(obj)
    }

    // TODO: remove listeners
    Object remove(Object obj) {
        if(obj instanceof V) {
            I id = containerIdReader(obj)
            if (!id) {
                return removeGlobal(obj)
            } else {
                return container[id].remove(obj)
            }
        } else if (obj instanceof I) {
            unbindFromEventBus(obj)
            container.remove(obj)
        }
    }

    // TODO: remove listeners
    void clear() {
        globals.clear()
        container.each { k, v ->
            v.clear()
        }
    }

    // TODO: remove listeners
    void clear(I id) {
        container[id].clear()
    }

    // TODO: remove listeners
    void clearGlobals() {
        new ArrayList(globals).each { V obj ->
            removeGlobal(obj)
        }
    }

    int size(I id) {
        return container[id].size()
    }

    protected void bindToEventBus(I id, DataflowQueue<ContainerEvent> queue) {
        PropertyChangeListener pcl = { sourceEvent ->
            def evt = new ContainerEvent<I>(this, id, sourceEvent)
            queue << evt // Add event to the sending queue too
            bus.publish(queue, evt)
        }
        listeners[id] = pcl
        container[id].addPropertyChangeListener pcl
    }

    void unbindFromEventBus(I id) {
        PropertyChangeListener pcl = listeners.remove(id)
        if (pcl)
            container[id].removePropertyChangeListener pcl
    }

}

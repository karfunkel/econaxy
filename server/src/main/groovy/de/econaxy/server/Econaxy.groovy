package de.econaxy.server

import de.econaxy.server.message.Messages
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerDolphin

class Econaxy {
    // --- static ---
    static final EventBus bus = new EventBus(500)
    static final Messages messages = new Messages()

    static final Map<Integer, Econaxy> serverMap = [:]

    static Econaxy getInstance(ServerDolphin dolphin) {
        getInstance(dolphin.id)
    }

    static Econaxy removeInstance(ServerDolphin dolphin) {
        removeInstance(dolphin.id)
    }

    static Econaxy getInstance(int id) {
        Econaxy econaxy = serverMap[id]
        if (!econaxy) {
            econaxy = new Econaxy(id)
            messages.init(econaxy)
            bus.subscribe(econaxy.eventQueue)
            serverMap[id] = econaxy
        }
        return econaxy
    }

    static Econaxy removeInstance(int id) {
        serverMap.remove(id)
    }

    // --- per instance ---

    final int id
    final DataflowQueue<ContainerEvent> eventQueue = new DataflowQueue<ContainerEvent>()

    Econaxy(int id) {
        this.id = id
    }

}

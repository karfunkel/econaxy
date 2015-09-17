package de.econaxy.server.message

import de.econaxy.server.CrossSessionContainer
import de.econaxy.server.Econaxy

class Messages extends CrossSessionContainer<Message, Integer> {
    Messages() {
        super('sessionId')
    }

    void init(Econaxy econaxy) {
        bus.subscribe(econaxy.eventQueue)
    }

    void bindToEventBus(Econaxy econaxy) {
        bindToEventBus(econaxy.id, econaxy.eventQueue)
    }
}

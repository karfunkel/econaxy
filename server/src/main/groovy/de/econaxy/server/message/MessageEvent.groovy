package de.econaxy.server.message

import org.opendolphin.core.server.DTO

class MessageEvent {
    enum Type { NEW, CHANGE, REBASE, REMOVE, RELEASE }

    Type type
    String qualifier
    Object value
    DTO    dto

    public TeamEvent(Type type, DTO dto) {
        this.type = type
        this.dto = dto
    }

    public TeamEvent(Type type, String qualifier, Object value) {
        this.type      = type
        this.qualifier = qualifier
        this.value     = value
    }
}

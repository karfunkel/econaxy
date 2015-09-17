package de.econaxy.server.message

import groovy.beans.Bindable
import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode

// TODO: PropertyChangeEvents -> to bus
@Bindable
@AutoClone
// Container id may not be part of equals/hashCode
@EqualsAndHashCode(excludes = ['user'])
class Message implements Cloneable {
    String id
    Integer sessionId
    Date timestamp
    String text

    Message(Integer sessionId = null, String text) {
        this.id = UUID.randomUUID()
        this.sessionId = sessionId
        this.text = text
        this.timestamp = new Date()
    }

    boolean isGlobal() {
        sessionId == null
    }
/*
    DTO toDTO() {
        new DTO([
                new Slot("id", id, "message.${id}.id", Tag.VALUE),
                new Slot("user", sessionId, "message.${id}.user", Tag.VALUE),
                new Slot("timestamp", timestamp.time, "message.${id}.timestamp", Tag.VALUE),
                new Slot("timestamp", Date.name, null, Tag.VALUE_TYPE),
                new Slot("text", text, "message.${id}.text", Tag.VALUE),
        ])
    }
*/
}

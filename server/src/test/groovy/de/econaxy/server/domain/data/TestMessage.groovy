package de.econaxy.server.domain.data

import de.econaxy.server.domain.DomainTrait
import de.econaxy.server.domain.Domain
import de.econaxy.server.domain.Parent

@Domain
class TestMessage {
    @Parent
    DomainTrait playerOrGame

    String text

    TestMessage(String text) {
        this.text = text
    }
}

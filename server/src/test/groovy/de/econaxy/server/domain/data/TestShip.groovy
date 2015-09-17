package de.econaxy.server.domain.data

import de.econaxy.server.domain.Domain
import de.econaxy.server.domain.Parent

@Domain
class TestShip {
    @Parent
    TestPlayer player
    String name

    TestShip(String name) {
        this.name = name
    }
}

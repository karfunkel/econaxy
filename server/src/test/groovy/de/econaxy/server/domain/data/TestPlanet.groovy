package de.econaxy.server.domain.data

import de.econaxy.server.domain.Domain
import de.econaxy.server.domain.Parent

@Domain
class TestPlanet {
    @Parent
    TestPlayer player
    String name

    TestPlanet(String name) {
        this.name = name
    }
}

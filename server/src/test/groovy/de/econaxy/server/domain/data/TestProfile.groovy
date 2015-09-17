package de.econaxy.server.domain.data

import de.econaxy.server.domain.Domain
import de.econaxy.server.domain.Parent

@Domain
class TestProfile {
    @Parent
    TestPlayer player
    String name

    TestProfile(String name) {
        this.name = name
    }
}

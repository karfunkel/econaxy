package de.econaxy.server.domain.data

import de.econaxy.server.domain.Container
import de.econaxy.server.domain.Domain
import de.econaxy.server.domain.Parent

@Domain
class TestPlayer {
    @Parent
    TestGame game
    String name
    @Container(TestShip)
    ObservableSet<TestShip> ships = []
    @Container(TestPlanet)
    ObservableSet<TestPlanet> planets = []
    @Container(TestMessage)
    ObservableSet<TestMessage> messages = []

    @Container
    TestProfile profile

    TestPlayer(String name) {
        this.name = name
    }
}

package de.econaxy.server.domain.data

import de.econaxy.server.domain.Collector
import de.econaxy.server.domain.Container
import de.econaxy.server.domain.Domain

@Domain
class TestGame {
    @Container(TestPlayer)
    ObservableSet<TestPlayer> players = []
    @Collector({ it >> TestPlayer >>> 'ships' })
    ObservableSet<TestShip> ships = []
    @Collector(TestPlayer)
    ObservableSet<TestPlanet> planets = []
    @Container(TestMessage)
    ObservableSet<TestMessage> messages = []
}

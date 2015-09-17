package de.econaxy.server.game

import de.econaxy.server.domain.*

import java.util.concurrent.atomic.AtomicInteger

@Domain
class Profile implements DomainTrait {
    @Parent
    Player player

    Date loginDate
    Locale locale = Locale.GERMAN
    String username

    protected static AtomicInteger guestCount = new AtomicInteger(1)

    Profile() {
        this.username = "User ${guestCount.getAndIncrement()}"
    }
}

@Domain
class Game implements DomainTrait {
    @Container(Player)
    ObservableSet<Player> players = []
    @Collector({ it >> Player >>> 'ships' })
    ObservableSet<Ship> ships = []
    @Collector(Player)
    ObservableSet<Planet> planets = []
    @Container(Message)
    ObservableSet<Message> messages = []

    Date startDate = new Date()
}

@Domain
class Player implements DomainTrait {
    @Parent
    Game game

    @Container(Planet)
    ObservableSet<Planet> planets = []
    @Container(Ship)
    ObservableSet<Ship> ships = []
    @Container(Message)
    ObservableSet<Message> messages = []

    @Container
    Profile profile

    String name

    Player(String name) {
        this.name = name
    }

}

@Domain
class Ship implements DomainTrait, Location {
    @Parent
    Player player

    String name

    Ship(String name) {
        this.name = name
    }
}

@Domain
class Planet implements DomainTrait, Location {
    @Parent
    Player player

    String name

    Planet(String name) {
        this.name = name
    }
}

@Domain
class Message implements DomainTrait {
    // TODO: Ablaufzeitpunnkt für Nachrichten (auto remove von Domainmodel)
    @Parent
    DomainTrait playerOrGame

    Date timestamp
    String text

    Message(String text) {
        this.timestamp = new Date()
        this.text = text
    }
}
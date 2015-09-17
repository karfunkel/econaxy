package de.econaxy.server.domain

import de.econaxy.server.domain.data.TestGame
import de.econaxy.server.domain.data.TestMessage
import de.econaxy.server.domain.data.TestPlanet
import de.econaxy.server.domain.data.TestPlayer
import de.econaxy.server.domain.data.TestProfile
import de.econaxy.server.domain.data.TestShip
import spock.lang.Specification

class DomainTraitTest extends Specification {

    def "Create new Game instance"() {
        when:
        TestGame game = new TestGame()

        then:
        game.parentProperty == null
        game.parent == null
        game.uid ==~ /\w{8}-\w{4}-\w{4}-\w{4}-\w{12}/

    }

    def "Add and remove a Player to Game instance"() {
        when:
        TestGame game = new TestGame()
        TestPlayer player = new TestPlayer('Sascha')

        then:
        player.name == 'Sascha'
        player.parentProperty == 'game'
        player.parent == null
        player.game == null

        when:
        game.players << player

        then:
        player.parentProperty == 'game'
        player.parent == game
        player.game == game

        when:
        game.players.remove player

        then:
        player.parentProperty == 'game'
        player.parent == null
        player.game == null
    }

    def "Test path for Domain"() {
        when:
        TestGame game = new TestGame()
        TestPlayer player = new TestPlayer('Sascha')
        TestShip ship = new TestShip('Enterprise')

        then:
        ship.path.toString() ==~ /TestShip\($ship.uid\)/
        player.path.toString() ==~ /TestPlayer\($player.uid\)/
        game.path.toString() ==~ /TestGame\($game.uid\)/

        when:
        game.players << player
        player.ships << ship

        then:
        ship.path.toString() ==~ /TestGame\($game.uid\)\.TestPlayer\($player.uid\)\.TestShip\($ship.uid\)/
        player.path.toString() ==~ /TestGame\($game.uid\)\.TestPlayer\($player.uid\)/
        game.path.toString() ==~ /TestGame\($game.uid\)/
    }

    def "Test Path"() {
        when:
        def path = new Path()

        then:
        path.container == null
        path.parts == []

        when:
        def game = new TestGame()
        path = new Path(game)

        then:
        path.container == null
        path.parts.last().uid == game.uid
        path.parts.last().type == TestGame

        when:
        path = new Path(game, 'players')

        then:
        path.container == 'players'
        path.parts.last().uid == game.uid
        path.parts.last().type == TestGame

        when:
        path = new Path() >> TestGame

        then:
        path.container == null
        path.parts.last().uid == '*'
        path.parts.last().type == TestGame

        when:
        path = new Path() >> TestGame >> '123456'

        then:
        path.container == null
        path.parts.last().uid == '123456'
        path.parts.last().type == TestGame

        when:
        path = new Path() >> TestGame >> '123456' >>> 'players'

        then:
        path.container == 'players'
        path.parts.last().uid == '123456'
        path.parts.last().type == TestGame

        when:
        path = new Path(game) >> TestPlayer >> TestShip >>> 'players'

        then:
        path.container == 'players'
        path.parts[0].uid == game.uid
        path.parts[0].type == TestGame
        path.parts[1].uid == '*'
        path.parts[1].type == TestPlayer
        path.parts[2].uid == '*'
        path.parts[2].type == TestShip
    }

    def "Test Path matching"() {
        setup:
        TestGame game = new TestGame()
        TestPlayer player = new TestPlayer('Sascha')
        TestShip ship = new TestShip('Enterprise')
        game.players << player
        player.ships << ship

        expect:
        ship.path.matches(new Path() >> TestGame >> TestPlayer >> TestShip)
        !ship.path.matches(new Path() >> TestGame >> TestShip >> TestPlayer)
        ship.path.matches(new Path(game) >> TestPlayer >> TestShip)
        !ship.path.matches(new Path(game) >> TestPlayer >> 'abc' >> TestShip)
        !ship.path.matches(new Path(game) >> TestPlayer >> TestShip >> 'abc')
        ship.path.matches(new Path(game) >> TestPlayer >> player.uid >> TestShip >> ship.uid)
        !ship.path.matches(new Path(game) >> TestPlayer >> 'abc' >> TestShip >> ship.uid)
        (player.path >>> 'ships').matches(new Path(game) >> TestPlayer >>> 'ships')
        !(player.path >>> 'ships').matches(new Path(game) >> TestPlayer >>> 'ship')
        !(player.path >>> 'ships').matches(new Path(game) >> TestPlayer)
        (player.path >>> 'ships').matches(new Path(game) >> TestPlayer >>> '.*')
        (player.path >>> 'ships').matches(new Path(game) >> TestPlayer >>> '*')
        !player.path.matches(new Path(game) >> TestPlayer >>> 'ships')
    }

    def "Test collectors"() {
        setup:
        TestGame game = new TestGame()
        TestPlayer player = new TestPlayer('Sascha')
        TestShip ship = new TestShip('Enterprise')
        TestPlanet planet = new TestPlanet('Terra')
        TestProfile profile = new TestProfile('Hans')
        TestMessage localMsg = new TestMessage('Local')
        TestMessage globalMsg = new TestMessage('Global')

        game.players << player
        player.ships << ship

        expect:
        game.ships.size() == 1
        game.ships[0] == ship

        when:
        player.planets << planet

        then:
        game.planets.size() == 1
        game.planets[0] == planet

        player.profile == null
        profile.parent == null

        when:
        player.profile = profile

        then:
        player.profile == profile
        profile.parent == player

        when:
        game.messages << globalMsg

        then:
        globalMsg.parent == game
        globalMsg.playerOrGame == game

        when:
        player.messages << localMsg

        then:
        localMsg.parent == player
        localMsg.playerOrGame == player
        globalMsg.playerOrGame == game
        globalMsg.parent == game
        game.messages.size() == 1

    }

    def "Test bus events"() {
        setup:
        DomainBus bus = DomainBus.instance
        TestGame game = new TestGame()
        TestPlayer player = new TestPlayer('Sascha')
        TestShip ship = new TestShip('Enterprise')
        List<DomainBus.DomainEvent> events = []
        bus.subscribe { DomainBus.DomainEvent evt ->
            events << evt
        }

        when:
        game.players << player

        then:
        events.size() == 3
        checkEvent(events[0], DomainBus.ChangeType.ADDED, player, null, game.path.withContainer('players'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, game, null, player.path, 'game')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, 1, 0, game.path.withContainer('players'), 'size')

        when:
        events.clear()
        player.ships << ship

        then:
        events.size() == 3
        checkEvent(events[0], DomainBus.ChangeType.ADDED, ship, null, player.path.withContainer('ships'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, player, null, ship.path, 'player')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, 1, 0, player.path.withContainer('ships'), 'size')

        when:
        events.clear()
        ship.name = 'Firebird'

        then:
        events.size() == 1
        checkEvent(events[0], DomainBus.ChangeType.CHANGED, 'Firebird', 'Enterprise', ship.path, 'name')

        when:
        TestShip ship2 = new TestShip('Enterprise2')
        player.ships << ship2

        then:
        player.ships.size() == 2
        game.ships.size() == 2

        when:
        events.clear()
        player.ships.remove(ship2)

        then:
        events.size() == 3
        checkEvent(events[0], DomainBus.ChangeType.REMOVED, null, ship2, player.path.withContainer('ships'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, null, player, ship2.path, 'player')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, 1, 2, player.path.withContainer('ships'), 'size')

        when:
        events.clear()
        player.ships.clear()

        then:
        events.size() == 3
        checkMultiEvent(events[0], DomainBus.ChangeType.CLEARED, [ship], player.path.withContainer('ships'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, null, player, ship.path, 'player')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, 0, 1, player.path.withContainer('ships'), 'size')

        when:
        events.clear()
        player.ships.addAll(ship, ship2)

        then:
        events.size() == 4
        checkMultiEvent(events[0], DomainBus.ChangeType.MULTI_ADD, [ship, ship2], player.path.withContainer('ships'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, player, null, ship.path, 'player')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, player, null, ship2.path, 'player')
        checkEvent(events[3], DomainBus.ChangeType.CHANGED, 2, 0, player.path.withContainer('ships'), 'size')

        when:
        events.clear()
        player.ships.removeAll(ship, ship2)
        events = events.sort { a, b ->
            b.type.name() <=> a.type.name() ?: a.propertyName <=> b.propertyName ?: b.path == ship.path ? 1 : -1
        }

        then:
        events.size() == 4
        checkMultiEvent(events[0], DomainBus.ChangeType.MULTI_REMOVE, [ship, ship2], player.path.withContainer('ships'), 'content')
        checkEvent(events[1], DomainBus.ChangeType.CHANGED, null, player, ship.path, 'player')
        checkEvent(events[2], DomainBus.ChangeType.CHANGED, null, player, ship2.path, 'player')
        checkEvent(events[3], DomainBus.ChangeType.CHANGED, 0, 2, player.path.withContainer('ships'), 'size')
    }

    private void checkEvent(DomainBus.DomainEvent event, DomainBus.ChangeType type,
                            def newValue, def oldValue, Path path, String propertyName) {
        assert event.type == type
        assert event.newValue == newValue
        assert event.oldValue == oldValue
        assert event.path == path
        assert event.propertyName == propertyName
    }

    private void checkMultiEvent(DomainBus.DomainEvent event, DomainBus.ChangeType type,
                                 def values, Path path, String propertyName) {
        assert event.type == type
        assert event.values.collect().sort { a, b -> a.uid <=> b.uid } == values.sort { a, b -> a.uid <=> b.uid }
        assert event.path == path
        assert event.propertyName == propertyName
    }
}

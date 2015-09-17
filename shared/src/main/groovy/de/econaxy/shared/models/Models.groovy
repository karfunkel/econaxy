package de.econaxy.shared.models

import de.econaxy.shared.*

//import de.econaxy.shared.transform.Model

// it.name.value
// it.name.label
// it.name.tooltip
// it.name.enabled
//@Model(id = 'profile')
class Profile {
    @Parent
    @Label("Profile")
    @Enabled(false)
    Player player

    @Label("Login")
    @Tooltip("Timestamp of the login")
    Date loginDate = { new Date() }()

    @Label("Language")
    Locale locale = Locale.GERMAN

    @Label("Name")
    @Tooltip("Username")
    String username
}

class Game {
    /* uid is automatic
    @Label("ID")
    @Tooltip("Unique ID")
    @WidgetHint("readonly")
    @HelpUrl("http://my.help.page")
    @Regex(".*")
    @Mandatory
    @Visible(false)
    @Enabled(false)
    @InitialValue({ UUID.randomUUID() })
    String id
    */

    @Label("Players")
    Set<Player> players = { [] }()

    @Label("Ships")
    Set<Ship> ships = []

    @Label("Planets")
    Set<Planet> planets = []

    @Label("Messages")
    Set<Message> messages = []

    @Label("Started at")
    @Tooltip("Timestamp this game has been started")
    Date startDate = { new Date() }()
}

class Player {
    @Parent
    @Label("Game")
    Game game

    @Label("Planets")
    Set<Planet> planets = []
    @Label("Ships")
    Set<Ship> ships = []
    @Label("Messages")
    Set<Message> messages = []

    @Label("Profile")
    @Enabled(false)
    Profile profile

    @Label("Name")
    String name = "Unknown Player"
}

class Ship {
    @Parent
    @Label("Player")
    Player player

    @Label("Name")
    String name = "Unknown Ship"
}

class Planet {
    @Parent
    @Label("Player")
    Player player

    @Label("Name")
    String name = "Unknown Planet"
}

//@Model(id = 'message', type = 'Message')
class Message {
    @Parent
    @Label("Player or Game")
    ModelBase playerOrGame

    @Label("Timestamp")
    Date timestamp = { new Date() }()

    @Label("Message")
    @Mandatory
    String text = ""
}

package de.econaxy.server.profile

import de.econaxy.server.game.Game
import groovy.beans.Bindable
import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode

import java.util.concurrent.atomic.AtomicInteger

// TODO: PropertyChangeEvents -> to bus
@Bindable
@AutoClone
// Container id may not be part of equals/hashCode
@EqualsAndHashCode(excludes = ['id'])
class Profile implements Cloneable {
    Integer id
    Date loginDate
    Locale locale = Locale.GERMAN
    String username

    protected static AtomicInteger guestCount = new AtomicInteger(1)

    Profile(Integer id) {
        this.id = id
        this.username = "User${guestCount.getAndIncrement()}"
    }
}

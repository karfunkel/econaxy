package de.econaxy.server.actions

import de.econaxy.PM
import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

class ConnectAction extends DolphinServerAction {

    @Override
    void registerIn(ActionRegistry registry) {
        serverDolphin.action('Connect') { Command command, List<Command> response ->
            changeValue(serverDolphin['profile']['loginDate'], new Date())
            println PM.Profile.id
        }
    }
}

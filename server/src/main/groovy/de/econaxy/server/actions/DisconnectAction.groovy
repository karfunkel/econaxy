package de.econaxy.server.actions

import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

class DisconnectAction extends DolphinServerAction {
    @Override
    void registerIn(ActionRegistry registry) {
        serverDolphin.action('Disconnect') { Command command, List<Command> response ->
            //serverDolphin.person.firstname.value
        }
    }
}

package de.econaxy.server

import de.econaxy.server.actions.ConnectAction
import de.econaxy.server.actions.DisconnectAction
import de.econaxy.server.actions.PingAction
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

class EconaxyDirector extends DolphinServerAction{

    @Override
    void registerIn(ActionRegistry registry) {
        serverDolphin.register(new ConnectAction())
        serverDolphin.register(new DisconnectAction())
        serverDolphin.register(new PingAction())
    }
}

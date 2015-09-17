package de.econaxy.server.actions

import de.econaxy.server.Econaxy

//import de.econaxy.Profile
import de.econaxy.shared.ActionCommand
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

class ConnectAction extends DolphinServerAction {

    @Override
    void registerIn(ActionRegistry registry) {
        serverDolphin.action(ActionCommand.CONNECT) { Command command, List<Command> response ->
            changeValue((ServerAttribute) serverDolphin['profile'].findAttributeByPropertyNameAndTag('loginDate', Tag.VALUE), new Date().time)
            Econaxy.messages.bindToEventBus(econaxy)

            //changeValue(Profile, Profile.LOGIN_DATE, new Date().time)
        }
    }
}

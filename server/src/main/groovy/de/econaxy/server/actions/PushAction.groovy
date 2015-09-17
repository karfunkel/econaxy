package de.econaxy.server.actions

import de.econaxy.server.message.MessageEvent
import de.econaxy.shared.ActionCommand
import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import java.util.concurrent.TimeUnit

import static de.econaxy.server.Econaxy.getEventQueue

class PushAction extends DolphinServerAction {
    @Override
    void registerIn(ActionRegistry registry) {
        serverDolphin.action(ActionCommand.PUSH) { Command command, List<Command> response ->
            try {
                processEventsFromQueue(60, TimeUnit.SECONDS)
            } catch (InterruptedException e) { /* do nothing */
            }
        }
    }

    private void processEventsFromQueue(int timeoutValue, TimeUnit timeoutUnit) throws InterruptedException {
        MessageEvent event = eventQueue.getVal(timeoutValue, timeoutUnit)
        while (event) {
            switch (event.type) {
                case MessageEvent.Type.NEW:
                    presentationModel(null, "Message", event.dto) // create on server side
                    break
                case MessageEvent.Type.CHANGE:
                    silent = true // do not issue additional posts on the bus from value changes that come from the bus
                    serverDolphin.findAllAttributesByQualifier(event.qualifier).each { attribute ->
                        if (attribute.presentationModel.presentationModelType == "Message")
                            attribute.value = event.value
                    }
                    silent = false
                    break
                case MessageEvent.Type.REBASE:
                    serverDolphin.findAllAttributesByQualifier(event.qualifier).each { attribute -> attribute.rebase() }
                    break
                case MessageEvent.Type.REMOVE:
                    Set<ServerPresentationModel> toDelete = []
                    serverDolphin.findAllAttributesByQualifier(event.qualifier).each { attribute ->
                        ServerPresentationModel pm = attribute.presentationModel
                        if (pm.presentationModelType == "Message") {
                            toDelete.add(pm)
                        }
                    }
                    toDelete.each { pm -> serverDolphin.remove(pm) }
                    break
            }
            event = eventQueue.getVal(20, TimeUnit.MILLISECONDS)
        }
    }
}

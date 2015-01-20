package de.econaxy.server.extensions

import de.econaxy.shared.models.ModelBase
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.action.DolphinServerAction

class DolphinExtension {
    private static WeakHashMap<ServerDolphin, Map<String, ?>> contextMap = [:]

    static Map<String, ?> getContext(ServerDolphin dolphin) {
        Map<String, ?> context = contextMap.get(dolphin)
        if (context == null) {
            context = [:]
            contextMap.put(dolphin, context)
        }
        return context
    }

    static ServerPresentationModel presentationModel(DolphinServerAction dolphinAction, Class<? extends ModelBase> modelClass) {
        return dolphinAction.serverDolphin[modelClass._ID]
    }

    static ServerAttribute attribute(DolphinServerAction dolphinAction, Class<? extends ModelBase> modelClass, String id) {
        return presentationModel(dolphinAction, modelClass)[id]
    }

    static
    def changeValue(DolphinServerAction dolphinAction, Class<? extends ModelBase> modelClass, String attributeId, Object value) {
        return dolphinAction.changeValue(attribute(dolphinAction, modelClass, attributeId), value)
    }

}

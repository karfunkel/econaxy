package de.econaxy.server.extensions

import org.opendolphin.core.server.ServerDolphin

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

}

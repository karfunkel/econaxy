package de.econaxy.server

import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.server.adapter.DolphinServlet

class EconaxyServlet extends DolphinServlet {
    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        serverDolphin.register(new EconaxyDirector())
    }
}

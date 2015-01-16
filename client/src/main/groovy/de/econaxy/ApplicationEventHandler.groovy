package de.econaxy

import de.econaxy.server.InternalServer
import griffon.core.Configuration
import griffon.core.GriffonApplication
import griffon.core.event.EventHandler
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler
import org.opendolphin.core.comm.JsonCodec

import javax.inject.Inject

class ApplicationEventHandler implements EventHandler {

    @Inject
    ClientDolphinProvider clientDolphinProvider

    @Inject
    InternalServer internalServer


    void onBootstrapEnd(GriffonApplication application) {
        Configuration config = application.configuration
        internalServer.init(application.configuration.connection.port, application.log)
        Thread.start { internalServer.run() }

        ClientDolphin dolphin = new ClientDolphin()
        dolphin.clientModelStore = new ClientModelStore(dolphin)
        String url = System.properties.remote ?: "$config.connection.schema://$config.connection.host:$config.connection.port/$config.connection.context"
        ClientConnector connector = new HttpClientConnector(dolphin, url)
        connector.codec = new JsonCodec()
        connector.uiThreadHandler = new JavaFXUiThreadHandler()
        dolphin.clientConnector = connector
        clientDolphinProvider.dolphin = dolphin
    }

    void onShutdownStart(GriffonApplication application) {
        clientDolphinProvider.dolphin.stopPushListening()
        clientDolphinProvider.dolphin.send("Disconnect") {
            internalServer.stop()
        }
    }
}
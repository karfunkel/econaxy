package de.econaxy

import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class EconaxyController {
    EconaxyModel model

    @Inject
    ClientDolphin dolphin
    GriffonApplication application

    def onReadyEnd(GriffonApplication application) {
        dolphin.presentationModel(new Profile())
        dolphin.send('Connect')
        println "------------->" + PM.Profile.id
    }

    //@Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        dolphin.send('Ping') { List<ClientPresentationModel> pMs ->
            for (pm in pMs) {
                model.messages << pm
            }
        }
        /*
        int count = model.clickCount.toInteger()
        model.clickCount = String.valueOf(count + 1)
        */
    }
}

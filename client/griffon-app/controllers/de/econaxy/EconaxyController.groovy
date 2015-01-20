package de.econaxy

import de.econaxy.shared.ActionCommand
import de.econaxy.shared.Constants
import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import org.opendolphin.binding.JFXBinder
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class EconaxyController {
    EconaxyModel model
    FactoryBuilderSupport builder

    @Inject
    ClientDolphin dolphin

    def onReadyEnd(GriffonApplication application) {
        model.profile = dolphin.presentationModel(new Profile())
        JFXBinder.bind "loginDate" of model.profile using {
            new Date(it).format(Constants.dateFormat) ?: ''
        } to "text" of builder.loginDate

        dolphin.send(ActionCommand.CONNECT)
    }

    //@Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        dolphin.send(ActionCommand.PING) { List<ClientPresentationModel> pMs ->
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

package de.econaxy

import de.econaxy.shared.ActionCommand
import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import org.opendolphin.binding.JFXBinder
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import de.econaxy.shared.converter.Converters as C

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class EconaxyController {
    EconaxyModel model
    FactoryBuilderSupport builder

    @Inject
    ClientDolphin dolphin

    def onReadyEnd(GriffonApplication application) {
        model.profile = dolphin.presentationModel('profile', 'Profile', [
                new ClientAttribute('loginDate', null, null, Tag.VALUE),
                new ClientAttribute('loginDate', Date.name, null, Tag.VALUE_TYPE),
                new ClientAttribute('loginDate', 'Login Date', null, Tag.LABEL),
                new ClientAttribute('loginDate', true, null, Tag.VISIBLE),
                new ClientAttribute('loginDate', false, null, Tag.ENABLED),
                new ClientAttribute('locale', Locale.GERMAN, null, Tag.VALUE),
        ] as ClientAttribute[])

        JFXBinder.bind "loginDate" of model.profile using C.chain(C.get(Long, Date), C.get(Date, String)) to "text" of builder.loginDate
        JFXBinder.bind "loginDate", Tag.VISIBLE of model.profile to "visible" of builder.loginDate
        JFXBinder.bind "loginDate", Tag.ENABLED of model.profile using C.INVERTER to "disabled" of builder.loginDate

        dolphin.send(ActionCommand.CONNECT) {
            dolphin.startPushListening(ActionCommand.PUSH, ActionCommand.RELEASE);
        }
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

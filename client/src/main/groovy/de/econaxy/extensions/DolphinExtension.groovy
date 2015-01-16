package de.econaxy.extensions

import de.econaxy.shared.models.ModelBase
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

class DolphinExtension {
    static ClientPresentationModel presentationModel(ClientDolphin dolphin, ModelBase model) {
        dolphin.presentationModel(model.id, model.type, model.attributes)
    }
}

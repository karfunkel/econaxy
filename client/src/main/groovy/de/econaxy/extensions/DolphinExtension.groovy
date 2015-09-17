package de.econaxy.extensions

import de.econaxy.shared.models.ModelBase
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

class DolphinExtension {
    static ClientPresentationModel presentationModel(ClientDolphin dolphin, ModelBase model) {
        dolphin.presentationModel(model.getClass()._ID, model.getClass()._TYPE, model.attributes)
        /*
        def model = dolphin.presentationModel('profile', 'Profile', [
                new ClientAttribute('loginDate', null, null, Tag.VALUE),
                new ClientAttribute('loginDate', Date.name, null, Tag.VALUE_TYPE),
                new ClientAttribute('loginDate', 'Login Date', null, Tag.LABEL),
                new ClientAttribute('loginDate', true, null, Tag.VISIBLE),
                new ClientAttribute('loginDate', false, null, Tag.ENABLED),
                new ClientAttribute('locale', Locale.GERMAN, null, Tag.VALUE),
        ] as ClientAttribute[])
        */
    }

}

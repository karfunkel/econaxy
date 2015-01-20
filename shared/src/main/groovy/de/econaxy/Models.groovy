package de.econaxy

import de.econaxy.shared.models.AbstractModel
import de.econaxy.shared.models.Model

@Model(id = 'profile')
class Profile extends AbstractModel {
    long loginDate
}
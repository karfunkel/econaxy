package de.econaxy

import de.econaxy.shared.models.AbstractModel
import de.econaxy.shared.models.Model

@Model
class Profile extends AbstractModel {
    String id = 'profile'
    String type = 'Profile'
    Date loginDate
}
package de.econaxy

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.opendolphin.core.client.ClientPresentationModel

@ArtifactProviderFor(GriffonModel)
@FXObservable
class EconaxyModel {
    ObservableList<ClientPresentationModel> messages = FXCollections.observableArrayList()
}
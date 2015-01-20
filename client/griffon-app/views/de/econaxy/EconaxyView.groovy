package de.econaxy

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class EconaxyView {
    FactoryBuilderSupport builder
    EconaxyModel model

    void initUI() {
        builder.application(title: application.configuration['application.title'],
                sizeToScene: true, centerOnScreen: true, name: 'mainWindow') {
            scene(fill: WHITE, width: 1200, height: 1000) {
                splitPane {
                    /*
                    dividerPosition(index: 0, position: 0.8)
                    tableView(id: 'messages', selectionMode: 'single') {
                        tableColumn(editable: false, property: "name", text: "Name", prefWidth: 150,
                                onEditCommit: { event ->
                                    Person item = event.tableView.items.get(event.tablePosition.row)
                                    item.name = event.newValue
                                }
                        )
                        tableColumn('Time', prefWidth: bind(messages.width() / 0.25))
                        tableColumn('Text', prefWidth: bind(messages.width() / 0.75))
                    }
                    */
                    label(id: 'loginDate', prefWidth: 200)
                    button(prefWidth: 200, clickAction)
                }
            }
        }
    }
}
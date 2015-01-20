package de.econaxy.shared.models

class AbstractModel implements ModelBase {
    Map<String, Object> getAttributes() {
        def attributeNames = this.metaClass.properties.name - ['id', 'type', 'class', 'attributeNames', 'attributes', 'propertyChangeListeners']
        attributeNames.collectEntries { name ->
            [name, getProperty(name)]
        }
    }
}

interface ModelBase {
    Map<String, Object> getAttributes()
}
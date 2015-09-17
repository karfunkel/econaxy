package de.econaxy.shared.models

abstract class ModelBase {
    Map<String, Object> getAttributes() {
        def attributeNames = this.metaClass.properties.name - ['id', 'type', 'class', 'attributeNames', 'attributes', 'propertyChangeListeners']
        attributeNames.collectEntries { name -> [name, getProperty(name)] }
    }


}

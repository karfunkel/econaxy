application {
    title = 'econaxy'
    startupGroups = ['econaxy']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "econaxy"
    'econaxy' {
        model      = 'de.econaxy.EconaxyModel'
        view       = 'de.econaxy.EconaxyView'
        controller = 'de.econaxy.EconaxyController'
    }
}

connection {
    schema = 'http'
    port = 8910
    host = "localhost"
    context = ""
}
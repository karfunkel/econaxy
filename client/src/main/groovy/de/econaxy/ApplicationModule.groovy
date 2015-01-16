package de.econaxy

import de.econaxy.server.InternalServer
import griffon.core.event.EventHandler
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import org.opendolphin.core.client.ClientDolphin

@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(EventHandler).to(ApplicationEventHandler).asSingleton()
        bind(ClientDolphinProvider).toInstance(new ClientDolphinProvider())
        bind(ClientDolphin).toProvider(ClientDolphinProvider)
        bind(InternalServer).to(InternalServer).asSingleton()
    }
}
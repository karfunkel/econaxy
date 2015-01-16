package de.econaxy

import org.opendolphin.core.client.ClientDolphin

import javax.inject.Provider

class ClientDolphinProvider implements Provider {
    ClientDolphin dolphin

    @Override
    Object get() {
        return dolphin
    }

    void setDolphin(ClientDolphin dolphin) {
        this.dolphin = dolphin
    }
}

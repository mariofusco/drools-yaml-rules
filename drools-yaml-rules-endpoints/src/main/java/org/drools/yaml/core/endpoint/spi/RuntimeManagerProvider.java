package org.drools.yaml.core.endpoint.spi;

import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;

public class RuntimeManagerProvider {
    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    @Produces
    @ApplicationScoped
    public RuntimeManager getCompilationManager() {
        return runtimeManager;
    }
}

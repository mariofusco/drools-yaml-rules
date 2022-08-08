package org.drools.yaml.core.endpoint.spi;

import org.kie.efesto.compilationmanager.api.service.CompilationManager;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;

public class CompilationManagerProvider {
    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();

    @Produces
    @ApplicationScoped
    public CompilationManager getCompilationManager() {
        return compilationManager;
    }
}

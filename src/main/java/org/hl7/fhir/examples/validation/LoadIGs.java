package org.hl7.fhir.examples.validation;

import org.hl7.fhir.validation.IgLoader;
import org.hl7.fhir.validation.ValidationEngine;

import java.io.IOException;

public class LoadIGs {
    public static IgLoader getIGLoaderForEngine(ValidationEngine validationEngine) throws IOException {
         return new IgLoader(
                validationEngine.getPcm(),
                validationEngine.getContext(),
                validationEngine.getVersion(),
                validationEngine.isDebug()
        );
    }

    public static void loadIG(ValidationEngine validationEngine, IgLoader igLoader, String source) throws IOException {
        igLoader.loadIg(
                validationEngine.getIgs(),
                validationEngine.getBinaries(),
                source,
                false
        );
    }
}

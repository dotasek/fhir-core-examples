package org.hl7.fhir.examples.validation;

import org.hl7.fhir.r5.conformance.R5ExtensionsLoader;
import org.hl7.fhir.r5.context.SystemOutLoggingService;
import org.hl7.fhir.utilities.TimeTracker;
import org.hl7.fhir.utilities.VersionUtilities;
import org.hl7.fhir.validation.IgLoader;
import org.hl7.fhir.validation.ValidationEngine;

import java.io.IOException;
import java.net.URISyntaxException;

public class ValidationEngineInitialization {

    final ValidationEngine validationEngine;
    final IgLoader igLoader;
    private ValidationEngineInitialization(ValidationEngine validationEngine, IgLoader igLoader) {
        this.validationEngine = validationEngine;
        this.igLoader = igLoader;
    }

    /**
     * This method will build a new {@link ValidationEngine} using
     *
     * @param fhirVersion The version of FHIR for this ValidationEngine
     *
     * @return The {@link ValidationEngine}.
     * @throws IOException        If creation of the {@link ValidationEngine} fails.
     * @throws URISyntaxException If creation of the {@link ValidationEngine} fails.
     */
    public static ValidationEngineInitialization buildValidationEngine(
            final String fhirVersion
    )
            throws IOException, URISyntaxException {


        // Determine the FHIR definition being used.
        final String definition = VersionUtilities.packageForVersion(fhirVersion) + "#" + VersionUtilities.getCurrentVersion(fhirVersion);

        // Instantiate an instance of a ValidationEngine.
        final ValidationEngine validationEngine = new ValidationEngine.ValidationEngineBuilder()
                .withVersion(fhirVersion)
                .withTimeTracker(new TimeTracker())
                .withUserAgent("company/validator")
                .fromSource(definition);

        // Set the terminology server
        validationEngine.setTerminologyServer(
                "http://tx.fhir.org",
                null,
                null
        );

        validationEngine.setDebug(false);
        validationEngine.getContext().setLogger(new SystemOutLoggingService(false));
        

        IgLoader igLoader = LoadIGs.getIGLoaderForEngine(validationEngine);
        LoadIGs.loadIG(validationEngine,igLoader,"hl7.terminology");

        // Return the ValidationEngine.
        return new ValidationEngineInitialization(validationEngine, igLoader);
    }

}

package org.hl7.fhir.examples.validation;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.conformance.R5ExtensionsLoader;
import org.hl7.fhir.r5.context.SystemOutLoggingService;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.utilities.TimeTracker;
import org.hl7.fhir.utilities.VersionUtilities;
import org.hl7.fhir.validation.ValidationEngine;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ValidationEngineExamples {

    /**
     * This method will build a new {@link ValidationEngine} using
     *
     * @param fhirVersion The version of FHIR for this ValidationEngine
     *
     * @return The {@link ValidationEngine}.
     * @throws IOException        If creation of the {@link ValidationEngine} fails.
     * @throws URISyntaxException If creation of the {@link ValidationEngine} fails.
     */
    public static ValidationEngine buildValidationEngine(
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

        // Ensure that no terminology server is configured.
        validationEngine.setTerminologyServer(
                "http://tx.fhir.org",
                null,
                null
        );

        validationEngine.setDebug(false);
        validationEngine.getContext().setLogger(new SystemOutLoggingService(false));

        // Define an instance of the IG loader.
       // final IgLoader igLoader = getIgLoader(validationEngine);

        // Load the R5-extensions.
        final R5ExtensionsLoader r5e = new R5ExtensionsLoader(validationEngine.getPcm(), validationEngine.getContext());
        r5e.loadR5Extensions();


        // Return the ValidationEngine.
        return validationEngine;
    }

    /**
     * This method will validate the specified FHIR resource {@link File}.
     *
     * @param fhirResourceFile
     * The FHIR resource file {@link File}.
     * @param validationEngine
     * The {@link ValidationEngine}.
     *
     * @return
     * The {@link List} of {@link OperationOutcome}s.
     *
     * @throws Exception
     * If the specified FHIR message is not a readable file or the
     * {@link ValidationEngine} is not defined or if the validation throws
     * an exception.
     */
    public static List<OperationOutcome> validateFhirResource(
            final File fhirResourceFile,
            final ValidationEngine validationEngine
    ) throws IOException {

        final List<String> sources = new ArrayList<>();
        final List<String> profiles = new ArrayList<>();

        profiles.add("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient");

        sources.add(fhirResourceFile.getAbsolutePath());

        // Call the validation logic.
        final Resource resource = validationEngine.validate(
                sources,
                profiles,
                null
        );

        final List<OperationOutcome> operationOutcomes = new ArrayList<>();

        // If the validation-response is that of a Bundle, then loop
        // its components to look for any OperationOutcomes and extract
        // the issues from them.
        if (resource instanceof Bundle)
        {
            for (org.hl7.fhir.r5.model.Bundle.BundleEntryComponent e : ((Bundle) resource).getEntry())
            {
                if (e.getResource() instanceof OperationOutcome)
                {
                    operationOutcomes.add((OperationOutcome) e.getResource());
                }
            }
        }
        // If the validation-response is that of an OperationOutcome,
        // then extract the issues from it.
        else if (resource instanceof OperationOutcome)
        {
            operationOutcomes.add((OperationOutcome) resource);
        }

        return operationOutcomes;
    }
}

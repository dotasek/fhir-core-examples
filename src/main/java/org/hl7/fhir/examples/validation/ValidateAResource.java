package org.hl7.fhir.examples.validation;

import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.model.BaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.utils.EOperationOutcome;
import org.hl7.fhir.validation.ValidationEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidateAResource {
    /**
     * This method will validate the specified FHIR resource {@link File}.
     *
     * @param fhirResourceFile
     * The FHIR resource file {@link File}.
     * @param validationEngine
     * The {@link ValidationEngine}.
     *
     * @param profiles
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
            final ValidationEngine validationEngine,
            List<String> profiles
    ) throws IOException, EOperationOutcome {


        // Call the validation logic.
        final BaseResource resource = validationEngine.validate(Manager.FhirFormat.JSON, new FileInputStream(fhirResourceFile), profiles);

        final List<OperationOutcome> operationOutcomes = new ArrayList<>();

        // If the validation-response is that of a Bundle, then loop
        // its components to look for any OperationOutcomes and extract
        // the issues from them.
        if (resource instanceof Bundle)
        {
            for (Bundle.BundleEntryComponent e : ((Bundle) resource).getEntry())
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

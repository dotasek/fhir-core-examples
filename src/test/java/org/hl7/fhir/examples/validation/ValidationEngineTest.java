package org.hl7.fhir.examples.validation;

import org.hl7.fhir.examples.validation.test.ImplementationGuideMatcher;
import org.hl7.fhir.examples.validation.test.IssueComponentMatcher;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.validation.IgLoader;
import org.hl7.fhir.validation.ValidationEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationEngineTest {

    final ClassLoader classLoader = getClass().getClassLoader();
    final File packageTgz = new File(classLoader.getResource("us-core-package.tgz").getFile());

    final File nonUSCoreConformantPatient = new File(classLoader.getResource("patient-non-uscore.json").getFile());

    final File usCoreConformantPatient = new File(classLoader.getResource("patient-uscore.json").getFile());

    final String usCorePatientProfileUri = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient";

    @Test
    @DisplayName("Build a validation engine, load necessary r5 extensions, add the US Core IG from a tgz, add the Patient profile and validate patient resources")
    void validateWithUSCoreIGAndPatientProfile() throws IOException, URISyntaxException {

        final ValidationEngineInitialization validationEngineInitialization = ValidationEngineInitialization.buildValidationEngine("4.0.1");

        final ValidationEngine validationEngine = validationEngineInitialization.validationEngine;
        final IgLoader igLoader = validationEngineInitialization.igLoader;

        LoadIGs.loadIG(validationEngine,igLoader,packageTgz.getAbsolutePath());

        // Prepare the ValidationEngine.
        validationEngine.prepare();

        assertThat(validationEngine.getIgs(), hasItem(new ImplementationGuideMatcher("http://hl7.org/fhir/us/core/ImplementationGuide/hl7.fhir.us.core", "6.0.0-ballot")));

        List<OperationOutcome> outcomeFailure = ValidateAResource.validateFhirResource(nonUSCoreConformantPatient, validationEngine,
                List.of(usCorePatientProfileUri));

        assertEquals(1, outcomeFailure.size(), "Should have only one validation outcome.");

        List<OperationOutcome.OperationOutcomeIssueComponent> failureIssueComponents = outcomeFailure.get(0).getIssue();

        assertEquals(3, countIssuesOfSeverity(failureIssueComponents, OperationOutcome.IssueSeverity.ERROR), "Should have 3 errors");

        assertThat(failureIssueComponents, hasItems(
                new IssueComponentMatcher(OperationOutcome.IssueSeverity.ERROR, "Patient.gender: minimum required = 1, but only found 0"),
                new IssueComponentMatcher(OperationOutcome.IssueSeverity.ERROR, "Patient.identifier: minimum required = 1, but only found 0"),
                new IssueComponentMatcher(OperationOutcome.IssueSeverity.ERROR, "Patient.name: minimum required = 1, but only found 0")
        ));

        List<OperationOutcome> outcomeSuccess = ValidateAResource.validateFhirResource(usCoreConformantPatient, validationEngine, new ArrayList<String>());

        List<OperationOutcome.OperationOutcomeIssueComponent> successIssueComponents = outcomeSuccess.get(0).getIssue();

        assertEquals(0, countIssuesOfSeverity(successIssueComponents, OperationOutcome.IssueSeverity.ERROR), "Should have no errors");

    }

    private int countIssuesOfSeverity( List<OperationOutcome.OperationOutcomeIssueComponent> issueComponents, OperationOutcome.IssueSeverity severity){
        int count = 0;
        for (OperationOutcome.OperationOutcomeIssueComponent issueComponent : issueComponents) {
            if (severity.equals(issueComponent.getSeverity())) {
                count++;
            }
        }
        return count;
    }
}

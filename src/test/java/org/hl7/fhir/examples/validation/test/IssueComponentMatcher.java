package org.hl7.fhir.examples.validation.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hl7.fhir.r5.model.OperationOutcome;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueComponentMatcher extends TypeSafeMatcher<OperationOutcome.OperationOutcomeIssueComponent> {

    private final OperationOutcome.IssueSeverity severity;
    private final Pattern regexPattern;

    public IssueComponentMatcher(OperationOutcome.IssueSeverity severity, String regexString) {
        this.severity = severity;
        this.regexPattern = Pattern.compile(regexString);
    }

    @Override
    protected boolean matchesSafely(OperationOutcome.OperationOutcomeIssueComponent operationOutcome) {
        if (!severity.equals(operationOutcome.getSeverity())) {
            return false;
        }
        if (!operationOutcome.getDetails().hasText()) {
            return false;
        }
        Matcher matcher = regexPattern.matcher(operationOutcome.getDetails().getText());
        return matcher.find();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an OperationOutcome with severity " + severity + " and containing a match for the regex '" + regexPattern.pattern() + "'");
    }
}

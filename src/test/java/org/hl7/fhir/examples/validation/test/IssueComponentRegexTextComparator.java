package org.hl7.fhir.examples.validation.test;


import org.hl7.fhir.r5.model.OperationOutcome;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IssueComponentRegexTextComparator implements Comparator<OperationOutcome.OperationOutcomeIssueComponent> {


    /**
     * Intended for assertj .usingElementComparator use. Allow the use of regex to match issues.
     *
     * Can only be used in conditions checking for relative equality (0).
     *
     * @param o1 the OperationOutcomeIssueComponent in the list
     * @param o2 the OperationOutcomeIssueComponent to check for. The text is interpreted as a regex to make these tests
     *          less brittle.
     * @return 0 if the severity of both issues is the same and the text of the first issue matches the regex in the
     * text of the second.
     */
    @Override
    public int compare(OperationOutcome.OperationOutcomeIssueComponent o1, OperationOutcome.OperationOutcomeIssueComponent o2) {
        if (o1.getSeverity().compareTo(o2.getSeverity()) != 0) {
            return o1.getSeverity().compareTo(o2.getSeverity());
        }
        if (o1.getDetails().hasText() == o2.getDetails().hasText()) {
            if (o2.getDetails().hasText()) {
                Pattern regexPattern = Pattern.compile(o2.getDetails().getText());
                Matcher matcher = regexPattern.matcher(o1.getDetails().getText());
                if (matcher.find()) {
                    return 0;
                }
            }
        }
        return -1;
    }
}

package org.hl7.fhir.examples.validation.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ImplementationGuideMatcher extends TypeSafeMatcher<org.hl7.fhir.r5.model.ImplementationGuide> {

    final String url;
    final String version;

    public ImplementationGuideMatcher(String url, String version) {
       this.url = url;
       this.version = version;
    }

    @Override
    protected boolean matchesSafely(org.hl7.fhir.r5.model.ImplementationGuide implementationGuide) {
        return implementationGuide.getUrl().equals(url) && implementationGuide.getVersion().equals(version);

    }

        @Override
        public void describeTo(Description description) {
        description.appendText("an Implementation Guide with url " + url + " and version " + version);
    }
}

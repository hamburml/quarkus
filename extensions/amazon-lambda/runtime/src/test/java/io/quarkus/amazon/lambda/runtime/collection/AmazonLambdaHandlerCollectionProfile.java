package io.quarkus.amazon.lambda.runtime.collection;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class AmazonLambdaHandlerCollectionProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("quarkus.lambda.handler", "collectionHandler");
    }
}

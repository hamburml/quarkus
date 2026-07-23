package io.quarkus.it.amazon.lambda.opentelemetry;

import jakarta.inject.Inject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

/**
 * Lambda handler that records one metric data point per invocation, so that tests can verify the
 * data point is exported right away instead of waiting for the periodic export interval.
 * <p>
 * Sending {@code "count"} as input returns the number of metric data points seen so far by the
 * {@link InMemoryMetricExporter}, instead of recording a new invocation. This avoids the need for a
 * separate HTTP endpoint (and therefore a second HTTP listener competing with the Lambda mock event
 * server for the test port) just to read back the exported metric data.
 */
public class CounterLambda implements RequestHandler<String, String> {

    @Inject
    Meter meter;

    @Inject
    InMemoryMetricExporter metricExporter;

    private volatile LongCounter invocationCounter;

    @Override
    public String handleRequest(String input, Context context) {
        if ("count".equals(input)) {
            return String.valueOf(metricExporter.countPointsFor("lambda.invocations"));
        }
        getInvocationCounter().add(1);
        return "ok";
    }

    private LongCounter getInvocationCounter() {
        if (invocationCounter == null) {
            invocationCounter = meter.counterBuilder("lambda.invocations").build();
        }
        return invocationCounter;
    }
}

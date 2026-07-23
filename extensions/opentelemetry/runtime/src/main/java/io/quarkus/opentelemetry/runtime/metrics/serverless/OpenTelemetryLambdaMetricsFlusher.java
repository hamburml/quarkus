package io.quarkus.opentelemetry.runtime.metrics.serverless;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.quarkus.amazon.lambda.runtime.spi.LambdaInvocationCompletedEvent;
import io.quarkus.opentelemetry.runtime.config.runtime.OTelRuntimeConfig;

/**
 * On AWS Lambda, the execution environment can be frozen or reclaimed as soon as an invocation finishes.
 * A {@code PeriodicMetricReader} relies on a background thread that only gets a chance to run between
 * invocations, which is not guaranteed to happen before the environment is frozen. Without this listener,
 * recorded metrics can therefore be delayed by minutes or lost entirely.
 * <p>
 * This bean is only registered when {@code quarkus.otel.simple} is enabled and the application is built as
 * an AWS Lambda function, see {@code OpenTelemetryLambdaProcessor}.
 */
@Singleton
public class OpenTelemetryLambdaMetricsFlusher {

    private static final Logger log = Logger.getLogger(OpenTelemetryLambdaMetricsFlusher.class);

    private final OpenTelemetry openTelemetry;
    private final OTelRuntimeConfig otelRuntimeConfig;

    public OpenTelemetryLambdaMetricsFlusher(OpenTelemetry openTelemetry, OTelRuntimeConfig otelRuntimeConfig) {
        this.openTelemetry = openTelemetry;
        this.otelRuntimeConfig = otelRuntimeConfig;
    }

    void onInvocationCompleted(@Observes LambdaInvocationCompletedEvent event) {
        if (openTelemetry instanceof OpenTelemetrySdk openTelemetrySdk) {
            var result = openTelemetrySdk.getSdkMeterProvider().forceFlush();
            var timeout = otelRuntimeConfig.metric().lambdaFlushTimeout();
            if (!result.join(timeout.toMillis(), MILLISECONDS).isSuccess()) {
                log.debug("Timed out while flushing OpenTelemetry metrics after a Lambda invocation");
            }
        }
    }
}

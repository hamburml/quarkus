package io.quarkus.opentelemetry.runtime.config.runtime;

import java.time.Duration;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

public interface MetricsRuntimeConfig {

    /**
     * The interval, between the start of two metric export attempts.
     * <p>
     * Default is 1min.
     *
     * @return the interval Duration.
     */
    @WithName("export.interval")
    @WithDefault("60s")
    Duration exportInterval();

    /**
     * The maximum amount of time to wait for metrics to be flushed after an AWS Lambda invocation
     * completes, when {@code quarkus.otel.simple} is enabled.
     * <p>
     * Only takes effect when the application is built as an AWS Lambda function. Kept deliberately
     * short, since unlike {@code quarkus.otel.experimental.shutdown-wait-time} (paid once, at
     * application shutdown), this delay is added to every single invocation.
     * <p>
     * Defaults to 100ms, which should comfortably cover an OpenTelemetry Collector running in its
     * own Lambda layer (a loopback call). If your OTLP destination is a remote collector, you may
     * need to increase this value.
     */
    @WithName("lambda-flush-timeout")
    @WithDefault("100ms")
    Duration lambdaFlushTimeout();
}

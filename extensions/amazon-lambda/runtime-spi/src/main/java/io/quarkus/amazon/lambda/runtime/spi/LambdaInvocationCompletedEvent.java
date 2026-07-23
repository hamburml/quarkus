package io.quarkus.amazon.lambda.runtime.spi;

import java.util.Objects;

/**
 * CDI event fired once a single AWS Lambda invocation has finished, right before control is handed back
 * to the Lambda runtime. This happens both for successful invocations ({@link Succeeded}) and for
 * invocations that failed with an exception ({@link Failed}).
 * <p>
 * The AWS Lambda execution environment may be frozen or reclaimed at any point after this event has been
 * observed, so it is the last reliable opportunity to flush any buffered telemetry (metrics, traces, logs)
 * that would otherwise only be sent out on a periodic schedule that might never get a chance to run again.
 */
public sealed interface LambdaInvocationCompletedEvent {

    /**
     * The invocation completed successfully.
     */
    record Succeeded() implements LambdaInvocationCompletedEvent {
    }

    /**
     * The invocation failed with an exception.
     *
     * @param cause the exception the invocation failed with; must not be {@code null}
     */
    record Failed(Throwable cause) implements LambdaInvocationCompletedEvent {
        public Failed {
            Objects.requireNonNull(cause, "cause must not be null");
        }
    }
}

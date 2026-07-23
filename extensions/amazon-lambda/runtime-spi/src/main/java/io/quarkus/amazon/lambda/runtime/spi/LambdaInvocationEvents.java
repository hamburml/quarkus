package io.quarkus.amazon.lambda.runtime.spi;

import org.jboss.logging.Logger;

import io.quarkus.arc.Arc;

/**
 * Fires {@link LambdaInvocationCompletedEvent} to any interested CDI beans (e.g. telemetry exporters),
 * so that they get a last chance to flush any buffered work before the AWS Lambda execution environment is
 * potentially frozen or reclaimed.
 * <p>
 * This is used from code that runs outside of CDI's control (e.g. the Lambda poll loop and the JVM runtime
 * handler wrapper), which is why the event is fired through a static {@link Arc#container()} lookup instead
 * of a regular {@code Event<T>} injection point.
 */
public final class LambdaInvocationEvents {

    private static final Logger log = Logger.getLogger(LambdaInvocationEvents.class);

    private LambdaInvocationEvents() {
    }

    /**
     * Notifies interested CDI beans that an invocation just finished successfully.
     */
    public static void fireSucceeded() {
        fire(new LambdaInvocationCompletedEvent.Succeeded());
    }

    /**
     * Notifies interested CDI beans that an invocation just failed.
     *
     * @param cause the exception the invocation failed with; must not be {@code null}
     */
    public static void fireFailed(Throwable cause) {
        fire(new LambdaInvocationCompletedEvent.Failed(cause));
    }

    private static void fire(LambdaInvocationCompletedEvent event) {
        try {
            if (Arc.container() != null && Arc.container().isRunning()) {
                Arc.container().beanManager().getEvent().fire(event);
            }
        } catch (RuntimeException e) {
            // Observers of this event (e.g. telemetry exporters) must never be able to break the
            // actual Lambda invocation lifecycle they are only supposed to observe.
            log.warn("Failed to notify observers that a Lambda invocation completed", e);
        }
    }
}

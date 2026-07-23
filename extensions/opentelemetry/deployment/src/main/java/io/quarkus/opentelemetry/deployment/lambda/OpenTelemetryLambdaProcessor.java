package io.quarkus.opentelemetry.deployment.lambda;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.opentelemetry.deployment.metric.MetricsEnabled;
import io.quarkus.opentelemetry.runtime.config.build.OTelBuildConfig;
import io.quarkus.opentelemetry.runtime.metrics.serverless.OpenTelemetryLambdaMetricsFlusher;

public class OpenTelemetryLambdaProcessor {

    @BuildStep(onlyIf = MetricsEnabled.class)
    void registerLambdaMetricsFlusher(Capabilities capabilities, OTelBuildConfig otelBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> beans) {
        if (otelBuildConfig.simple() && capabilities.isPresent(Capability.AMAZON_LAMBDA)) {
            beans.produce(new AdditionalBeanBuildItem(OpenTelemetryLambdaMetricsFlusher.class));
        }
    }
}

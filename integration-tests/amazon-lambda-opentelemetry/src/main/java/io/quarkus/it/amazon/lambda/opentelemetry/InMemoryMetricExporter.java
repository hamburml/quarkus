package io.quarkus.it.amazon.lambda.opentelemetry;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.enterprise.context.ApplicationScoped;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.quarkus.arc.Unremovable;

/**
 * Minimal in-memory {@link MetricExporter} used by the test to observe when metric data points are
 * actually exported, without depending on a real OTLP collector.
 */
@Unremovable
@ApplicationScoped
public class InMemoryMetricExporter implements MetricExporter {

    private final Queue<MetricData> exportedMetrics = new ConcurrentLinkedQueue<>();

    public int countPointsFor(String metricName) {
        return exportedMetrics.stream()
                .filter(metricData -> metricData.getName().equals(metricName))
                .mapToInt(metricData -> metricData.getData().getPoints().size())
                .sum();
    }

    public void reset() {
        exportedMetrics.clear();
    }

    @Override
    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return AggregationTemporality.CUMULATIVE;
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> metrics) {
        exportedMetrics.addAll(metrics);
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        exportedMetrics.clear();
        return CompletableResultCode.ofSuccess();
    }
}

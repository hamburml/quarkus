package io.quarkus.it.amazon.lambda.opentelemetry;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AmazonLambdaOpenTelemetryTestCase {

    @Test
    public void metricIsFlushedRightAfterInvocation() {
        given()
                .contentType("application/json")
                .accept("application/json")
                .body("\"invoke\"")
                .when()
                .post()
                .then()
                .statusCode(200)
                .body(equalTo("\"ok\""));

        // quarkus.otel.metric.export.interval is set to 1h in application.properties, so this can only
        // pass because the invocation itself triggered a flush, not because the periodic exporter ran.
        Awaitility.await().atMost(5, SECONDS)
                .untilAsserted(() -> assertTrue(metricPointCount() > 0));
    }

    private int metricPointCount() {
        return Integer.parseInt(
                given()
                        .contentType("application/json")
                        .accept("application/json")
                        .body("\"count\"")
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().body().asString().replace("\"", ""));
    }
}

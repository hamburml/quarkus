package io.quarkus.amazon.lambda.runtime.collection;

import static io.restassured.RestAssured.given;

import java.util.LinkedList;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(AmazonLambdaHandlerCollectionProfile.class)
public class AmazonLambdaHandlerCollectionTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void testFruitsLambda() throws Exception {

        List<Fruit> fruits = new LinkedList<>();
        fruits.add(new Fruit("apple", "an apple a day..."));
        fruits.add(new Fruit("strawberry", "red fruit"));

        given()
                .body(objectMapper.writeValueAsString(fruits))
                .when()
                .post()
                .then()
                .statusCode(204);
    }
}

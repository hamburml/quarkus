package io.quarkus.amazon.lambda.runtime.collection;

import java.util.List;

import jakarta.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.quarkus.logging.Log;

@Named("collectionHandler")
public class AmazonLambdaHandlerCollection implements RequestHandler<List<Fruit>, Void> {

    @Override
    public Void handleRequest(List<Fruit> input, Context context) {
        input.forEach(this::printName);
        return null;
    }

    private void printName(Fruit fruit) {
        Log.info(fruit.name());
        Log.info(fruit.description());
    }
}

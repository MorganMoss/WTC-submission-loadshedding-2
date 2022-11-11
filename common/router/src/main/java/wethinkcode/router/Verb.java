package wethinkcode.router;

import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Handler;

import java.util.function.BiConsumer;

public enum Verb {
    GET (ApiBuilder::get),
    POST (ApiBuilder::post);
    //TODO: Add the rest as needed
    final BiConsumer<String, Handler> verb;

    Verb(BiConsumer<String, Handler> verb){
        this.verb = verb;
    }
}

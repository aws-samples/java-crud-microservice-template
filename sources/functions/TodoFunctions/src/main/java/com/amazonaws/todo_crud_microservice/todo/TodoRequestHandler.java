/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.amazonaws.todo_crud_microservice.todo;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.DataAccess;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.TodoDynamoDataAccess;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;

import java.util.HashMap;
import java.util.Map;

public abstract class TodoRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

//    protected static final boolean LOCAL = !StringUtils.isEmpty(System.getenv("AWS_SAM_LOCAL"));

    protected final ObjectMapper mapper = new ObjectMapper();

    protected DataAccess<Todo> dataAccess;

    public TodoRequestHandler() {
        this(new TodoDynamoDataAccess());
    }

    TodoRequestHandler(DataAccess<Todo> todoDataAccess) {
        this.dataAccess = todoDataAccess;
    }

    private APIGatewayProxyResponseEvent response() {
        return response(null);
    }

    protected APIGatewayProxyResponseEvent response(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
    }

    protected APIGatewayProxyResponseEvent ok(String body) {
        return response().withStatusCode(200).withBody(body);
    }

    protected APIGatewayProxyResponseEvent badRequest(String message) {
        return response().withStatusCode(400).withBody("{\"error\":\"Bad request\", \"message\":\"" + message + "\"}");
    }

    protected APIGatewayProxyResponseEvent notFound(String message) {
        return response().withStatusCode(404).withBody("{\"error\":\"Not found\", \"message\":\"" + message + "\"}");
    }

    protected APIGatewayProxyResponseEvent created(String body) {
        return response().withStatusCode(201).withBody(body);
    }

    protected APIGatewayProxyResponseEvent error() {
        return response().withStatusCode(500).withBody("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }

}

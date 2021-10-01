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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.DataAccess;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.PaginatedList;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class ListTodosFunction extends TodoRequestHandler {

    private static final Logger log = LogManager.getLogger();

    public ListTodosFunction() {
        super();
    }

    public ListTodosFunction(DataAccess<Todo> todoDataAccess) {
        super(todoDataAccess);
    }

    @Logging
    @Tracing
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        String nextToken = event.getHeaders().get("X-next-token");

        try {
            PaginatedList<Todo> todos = dataAccess.list(nextToken);

            Map<String, String> headers = new HashMap<>();
            headers.put("X-max-results", String.valueOf(todos.getTotal()));
            if (todos.getNextToken() != null){
                headers.put("X-next-token", todos.getNextToken());
            }

            return response(headers).withStatusCode(200).withBody(mapper.writeValueAsString(todos.getItems()));
        } catch (Exception e) {
            log.error("Internal Error", e);
            return error();
        }
    }
}

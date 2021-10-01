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

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.*;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

public class UpdateTodoFunctionTest extends TodoFunctionsTests {

    @ParameterizedTest
    @Event(value = "update_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdateTodoOK(APIGatewayProxyRequestEvent event) {
        Todo todo = new Todo();
        todo.setId(ID);
        todo.setCreatedAt(new Date().getTime());
        todo.setTask("Do this");
        todo.setDescription("Do this");
        todo.setCompleted(false);
        given(dataAccess.get(ID)).willReturn(todo);

        UpdateTodoFunction handler = new UpdateTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"message\":\"item " + ID + " updated\"}");

        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
        Mockito.verify(dataAccess).update(argumentCaptor.capture());
        Todo capturedArgument = argumentCaptor.getValue();
        assertThat(capturedArgument.getId()).isEqualTo(ID);
        assertThat(capturedArgument.getTask()).isEqualTo("Add unit tests to my Lambda functions");
        assertThat(capturedArgument.getDescription()).isEqualTo("Create unit tests for each Lambda function in the crud microservice");
        assertThat(capturedArgument.getCreatedAt()).isCloseTo(new Date().getTime(), within(1000L));
        assertThat(capturedArgument.isCompleted()).isEqualTo(true);
    }

    @ParameterizedTest
    @Event(value = "update_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdateTodoNotFound(APIGatewayProxyRequestEvent event) {
        given(dataAccess.get(ID)).willReturn(null);

        UpdateTodoFunction handler = new UpdateTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("{\"error\":\"Not found\", \"message\":\"item " + ID + " not found\"}");
    }

    @ParameterizedTest
    @HandlerParams(
            events = @Events(
                    events = {
                            @Event(value = "update_todo/events/bad_request.json"),
                            @Event(value = "update_todo/events/malformed_todo.json"),
                    },
                    type = APIGatewayProxyRequestEvent.class

            ),
            responses = @Responses(
                    responses = {
                           @Response("update_todo/responses/bad_request.json"),
                           @Response("update_todo/responses/malformed_todo.json")
                    },
                    type = APIGatewayProxyResponseEvent.class
            )
    )
    public void testReadTodoBadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        UpdateTodoFunction handler = new UpdateTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "update_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdateTodoInternalError(APIGatewayProxyRequestEvent event) {
        UpdateTodoFunction handler = new UpdateTodoFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).get(ArgumentMatchers.any(String.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

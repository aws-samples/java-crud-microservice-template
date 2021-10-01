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
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.amazonaws.services.lambda.runtime.tests.annotations.Events;
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Responses;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.doThrow;

public class CreateTodoFunctionTest extends TodoFunctionsTests {

    @ParameterizedTest
    @HandlerParams(
            events = @Events(folder = "create_todo/events/", type = APIGatewayProxyRequestEvent.class),
            responses = @Responses(folder = "create_todo/responses/", type = APIGatewayProxyResponseEvent.class)
    )
    public void testCreateTodo(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        CreateTodoFunction handler = new CreateTodoFunction(dataAccess);

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());

        if (response.getStatusCode() == 201) {
            ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
            Mockito.verify(dataAccess).create(argumentCaptor.capture());
            Todo capturedArgument = argumentCaptor.getValue();
            assertThat(capturedArgument.getId()).isEqualTo("c6af9ac6-7b61-11e6-9a41-93e8deadbeef");
            assertThat(capturedArgument.getTask()).isEqualTo("Add integration tests to my crud application");
            assertThat(capturedArgument.getDescription()).isEqualTo("Create integration tests for todo crud microservices.");
            assertThat(capturedArgument.getCreatedAt()).isCloseTo(new Date().getTime(), within(1000L));
        }
    }

    @ParameterizedTest
    @Event(value = "create_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreateTodoInternalError(APIGatewayProxyRequestEvent event) {
        CreateTodoFunction handler = new CreateTodoFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).create(ArgumentMatchers.any(Todo.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

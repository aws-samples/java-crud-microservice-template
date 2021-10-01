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
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

public class ReadTodoFunctionTest  extends TodoFunctionsTests {

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "read_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "read_todo/responses/ok.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testReadTodoOK(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) throws JsonProcessingException, JSONException {
        Todo todo = new Todo();
        todo.setCreatedAt(1621847956);
        todo.setId(ID);
        todo.setTask("Add tests");
        todo.setDescription("Create Unit Tests");
        todo.setCompleted(false);

        given(dataAccess.get(ID)).willReturn(todo);

        ReadTodoFunction handler = new ReadTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(200);
        JSONAssert.assertEquals(mapper.writeValueAsString(todo), response.getBody(), JSONCompareMode.LENIENT);
    }

    @ParameterizedTest
    @Event(value = "read_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testReadTodoNotFound(APIGatewayProxyRequestEvent event) {
        given(dataAccess.get(ID)).willReturn(null);

        ReadTodoFunction handler = new ReadTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("{\"error\":\"Not found\", \"message\":\"item " + ID + " not found\"}");
    }

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "read_todo/events/bad_request.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "read_todo/responses/bad_request.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testReadTodoBadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        ReadTodoFunction handler = new ReadTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "read_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreateTodoInternalError(APIGatewayProxyRequestEvent event) {
        ReadTodoFunction handler = new ReadTodoFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).get(ID);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

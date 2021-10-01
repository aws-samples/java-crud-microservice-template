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
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class DeleteTodoFunctionTest extends TodoFunctionsTests {

    @ParameterizedTest
    @Event(value = "delete_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testDeleteTodoOK(APIGatewayProxyRequestEvent event) {
        DeleteTodoFunction handler = new DeleteTodoFunction(dataAccess);
        handler.handleRequest(event, context);

        verify(dataAccess).delete(ID);
    }

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "delete_todo/events/bad_request.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "delete_todo/responses/bad_request.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testDeleteTodoBadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        DeleteTodoFunction handler = new DeleteTodoFunction(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "delete_todo/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testDeleteTodoInternalError(APIGatewayProxyRequestEvent event) {
        DeleteTodoFunction handler = new DeleteTodoFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).delete(ArgumentMatchers.any(String.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

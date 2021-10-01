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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.PaginatedList;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONCompareMode;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ListTodoFunctionTest extends TodoFunctionsTests {

    @ParameterizedTest
    @Event(value = "list_todos/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testListTodosOK(APIGatewayProxyRequestEvent event) throws JsonProcessingException, JSONException {
        Todo todo = new Todo();
        todo.setCreatedAt(1621847956);
        todo.setId(ID);
        todo.setTask("Add tests");
        todo.setDescription("Create Unit Tests");

        List<Todo> items = Collections.singletonList(todo);
        PaginatedList<Todo> todos = new PaginatedList<>(items, 3, null);

        given(dataAccess.list(isNull())).willReturn(todos);

        ListTodosFunction listTodosFunction = new ListTodosFunction(dataAccess);
        APIGatewayProxyResponseEvent responseEvent = listTodosFunction.handleRequest(event, context);

        assertThat(responseEvent.getHeaders()).doesNotContainKey("X-next-token");
        assertThat(responseEvent.getHeaders()).containsKey("X-max-results");
        assertThat(responseEvent.getHeaders().get("X-max-results")).isEqualTo("3");

        assertEquals(mapper.writeValueAsString(items), responseEvent.getBody(), JSONCompareMode.LENIENT);

        verify(dataAccess).list(isNull());
    }

    @ParameterizedTest
    @Event(value = "list_todos/events/pagination.json", type = APIGatewayProxyRequestEvent.class)
    public void testListTodosPagination(APIGatewayProxyRequestEvent event) throws JsonProcessingException, JSONException {
        Todo todo = new Todo();
        todo.setCreatedAt(1621847956);
        todo.setId(ID);
        todo.setTask("Add tests");
        todo.setDescription("Create Unit Tests");

        List<Todo> items = Collections.singletonList(todo);
        PaginatedList<Todo> todos = new PaginatedList<>(items, 3, "6c0495b4-c4f5-436a-a15f-1603fa4904e9");

        given(dataAccess.list(anyString())).willReturn(todos);

        ListTodosFunction listTodosFunction = new ListTodosFunction(dataAccess);
        APIGatewayProxyResponseEvent responseEvent = listTodosFunction.handleRequest(event, context);

        assertThat(responseEvent.getHeaders()).containsKey("X-next-token");
        assertThat(responseEvent.getHeaders().get("X-next-token")).isEqualTo("6c0495b4-c4f5-436a-a15f-1603fa4904e9");
        assertThat(responseEvent.getHeaders()).containsKey("X-max-results");
        assertThat(responseEvent.getHeaders().get("X-max-results")).isEqualTo("3");

        assertEquals(mapper.writeValueAsString(items), responseEvent.getBody(), JSONCompareMode.LENIENT);

        verify(dataAccess).list("a51b03b3-3d3d-461b-86fa-c0076158cbc5");
    }

    @ParameterizedTest
    @Event(value = "list_todos/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreateTodoInternalError(APIGatewayProxyRequestEvent event) {
        ListTodosFunction handler = new ListTodosFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).list(isNull());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

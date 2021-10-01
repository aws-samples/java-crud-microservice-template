package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.PaginatedList;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
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

public class List{{cookiecutter.object_model|capitalize}}FunctionTest extends {{cookiecutter.object_model|capitalize}}FunctionsTests {

    @ParameterizedTest
    @Event(value = "list_{{cookiecutter.object_model}}s/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testList{{cookiecutter.object_model|capitalize}}sOK(APIGatewayProxyRequestEvent event) throws JsonProcessingException, JSONException {
        {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = new {{cookiecutter.object_model|capitalize}}();
        {{cookiecutter.object_model}}.setCreatedAt(1621847956);
        {{cookiecutter.object_model}}.setId(ID);
        {{cookiecutter.object_model}}.setTask("Add tests");
        {{cookiecutter.object_model}}.setDescription("Create Unit Tests");

        List<{{cookiecutter.object_model|capitalize}}> items = Collections.singletonList({{cookiecutter.object_model}});
        PaginatedList<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}s = new PaginatedList<>(items, 3, null);

        given(dataAccess.list(isNull())).willReturn({{cookiecutter.object_model}}s);

        List{{cookiecutter.object_model|capitalize}}sFunction list{{cookiecutter.object_model|capitalize}}sFunction = new List{{cookiecutter.object_model|capitalize}}sFunction(dataAccess);
        APIGatewayProxyResponseEvent responseEvent = list{{cookiecutter.object_model|capitalize}}sFunction.handleRequest(event, context);

        assertThat(responseEvent.getHeaders()).doesNotContainKey("X-next-token");
        assertThat(responseEvent.getHeaders()).containsKey("X-max-results");
        assertThat(responseEvent.getHeaders().get("X-max-results")).isEqualTo("3");

        assertEquals(mapper.writeValueAsString(items), responseEvent.getBody(), JSONCompareMode.LENIENT);

        verify(dataAccess).list(isNull());
    }

    @ParameterizedTest
    @Event(value = "list_{{cookiecutter.object_model}}s/events/pagination.json", type = APIGatewayProxyRequestEvent.class)
    public void testList{{cookiecutter.object_model|capitalize}}sPagination(APIGatewayProxyRequestEvent event) throws JsonProcessingException, JSONException {
        {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = new {{cookiecutter.object_model|capitalize}}();
        {{cookiecutter.object_model}}.setCreatedAt(1621847956);
        {{cookiecutter.object_model}}.setId(ID);
        {{cookiecutter.object_model}}.setTask("Add tests");
        {{cookiecutter.object_model}}.setDescription("Create Unit Tests");

        List<{{cookiecutter.object_model|capitalize}}> items = Collections.singletonList({{cookiecutter.object_model}});
        PaginatedList<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}s = new PaginatedList<>(items, 3, "6c0495b4-c4f5-436a-a15f-1603fa4904e9");

        given(dataAccess.list(anyString())).willReturn({{cookiecutter.object_model}}s);

        List{{cookiecutter.object_model|capitalize}}sFunction list{{cookiecutter.object_model|capitalize}}sFunction = new List{{cookiecutter.object_model|capitalize}}sFunction(dataAccess);
        APIGatewayProxyResponseEvent responseEvent = list{{cookiecutter.object_model|capitalize}}sFunction.handleRequest(event, context);

        assertThat(responseEvent.getHeaders()).containsKey("X-next-token");
        assertThat(responseEvent.getHeaders().get("X-next-token")).isEqualTo("6c0495b4-c4f5-436a-a15f-1603fa4904e9");
        assertThat(responseEvent.getHeaders()).containsKey("X-max-results");
        assertThat(responseEvent.getHeaders().get("X-max-results")).isEqualTo("3");

        assertEquals(mapper.writeValueAsString(items), responseEvent.getBody(), JSONCompareMode.LENIENT);

        verify(dataAccess).list("a51b03b3-3d3d-461b-86fa-c0076158cbc5");
    }

    @ParameterizedTest
    @Event(value = "list_{{cookiecutter.object_model}}s/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreate{{cookiecutter.object_model|capitalize}}InternalError(APIGatewayProxyRequestEvent event) {
        List{{cookiecutter.object_model|capitalize}}sFunction handler = new List{{cookiecutter.object_model|capitalize}}sFunction(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).list(isNull());

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

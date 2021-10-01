package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};


import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

public class Read{{cookiecutter.object_model|capitalize}}FunctionTest  extends {{cookiecutter.object_model|capitalize}}FunctionsTests {

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "read_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "read_{{cookiecutter.object_model}}/responses/ok.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testRead{{cookiecutter.object_model|capitalize}}OK(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) throws JsonProcessingException, JSONException {
        {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = new {{cookiecutter.object_model|capitalize}}();
        {{cookiecutter.object_model}}.setCreatedAt(1621847956);
        {{cookiecutter.object_model}}.setId(ID);
        {{cookiecutter.object_model}}.setTask("Add tests");
        {{cookiecutter.object_model}}.setDescription("Create Unit Tests");
        {{cookiecutter.object_model}}.setCompleted(false);

        given(dataAccess.get(ID)).willReturn({{cookiecutter.object_model}});

        Read{{cookiecutter.object_model|capitalize}}Function handler = new Read{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(200);
        JSONAssert.assertEquals(mapper.writeValueAsString({{cookiecutter.object_model}}), response.getBody(), JSONCompareMode.LENIENT);
    }

    @ParameterizedTest
    @Event(value = "read_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testRead{{cookiecutter.object_model|capitalize}}NotFound(APIGatewayProxyRequestEvent event) {
        given(dataAccess.get(ID)).willReturn(null);

        Read{{cookiecutter.object_model|capitalize}}Function handler = new Read{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("{\"error\":\"Not found\", \"message\":\"item " + ID + " not found\"}");
    }

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "read_{{cookiecutter.object_model}}/events/bad_request.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "read_{{cookiecutter.object_model}}/responses/bad_request.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testRead{{cookiecutter.object_model|capitalize}}BadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        Read{{cookiecutter.object_model|capitalize}}Function handler = new Read{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "read_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreate{{cookiecutter.object_model|capitalize}}InternalError(APIGatewayProxyRequestEvent event) {
        Read{{cookiecutter.object_model|capitalize}}Function handler = new Read{{cookiecutter.object_model|capitalize}}Function(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).get(ID);

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

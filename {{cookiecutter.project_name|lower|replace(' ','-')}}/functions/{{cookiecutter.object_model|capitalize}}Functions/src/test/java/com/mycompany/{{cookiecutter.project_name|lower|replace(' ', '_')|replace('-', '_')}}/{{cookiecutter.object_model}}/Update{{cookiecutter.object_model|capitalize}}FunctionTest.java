package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.*;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
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

public class Update{{cookiecutter.object_model|capitalize}}FunctionTest extends {{cookiecutter.object_model|capitalize}}FunctionsTests {

    @ParameterizedTest
    @Event(value = "update_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdate{{cookiecutter.object_model|capitalize}}OK(APIGatewayProxyRequestEvent event) {
        {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = new {{cookiecutter.object_model|capitalize}}();
        {{cookiecutter.object_model}}.setId(ID);
        {{cookiecutter.object_model}}.setCreatedAt(new Date().getTime());
        {{cookiecutter.object_model}}.setTask("Do this");
        {{cookiecutter.object_model}}.setDescription("Do this");
        {{cookiecutter.object_model}}.setCompleted(false);
        given(dataAccess.get(ID)).willReturn({{cookiecutter.object_model}});

        Update{{cookiecutter.object_model|capitalize}}Function handler = new Update{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"message\":\"item " + ID + " updated\"}");

        ArgumentCaptor<{{cookiecutter.object_model|capitalize}}> argumentCaptor = ArgumentCaptor.forClass({{cookiecutter.object_model|capitalize}}.class);
        Mockito.verify(dataAccess).update(argumentCaptor.capture());
        {{cookiecutter.object_model|capitalize}} capturedArgument = argumentCaptor.getValue();
        assertThat(capturedArgument.getId()).isEqualTo(ID);
        assertThat(capturedArgument.getTask()).isEqualTo("Add unit tests to my Lambda functions");
        assertThat(capturedArgument.getDescription()).isEqualTo("Create unit tests for each Lambda function in the crud microservice");
        assertThat(capturedArgument.getCreatedAt()).isCloseTo(new Date().getTime(), within(1000L));
        assertThat(capturedArgument.isCompleted()).isEqualTo(true);
    }

    @ParameterizedTest
    @Event(value = "update_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdate{{cookiecutter.object_model|capitalize}}NotFound(APIGatewayProxyRequestEvent event) {
        given(dataAccess.get(ID)).willReturn(null);

        Update{{cookiecutter.object_model|capitalize}}Function handler = new Update{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("{\"error\":\"Not found\", \"message\":\"item " + ID + " not found\"}");
    }

    @ParameterizedTest
    @HandlerParams(
            events = @Events(
                    events = {
                            @Event(value = "update_{{cookiecutter.object_model}}/events/bad_request.json"),
                            @Event(value = "update_{{cookiecutter.object_model}}/events/malformed_{{cookiecutter.object_model}}.json"),
                    },
                    type = APIGatewayProxyRequestEvent.class

            ),
            responses = @Responses(
                    responses = {
                           @Response("update_{{cookiecutter.object_model}}/responses/bad_request.json"),
                           @Response("update_{{cookiecutter.object_model}}/responses/malformed_{{cookiecutter.object_model}}.json")
                    },
                    type = APIGatewayProxyResponseEvent.class
            )
    )
    public void testRead{{cookiecutter.object_model|capitalize}}BadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        Update{{cookiecutter.object_model|capitalize}}Function handler = new Update{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "update_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testUpdate{{cookiecutter.object_model|capitalize}}InternalError(APIGatewayProxyRequestEvent event) {
        Update{{cookiecutter.object_model|capitalize}}Function handler = new Update{{cookiecutter.object_model|capitalize}}Function(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).get(ArgumentMatchers.any(String.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

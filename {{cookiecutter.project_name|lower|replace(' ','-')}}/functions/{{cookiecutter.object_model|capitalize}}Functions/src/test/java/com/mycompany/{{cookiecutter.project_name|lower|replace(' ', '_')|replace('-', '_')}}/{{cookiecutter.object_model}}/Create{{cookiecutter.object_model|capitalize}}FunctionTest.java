package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.amazonaws.services.lambda.runtime.tests.annotations.Events;
import com.amazonaws.services.lambda.runtime.tests.annotations.HandlerParams;
import com.amazonaws.services.lambda.runtime.tests.annotations.Responses;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.doThrow;

public class Create{{cookiecutter.object_model|capitalize}}FunctionTest extends {{cookiecutter.object_model|capitalize}}FunctionsTests {

    @ParameterizedTest
    @HandlerParams(
            events = @Events(folder = "create_{{cookiecutter.object_model}}/events/", type = APIGatewayProxyRequestEvent.class),
            responses = @Responses(folder = "create_{{cookiecutter.object_model}}/responses/", type = APIGatewayProxyResponseEvent.class)
    )
    public void testCreate{{cookiecutter.object_model|capitalize}}(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        Create{{cookiecutter.object_model|capitalize}}Function handler = new Create{{cookiecutter.object_model|capitalize}}Function(dataAccess);

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());

        if (response.getStatusCode() == 201) {
            ArgumentCaptor<{{cookiecutter.object_model|capitalize}}> argumentCaptor = ArgumentCaptor.forClass({{cookiecutter.object_model|capitalize}}.class);
            Mockito.verify(dataAccess).create(argumentCaptor.capture());
            {{cookiecutter.object_model|capitalize}} capturedArgument = argumentCaptor.getValue();
            assertThat(capturedArgument.getId()).isEqualTo("c6af9ac6-7b61-11e6-9a41-93e8deadbeef");
            assertThat(capturedArgument.getTask()).isEqualTo("Add integration tests to my crud application");
            assertThat(capturedArgument.getDescription()).isEqualTo("Create integration tests for {{cookiecutter.object_model}} crud microservices.");
            assertThat(capturedArgument.getCreatedAt()).isCloseTo(new Date().getTime(), within(1000L));
        }
    }

    @ParameterizedTest
    @Event(value = "create_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testCreate{{cookiecutter.object_model|capitalize}}InternalError(APIGatewayProxyRequestEvent event) {
        Create{{cookiecutter.object_model|capitalize}}Function handler = new Create{{cookiecutter.object_model|capitalize}}Function(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).create(ArgumentMatchers.any({{cookiecutter.object_model|capitalize}}.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

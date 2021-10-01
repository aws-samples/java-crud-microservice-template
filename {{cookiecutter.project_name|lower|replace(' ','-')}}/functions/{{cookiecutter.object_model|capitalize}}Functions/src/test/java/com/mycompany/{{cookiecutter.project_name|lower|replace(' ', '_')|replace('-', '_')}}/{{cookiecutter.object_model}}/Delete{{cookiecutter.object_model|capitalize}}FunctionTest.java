package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

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

public class Delete{{cookiecutter.object_model|capitalize}}FunctionTest extends {{cookiecutter.object_model|capitalize}}FunctionsTests {

    @ParameterizedTest
    @Event(value = "delete_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testDelete{{cookiecutter.object_model|capitalize}}OK(APIGatewayProxyRequestEvent event) {
        Delete{{cookiecutter.object_model|capitalize}}Function handler = new Delete{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        handler.handleRequest(event, context);

        verify(dataAccess).delete(ID);
    }

    @ParameterizedTest
    @HandlerParams(
            event = @Event(value = "delete_{{cookiecutter.object_model}}/events/bad_request.json", type = APIGatewayProxyRequestEvent.class),
            response = @Response(value = "delete_{{cookiecutter.object_model}}/responses/bad_request.json", type = APIGatewayProxyResponseEvent.class)
    )
    public void testDelete{{cookiecutter.object_model|capitalize}}BadRequest(APIGatewayProxyRequestEvent event, APIGatewayProxyResponseEvent response) {
        Delete{{cookiecutter.object_model|capitalize}}Function handler = new Delete{{cookiecutter.object_model|capitalize}}Function(dataAccess);
        APIGatewayProxyResponseEvent result = handler.handleRequest(event, context);

        assertThat(result.getStatusCode()).isEqualTo(response.getStatusCode());
        assertThat(result.getBody()).isEqualTo(response.getBody());
    }

    @ParameterizedTest
    @Event(value = "delete_{{cookiecutter.object_model}}/events/ok.json", type = APIGatewayProxyRequestEvent.class)
    public void testDelete{{cookiecutter.object_model|capitalize}}InternalError(APIGatewayProxyRequestEvent event) {
        Delete{{cookiecutter.object_model|capitalize}}Function handler = new Delete{{cookiecutter.object_model|capitalize}}Function(dataAccess);

        doThrow(DynamoDbException.class).when(dataAccess).delete(ArgumentMatchers.any(String.class));

        APIGatewayProxyResponseEvent response = handler.handleRequest(event, context);

        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }
}

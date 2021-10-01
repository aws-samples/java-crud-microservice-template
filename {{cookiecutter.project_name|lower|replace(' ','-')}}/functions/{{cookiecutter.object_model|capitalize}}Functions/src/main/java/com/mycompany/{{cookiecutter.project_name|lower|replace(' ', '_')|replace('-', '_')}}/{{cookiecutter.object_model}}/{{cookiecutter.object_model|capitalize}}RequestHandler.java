package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.{{cookiecutter.object_model|capitalize}}DynamoDataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import software.amazon.awssdk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class {{cookiecutter.object_model|capitalize}}RequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

//    protected static final boolean LOCAL = !StringUtils.isEmpty(System.getenv("AWS_SAM_LOCAL"));

    protected final ObjectMapper mapper = new ObjectMapper();

    protected DataAccess<{{cookiecutter.object_model|capitalize}}> dataAccess;

    public {{cookiecutter.object_model|capitalize}}RequestHandler() {
        this(new {{cookiecutter.object_model|capitalize}}DynamoDataAccess());
    }

    {{cookiecutter.object_model|capitalize}}RequestHandler(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
        this.dataAccess = {{cookiecutter.object_model}}DataAccess;
    }

    private APIGatewayProxyResponseEvent response() {
        return response(null);
    }

    protected APIGatewayProxyResponseEvent response(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
    }

    protected APIGatewayProxyResponseEvent ok(String body) {
        return response().withStatusCode(200).withBody(body);
    }

    protected APIGatewayProxyResponseEvent badRequest(String message) {
        return response().withStatusCode(400).withBody("{\"error\":\"Bad request\", \"message\":\"" + message + "\"}");
    }

    protected APIGatewayProxyResponseEvent notFound(String message) {
        return response().withStatusCode(404).withBody("{\"error\":\"Not found\", \"message\":\"" + message + "\"}");
    }

    protected APIGatewayProxyResponseEvent created(String body) {
        return response().withStatusCode(201).withBody(body);
    }

    protected APIGatewayProxyResponseEvent error() {
        return response().withStatusCode(500).withBody("{\"error\":\"Internal Server Error\", \"message\":\"Unexpected error\"}");
    }

}

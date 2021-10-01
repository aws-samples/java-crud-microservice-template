package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.PaginatedList;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class List{{cookiecutter.object_model|capitalize}}sFunction extends {{cookiecutter.object_model|capitalize}}RequestHandler {

    private static final Logger log = LogManager.getLogger();

    public List{{cookiecutter.object_model|capitalize}}sFunction() {
        super();
    }

    public List{{cookiecutter.object_model|capitalize}}sFunction(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
        super({{cookiecutter.object_model}}DataAccess);
    }

    @Logging
    @Tracing
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        String nextToken = event.getHeaders().get("X-next-token");

        try {
            PaginatedList<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}s = dataAccess.list(nextToken);

            Map<String, String> headers = new HashMap<>();
            headers.put("X-max-results", String.valueOf({{cookiecutter.object_model}}s.getTotal()));
            if ({{cookiecutter.object_model}}s.getNextToken() != null){
                headers.put("X-next-token", {{cookiecutter.object_model}}s.getNextToken());
            }

            return response(headers).withStatusCode(200).withBody(mapper.writeValueAsString({{cookiecutter.object_model}}s.getItems()));
        } catch (Exception e) {
            log.error("Internal Error", e);
            return error();
        }
    }
}

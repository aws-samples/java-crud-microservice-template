package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;

/**
 * Handler for requests to Lambda function.
 */
public class Read{{cookiecutter.object_model|capitalize}}Function extends {{cookiecutter.object_model|capitalize}}RequestHandler {

    private static final Logger log = LogManager.getLogger();

    public Read{{cookiecutter.object_model|capitalize}}Function() {
        super();
    }

    public Read{{cookiecutter.object_model|capitalize}}Function(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
        super({{cookiecutter.object_model}}DataAccess);
    }

    @Logging
    @Tracing
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        try {
            String id = event.getPathParameters().get("id");
            if (StringUtils.isEmpty(id)) {
                return badRequest("id is missing");
            }

            {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = dataAccess.get(id);
            if ({{cookiecutter.object_model}} == null) {
                return notFound("item "+ id +" not found");
            }

            return ok(mapper.writeValueAsString({{cookiecutter.object_model}}));

        }  catch (Exception e) {
            log.error("Internal Error", e);
            return error();
        }
    }
}

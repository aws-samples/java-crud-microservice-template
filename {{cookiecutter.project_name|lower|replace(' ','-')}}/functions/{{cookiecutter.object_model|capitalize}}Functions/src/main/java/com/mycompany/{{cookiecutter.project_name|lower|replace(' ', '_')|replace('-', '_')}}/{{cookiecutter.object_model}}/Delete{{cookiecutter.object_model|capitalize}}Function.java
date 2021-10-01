package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.Unit;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.metrics.Metrics;
import software.amazon.lambda.powertools.metrics.MetricsUtils;
import software.amazon.lambda.powertools.tracing.Tracing;

/**
 * Handler for requests to Lambda function.
 */
public class Delete{{cookiecutter.object_model|capitalize}}Function extends {{cookiecutter.object_model|capitalize}}RequestHandler {

    private static final Logger log = LogManager.getLogger();

    private MetricsLogger metricsLogger = MetricsUtils.metricsLogger();

    public Delete{{cookiecutter.object_model|capitalize}}Function() {
        super();
    }

    public Delete{{cookiecutter.object_model|capitalize}}Function(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
        super({{cookiecutter.object_model}}DataAccess);
    }

    @Logging
    @Tracing
    @Metrics
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        try {
            String id = event.getPathParameters().get("id");
            if (StringUtils.isEmpty(id)) {
                return badRequest("id is missing");
            }

            dataAccess.delete(id);

            metricsLogger.putMetric("Deleted", 1, Unit.COUNT);
            metricsLogger.putMetadata("{{cookiecutter.object_model}}_id", id);

            return ok("");

        }  catch (Exception e) {
            log.error("Internal Error", e);
            return error();
        }
    }
}

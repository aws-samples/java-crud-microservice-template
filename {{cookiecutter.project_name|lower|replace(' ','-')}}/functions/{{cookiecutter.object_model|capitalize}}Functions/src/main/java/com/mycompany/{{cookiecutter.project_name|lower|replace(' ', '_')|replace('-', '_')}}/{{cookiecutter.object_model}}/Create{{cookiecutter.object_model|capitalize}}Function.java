package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.Unit;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.metrics.Metrics;
import software.amazon.lambda.powertools.metrics.MetricsUtils;
import software.amazon.lambda.powertools.tracing.Tracing;

import javax.validation.*;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lambda function for creating {@link {{cookiecutter.object_model|capitalize}}}
 */
public class Create{{cookiecutter.object_model|capitalize}}Function extends {{cookiecutter.object_model|capitalize}}RequestHandler {

    private static final Logger log = LogManager.getLogger();

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private MetricsLogger metricsLogger = MetricsUtils.metricsLogger();

    public Create{{cookiecutter.object_model|capitalize}}Function() {
        super();
    }

    Create{{cookiecutter.object_model|capitalize}}Function(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
        super({{cookiecutter.object_model}}DataAccess);
    }

    @Logging
    @Tracing
    @Metrics
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        try {
            {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = mapper.readValue(event.getBody(), {{cookiecutter.object_model|capitalize}}.class);
            validate{{cookiecutter.object_model|capitalize}}({{cookiecutter.object_model}});

            String id = event.getRequestContext().getRequestId();
            {{cookiecutter.object_model}}.setId(id);
            {{cookiecutter.object_model}}.setCreatedAt(new Date().getTime());

            dataAccess.create({{cookiecutter.object_model}});

            metricsLogger.putMetadata("{{cookiecutter.object_model}}_id", id);
            metricsLogger.putMetric("Created", 1, Unit.COUNT);
            if ({{cookiecutter.object_model}}.isCompleted()) {
                metricsLogger.putMetric("Completed", 1, Unit.COUNT);
            }

            return created("{\"message\":\"item " + id + " created\"}");

        } catch (JsonParseException |
                JsonMappingException e) {
            log.error(e.getMessage());
            return badRequest("{{cookiecutter.object_model|capitalize}} is malformed");
        } catch (
                ValidationException e) {
            return badRequest(e.getMessage());
        } catch (
                Exception e) {
            log.error("Internal Error", e);
            return error();
        }

    }

    private void validate{{cookiecutter.object_model|capitalize}}({{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}}) {
        Set<ConstraintViolation<{{cookiecutter.object_model|capitalize}}>> violations = validator.validate({{cookiecutter.object_model}});
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid {{cookiecutter.object_model|capitalize}}: " + violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
        }
    }
}

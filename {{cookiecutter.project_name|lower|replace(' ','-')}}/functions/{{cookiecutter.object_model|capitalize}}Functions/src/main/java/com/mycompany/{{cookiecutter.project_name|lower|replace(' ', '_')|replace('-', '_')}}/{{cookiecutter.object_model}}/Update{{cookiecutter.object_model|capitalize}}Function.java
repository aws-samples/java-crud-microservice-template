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
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.Unit;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.metrics.Metrics;
import software.amazon.lambda.powertools.metrics.MetricsUtils;
import software.amazon.lambda.powertools.tracing.Tracing;

import javax.validation.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class Update{{cookiecutter.object_model|capitalize}}Function extends {{cookiecutter.object_model|capitalize}}RequestHandler {

    private static final Logger log = LogManager.getLogger();

    private MetricsLogger metricsLogger = MetricsUtils.metricsLogger();

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public Update{{cookiecutter.object_model|capitalize}}Function() {
        super();
    }

    public Update{{cookiecutter.object_model|capitalize}}Function(DataAccess<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}DataAccess) {
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

            {{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}} = mapper.readValue(event.getBody(), {{cookiecutter.object_model|capitalize}}.class);
            validate{{cookiecutter.object_model|capitalize}}({{cookiecutter.object_model}});

            {{cookiecutter.object_model|capitalize}} old{{cookiecutter.object_model|capitalize}} = dataAccess.get(id);
            if (old{{cookiecutter.object_model|capitalize}} == null) {
                return notFound("item "+ id +" not found");
            }

            {{cookiecutter.object_model}}.setId(old{{cookiecutter.object_model|capitalize}}.getId());
            {{cookiecutter.object_model}}.setCreatedAt(old{{cookiecutter.object_model|capitalize}}.getCreatedAt());
            dataAccess.update({{cookiecutter.object_model}});

            metricsLogger.putMetadata("{{cookiecutter.object_model}}_id", id);
            metricsLogger.putMetric("Updated", 1, Unit.COUNT);
            if ({{cookiecutter.object_model}}.isCompleted()) {
                metricsLogger.putMetric("Completed", 1, Unit.COUNT);
            }

            return ok("{\"message\":\"item " + {{cookiecutter.object_model}}.getId() + " updated\"}");

        } catch (JsonParseException | JsonMappingException e) {
            return badRequest("{{cookiecutter.object_model|capitalize}} is malformed");
        } catch (ValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
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

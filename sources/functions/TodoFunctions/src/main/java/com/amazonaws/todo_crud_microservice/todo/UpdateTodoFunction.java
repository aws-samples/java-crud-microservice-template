/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.amazonaws.todo_crud_microservice.todo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.amazonaws.todo_crud_microservice.todo.dataaccess.DataAccess;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
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
public class UpdateTodoFunction extends TodoRequestHandler {

    private static final Logger log = LogManager.getLogger();

    private MetricsLogger metricsLogger = MetricsUtils.metricsLogger();

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public UpdateTodoFunction() {
        super();
    }

    public UpdateTodoFunction(DataAccess<Todo> todoDataAccess) {
        super(todoDataAccess);
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

            Todo todo = mapper.readValue(event.getBody(), Todo.class);
            validateTodo(todo);

            Todo oldTodo = dataAccess.get(id);
            if (oldTodo == null) {
                return notFound("item "+ id +" not found");
            }

            todo.setId(oldTodo.getId());
            todo.setCreatedAt(oldTodo.getCreatedAt());
            dataAccess.update(todo);

            metricsLogger.putMetadata("todo_id", id);
            metricsLogger.putMetric("Updated", 1, Unit.COUNT);
            if (todo.isCompleted()) {
                metricsLogger.putMetric("Completed", 1, Unit.COUNT);
            }

            return ok("{\"message\":\"item " + todo.getId() + " updated\"}");

        } catch (JsonParseException | JsonMappingException e) {
            return badRequest("Todo is malformed");
        } catch (ValidationException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Internal Error", e);
            return error();
        }
    }

    private void validateTodo(Todo todo) {
        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid Todo: " + violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
        }
    }
}

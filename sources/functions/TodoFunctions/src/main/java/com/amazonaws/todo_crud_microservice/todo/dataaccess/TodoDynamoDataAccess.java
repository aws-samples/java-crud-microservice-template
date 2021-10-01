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
package com.amazonaws.todo_crud_microservice.todo.dataaccess;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.amazonaws.todo_crud_microservice.todo.model.Todo;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.Select;
import software.amazon.awssdk.utils.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TodoDynamoDataAccess implements DataAccess<Todo> {

    private static final String DDB_TABLE = System.getenv("TABLE_NAME");
    private static final String PAGE_SIZE_STR = System.getenv("PAGE_SIZE");
    private static final Integer PAGE_SIZE = PAGE_SIZE_STR != null ? Integer.parseInt(PAGE_SIZE_STR) : 10;
    private static final String LOCAL = System.getenv("AWS_SAM_LOCAL");

    private static final DynamoDbClient ddb;

    static {
        DynamoDbClientBuilder ddbBuilder = DynamoDbClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .httpClient(UrlConnectionHttpClient.builder().build());

        if (!StringUtils.isEmpty(LOCAL)) {
            ddbBuilder.endpointOverride(URI.create("http://dynamodb-local:8000"));
        } else {
            ddbBuilder.region(Region.of(System.getenv("AWS_REGION")))
                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                            .addExecutionInterceptor(new TracingInterceptor()).build());
        }
        ddb = ddbBuilder.build();
    }

    private static final DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddb)
            .build();

    private static final DynamoDbTable<Todo> todoTable = client.table(DDB_TABLE, TableSchema.fromBean(Todo.class));

    @Override
    public void create(Todo todo) {
        todoTable.putItem(todo);
    }

    @Override
    public Todo get(String id) {
        return todoTable.getItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public void update(Todo todo) {
        todoTable.updateItem(todo);
    }

    @Override
    public void delete(String id) {
        todoTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public PaginatedList<Todo> list(String nextToken) {
        ScanResponse total = ddb.scan(builder -> builder.tableName(DDB_TABLE).select(Select.COUNT));

        ScanRequest.Builder builder = ScanRequest.builder().tableName(DDB_TABLE).limit(PAGE_SIZE);
        if (nextToken != null) {
            Map<String, AttributeValue> start = new HashMap<>();
            start.put("id", AttributeValue.builder().s(nextToken).build());
            builder.exclusiveStartKey(start);
        }
        ScanResponse response = ddb.scan(builder.build());

        return new PaginatedList<>(
                response.items().stream().map(this::mapTodo).collect(Collectors.toList()),
                total.count(),
                response.hasLastEvaluatedKey() ? response.lastEvaluatedKey().get("id").s() : null
        );
    }

    private Todo mapTodo(Map<String, AttributeValue> item) {
        return new Todo(
                item.get("id").s(),
                Long.parseLong(item.get("createdAt").n()),
                item.get("task").s(),
                item.get("description").s(),
                item.get("completed").bool());
    }
}

package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
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

public class {{cookiecutter.object_model|capitalize}}DynamoDataAccess implements DataAccess<{{cookiecutter.object_model|capitalize}}> {

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

    private static final DynamoDbTable<{{cookiecutter.object_model|capitalize}}> {{cookiecutter.object_model}}Table = client.table(DDB_TABLE, TableSchema.fromBean({{cookiecutter.object_model|capitalize}}.class));

    @Override
    public void create({{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}}) {
        {{cookiecutter.object_model}}Table.putItem({{cookiecutter.object_model}});
    }

    @Override
    public {{cookiecutter.object_model|capitalize}} get(String id) {
        return {{cookiecutter.object_model}}Table.getItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public void update({{cookiecutter.object_model|capitalize}} {{cookiecutter.object_model}}) {
        {{cookiecutter.object_model}}Table.updateItem({{cookiecutter.object_model}});
    }

    @Override
    public void delete(String id) {
        {{cookiecutter.object_model}}Table.deleteItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public PaginatedList<{{cookiecutter.object_model|capitalize}}> list(String nextToken) {
        ScanResponse total = ddb.scan(builder -> builder.tableName(DDB_TABLE).select(Select.COUNT));

        ScanRequest.Builder builder = ScanRequest.builder().tableName(DDB_TABLE).limit(PAGE_SIZE);
        if (nextToken != null) {
            Map<String, AttributeValue> start = new HashMap<>();
            start.put("id", AttributeValue.builder().s(nextToken).build());
            builder.exclusiveStartKey(start);
        }
        ScanResponse response = ddb.scan(builder.build());

        return new PaginatedList<>(
                response.items().stream().map(this::map{{cookiecutter.object_model|capitalize}}).collect(Collectors.toList()),
                total.count(),
                response.hasLastEvaluatedKey() ? response.lastEvaluatedKey().get("id").s() : null
        );
    }

    private {{cookiecutter.object_model|capitalize}} map{{cookiecutter.object_model|capitalize}}(Map<String, AttributeValue> item) {
        return new {{cookiecutter.object_model|capitalize}}(
                item.get("id").s(),
                Long.parseLong(item.get("createdAt").n()),
                item.get("task").s(),
                item.get("description").s(),
                item.get("completed").bool());
    }
}

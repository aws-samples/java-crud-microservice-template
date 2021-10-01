package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import javax.validation.constraints.NotNull;

@DynamoDbBean
public class {{cookiecutter.object_model|capitalize}} {
    private String id;
    private long createdAt;
    @NotNull(message = "task must not be null")
    private String task;
    @NotNull(message = "description must not be null")
    private String description;
    private boolean completed;

    public {{cookiecutter.object_model|capitalize}}() { }

    public {{cookiecutter.object_model|capitalize}}(String id, long createdAt, String task, String description, boolean completed) {
        this.id = id;
        this.createdAt = createdAt;
        this.task = task;
        this.description = description;
        this.completed = completed;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess;

public interface DataAccess<T> {
    void create(T item);

    T get(String id);

    void update(T item);

    void delete(String id);

    PaginatedList<T> list(String nextToken);
}

"""
    Tests cookiecutter baking process and rendered content
"""

def test_project_tree(cookies):
    result = cookies.bake(extra_context={
        'project_name': 'product-crud Microservice',
        'object_model': 'product'
    })
    assert result.exit_code == 0
    assert result.exception is None
    assert result.project.basename == 'product-crud-microservice'
    assert result.project.isdir()
    assert result.project.join('template.yaml').isfile()
    assert result.project.join('README.md').isfile()
    assert result.project.join('functions').isdir()
    assert result.project.join('functions', 'ProductFunctions').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'CreateProductFunction.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'DeleteProductFunction.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'ReadProductFunction.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'UpdateProductFunction.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'ListProductsFunction.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'dataaccess').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'dataaccess', 'DataAccess.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'dataaccess', 'PaginatedList.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'dataaccess', 'ProductDynamoDataAccess.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'model').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'model', 'Product.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product').isdir()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'CreateProductFunctionTest.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'DeleteProductFunctionTest.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'ReadProductFunctionTest.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'UpdateProductFunctionTest.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'ListProductFunctionTest.java').isfile()
    assert result.project.join('functions', 'ProductFunctions', 'src', 'test', 'java', 'com', 'mycompany', 'product_crud_microservice', 'product', 'ProductFunctionsTests.java').isfile()


def test_app_content(cookies):
    result = cookies.bake(extra_context={
        'project_name': 'my_lambda',
        'object_model': 'user'
    })
    app_file = result.project.join('functions', 'UserFunctions', 'src', 'main', 'java', 'com', 'mycompany', 'my_lambda', 'user', 'dataaccess', 'UserDynamoDataAccess.java')
    app_content = app_file.readlines()
    app_content = ''.join(app_content)

    contents = (
        "DynamoDbClientBuilder ddbBuilder = DynamoDbClient.builder()",
        "DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder()",
        "DynamoDbTable<User> userTable = client.table(DDB_TABLE, TableSchema.fromBean(User.class))",
        "response.items().stream().map(this::mapUser).collect(Collectors.toList())"
    )

    for content in contents:
        assert content in app_content
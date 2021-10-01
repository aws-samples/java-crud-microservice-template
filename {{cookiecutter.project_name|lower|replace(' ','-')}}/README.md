# java-crud-microservice

This project contains source code and supporting files for a serverless CRUD microservice built in Java. As a demo project it simply handles '{{cookiecutter.object_model|capitalize}}s'.
It contains the following files:

- functions/{{cookiecutter.object_model|capitalize}}Functions/src/main - Code for the application's Lambda functions. Each Function has its own class.
- events - Invocation events that you can use to invoke the function.
- functions/{{cookiecutter.object_model|capitalize}}Functions/src/test - Unit tests for the application code. 
- template.yaml - A SAM template that defines the application's AWS resources.

The application uses several AWS resources, including Lambda functions, Amazon DynamoDB table and an API Gateway API. These resources are defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same deployment process that updates your application code.

This demo code has been developed using an integrated development environment (IntelliJ) and the [AWS Toolkit for IntelliJ](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html).  

## Deploy the sample application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java11 - [Install the Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Use the SAM CLI to build and test locally

Build your application with the `sam build` command.

```bash
java-crud-microservice$ sam build
```

The SAM CLI installs dependencies defined in `functions/{{cookiecutter.object_model|capitalize}}Functions/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

```bash
java-crud-microservice$ docker network create lambda-local 
java-crud-microservice$ docker run -d -p 8000:8000 --network lambda-local --name dynamodb-local amazon/dynamodb-local
java-crud-microservice$ aws dynamodb create-table --table-name {{cookiecutter.object_model}}s-local --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000
```

Run functions locally and invoke them with the `sam local invoke` command.

```bash
java-crud-microservice$ sam local invoke List{{cookiecutter.object_model|capitalize}}sFunction --skip-pull-image --event events/list_{{cookiecutter.object_model}}s.json --env-vars events/env.json --docker-network lambda-local
java-crud-microservice$ sam local invoke Create{{cookiecutter.object_model|capitalize}}Function --skip-pull-image  --event events/create_{{cookiecutter.object_model}}.json --env-vars events/env.json --docker-network lambda-local
java-crud-microservice$ sam local invoke Read{{cookiecutter.object_model|capitalize}}Function --skip-pull-image  --event events/get_{{cookiecutter.object_model}}.json --env-vars events/env.json --docker-network lambda-local
java-crud-microservice$ sam local invoke Update{{cookiecutter.object_model|capitalize}}Function --skip-pull-image  --event events/update_{{cookiecutter.object_model}}.json --env-vars events/env.json --docker-network lambda-local
java-crud-microservice$ sam local invoke Delete{{cookiecutter.object_model|capitalize}}Function --skip-pull-image  --event events/delete_{{cookiecutter.object_model}}.json --env-vars events/env.json --docker-network lambda-local
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
java-crud-microservice$ sam local start-api
java-crud-microservice$ curl http://localhost:3000/
```

## Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

## Fetch, tail, and filter Lambda function logs

To simplify troubleshooting, SAM CLI has a command called `sam logs`. `sam logs` lets you fetch logs generated by your deployed Lambda function from the command line. In addition to printing the logs on the terminal, this command has several nifty features to help you quickly find the bug.

`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
java-crud-microservice$ sam logs -n Create{{cookiecutter.object_model|capitalize}}Function --stack-name java-crud-microservice --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `functions/{{cookiecutter.object_model|capitalize}}Functions/src/test` folder in this project. They use the [aws-lambda-java-tests](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-tests) library to inject events and read responses from a resource folder.

```bash
java-crud-microservice$ cd functions/{{cookiecutter.object_model|capitalize}}Functions
{{cookiecutter.object_model|capitalize}}Functions$ mvn test
```

You can also get the code coverage (with jacoco) using:

```bash
{{cookiecutter.object_model|capitalize}}Functions$ mvn verify
```

## Cleanup

To delete the application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name java-crud-microservice
```

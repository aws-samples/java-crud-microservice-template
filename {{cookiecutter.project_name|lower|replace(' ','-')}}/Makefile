ifndef VERBOSE
MAKEFLAGS += --no-print-directory
endif
SHELL := /bin/bash
.DEFAULT_GOAL := help

help:
	@ echo "Use one of the following targets:"
	@ tail -n +8 Makefile |\
	egrep "^[a-z]+[\ :]" |\
	tr -d : |\
	tr " " "/" |\
	sed "s/^/ - /g"
	@ echo "Read the Makefile for further details"

clean:
	@ echo "Cleaning the local environment"
	@ mvn -f functions/{{cookiecutter.object_model|capitalize}}Functions/pom.xml clean
	@ rm -rf .aws-sam
	@ echo "All done!"

build:
	@ sam build --parallel --cached

test:
	@ mvn -f functions/{{cookiecutter.object_model|capitalize}}Functions/pom.xml test

deploy:
	@ sam deploy

bd:
	build
	deploy

network:
	@ docker network inspect lambda-local || docker network create lambda-local

start-dynamo:
	@ docker network inspect lambda-local
	@ docker run -d --rm -p 8000:8000 --network lambda-local --name dynamodb-local amazon/dynamodb-local
	@ aws dynamodb create-table --table-name {{cookiecutter.object_model}}s-local --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000

stop-dynamo:
	@ docker stop dynamodb-local

local-invoke:
	# usage: make local-invoke fn=MyFunction ev=eventfile.json
	@ sam local invoke $(fn) --skip-pull-image --event $(ev) --env-vars events/env.json --docker-network lambda-local

start-api:
	@ sam local start-api --docker-network lambda-local --env-vars events/env.json --parameter-overrides PageSize=5,LogLevel=DEBUG
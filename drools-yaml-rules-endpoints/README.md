# Drools with yaml/json rules

## Description

A rest service to define and execute a drools rules set in yaml/json format.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.1+ installed

When using native image compilation, you will also need:
  - [GraalVM 21.1.0](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-21.1.0) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/drools-yaml-rules-1.0.0-SNAPSHOT-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/q/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/q/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Example usage

### POST /create-rules-executor

Creates a rules executor with the set of rules defined in the json payload as in the following example:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{
   "host_rules":[
      {
         "name":"R1",
         "condition":"sensu.data.i == 1",
         "action":{
            "assert_fact":{
               "ruleset":"Test rules4",
               "fact":{
                  "j":1
               }
            }
         }
      },
      {
         "name":"R2",
         "condition":"sensu.data.i == 2",
         "action":{
            "run_playbook":[
               {
                  "name":"hello_playbook.yml"
               }
            ]
         }
      },
      {
         "name":"R3",
         "condition":{
            "any":[
               {
                  "all":[
                     "sensu.data.i == 3",
                     "j == 2"
                  ]
               },
               {
                  "all":[
                     "sensu.data.i == 4",
                     "j == 3"
                  ]
               }
            ]
         },
         "action":{
            "retract_fact":{
               "ruleset":"Test rules4",
               "fact":{
                  "j":3
               }
            }
         }
      },
      {
         "name":"R4",
         "condition":"j == 1",
         "action":{
            "post_event":{
               "ruleset":"Test rules4",
               "fact":{
                  "j":4
               }
            }
         }
      }
   ]
}' http://localhost:8080/create-rules-executor
```

As response it will return a simple number which is the identifier of the generated rules executor. Use this number in the URL of subsequent calls to that executor.

Note that the condition activating the rule can be a simple one, made only by one single constraint, or a nested combination of `AND` and `OR` like in `R3`. There `all` means that all conditions must be met in order to activate the rule, so it's equivalent to a `AND`, while `any` means that any of them is sufficient, equivalent to a `OR`.

### POST /rules-executors/{id}/execute

Processes the event passed in the json payload, also executing the consequences of the rules (actions) that it activates.

```
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{ "sensu": { "data": { "i":1 } } }' http://localhost:8080/rules-executors/1/execute
```

This call, other than having the side-effect of actually executing the activated rules, returns a value representing the number of executed rules.

### POST /rules-executors/{id}/process

Processes the event passed in the json payload, without executing the consequences of the rules (actions), but only returning the list of rules activated by the event.

```
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{ "sensu": { "data": { "i":1 } } }' http://localhost:8080/rules-executors/1/process
```

Example response:

```json
[
  {
    "ruleName": "R1",
    "facts": {
      "sensu": {
        "type": "sensu",
        "values": {
          "data.i": 1
        }
      }
    }
  }
]
```

Note that if the engine is used only in this way, i.e. only to evaluate rules but not to fire them, the rules actions are useless and they can be safely omitted in the json payload defining the rule set.  

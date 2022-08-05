/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.yaml.durable;


import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class DurableRestTest {

    private static final String DURABLE_RULES_JSON =
            "{\n" +
            "   \"myrules\":{\n" +
            "      \"R1\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"m\":{\n" +
            "                  \"$lt\":{\n" +
            "                     \"sensu.data.i\": 2\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec first\"\n" +
            "      },\n" +
            "      \"R2\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"first\":{\n" +
            "                  \"sensu.data.i\": 3\n" +
            "               },\n" +
            "               \"second\":{\n" +
            "                  \"j\": 2\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec second\"\n" +
            "      },\n" +
            "      \"R3\":{\n" +
            "         \"any\":[\n" +
            "            {\n" +
            "               \"all\":[\n" +
            "                  {\n" +
            "                     \"first\":{\n" +
            "                        \"sensu.data.i\": 3\n" +
            "                     },\n" +
            "                     \"second\":{\n" +
            "                        \"j\": 2\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            {\n" +
            "               \"all\":[\n" +
            "                  {\n" +
            "                     \"first\":{\n" +
            "                        \"sensu.data.i\": 4\n" +
            "                     },\n" +
            "                     \"second\":{\n" +
            "                        \"j\": 3\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec third\"\n" +
            "      }\n" +
            "   }\n" +
            "}";

    @Test
    public void testProcess() {
        // return the id of the newly generated RulesExecutor
        long id = given()
                .body(DURABLE_RULES_JSON)
                .contentType(ContentType.JSON)
                .when()
                .post("/create-durable-rules-executor").as(long.class);

        given()
                .body( "{ \"facts\": [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-durable-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body(is("[]"));

        given()
                .body( "{ \"sensu\": { \"data\": { \"i\":4 } } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-durable-executors/" + id + "/process-events")
                .then()
                .statusCode(200)
                .body("R3", notNullValue(),
                        "R3.second.j", hasItem(3));
    }

    private static final String JSON_WITH_NEQ_ON_MISSING_VALUE =
            "{\n" +
            "   \"myrules\":{\n" +
            "      \"R1\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"m\":{\n" +
            "                  \"payload.text\": \"GET\"\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec first\"\n" +
            "      },\n" +
            "      \"R2\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"m\":{\n" +
            "                  \"$neq\":{\n" +
            "                     \"payload.text\": \"GET\"\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec second\"\n" +
            "      }\n" +
            "   }\n" +
            "}";

    @Test
    public void testProcessWithMissingValue() {
        // return the id of the newly generated RulesExecutor
        long id = given()
                .body(JSON_WITH_NEQ_ON_MISSING_VALUE)
                .contentType(ContentType.JSON)
                .when()
                .post("/create-durable-rules-executor").as(long.class);

//        [{
//            "R1":{
//                "payload":{
//                    "text":"GET"
//                }
//            }
//
//        }]

        given()
                .body( "{ \"payload\": { \"text\": \"GET\" } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-durable-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body("R1", notNullValue(),
                        "R1.m.payload.text", hasItem("GET"));

        given()
                .body( "{ \"payload\": { \"value\": \"GET\" } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-durable-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }
}

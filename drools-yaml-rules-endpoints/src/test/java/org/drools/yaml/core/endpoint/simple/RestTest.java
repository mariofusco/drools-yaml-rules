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
package org.drools.yaml.core.endpoint.simple;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class RestTest {

    private static final String JSON_RULES_1 =
            "{\n" +
            "  \"host_rules\": [\n" +
            "    {\n" +
            "      \"name\": \"R1\",\n" +
            "      \"condition\": \"sensu.data.i == 1\",\n" +
            "      \"action\": {\n" +
            "        \"assert_fact\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 1\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R2\",\n" +
            "      \"condition\": \"sensu.data.i == 2\",\n" +
            "      \"action\": {\n" +
            "        \"run_playbook\": [\n" +
            "          {\n" +
            "            \"name\": \"hello_playbook.yml\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R3\",\n" +
            "      \"condition\": \"sensu.data.i == 3\",\n" +
            "      \"action\": {\n" +
            "        \"retract_fact\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 3\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R4\",\n" +
            "      \"condition\": \"j == 1\",\n" +
            "      \"action\": {\n" +
            "        \"post_event\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 4\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void testProcess() {
        // return the id of the newly generated RulesExecutor
        long id = given()
                .body(JSON_RULES_1)
                .contentType(ContentType.JSON)
                .when()
                .post("/create-rules-executor").as(long.class);

//        [
//            {
//                "ruleName": "R1",
//                "facts": {
//                    "sensu": {
//                        "data": {
//                            "i": 1
//                        }
//                    }
//                }
//            }
//        ]

        given()
                .body( "{ \"sensu\": { \"data\": { \"i\":1 } } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body("ruleName", hasItem("R1"),
                      "facts.sensu.data.i", hasItem(1));

//        [
//            {
//                "ruleName": "R4",
//                "facts": {
//                    "j": 1
//                }
//            }
//        ]

        given()
                .body( "{ \"j\":1 }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body("ruleName", hasItem("R4"),
                      "facts.j", hasItem(1));
//                .log().body();
    }

    @Test
    public void testExecute() {
        // return the id of the newly generated RulesExecutor
        long id = given()
                .body(JSON_RULES_1)
                .contentType(ContentType.JSON)
                .when()
                .post("/create-rules-executor").as(long.class); // returns the number of processed rules

        given()
                .body( "{ \"sensu\": { \"data\": { \"i\":1 } } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-executors/" + id + "/execute")
                .then()
                .statusCode(200)
                .body(is("2")); // returns the number of executed rules
    }

    private static final String JSON_RULES_2 =
            "{\n" +
            "   \"host_rules\":[\n" +
            "      {\n" +
            "         \"name\":\"R1\",\n" +
            "         \"condition\":\"sensu.data.i == 1\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"name\":\"R2\",\n" +
            "         \"condition\":{\n" +
            "            \"all\":[\n" +
            "               \"sensu.data.i == 3\",\n" +
            "               \"j == 2\"\n" +
            "            ]\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"name\":\"R3\",\n" +
            "         \"condition\":{\n" +
            "            \"any\":[\n" +
            "               {\n" +
            "                  \"all\":[\n" +
            "                     \"sensu.data.i == 3\",\n" +
            "                     \"j == 2\"\n" +
            "                  ]\n" +
            "               },\n" +
            "               {\n" +
            "                  \"all\":[\n" +
            "                     \"sensu.data.i == 4\",\n" +
            "                     \"j == 3\"\n" +
            "                  ]\n" +
            "               }\n" +
            "            ]\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    @Test
    public void testProcessWithLogicalOperators() {
        // return the id of the newly generated RulesExecutor
        long id = given()
                .body(JSON_RULES_2)
                .contentType(ContentType.JSON)
                .when()
                .post("/create-rules-executor").as(long.class);

        given()
                .body( "{ \"facts\": [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body(is("[]"));

        given()
                .body( "{ \"sensu\": { \"data\": { \"i\":4 } } }" )
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-executors/" + id + "/process")
                .then()
                .statusCode(200)
                .body("ruleName", hasItem("R3"),
                        "facts.j", hasItem(3));
    }
}

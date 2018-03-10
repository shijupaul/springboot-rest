package com.shijusoft.exception.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;
import com.shijusoft.exception.dto.Address;
import com.shijusoft.exception.dto.Person;
import com.shijusoft.exception.service.PersonService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by spaul on 22/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleControllerWithRestAssuredAndMockBeansIT {

    @LocalServerPort
    private int port;

    @MockBean
    private PersonService personService;

    private Person testPerson;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.basePath = "/person";
        testPerson = new Person(1,"Test Person", new Address());
    }

    @Test
    public void testKnownPersonCanBeRetrievedSuccessfully() throws Exception {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.of(testPerson));

        Response response = given()
                .pathParam("personId", testPerson.getId())
                .when()
                .get("/{personId}");

        assertThat("Status code don't match.", response.getStatusCode(), Matchers.is(200));
        Person responsePerson =    (Person) getObject(response, Person.class);
        assertThat("Not expected Person.", responsePerson, Matchers.is(testPerson));
    }


    @Test
    public void testTryingToRetrieveUnknownPersonFails() {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.empty());

        Response response = given()
                .pathParam("personId", testPerson.getId())
                .when()
                .get("/{personId}");
        assertThat("Status code don't match.", response.getStatusCode(), Matchers.is(404));

    }

    @Test
    public void testCreateNewPersonSucceeds() throws Exception {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.empty(), Optional.of(testPerson));
        personService.add(testPerson);
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .body(testPerson)
                 .when()
                .post();

        assertThat("Status code don't match.", response.getStatusCode(), Matchers.is(200));
        Person responsePerson =    (Person) getObject(response, Person.class);
        assertThat("Not expected Person.", responsePerson, Matchers.is(testPerson));
    }

    @Test
    public void testCreateNewPersonFailsForDuplicateRecord() {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.of(testPerson));
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testPerson)
                .when()
                .post();
        assertThat("Status code don't match.", response.getStatusCode(), Matchers.is(500));
    }

    private Object getObject(Response response, Class clazz) throws  Exception {
        return objectMapper.readValue(response.getBody().print(), clazz);
    }

}

package com.shijusoft.exception.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.module.mockmvc.response.MockMvcResponse;
import com.shijusoft.exception.dto.Address;
import com.shijusoft.exception.dto.Person;
import com.shijusoft.exception.service.PersonService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by spaul on 21/02/2018.
 *
 * This class wraps MockMvc with RestAssured
 * so that we can have BDD type stuff
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {SimpleController.class})
public class SimpleControllerTest {

    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Person testPerson;

    @Before
    public void before() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.basePath = "/person";
        testPerson = new Person(1,"Test Person", new Address());
    }

    @Test
    public void testPersonCanBeRetrievedForTheGivenId() throws Exception {

        when(personService.findMatchingPerson(1)).thenReturn(Optional.of(testPerson));

        MockMvcResponse response = RestAssuredMockMvc.given()
                .when()
                .get("/{personId}", testPerson.getId());
        assertThat("Status code don't match.", response.getStatusCode(), Matchers.is(200));
        Person responsePerson =    (Person) getObject(response, Person.class);
        assertThat("Not expected Person.", responsePerson, Matchers.is(testPerson));

    }

    private Object getObject(MockMvcResponse response, Class clazz) throws  Exception {
        return objectMapper.readValue(response.getBody().print(), clazz);
    }
}

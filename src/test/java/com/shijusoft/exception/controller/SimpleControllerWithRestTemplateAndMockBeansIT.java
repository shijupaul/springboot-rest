package com.shijusoft.exception.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;
import com.shijusoft.exception.dto.Address;
import com.shijusoft.exception.dto.Person;
import com.shijusoft.exception.service.PersonService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by spaul on 22/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleControllerWithRestTemplateAndMockBeansIT {

    @LocalServerPort
    private int port;

    @MockBean
    private PersonService personService;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseurl;

    private Person testPerson;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        baseurl = "http://localhost:" + port;
        testPerson = new Person(1,"Test Person", new Address());
    }

    @Test
    public void testKnownPersonCanBeRetrievedSuccessfully() throws Exception {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.of(testPerson));

        ResponseEntity<Person> responseEntity = restTemplate.getForEntity(baseurl +"/person/{personId}", Person.class,testPerson.getId());
        assertThat("Entity don't match", responseEntity.getBody(), Matchers.is(testPerson));
        assertThat("Response code don't match", responseEntity.getStatusCode().value(), Matchers.is(200));
    }

    @Test
    public void testTryingToRetrieveUnknownPersonFails() {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.empty());
        ResponseEntity<Person> responseEntity = restTemplate.getForEntity(baseurl +"/person/{personId}", Person.class,testPerson.getId());

        assertThat("Response code don't match", responseEntity.getStatusCode().value(), Matchers.is(404));
    }

    @Test
    public void testCreateNewPersonSucceeds() throws Exception {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.empty(), Optional.of(testPerson));

        ResponseEntity<Person> responseEntity =  restTemplate.postForEntity(baseurl +"/person", testPerson,Person.class);
        assertThat("Entity don't match", responseEntity.getBody(), Matchers.is(testPerson));
        assertThat("Response code don't match", responseEntity.getStatusCode().value(), Matchers.is(200));
    }

    @Test
    public void testCreateNewPersonFailsForDuplicateRecord() {
        when(personService.findMatchingPerson(testPerson.getId())).thenReturn(Optional.of(testPerson));

        ResponseEntity<Person> responseEntity =  restTemplate.postForEntity(baseurl +"/person", testPerson,Person.class);
        assertThat("Response code don't match", responseEntity.getStatusCode().value(), Matchers.is(500));
    }

    private Object getObject(Response response, Class clazz) throws  Exception {
        return objectMapper.readValue(response.getBody().print(), clazz);
    }

}

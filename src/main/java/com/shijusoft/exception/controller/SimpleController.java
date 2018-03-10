package com.shijusoft.exception.controller;

import com.shijusoft.exception.dto.Address;
import com.shijusoft.exception.dto.Person;
import com.shijusoft.exception.error.ErrorDetails;
import com.shijusoft.exception.error.NotFoundException;
import com.shijusoft.exception.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by spaul on 21/02/2018.
 */
@RequestMapping(value = "/person")
@RestController
@Api(value = "Person Manager",description = "Rest endpoints to handle person management")
public class SimpleController {

    @Autowired
    private PersonService personService;

    @ApiOperation(value = "Retreive Person using Id", response = Resource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person Found", response = Resource.class),
            @ApiResponse(code = 404, message = "No Matching Person Found", response = ErrorDetails.class)
    })
    @GetMapping(value = "/{personId}")
    public ResponseEntity<Resource> getPerson(@PathVariable("personId") int personId) {
        Optional<Person> person = personService.findMatchingPerson(personId);
        if (person.isPresent()) {
            Resource<Person> resource = new Resource(person.get());

            ControllerLinkBuilder addressRelation = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).getAddress(personId));
            ControllerLinkBuilder allPersonRelation = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).getAll());

            resource.add(addressRelation.withRel("address"), allPersonRelation.withRel("all known persons"));

            return new ResponseEntity<Resource>(resource, HttpStatus.OK);
        }

        throw new NotFoundException("Person not found");
    }

    @ApiOperation(value = "Retreive Person Address", response = Resource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Address found for the given person id."),
            @ApiResponse(code = 404, message = "Address not found for the given person id.")
    })
    @GetMapping(value = "/{personId}/address")
    public ResponseEntity<Resource> getAddress(@PathVariable("personId") int personId) {
        Optional<Person> person = personService.findMatchingPerson(personId);
        if (person.isPresent()) {
            Resource<Address> resource = new Resource(person.get().getAddress());
            ControllerLinkBuilder personRelation = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).getPerson(personId));

            resource.add(personRelation.withRel("person"));
            return new ResponseEntity<Resource>(resource, HttpStatus.OK);
        }
        throw new NotFoundException("Address not found");
    }

    @ApiOperation(value = "Save Person", response = Resource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person created successfully."),
            @ApiResponse(code = 500, message = "Duplicate record with same Id found.")
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resource> savePerson(@RequestBody Person person) {
        if (personService.findMatchingPerson(person.getId()).isPresent()) {
            throw new RuntimeException("Person already exists");
        } else {
            personService.add(person);
            return getPerson(person.getId());
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Resources> getAll() {
        return new ResponseEntity<Resources>(new Resources(personService.getAll()), HttpStatus.OK);
    }


}

package com.shijusoft.exception.service;

import com.shijusoft.exception.dto.Address;
import com.shijusoft.exception.dto.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by spaul on 21/02/2018.
 */
@Service
public class PersonService {

    private static final List<Person> knownPersons = new ArrayList();

    public PersonService() {
        knownPersons.addAll(Arrays.asList(
                new Person(1,"Shiju Paul", new Address()),
                new Person(2,"Jeena Shiju", new Address()),
                new Person(3,"Joel Joseph", new Address()),
                new Person(4,"Joshua Joseph", new Address())
        ));
    }

    public Optional<Person> findMatchingPerson(int personId) {
        return knownPersons.stream().filter(person -> person.getId()== personId).findFirst();
    }

    public void add(Person person) {
        knownPersons.add(person);
    }

    public List<Person> getAll() {
        return knownPersons;
    }
}

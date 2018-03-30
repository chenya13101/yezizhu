package com.vincent.demo.cache.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.demo.cache.bean.Person;
import com.vincent.demo.cache.service.PersonService;

@RestController
@RequestMapping("person")
public class PersonRest {

	@Autowired
	PersonService personService;

	@RequestMapping("save")
	public Person save(Long id) {
		// http://localhost:9091/spring-boot/person/save?id=1
		Person nPerson = new Person();
		nPerson.setId(id);
		nPerson.setAge(10);
		nPerson.setName("yezizhu" + id);

		personService.save(nPerson);
		return nPerson;
	}

	@RequestMapping("remove")
	public void remove(Long id) {
		personService.remove(id);
	}

	@RequestMapping("findOne")
	public Person findOne(Person person) {
		// http://localhost:9091/spring-boot/person/findOne?id=3
		Person nPerson = new Person();
		nPerson.setId(person.getId());
		return personService.findOne(nPerson);
	}

	@RequestMapping("findOneById")
	public Person findOne(Long id) {
		// http://localhost:9091/spring-boot/person/findOneById?id=1
		return personService.findOneById(id);
	}
}

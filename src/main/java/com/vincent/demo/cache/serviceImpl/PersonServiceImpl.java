package com.vincent.demo.cache.serviceImpl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.vincent.demo.cache.bean.Person;
import com.vincent.demo.cache.service.PersonService;

@Service
public class PersonServiceImpl implements PersonService {

	@CachePut(value = "people", key = "#person.id")
	@Override
	public Person save(Person person) {
		System.out.println("保存person");
		return null;
	}

	@CacheEvict(value = "people")
	@Override
	public void remove(Long id) {
		System.out.println("删除person by id = " + id);

	}

	@Cacheable(value = "people", key = "#person.id")
	@Override
	public Person findOne(Person person) {
		System.out.println("查找person");

		Person nPerson = new Person();
		nPerson.setId(person.getId());
		nPerson.setAge(10);
		nPerson.setName("yezizhu");
		return nPerson;
	}

	@Cacheable(value = "people", key = "#id")
	@Override
	public Person findOneById(Long id) {
		System.out.println("findOneById 查找person");
		Person nPerson = new Person();
		nPerson.setId(id);
		nPerson.setName("Chen" + id);
		return nPerson;
	}

}

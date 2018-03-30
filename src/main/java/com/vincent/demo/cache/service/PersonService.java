package com.vincent.demo.cache.service;

import com.vincent.demo.cache.bean.Person;

public interface PersonService {
	public Person save(Person person);

	public void remove(Long id);

	public Person findOne(Person person);

	public Person findOneById(Long id);
}

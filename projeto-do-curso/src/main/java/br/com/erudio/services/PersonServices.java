package br.com.erudio.services;

import java.util.List;
import java.util.stream.Collectors;

import br.com.erudio.controller.PersonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.converter.DozerConverter;
import br.com.erudio.data.model.Person;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.repository.PersonRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {
	
	@Autowired
	PersonRepository repository;
		
	public PersonVO create(PersonVO person) {
		var entity = DozerConverter.parseObject(person, Person.class);
		return createVoWithHateoasLinks(DozerConverter.parseObject(repository.save(entity), PersonVO.class));
	}
	
	public List<PersonVO> findAll() {
		return DozerConverter.parseListObjects(repository.findAll(), PersonVO.class).stream()
				.map(PersonServices::createVoWithHateoasLinks).collect(Collectors.toList());
	}	
	
	public PersonVO findById(Long id) {

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		return createVoWithHateoasLinks(DozerConverter.parseObject(entity, PersonVO.class));
	}

	public PersonVO update(PersonVO person) {
		var entity = repository.findById(person.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		var vo = DozerConverter.parseObject(repository.save(entity), PersonVO.class);
		return createVoWithHateoasLinks(createVoWithHateoasLinks(vo));
	}	
	
	public void delete(Long id) {
		Person entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}

	private static PersonVO createVoWithHateoasLinks(PersonVO vo) {
		return vo
				.add(linkTo(methodOn(PersonController.class).create(vo)).withRel("create").withType("POST"))
				.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"))
				.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withRel("findById").withType("GET"))
				.add(linkTo(methodOn(PersonController.class).update(vo)).withRel("update").withType("PUT"))
				.add(linkTo(methodOn(PersonController.class).delete(vo.getId())).withRel("delete").withType("DELETE"));
	}

}

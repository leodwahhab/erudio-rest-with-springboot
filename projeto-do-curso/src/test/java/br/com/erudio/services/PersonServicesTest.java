package br.com.erudio.services;

import br.com.erudio.converter.mocks.MockPerson;
import br.com.erudio.data.model.Person;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // define que as instâncias existirão somente durante os testes dessa classe
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    MockPerson input;

    @InjectMocks
    private PersonServices service;

    @Mock //@Autowired vira @Mock nos testes
    PersonRepository personRepository;

    @BeforeEach
    void setUp() { // operação a ser executada antes de cada teste
        input = new MockPerson();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findById() {
        //given
        Person person = input.mockEntity(1); // seta a quantidade de instancias para o teste
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        //when
        var result = service.findById(1L);

        //then
        assertPersonVO(result);

    }

    @Test
    void create() {
        //given
        Person person = input.mockEntity(1); // seta a quantidade de instancias para o teste
        person.setId(1L);

        PersonVO vo = input.mockVO(1);

        when(personRepository.save(person)).thenReturn(person);

        //when
        var result = service.create(vo);

        //then
        assertPersonVO(result);
    }

    @Test
    void createWithNull() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> service.create(null));
        String expectedMessage = "Não é possível persistir um objeto nulo";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void update() {
        //given
        Person person = input.mockEntity(1); // seta a quantidade de instancias para o teste
        person.setId(1L);

        PersonVO vo = input.mockVO(1);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(person);

        //when
        var result = service.update(vo);

        //then

    }

    @Test
    void updateWithNull() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> service.update(null));
        String expectedMessage = "Não é possível persistir um objeto nulo";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void delete() {
        //given
        Person person = input.mockEntity(1); // seta a quantidade de instancias para o teste
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);

        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void findAll() {
        List<Person> list = input.mockEntityList();
        when(personRepository.findAll()).thenReturn(list);

        List<PersonVO> personList = service.findAll();

        assertNotNull(personList);
        assertEquals(14, personList.size());
        assertPersonVO(personList.get(1));
    }

    void assertPersonVO(PersonVO result) {
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks().stream().anyMatch(
                link -> link.getRel().value().equals("findById")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("GET")
        ));
        assertNotNull(result.getLinks().stream().anyMatch(
                link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET")
        ));
        assertNotNull(result.getLinks().stream().anyMatch(
                link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("POST")
        ));
        assertNotNull(result.getLinks().stream().anyMatch(
                link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("PUT")
        ));
        assertNotNull(result.getLinks().stream().anyMatch(
                link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/person/v1/1")
                        && link.getType().equals("DELETE")
        ));
        assertEquals("Address Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
    }
}
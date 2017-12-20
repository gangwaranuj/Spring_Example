package com.workmarket.utility;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.service.business.dto.ContractDTO;
import com.workmarket.service.business.dto.SpecialtyDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class BeanUtilitiesTest {

	@Before
	public void before() {
	}

	@Test
	public void test_newBean() throws Exception {
		SpecialtyDTO specialtyDTO = new SpecialtyDTO();
		specialtyDTO.setName("me");
		specialtyDTO.setDescription("I'm special");

		Specialty specialty = BeanUtilities.newBean(Specialty.class, specialtyDTO);

		assertEquals("me", specialty.getName());
		assertEquals("I'm special", specialty.getDescription());
	}

	@Test
	public void test_newBeanNull() throws Exception {
		ContractDTO contractDTO = new ContractDTO();
		contractDTO.setEntityId(null);
		contractDTO.setName("name");

		Contract contract = BeanUtilities.newBean(Contract.class, contractDTO);

		assertNull(contract.getId());
		assertEquals("name", contract.getName());
	}

	@Test
	public void test_getPropertyValueMap() throws Exception {
		SpecialtyDTO specialtyDTO = new SpecialtyDTO();
		specialtyDTO.setDescription("chewbacca");
		Map<String, Object> map = BeanUtilities.getPropertyValueMap(specialtyDTO);

		assertNotNull(map);
		assertTrue(map.containsKey("description"));
		assertEquals("chewbacca", map.get("description"));
	}

	public static class Person {
		private Long id;
		private String name;

		private Person(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	public void test_newCollectionPropertyToMap() throws Exception {
		List<Person> persons = Lists.newArrayList();

		persons.add(new Person(1L, "Person1"));
		persons.add(new Person(2L, "Person2"));

		Map<String, String> map = BeanUtilities.newCollectionPropertyToMap(persons, "id", "name");

		assertEquals(2, map.size());
		assertTrue(map.containsKey("2"));
		assertEquals("Person2", map.get("2"));
	}

	@Test
	public void testHasAnyNullProperties_NoNullProperties_False() throws IllegalAccessException {
		SpecialtyDTO specialtyDTO = new SpecialtyDTO();

		specialtyDTO.setName("me");
		specialtyDTO.setDescription("I'm special");
		specialtyDTO.setIndustryId(1L);
		specialtyDTO.setSpecialtyId(2L);

		assertFalse(BeanUtilities.hasAnyNullProperties(specialtyDTO));
	}

	@Test
	public void testHasAnyNullProperties_OneNullProperties_True() throws IllegalAccessException {
		SpecialtyDTO specialtyDTO = new SpecialtyDTO();

		specialtyDTO.setDescription("I'm special");
		specialtyDTO.setIndustryId(1L);
		specialtyDTO.setSpecialtyId(2L);

		assertTrue(BeanUtilities.hasAnyNullProperties(specialtyDTO));
	}

	@Test
	public void testHasAnyNullProperties_AllNullProperties_True() throws IllegalAccessException {
		SpecialtyDTO specialtyDTO = new SpecialtyDTO();

		assertTrue(BeanUtilities.hasAnyNullProperties(specialtyDTO));
	}

	@Test(expected=NullPointerException.class)
	public void testHasAnyNullProperties_NullObject_NPEThrown() throws IllegalAccessException {
		SpecialtyDTO specialtyDTO = null;

		BeanUtilities.hasAnyNullProperties(specialtyDTO);
	}
}

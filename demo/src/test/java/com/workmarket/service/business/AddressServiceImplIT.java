package com.workmarket.service.business;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.models.MessageBundle;
import org.hibernate.NonUniqueResultException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
public class AddressServiceImplIT extends BaseServiceIT {

	@Autowired AddressService addressService;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void verifyAndSave_success() throws Exception {
		Assert.notNull(addressService.verifyAndSave(createAddress(), new MessageBundle()));
	}

	@Ignore
	@Test
	@Transactional
	public void verifyAndSave_withInvalidPostalCode_fail() throws Exception {
		Address badAddress = createAddress();
		badAddress.setPostalCode("1234");
		Assert.isNull(addressService.verifyAndSave(badAddress, new MessageBundle()));
	}

	@Test
	public void addNewStateToAddress_withDuplicateState_throwsExceptionAndRollsBackTheInsert() throws Exception {
		Address address = createAddress();
		AddressDTO dto = createAddressDTO();

		dto.setState("TX");

		exception.expect(NonUniqueResultException.class);
		exception.expectMessage("query did not return a unique result: 2");

		addressService.addNewStateToAddress(address, dto.getCountry(), dto.getState());

		State state = invariantDataService.findStateWithCountryAndState(address.getCountry().getName(), address.getState().getName());

		assertThat(state, is(not(nullValue())));
	}

}

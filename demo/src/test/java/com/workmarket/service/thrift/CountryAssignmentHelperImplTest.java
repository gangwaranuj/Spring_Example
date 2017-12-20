package com.workmarket.service.thrift;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.AddressService;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jasonpendrey
 * Date: 7/8/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class CountryAssignmentHelperImplTest {

	@Mock private com.workmarket.service.infra.business.AuthenticationService authenticationService;
	@Mock private com.workmarket.service.business.ProfileService profileService;
	@Mock private AddressService addressService;
	@InjectMocks CountryAssignmentHelperImpl countryAssignmentHelperImpl;

	Profile profile = mock(Profile.class);
	User user = mock(User.class);

	@Before
	public void setUp() throws Exception {
		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(user.getId()).thenReturn(1L);
		when(profileService.findProfile(anyLong())).thenReturn(profile);

		when(profile.getAddressId()).thenReturn(1L);

	}

	@Test
	public void getCountryForAssignments_withCountry_validCountry() throws Exception {
		String country = countryAssignmentHelperImpl.getCountryForAssignments(WorkUploadColumn.LOCATION_COUNTRY, new WorkUploaderBuildResponse(new Work()), "USA");
		Assert.assertEquals("USA",country);
	}

	@Ignore
	@Test
	public void getCountryForAssignments_withoutCountry_ErrorReturned() throws Exception {
		Address address = mock(Address.class);
		when(addressService.findById(anyLong())).thenReturn(address);
		when(address.getCountry()).thenReturn(null);
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		Assert.assertEquals("Assignment Location Country cannot be determined, please include country or update profile address",response.getErrors().get(0).getMessage());
	}
}

package com.workmarket.domains.model.linkedin;


import com.google.code.linkedinapi.schema.PhoneType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.Assert;

import java.util.HashSet;

@RunWith(BlockJUnit4ClassRunner.class)
public class LinkedInPersonTest {
	@Test
	public void test_extractPostalCode() {
		LinkedInPerson linkedInPerson = new LinkedInPerson();

		String mainAddress = "13131 Mockingbird Lane.\nHollywood, CA 10101";

		linkedInPerson.setMainAddress(mainAddress);
		String postalCode = linkedInPerson.getPostalCode();
		Assert.assertEquals(postalCode, "10101");

		mainAddress = "13131\nMockingbird Lane.\nHollywood, CA\n10101\n\n\n";

		linkedInPerson.setMainAddress(mainAddress);
		postalCode = linkedInPerson.getPostalCode();
		Assert.assertEquals(postalCode, "10101");

		mainAddress = "13131\nMockingbird Lane.\nHollywood, CA";

		linkedInPerson.setMainAddress(mainAddress);
		postalCode = linkedInPerson.getPostalCode();
		Assert.assertEquals(postalCode, null);

		linkedInPerson.setMainAddress(null);
		postalCode = linkedInPerson.getPostalCode();
		Assert.assertEquals(postalCode, null);
	}

	@Test
	public void test_extractPhoneNumber() {
		LinkedInPerson linkedInPerson = new LinkedInPerson();

		HashSet<LinkedInPhoneNumber> lips = new HashSet<LinkedInPhoneNumber>();

		LinkedInPhoneNumber lip = new LinkedInPhoneNumber();
		lip.setPhoneNumber("111-111-1111");
		lip.setPhoneType(PhoneType.MOBILE);
		lips.add(lip);

		lip = new LinkedInPhoneNumber();
		lip.setPhoneNumber("222-222-2222");
		lip.setPhoneType(PhoneType.WORK);
		lips.add(lip);

		linkedInPerson.setLinkedInPhoneNumbers(lips);

		String phoneNumber = linkedInPerson.getMobileOrOtherPhoneNumber();
		Assert.assertEquals(phoneNumber, "111-111-1111");

		lips = new HashSet<LinkedInPhoneNumber>();
		lip = new LinkedInPhoneNumber();
		lip.setPhoneNumber("222-222-2222");
		lip.setPhoneType(PhoneType.WORK);
		lips.add(lip);

		linkedInPerson.setLinkedInPhoneNumbers(lips);

		phoneNumber = linkedInPerson.getMobileOrOtherPhoneNumber();
		Assert.assertEquals(phoneNumber, "222-222-2222");

		linkedInPerson.setLinkedInPhoneNumbers(null);
		phoneNumber = linkedInPerson.getMobileOrOtherPhoneNumber();
		Assert.assertEquals(phoneNumber, null);
	}
}
package com.workmarket.dao.random;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.configuration.Constants;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserRandomIdentifierDAOIT extends BaseServiceIT {

	@Autowired UserRandomIdentifierDAO userRandomIdentifier;
	final static double BASE =  Constants.USER_NUMBER_IDENTIFIER_LENGTH;

	@Test
	public void testRandomIdentifier() {
		for (int i = 0; i < 5; i++) {
			String number = userRandomIdentifier.generateUniqueNumber();
			Assert.assertTrue(number.length() <= BASE);
			Assert.assertTrue(number.length() <= userRandomIdentifier.getNumberLength());
			Assert.assertFalse(number.substring(0, 1).equals("0"));
		}
	}

}

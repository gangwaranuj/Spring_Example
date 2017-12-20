package com.workmarket.dao.random;

import com.workmarket.dao.exception.random.IdentifierGenerationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

/**
 * User: alexsilva Date: 4/9/14 Time: 5:01 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class WorkRandomIdentifierDAOImplTest {

	@Mock JdbcTemplate jdbcTemplate;
	private WorkRandomIdentifierDAOImpl workRandomIdentifierDAO;

	@Before
	public void setup() {
		workRandomIdentifierDAO = spy(new WorkRandomIdentifierDAOImpl(jdbcTemplate));
	}

	@Test(expected = IdentifierGenerationException.class)
	public void testGenerateUniqueNumber_throwException() throws Exception {
		doReturn(null).when(workRandomIdentifierDAO).generateRandomNumber();
		workRandomIdentifierDAO.generateUniqueNumber();
	}
}

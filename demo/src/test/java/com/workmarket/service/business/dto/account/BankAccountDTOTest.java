package com.workmarket.service.business.dto.account;


import com.workmarket.domains.payments.model.BankAccountDTO;
import org.junit.Assert;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;

public class BankAccountDTOTest {


	@Test
	public void testToStringMethodWithNullsForDateMethods(){

		BankAccountDTO dto = new BankAccountDTO();
		dto.setDobDay(null);
		dto.setDobYear(null);
		dto.setDobMonth(null);

		try{
			dto.toString();
		}catch(Exception ex){
			Assert.fail(ExceptionUtils.getMessage(ex));
		}

	}
}

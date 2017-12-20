/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransactionType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @since 5/1/2011
 */
@Component
@Scope(value = "prototype")
public class FinishedWorkFeeLane3 extends FinishedWorkFeeLane2 {


	public FinishedWorkFeeLane3() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.NEW_WORK_LANE_3);
	}

}
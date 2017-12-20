package com.workmarket.domains.model.screening;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="drug_test")
@DiscriminatorValue(Screening.DRUG_TEST_TYPE)
public class DrugTest extends Screening {
	
	private static final long serialVersionUID = 1L;
	
}
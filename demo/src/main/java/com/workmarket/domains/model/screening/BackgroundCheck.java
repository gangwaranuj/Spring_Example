package com.workmarket.domains.model.screening;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="background_check")
@DiscriminatorValue(Screening.BACKGROUND_CHECK_TYPE)
public class BackgroundCheck extends Screening {
	
	private static final long serialVersionUID = 1L;
	
}
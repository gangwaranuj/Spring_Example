package com.workmarket.service.business.dto;

import com.workmarket.domains.model.Industry;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class IndustryDTO implements Serializable {

	private static final long serialVersionUID = 4012999581082258609L;
	private Long id;
	private String name;
	private String otherName;

	public static IndustryDTO newDTO(Industry industry) {
		IndustryDTO dto = new IndustryDTO();
		dto.setId(industry.getId());
		dto.setName(industry.getName());
		dto.setOtherName(industry.getOtherName());
		return dto;
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

	public String getOtherName() { return otherName; }

	public void setOtherName(String otherName){ this.otherName = otherName; }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IndustryDTO)) {
			return false;
		}

		IndustryDTO that = (IndustryDTO) obj;
		return new EqualsBuilder()
			.append(id, that.getId())
			.build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.build();
	}
}

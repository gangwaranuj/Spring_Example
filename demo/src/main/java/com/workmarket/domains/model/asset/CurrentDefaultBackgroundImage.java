package com.workmarket.domains.model.asset;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "currentDefaultBackgroundImage")
@Table(name = "current_default_background_image")
public class CurrentDefaultBackgroundImage extends AbstractEntity {

	private static final long serialVersionUID = 1512722196465724115L;
	private DefaultBackgroundImage defaultBackgroundImage;

	@Fetch(FetchMode.JOIN)
	@OneToOne
	@JoinColumn(name = "default_background_image_id")
	public DefaultBackgroundImage getDefaultBackgroundImage() {
		return defaultBackgroundImage;
	}

	public void setDefaultBackgroundImage(DefaultBackgroundImage defaultBackgroundImage) {
		this.defaultBackgroundImage = defaultBackgroundImage;
	}

}

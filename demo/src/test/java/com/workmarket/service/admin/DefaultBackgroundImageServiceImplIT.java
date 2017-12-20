package com.workmarket.service.admin;

import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DefaultBackgroundImageServiceImplIT extends BaseServiceIT {

	@Autowired DefaultBackgroundImageService defaultBackgroundImageService;

	private DefaultBackgroundImage image1, image2;

	@After
	public void deleteImages() {
		defaultBackgroundImageService.removeBackgroundImage(image1);
		defaultBackgroundImageService.removeBackgroundImage(image2);
	}

	@Test
	public void getAllDefaultBackgroundImages_returnAll() throws Exception {
		image1 = createDefaultBackgroundImage();
		image2 = createDefaultBackgroundImage();
		List<DefaultBackgroundImage> images = defaultBackgroundImageService.getAll();
		assertThat(images, hasItems(image1, image2));
	}

	@Test
	public void setCurrentDefaultBackgroundImage_success() throws Exception {
		Long currentDefaultImage = defaultBackgroundImageService.getCurrentDefaultBackgroundImage().get().getId();

		image1 = createDefaultBackgroundImage();
		defaultBackgroundImageService.setCurrentDefaultBackgroundImage(image1.getId());
		image2 = defaultBackgroundImageService.getCurrentDefaultBackgroundImage().get();
		assertEquals(image1, image2);

		defaultBackgroundImageService.setCurrentDefaultBackgroundImage(currentDefaultImage);
	}

	@Test
	public void getCurrentDefaultBackgroundImage_success() throws Exception {
		assertNotNull(defaultBackgroundImageService.getCurrentDefaultBackgroundImage().get());
	}

	@Test
	public void removeBackgroundImage_success() throws Exception {
		image1 = createDefaultBackgroundImage();
		Long id = image1.getId();
		defaultBackgroundImageService.removeBackgroundImage(image1);
		assertNull(defaultBackgroundImageService.getDefaultBackgroundImage(id));
	}

	@Test
	public void storeBackgroundImage_success() throws Exception {
		AssetDTO dto = newAssetDTO();
		image1 = defaultBackgroundImageService.saveBackgroundImage(dto);
		assertNotNull(image1);
		List<DefaultBackgroundImage> images = defaultBackgroundImageService.getAll();
		assertThat(images, hasItems(image1));
	}

	private DefaultBackgroundImage createDefaultBackgroundImage() throws Exception {
		AssetDTO dto = newAssetDTO();
		return defaultBackgroundImageService.saveBackgroundImage(dto);
	}
}
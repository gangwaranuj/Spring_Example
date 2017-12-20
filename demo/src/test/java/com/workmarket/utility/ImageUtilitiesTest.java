package com.workmarket.utility;

import com.workmarket.test.BrokenTest;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.image.GraphicsMagickConverter;
import org.junit.Assert;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationcontext.xml")
@Category(BrokenTest.class)
@Ignore
public class ImageUtilitiesTest {

	protected static final String IMAGE_TEST_FILE = "/tmp/tmpPNGImage.png";
	
	private ConvertCmd instance1 = null; 
	private ConvertCmd instance2 = null; 
	
	@Autowired private ResourceLoader resourceLoader;
	
	@Before
	public void before() throws Exception {
		File tmpPNGImage = new File(IMAGE_TEST_FILE);
		if (tmpPNGImage.exists())
			tmpPNGImage.delete();
		Resource tmpPNGImageResource = resourceLoader.getResource("classpath:earth.png");
		IOUtils.copy(tmpPNGImageResource.getInputStream(), new FileOutputStream(IMAGE_TEST_FILE));

		instance1 = GraphicsMagickConverter.getInstance();
		instance2 = GraphicsMagickConverter.getInstance();

	}

	@After
	public void after() throws Exception {
		File tmpPNGImage = new File(IMAGE_TEST_FILE);
		if (tmpPNGImage.exists())
			tmpPNGImage.delete();
	}

	@Test
	public void test_resizeImage() throws Exception {

		String filename = String.format("/Users/rocio/Desktop/resized-%s.png", RandomUtilities.generateNumericString(10));

		ImageUtilities.scaleImage(IMAGE_TEST_FILE, filename, 100, 100);

		Assert.assertTrue(new File(filename).exists());
	}

	@Test
	public void test_cropImage() throws Exception {
		String filename = String.format("/Users/rocio/Desktop/cropped-%s.png", RandomUtilities.generateNumericString(10));

		ImageUtilities.cropImage(IMAGE_TEST_FILE, filename, 200, 200, 400, 600);

		Assert.assertTrue(new File(filename).exists());
	}

	@Test
	public void test_cropAndResizeImage() throws Exception {
		String filename = String.format("/Users/rocio/Desktop/croppedResized-%s.png", RandomUtilities.generateNumericString(10));

		ImageUtilities.cropImage(IMAGE_TEST_FILE, filename, 200, 200, 400, 600);
		ImageUtilities.scaleImage(filename, filename, 100, 200);

		Assert.assertTrue(new File(filename).exists());
	}

	@Test
	public void test_GraphickMagic() throws Exception {
		Assert.assertEquals(instance1, instance2);
		IMOperation op = new IMOperation();
		op.addImage("/Users/rocio/Desktop/Lund.jpg");
		op.crop(150, 200, 2, 0);
		op.addImage("/Users/rocio/Desktop/Lund4.png");

		// execute the operation
		instance1.run(op);
	}


}

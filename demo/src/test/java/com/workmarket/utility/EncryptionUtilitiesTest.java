package com.workmarket.utility;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import com.workmarket.configuration.EncryptionProperties;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

@RunWith(BlockJUnit4ClassRunner.class)
public class EncryptionUtilitiesTest {

	@Before
	public void before() {
		EncryptionProperties.setSalt("a67efc79e797ea7cde319ae84f57d9d3e2c8749a");
		EncryptionProperties.setSecret("e12aee81d812ae7cde319be81f57d9d3b2c8b49a");
	}

	@Test
	public void test_Md5PasswordEncoder() throws Exception {
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		Assert.assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", encoder.encodePassword("password", ""));
		Assert.assertEquals("c4ca4238a0b923820dcc509a6f75849b", encoder.encodePassword("" + 1, ""));
		Assert.assertEquals("c81e728d9d4c2f636f067f89cc14862c", encoder.encodePassword("" + 2, ""));
		Assert.assertEquals("41a4a5a3eb9934a4c00dc3c2a42277b6", encoder.encodePassword("" + 1239128794, ""));
	}

	@Test
	public void test_ShaPasswordEncoder() throws Exception {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
		Assert.assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", encoder.encodePassword("password", ""));
		Assert.assertEquals("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b", encoder.encodePassword("" + 1, ""));
		Assert.assertEquals("d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35", encoder.encodePassword("" + 2, ""));
		Assert.assertEquals("afc312dee8de1bf649e683f7ae06392f1d1a3d058e1d4d73ce8a7f2cf3399b26", encoder.encodePassword("" + 1239128794, ""));
	}

	@Test
	public void test_getMD5DigestStable() throws Exception {
		Assert.assertEquals("5aa507e8c3f15e86f914384affcb6258", EncryptionUtilities.getMD5Digest("Hello"));
		Assert.assertEquals("98e02c10fbe1bd409fdbedee4403ed6b", EncryptionUtilities.getMD5Digest("" + 1));
		Assert.assertEquals("8cb49de114f36231c26259a37edfe62b", EncryptionUtilities.getMD5Digest("" + 2));
		Assert.assertEquals("3b49b7065bc7dbbd6a339ec72686f13e", EncryptionUtilities.getMD5Digest("" + 1239128794));

		Assert.assertEquals("98e02c10fbe1bd409fdbedee4403ed6b", EncryptionUtilities.getMD5Digest(1L));
		Assert.assertEquals("8cb49de114f36231c26259a37edfe62b", EncryptionUtilities.getMD5Digest(2L));
		Assert.assertEquals("3b49b7065bc7dbbd6a339ec72686f13e", EncryptionUtilities.getMD5Digest(1239128794L));
	}

	@Test
	public void test_isPasswordLegal() throws Exception {
		Assert.assertTrue(EncryptionUtilities.isPasswordLegal("password"));
		Assert.assertTrue(EncryptionUtilities.isPasswordLegal("password123445"));

		Assert.assertFalse(EncryptionUtilities.isPasswordLegal("passwo rd123445"));
		Assert.assertFalse(EncryptionUtilities.isPasswordLegal("passwo/[]{} rd123445"));
	}

	@Test
	public void test_hashPassword() throws Exception {
		String digest = EncryptionUtilities.hashPassword("password", "salt");
		Assert.assertEquals("ced22f25384e53c51e100acead5cfafd761f18150ef141be6896700d5a0c8ae3", digest);
		Assert.assertTrue(EncryptionUtilities.isPasswordValid(digest, "password", "salt"));
		Assert.assertFalse(EncryptionUtilities.isPasswordValid(digest, "password1", "salt"));
		Assert.assertFalse(EncryptionUtilities.isPasswordValid(digest, "password", "salt1"));
		Assert.assertFalse(EncryptionUtilities.isPasswordValid(digest + "1", "password", "salt"));
	}

	@Test
	public void test_newSalt() throws Exception {
		String salt = EncryptionUtilities.newSalt();
		Assert.assertNotNull(salt);
		Assert.assertTrue(salt.length() == 40);
	}

	@Test
	public void test_encryptSSN() throws Exception {
		String ssn = "094-91-2311";

		String cryptext1 = EncryptionUtilities.encrypt(ssn);
		String cryptext2 = EncryptionUtilities.encrypt(ssn);

		Assert.assertTrue(cryptext1.length() > 10);
		Assert.assertNotSame(cryptext1, cryptext2);
		Assert.assertEquals(ssn, EncryptionUtilities.decrypt(cryptext1));
		Assert.assertEquals(ssn, EncryptionUtilities.decrypt(cryptext2));
	}

	@Test
	public void test_encryptLong() throws Exception {
		Assert.assertEquals(Long.valueOf(2487L), EncryptionUtilities.decryptLong(EncryptionUtilities.encryptLong(2487L)));
	}

	@Test
	public void test_getMD5Digest() throws Exception {
		Assert.assertEquals("98e02c10fbe1bd409fdbedee4403ed6b", EncryptionUtilities.getMD5Digest(1L));
	}
}

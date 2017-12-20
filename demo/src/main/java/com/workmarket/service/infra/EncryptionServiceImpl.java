package com.workmarket.service.infra;

import org.springframework.stereotype.Service;

import com.workmarket.utility.EncryptionUtilities;

@Service
public class EncryptionServiceImpl implements EncryptionService {


    @Override
    public String encrypt(String plainText) {
        return EncryptionUtilities.encrypt(plainText);
    }

    @Override
    public String decrypt(String cryptText) {
        return EncryptionUtilities.decrypt(cryptText);
    }

    @Override
    public String getMD5Digest(String message) {
        return EncryptionUtilities.getMD5Digest(message);
    }

    @Override
    public String getMD5Digest(Number number) {
        return EncryptionUtilities.getMD5Digest(number);
    }

    @Override
    public String hashPassword(String password, String salt) {
        return EncryptionUtilities.hashPassword(password, salt);
    }

    @Override
    public boolean isPasswordValid(String digest, String password, String salt) {
        return EncryptionUtilities.isPasswordValid(digest, password, salt);
    }

	@Override
	public long decryptId(String encryptedId) {
		return EncryptionUtilities.decryptLong(encryptedId);
	}
}

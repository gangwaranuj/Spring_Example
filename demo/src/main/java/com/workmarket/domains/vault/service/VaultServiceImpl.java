package com.workmarket.domains.vault.service;

import com.workmarket.domains.vault.dao.VaultDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by ant on 2/25/15.
 */
@Service
public class VaultServiceImpl implements VaultService {

	@Autowired protected VaultDAO vaultDAO;

	public VaultDAO getVaultDAO() {
		return vaultDAO;
	}
	public void setVaultDAO(VaultDAO vaultDAO) {
		this.vaultDAO = vaultDAO;
	}

	public void put(String className, long id, Map<String,String> properties) {
		vaultDAO.put(className, id, properties);
	}

	public Map<String,String> get(String securedMode, String className, long id) {
		return vaultDAO.get(securedMode, className, id);
	}

}

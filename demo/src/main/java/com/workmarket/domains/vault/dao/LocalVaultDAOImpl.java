package com.workmarket.domains.vault.dao;

import com.workmarket.domains.vault.model.Securable;
import com.workmarket.utility.StringUtilities;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2/25/15.
 */
@Repository(value="localVaultDAO")
public class LocalVaultDAOImpl implements VaultDAO {

	protected Map<String,Map<Long,Map<String,String>>> vault = new HashMap<>();
	public final static int OBSCURE_ALL_CHARS = -1;

	@Override
	public void put(String className, long id, Map<String,String> properties) {
		Assert.notNull(className);
		Assert.notEmpty(properties);
		if(!vault.containsKey(className)) {
			Map<Long,Map<String,String>> classMap = new HashMap<>();
			classMap.put(id,properties);
			vault.put(className,classMap);
		} else {
			Map<Long,Map<String,String>> classMap = vault.get(className);
			classMap.put(id, properties);
		}
	}

	@Override
	public Map<String,String> get(String securedMode, String className, long id) {
		Assert.notNull(securedMode);
		Assert.notNull(className);
		Map<Long,Map<String,String>> classMap = vault.get(className);
		if(classMap == null) return null;
		Map<String,String> instanceMap = classMap.get(id);
		if(instanceMap != null) {
			for(Map.Entry<String, String> entry : instanceMap.entrySet()) {
				entry.setValue(getSecuredProperty(securedMode, entry.getValue()));
			}
		}

		return instanceMap;
	}

	public String getSecuredProperty(String securedMode, String value) {
		String s = null;
		if (value != null) {
			switch (securedMode) {
				case Securable.NONSECURE_MODE:
					s = value;
				case Securable.SECURE_MODE:
					s = StringUtilities.showLastNDigits(value, 'X', value.length());
				case Securable.PARTIALLY_SECURE_MODE:
						 default:
			    s = StringUtilities.showLastNDigits(value, 'X', 4);
			}
		}
		return s;
	}

}

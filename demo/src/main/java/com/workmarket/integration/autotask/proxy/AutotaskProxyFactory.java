package com.workmarket.integration.autotask.proxy;

import com.workmarket.domains.model.integration.autotask.AutotaskUser;

/**
 * Created by nick on 2012-12-21 1:01 PM
 */
public interface AutotaskProxyFactory {

	public AutotaskProxy newInstance(AutotaskUser user);

	public AutotaskProxy newInstance();

	public AutotaskProxy newInstance(String userName, String password, String zoneUrl);

}

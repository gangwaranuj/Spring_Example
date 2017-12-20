package com.workmarket.dao;

import com.workmarket.domains.model.SocialUserConnection;
import org.springframework.social.connect.ConnectionKey;

/**
 * User: micah
 * Date: 3/17/13
 * Time: 12:25 PM
 */
public interface SocialUserConnectionDAO {
	public SocialUserConnection findBySocialKey(ConnectionKey key);
}

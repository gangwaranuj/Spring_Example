package com.workmarket.dao.notification;

import static org.junit.Assert.*;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotificationPreference;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
// these test is not about a specific user but more about insuring that our SQL
// will correctly retrieve a record so what user we use doesn't really matter
public class UserNotificationPreferenceDAOImplIT extends BaseServiceIT {

	@Autowired private UserNotificationPreferenceDAO userNotificationPreferenceDAO;
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Test
	@Transactional
	public void findByUser_success() throws Exception {
		String query = "select distinct user_id from user_notification_preference limit 1";
		Long userId = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, Long.class);
		assertNotNull(userId);

		List<UserNotificationPreference> preferences = userNotificationPreferenceDAO.findByUser(userId);
		assertNotNull(preferences);
		assertTrue(preferences.size() > 0);

		// now verify that for a preference that it is fully loaded
		UserNotificationPreference unp = preferences.get(0);
		assertNotNull(unp.getDispatchEmailFlag());
		assertNotNull(unp.getDispatchSmsFlag());
		assertNotNull(unp.getDispatchBullhornFlag());
		assertNotNull(unp.getDispatchVoiceFlag());
		assertNotNull(unp.getDispatchPushFlag());
		assertNotNull(unp.getFollowFlag());
		assertNotNull(unp.getEmailFlag());
		assertNotNull(unp.getBullhornFlag());
		assertNotNull(unp.getNotificationType());
		assertNotNull(unp.getPushFlag());
		assertNotNull(unp.getSmsFlag());
		assertNotNull(unp.getVoiceFlag());
	}

	@Test
	@Transactional
	public void findByUser_notFound() throws Exception {
		List<UserNotificationPreference> preferences = userNotificationPreferenceDAO.findByUser(Long.MAX_VALUE);
		assertNotNull(preferences);
		assertEquals(preferences.size(), 0);
	}

	@Test
	@Transactional
	public void findByUserWithDefault_invalidUser() throws Exception {
		List<NotificationType> preferences = userNotificationPreferenceDAO.findByUserWithDefault(Long.MAX_VALUE);
		assertNotNull(preferences);
		assertTrue(preferences.size() > 0);

		// verify the object is populated
		NotificationType np = preferences.get(0);
		assertNotNull(np.getDispatchEmailFlag());
		assertNotNull(np.getDispatchSmsFlag());
		assertNotNull(np.getDispatchBullhornFlag());
		assertNotNull(np.getDispatchVoiceFlag());
		assertNotNull(np.getDispatchPushFlag());
		assertNotNull(np.getFollowFlag());
		assertNotNull(np.getEmailFlag());
		assertNotNull(np.getBullhornFlag());
		assertNotNull(np.getPushFlag());
		assertNotNull(np.getSmsFlag());
		assertNotNull(np.getVoiceFlag());
	}

	@Test
	@Transactional
	public void findByUserWithDefault_validUserNoPrefs() throws Exception {
		String query = "select distinct id from user where id not in (select distinct user_id from user_notification_preference) limit 1";
		Long userId = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, Long.class);
		assertNotNull(userId);

		List<NotificationType> preferences = userNotificationPreferenceDAO.findByUserWithDefault(1l);
		assertNotNull(preferences);
		assertTrue(preferences.size() > 0);

		// verify the object is populated
		NotificationType np = preferences.get(0);
		assertNotNull(np.getDispatchEmailFlag());
		assertNotNull(np.getDispatchSmsFlag());
		assertNotNull(np.getDispatchBullhornFlag());
		assertNotNull(np.getDispatchVoiceFlag());
		assertNotNull(np.getDispatchPushFlag());
		assertNotNull(np.getFollowFlag());
		assertNotNull(np.getEmailFlag());
		assertNotNull(np.getBullhornFlag());
		assertNotNull(np.getPushFlag());
		assertNotNull(np.getSmsFlag());
		assertNotNull(np.getVoiceFlag());

	}

	@Test
	@Transactional
	public void findByUserWithDefault_existingUser() throws Exception {
		String query = "select distinct user_id from user_notification_preference limit 1";
		Long userId = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, Long.class);
		assertNotNull(userId);

		List<NotificationType> preferences = userNotificationPreferenceDAO.findByUserWithDefault(userId);
		assertNotNull(preferences);
		assertTrue(preferences.size() > 0);

		// verify the object is populated
		NotificationType np = preferences.get(0);
		assertNotNull(np.getDispatchEmailFlag());
		assertNotNull(np.getDispatchSmsFlag());
		assertNotNull(np.getDispatchBullhornFlag());
		assertNotNull(np.getDispatchVoiceFlag());
		assertNotNull(np.getDispatchPushFlag());
		assertNotNull(np.getFollowFlag());
		assertNotNull(np.getEmailFlag());
		assertNotNull(np.getBullhornFlag());
		assertNotNull(np.getPushFlag());
		assertNotNull(np.getSmsFlag());
		assertNotNull(np.getVoiceFlag());

	}

	@Test
	@Transactional
	public void findUsersByCompanyAndNotificationType_Success() throws Exception {
		String query = "select distinct user_id, notification_type_code from user_notification_preference where email_flag = 1 limit 1";
		UserAndCode uac = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, new UserAndCodeRowMapper());
		assertNotNull(uac);

		String companyQuery = "select company_id from user where id = " + uac.userId;
		Long companyId = jdbcTemplate.queryForObject(companyQuery, Collections.EMPTY_MAP, Long.class);
		assertNotNull(companyId);

		List<User>  users = userNotificationPreferenceDAO.findUsersByCompanyAndNotificationType(companyId, uac.notificationTypeCode);
		assertNotNull(users);
		assertTrue(users.size() > 0);
	}

	@Test
	@Transactional
	public void findUsersByCompanyAndNotificationType_NoneFound() throws Exception {
		String query = "select distinct user_id, notification_type_code from user_notification_preference where email_flag = 0 limit 1";
		UserAndCode uac = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, new UserAndCodeRowMapper());
		assertNotNull(uac);

		List<User>  users = userNotificationPreferenceDAO.findUsersByCompanyAndNotificationType(uac.userId, uac.notificationTypeCode);
		assertNotNull(users);
		assertEquals(0, users.size());
	}

	@Test
	@Transactional
	public void findByUserAndNotificationType_Success() throws Exception {
		String query = "select distinct user_id, notification_type_code from user_notification_preference where email_flag = 0 and follow_flag = 1 limit 1";
		UserAndCode uac = jdbcTemplate.queryForObject(query, Collections.EMPTY_MAP, new UserAndCodeRowMapper());
		assertNotNull(uac);

		UserNotificationPreference pref = userNotificationPreferenceDAO.findByUserAndNotificationType(uac.userId, uac.notificationTypeCode);
		assertNotNull(pref);
		assertFalse(pref.getEmailFlag());
		assertTrue(pref.getFollowFlag());
		assertFalse(pref.getDispatchEmailFlag());
	}

	class UserAndCode {
		public Long userId;
		public String notificationTypeCode;
	}

	class UserAndCodeRowMapper implements RowMapper<UserAndCode> {
		@Override
		public UserAndCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserAndCode uac = new UserAndCode();
			uac.userId = rs.getLong("user_id");
			uac.notificationTypeCode = rs.getString("notification_type_code");
			return uac;
		}
	}
}

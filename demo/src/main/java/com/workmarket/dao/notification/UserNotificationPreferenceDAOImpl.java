package com.workmarket.dao.notification;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotificationPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class UserNotificationPreferenceDAOImpl extends AbstractDAO<UserNotificationPreference> implements UserNotificationPreferenceDAO  {

	@Autowired @Qualifier("readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private UserDAO userDAO;

	protected Class<UserNotificationPreference> getEntityClass() {
		return UserNotificationPreference.class;
	}

	public UserNotificationPreference findByUserAndNotificationType(Long userId, String notificationTypeCode) {
		UserNotificationPreference preference = (UserNotificationPreference)getFactory().getCurrentSession().getNamedQuery("userNotificationPreference.byUserAndType")
				.setLong("user_id", userId)
				.setString("notification_type_code", notificationTypeCode)
				.uniqueResult();

		// If the user hasn't set up his preferences, lookup the notification type
		// and determine if it's one of the defaults.
		if (preference == null) {
			NotificationType type = (NotificationType)getFactory().getCurrentSession().get(NotificationType.class, notificationTypeCode);
			Assert.notNull(userId);
			User user = userDAO.get(userId);
			Assert.notNull(user);

			preference = new UserNotificationPreference();
			preference.setNotificationType(new NotificationType(notificationTypeCode));
			preference.setUser(user);
			preference.setEmailFlag((type != null ? type.isDefault() : true));
			preference.setFollowFlag((type != null ? type.isDefault() : true));
			preference.setBullhornFlag((type != null ? type.isBullhornDefault() : true));
			preference.setSmsFlag(type != null ? type.getSmsFlag() : false);
			preference.setVoiceFlag(type != null ? type.getVoiceFlag() : false);
			preference.setPushFlag(type != null ? type.getPushFlag() : false);
			preference.setDispatchBullhornFlag(preference.getBullhornFlag());
			preference.setDispatchEmailFlag(preference.getEmailFlag());
			preference.setDispatchPushFlag(preference.getPushFlag());
			preference.setDispatchSmsFlag(preference.getSmsFlag());
			preference.setDispatchVoiceFlag(preference.getVoiceFlag());
		}

		return preference;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findUsersByCompanyAndNotificationType(Long companyId, String notificationTypeCode) {

		return (List<User>)getFactory().getCurrentSession()
				.createQuery("SELECT DISTINCT user " +
						"FROM userNotificationPreference AS preference " +
						"JOIN preference.user AS user " +
						"WHERE preference.emailFlag = 1" +
						"AND preference.notificationType.code = :notificationTypeCode " +
						"AND user.userStatusType.code = :approvedStatus " +
						"AND user.emailConfirmed = :emailConfirmed " +
						"AND user.company.id = :companyId")
				.setParameter("companyId", companyId)
				.setParameter("approvedStatus", UserStatusType.APPROVED)
				.setParameter("emailConfirmed", Boolean.TRUE)
				.setParameter("notificationTypeCode", notificationTypeCode).list();
	}

	@SuppressWarnings("unchecked")
	public List<UserNotificationPreference> findByUser(Long userId) {
		return getFactory().getCurrentSession().getNamedQuery("userNotificationPreference.byUser")
				.setLong("user_id", userId)
				.list();
	}

	@Override
	public List<NotificationType> findByUserWithDefault(Long userId) {

		StringBuilder sql = new StringBuilder("SELECT notification.code, notification.description, notification.default_flag, notification.default_bullhorn_flag, ")
				.append(" IFNULL(user_pref.email_flag, notification.default_flag) as email_flag, ")
				.append(" IFNULL(user_pref.follow_flag, notification.default_flag) as follow_flag, ")
				.append(" IFNULL(user_pref.bullhorn_flag, notification.default_bullhorn_flag) as bullhorn_flag, ")
				.append(" IFNULL(user_pref.sms_flag, false) as sms_flag, ")
				.append(" IFNULL(user_pref.voice_flag, false) as voice_flag, ")
				.append(" IFNULL(user_pref.push_flag, false) as push_flag, ")
				.append(" IFNULL(user_pref.dispatch_email_flag, notification.dispatch_email_flag) as dispatch_email_flag, ")
				.append(" IFNULL(user_pref.dispatch_bullhorn_flag, notification.dispatch_bullhorn_flag) as dispatch_bullhorn_flag, ")
				.append(" IFNULL(user_pref.dispatch_sms_flag, notification.dispatch_sms_flag) as dispatch_sms_flag, ")
				.append(" IFNULL(user_pref.dispatch_voice_flag, notification.dispatch_voice_flag) as dispatch_voice_flag, ")
				.append(" IFNULL(user_pref.dispatch_push_flag, notification.dispatch_push_flag) as dispatch_push_flag ")
				.append(" FROM      notification_type notification ")
				.append(" LEFT JOIN user_notification_preference user_pref ")
				.append(" ON        notification.code = user_pref.notification_type_code ")
				.append(" AND       user_pref.user_id = :userId ")
				.append(" WHERE     notification.configurable_flag = true ");

		Map<String,Object> params  = Maps.newHashMap();
		params.put("userId", userId);

		RowMapper<NotificationType> mapper = new RowMapper<NotificationType>() {
			public NotificationType mapRow(ResultSet rs, int rowNum) throws SQLException {
				NotificationType notification = new NotificationType();
				notification.setCode(rs.getString("code"));
				notification.setDescription(rs.getString("description"));
				notification.setEmailFlag(rs.getBoolean("email_flag"));
				notification.setFollowFlag(rs.getBoolean("follow_flag"));
				notification.setBullhornFlag(rs.getBoolean("bullhorn_flag"));
				notification.setPushFlag(rs.getBoolean("push_flag"));
				notification.setSmsFlag(rs.getBoolean("sms_flag"));
				notification.setDefault(rs.getBoolean("default_flag"));
				notification.setBullhornDefault(rs.getBoolean("default_bullhorn_flag"));
				notification.setVoiceFlag(rs.getBoolean("voice_flag"));

				notification.setDispatchEmailFlag(rs.getBoolean("dispatch_email_flag"));
				notification.setDispatchBullhornFlag(rs.getBoolean("dispatch_bullhorn_flag"));
				notification.setDispatchPushFlag(rs.getBoolean("dispatch_push_flag"));
				notification.setDispatchSmsFlag(rs.getBoolean("dispatch_sms_flag"));
				notification.setDispatchVoiceFlag(rs.getBoolean("dispatch_voice_flag"));

				return notification;
			}
		};

		return jdbcTemplate.query(sql.toString(), params, mapper);

	}
}

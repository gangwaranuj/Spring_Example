package com.workmarket.domains.model.integration.autotask;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.integration.IntegrationUser;
import com.workmarket.utility.EncryptionUtilities;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "autotaskUser")
@Table(name = "autotask_user")
@NamedQueries({
		@NamedQuery(name ="autotaskUser.byCompanyId", query = "from autotaskUser auto join fetch auto.user user join fetch user.company company where company.id = :companyId")
})
public class AutotaskUser extends IntegrationUser {
	private static final long serialVersionUID = 3534420933195056139L;

	private String userName;

	private String password;

	private User user;

	// TODO: validate on pattern
	private String zoneUrl;

	public AutotaskUser() {
		super();
	}

	public AutotaskUser(User user, String userName, String password, String zoneUrl) {
		super();
		this.user = user;
		this.userName = userName;
		this.setPassword(password);
		this.zoneUrl = zoneUrl;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id")
	public User getUser() {
		return user;
	}

	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}

	@Column(name = "password", nullable = false, length = 50)
	public String getHashedPassword() {
		return password;
	}

	@Column(name = "zone_url")
	public String getZoneUrl() {
		return zoneUrl;
	}

	public void setHashedPassword(String password) {
		this.password = password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setZoneUrl(String zoneUrl) {
		this.zoneUrl = zoneUrl;
	}

	@Transient
	public boolean hasZoneUrl() {
		return StringUtils.isNotBlank(zoneUrl);
	}

	@Transient
	public String getPassword() {
		return EncryptionUtilities.decrypt(password);
	}

	public void setPassword(String password) {
		this.password = EncryptionUtilities.encrypt(password);
	}

	@Transient
	public Long getUserId() {
		User user = getUser();
		return (user != null) ? user.getId() : null;
	}
}

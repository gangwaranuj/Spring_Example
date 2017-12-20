package com.workmarket.domains.model.authnz;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.SubnetUtils;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AuditChanges
@Entity(name="authnzInetAddress")
@Table(name="company_authorized_ip")
public class AuthorizedInetAddress extends DeletableEntity {

	//NOTE Cribbed from {@link org.apache.commons.net.util.SubnetUtils}
	private static final String IP_ADDRESS = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
	private static final String MASK_FORMAT = IP_ADDRESS + "/" + IP_ADDRESS;
	private static final String CIDR_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
	private static final Pattern maskPattern = Pattern.compile(MASK_FORMAT);
	private static final Pattern cidrPattern = Pattern.compile(CIDR_FORMAT);

	private Company company;
	private String inetAddress;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="company_id")
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="ip_address")
	public String getInetAddress() {
		return inetAddress;
	}
	public void setInetAddress(String inetAddress) {
		this.inetAddress = inetAddress;
	}

	@Transient
	public boolean isInRange(String address) {
		// If a straight IP compare, match the strings
		// Otherwise assume a net-mask

		if (!StringUtils.contains(inetAddress, "/")) {
			return StringUtils.equals(inetAddress, address);
		}

		SubnetUtils util = getSubnet(inetAddress);
		/**
		 * @see http://stackoverflow.com/q/577363/80778
		 * NOTE In current version of Commons Net library, {@link org.apache.commons.net.util.SubnetUtils.SubnetInfo.isInRange()}
		 * has a bug that fails to check against the low range of an IP mask.
		 * https://issues.apache.org/jira/browse/NET-236
		 */
		if (util != null) {
			int mask = util.getInfo().asInteger(util.getInfo().getNetmask());
			int subnet = util.getInfo().asInteger(util.getInfo().getNetworkAddress());
			int ip = util.getInfo().asInteger(address);
			return (subnet & mask) == (ip & mask);
		}

		return false;
	}

	@Transient
	private SubnetUtils getSubnet(String a) {
		Matcher maskMatcher = maskPattern.matcher(a);
		Matcher cidrMatcher = cidrPattern.matcher(a);

		SubnetUtils subnet = null;
		if (maskMatcher.matches()) {
			subnet = new SubnetUtils(maskMatcher.group(1), maskMatcher.group(2));
		} else if (cidrMatcher.matches()) {
			subnet = new SubnetUtils(a);
		}
		return subnet;
	}
}

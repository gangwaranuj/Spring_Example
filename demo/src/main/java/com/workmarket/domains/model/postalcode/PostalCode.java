package com.workmarket.domains.model.postalcode;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.datetime.TimeZone;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.regex.Pattern;

@Entity(name = "postalCode")
@Table(name = "postal_code")
public class PostalCode extends AbstractEntity {

	private static final long serialVersionUID = -2772138742830402622L;

	private String postalCode;
	private String city;
	private State stateProvince;
	private Country country;
	private String areaCode;
	private Double latitude;
	private Double longitude;
	private TimeZone timeZone;

	public static final int
		POSTAL_CODE_MAX = 7,
		POSTAL_CODE_MIN = 3;

	public static final Pattern
		canadaPattern = Pattern.compile("(\\p{Alpha}\\p{Digit}\\p{Alpha}) ?(\\p{Digit}\\p{Alpha}\\p{Digit})?"),
		usaPattern = Pattern.compile("\\p{Digit}{5}");

	public static PostalCode fromString(final String s) {
		if (s == null) {
			return null;
		}
		if (canadaPattern.matcher(s).matches()) {
			return newPostalCode(s, Country.CANADA);
		}
		if (usaPattern.matcher(s).matches()) {
			return newPostalCode(s, Country.USA);
		}
		return null;
	}

	@Column(name = "postal_code", nullable = false)
	@Size(min = POSTAL_CODE_MIN, max = POSTAL_CODE_MAX)
	public String getPostalCode() {
		return postalCode;
	}

	@Column(name = "city", nullable = false, length = 100)
	public String getCity() {
		return city;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="state_province", referencedColumnName="id", nullable = false)
	public State getStateProvince() {
		return stateProvince;
	}

	@Column(name = "areacode", nullable = false)
	public String getAreaCode() {
		return areaCode;
	}

	@Column(name = "latitude", nullable = false)
	public Double getLatitude() {
		return latitude;
	}

	@Column(name = "longitude", nullable = false)
	public Double getLongitude() {
		return longitude;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setStateProvince(State stateProvince) {
		this.stateProvince = stateProvince;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "time_zone_id", nullable = false)
	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return getPostalCode();
	}

	@Transient
	public String getTimeZoneName() {
		return timeZone.getTimeZoneId();
	}

	@Transient
	public static PostalCode newPostalCode(final String p, final String countryCode) {
		PostalCode postalCode = new PostalCode();
		postalCode.setCountry(Country.valueOf(countryCode));
		postalCode.setPostalCode(p.replaceAll(" ", ""));
		return postalCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PostalCode)) return false;

		PostalCode that = (PostalCode) o;

		if (areaCode != null ? !areaCode.equals(that.areaCode) : that.areaCode != null) { return false; }
		if (city != null ? !city.equals(that.city) : that.city != null) { return false; }
		if (country != null ? !country.equals(that.country) : that.country != null) { return false; }
		if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) { return false; }
		if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) { return false; }
		if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) { return false; }
		if (stateProvince != null ? !stateProvince.equals(that.stateProvince) : that.stateProvince != null)
			{ return false; }
		if (timeZone != null ? !timeZone.equals(that.timeZone) : that.timeZone != null) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (stateProvince != null ? stateProvince.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (areaCode != null ? areaCode.hashCode() : 0);
		result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
		result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
		result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
		return result;
	}
}

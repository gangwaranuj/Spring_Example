package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	private String city;
	private String state;
	private String postalCode;
	private String locationName;
	private String locationNumber;
	private String country;
	private Double latitude;
	private Double longitude;

	public DashboardAddress() {
	}

	public String getCity() {
		return this.city;
	}

	public DashboardAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public boolean isSetCity() {
		return this.city != null;
	}

	public String getState() {
		return this.state;
	}

	public DashboardAddress setState(String state) {
		this.state = state;
		return this;
	}

	public boolean isSetState() {
		return this.state != null;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public DashboardAddress setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public boolean isSetPostalCode() {
		return this.postalCode != null;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public DashboardAddress setLocationName(String locationName) {
		this.locationName = locationName;
		return this;
	}

	public boolean isSetLocationName() {
		return this.locationName != null;
	}

	public String getLocationNumber() {
		return this.locationNumber;
	}

	public DashboardAddress setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
		return this;
	}

	public boolean isSetLocationNumber() {
		return this.locationNumber != null;
	}

	public String getCountry() {
		return this.country;
	}

	public DashboardAddress setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isSetCountry() {
		return this.country != null;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public boolean isSetLatitude() {
		return this.latitude != null;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public boolean isSetLongitude() {
		return this.longitude != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardAddress)
			return this.equals((DashboardAddress) that);
		return false;
	}

	private boolean equals(DashboardAddress that) {
		if (that == null)
			return false;

		boolean this_present_city = true && this.isSetCity();
		boolean that_present_city = true && that.isSetCity();
		if (this_present_city || that_present_city) {
			if (!(this_present_city && that_present_city))
				return false;
			if (!this.city.equals(that.city))
				return false;
		}

		boolean this_present_state = true && this.isSetState();
		boolean that_present_state = true && that.isSetState();
		if (this_present_state || that_present_state) {
			if (!(this_present_state && that_present_state))
				return false;
			if (!this.state.equals(that.state))
				return false;
		}

		boolean this_present_postalCode = true && this.isSetPostalCode();
		boolean that_present_postalCode = true && that.isSetPostalCode();
		if (this_present_postalCode || that_present_postalCode) {
			if (!(this_present_postalCode && that_present_postalCode))
				return false;
			if (!this.postalCode.equals(that.postalCode))
				return false;
		}

		boolean this_present_locationName = true && this.isSetLocationName();
		boolean that_present_locationName = true && that.isSetLocationName();
		if (this_present_locationName || that_present_locationName) {
			if (!(this_present_locationName && that_present_locationName))
				return false;
			if (!this.locationName.equals(that.locationName))
				return false;
		}

		boolean this_present_locationNumber = true && this.isSetLocationNumber();
		boolean that_present_locationNumber = true && that.isSetLocationNumber();
		if (this_present_locationNumber || that_present_locationNumber) {
			if (!(this_present_locationNumber && that_present_locationNumber))
				return false;
			if (!this.locationNumber.equals(that.locationNumber))
				return false;
		}

		boolean this_present_country = true && this.isSetCountry();
		boolean that_present_country = true && that.isSetCountry();
		if (this_present_country || that_present_country) {
			if (!(this_present_country && that_present_country))
				return false;
			if (!this.country.equals(that.country))
				return false;
		}

		boolean this_present_latitude = true && this.isSetLatitude();
		boolean that_present_latitude = true && that.isSetLatitude();
		if (this_present_latitude || that_present_latitude) {
			if (!(this_present_latitude && that_present_latitude))
				return false;
			if (!this.latitude.equals(that.latitude))
				return false;
		}

		boolean this_present_longitude = true && this.isSetLongitude();
		boolean that_present_longitude = true && that.isSetLongitude();
		if (this_present_longitude || that_present_longitude) {
			if (!(this_present_longitude && that_present_longitude))
				return false;
			if (!this.longitude.equals(that.longitude))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_city = true && (isSetCity());
		builder.append(present_city);
		if (present_city)
			builder.append(city);

		boolean present_state = true && (isSetState());
		builder.append(present_state);
		if (present_state)
			builder.append(state);

		boolean present_postalCode = true && (isSetPostalCode());
		builder.append(present_postalCode);
		if (present_postalCode)
			builder.append(postalCode);

		boolean present_locationName = true && (isSetLocationName());
		builder.append(present_locationName);
		if (present_locationName)
			builder.append(locationName);

		boolean present_locationNumber = true && (isSetLocationNumber());
		builder.append(present_locationNumber);
		if (present_locationNumber)
			builder.append(locationNumber);

		boolean present_country = true && (isSetCountry());
		builder.append(present_country);
		if (present_country)
			builder.append(country);

		boolean present_latitude = true && (isSetLatitude());
		builder.append(present_latitude);
		if (present_latitude)
			builder.append(latitude);

		boolean present_longitude = true && (isSetLongitude());
		builder.append(present_longitude);
		if (present_longitude)
			builder.append(longitude);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardAddress(");
		boolean first = true;

		if (isSetCity()) {
			sb.append("city:");
			if (this.city == null) {
				sb.append("null");
			} else {
				sb.append(this.city);
			}
			first = false;
		}
		if (isSetState()) {
			if (!first) sb.append(", ");
			sb.append("state:");
			if (this.state == null) {
				sb.append("null");
			} else {
				sb.append(this.state);
			}
			first = false;
		}
		if (isSetPostalCode()) {
			if (!first) sb.append(", ");
			sb.append("postalCode:");
			if (this.postalCode == null) {
				sb.append("null");
			} else {
				sb.append(this.postalCode);
			}
			first = false;
		}
		if (isSetLocationName()) {
			if (!first) sb.append(", ");
			sb.append("locationName:");
			if (this.locationName == null) {
				sb.append("null");
			} else {
				sb.append(this.locationName);
			}
			first = false;
		}
		if (isSetLocationNumber()) {
			if (!first) sb.append(", ");
			sb.append("locationNumber:");
			if (this.locationNumber == null) {
				sb.append("null");
			} else {
				sb.append(this.locationNumber);
			}
			first = false;
		}
		if (isSetCountry()) {
			if (!first) sb.append(", ");
			sb.append("country:");
			if (this.country == null) {
				sb.append("null");
			} else {
				sb.append(this.country);
			}
			first = false;
		}
		if (isSetLatitude()) {
			if (!first) sb.append(", ");
			sb.append("latitude:");
			if (this.latitude == null) {
				sb.append("null");
			} else {
				sb.append(this.latitude);
			}
			first = false;
		}
		if (isSetLongitude()) {
			if (!first) sb.append(", ");
			sb.append("longitude:");
			if (this.longitude == null) {
				sb.append("null");
			} else {
				sb.append(this.longitude);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.services.realtime;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.model.lane.LaneType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RealtimeResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userNumber;
	private String name;
	private double distance;
	private List<ResourceIconType> icons;
	private com.workmarket.thrift.core.Address address;
	private GeoPoint location;
	private double averageStarRating;
	private int numberOfRatings;
	private long dateSentOn;
	private List<com.workmarket.thrift.work.ResourceNote> hoverNotes;
	private String workPhoneNumber;
	private String mobilePhoneNumber;
	private String email;
	private LaneType laneType;
	private long resourceId;
	private String companyName;
	private String firstName;
	private String lastName;

	public RealtimeResource() {
	}

	public RealtimeResource(String userNumber, String name) {
		this();
		this.userNumber = userNumber;
		this.name = name;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public RealtimeResource setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public String getName() {
		return this.name;
	}

	public RealtimeResource setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public double getDistance() {
		return this.distance;
	}

	public RealtimeResource setDistance(double distance) {
		this.distance = distance;
		return this;
	}

	public boolean isSetDistance() {
		return (distance > 0D);
	}

	public int getIconsSize() {
		return (this.icons == null) ? 0 : this.icons.size();
	}

	public java.util.Iterator<ResourceIconType> getIconsIterator() {
		return (this.icons == null) ? null : this.icons.iterator();
	}

	public void addToIcons(ResourceIconType elem) {
		if (this.icons == null) {
			this.icons = new ArrayList<ResourceIconType>();
		}
		this.icons.add(elem);
	}

	public List<ResourceIconType> getIcons() {
		return this.icons;
	}

	public RealtimeResource setIcons(List<ResourceIconType> icons) {
		this.icons = icons;
		return this;
	}

	public boolean isSetIcons() {
		return this.icons != null;
	}

	public com.workmarket.thrift.core.Address getAddress() {
		return this.address;
	}

	public RealtimeResource setAddress(com.workmarket.thrift.core.Address address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	public GeoPoint getLocation() {
		return this.location;
	}

	public RealtimeResource setLocation(GeoPoint location) {
		this.location = location;
		return this;
	}

	public boolean isSetLocation() {
		return this.location != null;
	}

	public double getAverageStarRating() {
		return this.averageStarRating;
	}

	public RealtimeResource setAverageStarRating(double averageStarRating) {
		this.averageStarRating = averageStarRating;
		return this;
	}

	public boolean isSetAverageStarRating() {
		return (averageStarRating > 0D);
	}

	public int getNumberOfRatings() {
		return this.numberOfRatings;
	}

	public RealtimeResource setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
		return this;
	}

	public boolean isSetNumberOfRatings() {
		return (numberOfRatings > 0);
	}

	public long getDateSentOn() {
		return this.dateSentOn;
	}

	public RealtimeResource setDateSentOn(long dateSentOn) {
		this.dateSentOn = dateSentOn;
		return this;
	}

	public boolean isSetDateSentOn() {
		return (dateSentOn > 0L);
	}

	public int getHoverNotesSize() {
		return (this.hoverNotes == null) ? 0 : this.hoverNotes.size();
	}

	public java.util.Iterator<com.workmarket.thrift.work.ResourceNote> getHoverNotesIterator() {
		return (this.hoverNotes == null) ? null : this.hoverNotes.iterator();
	}

	public void addToHoverNotes(com.workmarket.thrift.work.ResourceNote elem) {
		if (this.hoverNotes == null) {
			this.hoverNotes = new ArrayList<com.workmarket.thrift.work.ResourceNote>();
		}
		this.hoverNotes.add(elem);
	}

	public List<com.workmarket.thrift.work.ResourceNote> getHoverNotes() {
		return this.hoverNotes;
	}

	public RealtimeResource setHoverNotes(List<com.workmarket.thrift.work.ResourceNote> hoverNotes) {
		this.hoverNotes = hoverNotes;
		return this;
	}

	public boolean isSetHoverNotes() {
		return this.hoverNotes != null;
	}


	public String getWorkPhoneNumber() {
		return this.workPhoneNumber;
	}

	public RealtimeResource setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
		return this;
	}

	public boolean isSetWorkPhoneNumber() {
		return this.workPhoneNumber != null;
	}

	public String getMobilePhoneNumber() {
		return this.mobilePhoneNumber;
	}

	public RealtimeResource setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
		return this;
	}

	public boolean isSetMobilePhoneNumber() {
		return this.mobilePhoneNumber != null;
	}

	public String getEmail() {
		return this.email;
	}

	public RealtimeResource setEmail(String email) {
		this.email = email;
		return this;
	}

	public boolean isSetEmail() {
		return this.email != null;
	}

	public LaneType getLaneType() {
		return this.laneType;
	}

	public RealtimeResource setLaneType(LaneType laneType) {
		this.laneType = laneType;
		return this;
	}

	public boolean isSetLaneType() {
		return this.laneType != null;
	}

	public long getResourceId() {
		return this.resourceId;
	}

	public RealtimeResource setResourceId(long resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public boolean isSetResourceId() {
		return (resourceId > 0L);
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public RealtimeResource setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public boolean isSetCompanyName() {
		return this.companyName != null;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public RealtimeResource setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public boolean isSetFirstName() {
		return this.firstName != null;
	}

	public String getLastName() {
		return this.lastName;
	}

	public RealtimeResource setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public boolean isSetLastName() {
		return this.lastName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeResource)
			return this.equals((RealtimeResource) that);
		return false;
	}

	private boolean equals(RealtimeResource that) {
		if (that == null)
			return false;

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_distance = true && this.isSetDistance();
		boolean that_present_distance = true && that.isSetDistance();
		if (this_present_distance || that_present_distance) {
			if (!(this_present_distance && that_present_distance))
				return false;
			if (this.distance != that.distance)
				return false;
		}

		boolean this_present_icons = true && this.isSetIcons();
		boolean that_present_icons = true && that.isSetIcons();
		if (this_present_icons || that_present_icons) {
			if (!(this_present_icons && that_present_icons))
				return false;
			if (!this.icons.equals(that.icons))
				return false;
		}

		boolean this_present_address = true && this.isSetAddress();
		boolean that_present_address = true && that.isSetAddress();
		if (this_present_address || that_present_address) {
			if (!(this_present_address && that_present_address))
				return false;
			if (!this.address.equals(that.address))
				return false;
		}

		boolean this_present_location = true && this.isSetLocation();
		boolean that_present_location = true && that.isSetLocation();
		if (this_present_location || that_present_location) {
			if (!(this_present_location && that_present_location))
				return false;
			if (!this.location.equals(that.location))
				return false;
		}

		boolean this_present_averageStarRating = true && this.isSetAverageStarRating();
		boolean that_present_averageStarRating = true && that.isSetAverageStarRating();
		if (this_present_averageStarRating || that_present_averageStarRating) {
			if (!(this_present_averageStarRating && that_present_averageStarRating))
				return false;
			if (this.averageStarRating != that.averageStarRating)
				return false;
		}

		boolean this_present_numberOfRatings = true && this.isSetNumberOfRatings();
		boolean that_present_numberOfRatings = true && that.isSetNumberOfRatings();
		if (this_present_numberOfRatings || that_present_numberOfRatings) {
			if (!(this_present_numberOfRatings && that_present_numberOfRatings))
				return false;
			if (this.numberOfRatings != that.numberOfRatings)
				return false;
		}

		boolean this_present_dateSentOn = true && this.isSetDateSentOn();
		boolean that_present_dateSentOn = true && that.isSetDateSentOn();
		if (this_present_dateSentOn || that_present_dateSentOn) {
			if (!(this_present_dateSentOn && that_present_dateSentOn))
				return false;
			if (this.dateSentOn != that.dateSentOn)
				return false;
		}

		boolean this_present_hoverNotes = true && this.isSetHoverNotes();
		boolean that_present_hoverNotes = true && that.isSetHoverNotes();
		if (this_present_hoverNotes || that_present_hoverNotes) {
			if (!(this_present_hoverNotes && that_present_hoverNotes))
				return false;
			if (!this.hoverNotes.equals(that.hoverNotes))
				return false;
		}

		boolean this_present_workPhoneNumber = true && this.isSetWorkPhoneNumber();
		boolean that_present_workPhoneNumber = true && that.isSetWorkPhoneNumber();
		if (this_present_workPhoneNumber || that_present_workPhoneNumber) {
			if (!(this_present_workPhoneNumber && that_present_workPhoneNumber))
				return false;
			if (!this.workPhoneNumber.equals(that.workPhoneNumber))
				return false;
		}

		boolean this_present_mobilePhoneNumber = true && this.isSetMobilePhoneNumber();
		boolean that_present_mobilePhoneNumber = true && that.isSetMobilePhoneNumber();
		if (this_present_mobilePhoneNumber || that_present_mobilePhoneNumber) {
			if (!(this_present_mobilePhoneNumber && that_present_mobilePhoneNumber))
				return false;
			if (!this.mobilePhoneNumber.equals(that.mobilePhoneNumber))
				return false;
		}

		boolean this_present_email = true && this.isSetEmail();
		boolean that_present_email = true && that.isSetEmail();
		if (this_present_email || that_present_email) {
			if (!(this_present_email && that_present_email))
				return false;
			if (!this.email.equals(that.email))
				return false;
		}

		boolean this_present_laneType = true && this.isSetLaneType();
		boolean that_present_laneType = true && that.isSetLaneType();
		if (this_present_laneType || that_present_laneType) {
			if (!(this_present_laneType && that_present_laneType))
				return false;
			if (!this.laneType.equals(that.laneType))
				return false;
		}

		boolean this_present_resourceId = true && this.isSetResourceId();
		boolean that_present_resourceId = true && that.isSetResourceId();
		if (this_present_resourceId || that_present_resourceId) {
			if (!(this_present_resourceId && that_present_resourceId))
				return false;
			if (this.resourceId != that.resourceId)
				return false;
		}

		boolean this_present_companyName = true && this.isSetCompanyName();
		boolean that_present_companyName = true && that.isSetCompanyName();
		if (this_present_companyName || that_present_companyName) {
			if (!(this_present_companyName && that_present_companyName))
				return false;
			if (!this.companyName.equals(that.companyName))
				return false;
		}

		boolean this_present_firstName = true && this.isSetFirstName();
		boolean that_present_firstName = true && that.isSetFirstName();
		if (this_present_firstName || that_present_firstName) {
			if (!(this_present_firstName && that_present_firstName))
				return false;
			if (!this.firstName.equals(that.firstName))
				return false;
		}

		boolean this_present_lastName = true && this.isSetLastName();
		boolean that_present_lastName = true && that.isSetLastName();
		if (this_present_lastName || that_present_lastName) {
			if (!(this_present_lastName && that_present_lastName))
				return false;
			if (!this.lastName.equals(that.lastName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_distance = true && (isSetDistance());
		builder.append(present_distance);
		if (present_distance)
			builder.append(distance);

		boolean present_icons = true && (isSetIcons());
		builder.append(present_icons);
		if (present_icons)
			builder.append(icons);

		boolean present_address = true && (isSetAddress());
		builder.append(present_address);
		if (present_address)
			builder.append(address);

		boolean present_location = true && (isSetLocation());
		builder.append(present_location);
		if (present_location)
			builder.append(location);

		boolean present_averageStarRating = true && (isSetAverageStarRating());
		builder.append(present_averageStarRating);
		if (present_averageStarRating)
			builder.append(averageStarRating);

		boolean present_numberOfRatings = true && (isSetNumberOfRatings());
		builder.append(present_numberOfRatings);
		if (present_numberOfRatings)
			builder.append(numberOfRatings);

		boolean present_dateSentOn = true && (isSetDateSentOn());
		builder.append(present_dateSentOn);
		if (present_dateSentOn)
			builder.append(dateSentOn);

		boolean present_hoverNotes = true && (isSetHoverNotes());
		builder.append(present_hoverNotes);
		if (present_hoverNotes)
			builder.append(hoverNotes);

		boolean present_workPhoneNumber = true && (isSetWorkPhoneNumber());
		builder.append(present_workPhoneNumber);
		if (present_workPhoneNumber)
			builder.append(workPhoneNumber);

		boolean present_mobilePhoneNumber = true && (isSetMobilePhoneNumber());
		builder.append(present_mobilePhoneNumber);
		if (present_mobilePhoneNumber)
			builder.append(mobilePhoneNumber);

		boolean present_email = true && (isSetEmail());
		builder.append(present_email);
		if (present_email)
			builder.append(email);

		boolean present_laneType = true && (isSetLaneType());
		builder.append(present_laneType);
		if (present_laneType)
			builder.append(laneType);

		boolean present_resourceId = true && (isSetResourceId());
		builder.append(present_resourceId);
		if (present_resourceId)
			builder.append(resourceId);

		boolean present_companyName = true && (isSetCompanyName());
		builder.append(present_companyName);
		if (present_companyName)
			builder.append(companyName);

		boolean present_firstName = true && (isSetFirstName());
		builder.append(present_firstName);
		if (present_firstName)
			builder.append(firstName);

		boolean present_lastName = true && (isSetLastName());
		builder.append(present_lastName);
		if (present_lastName)
			builder.append(lastName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeResource(");
		boolean first = true;

		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (isSetDistance()) {
			if (!first) sb.append(", ");
			sb.append("distance:");
			sb.append(this.distance);
			first = false;
		}
		if (isSetIcons()) {
			if (!first) sb.append(", ");
			sb.append("icons:");
			if (this.icons == null) {
				sb.append("null");
			} else {
				sb.append(this.icons);
			}
			first = false;
		}
		if (isSetAddress()) {
			if (!first) sb.append(", ");
			sb.append("address:");
			if (this.address == null) {
				sb.append("null");
			} else {
				sb.append(this.address);
			}
			first = false;
		}
		if (isSetLocation()) {
			if (!first) sb.append(", ");
			sb.append("location:");
			if (this.location == null) {
				sb.append("null");
			} else {
				sb.append(this.location);
			}
			first = false;
		}
		if (isSetAverageStarRating()) {
			if (!first) sb.append(", ");
			sb.append("averageStarRating:");
			sb.append(this.averageStarRating);
			first = false;
		}
		if (isSetNumberOfRatings()) {
			if (!first) sb.append(", ");
			sb.append("numberOfRatings:");
			sb.append(this.numberOfRatings);
			first = false;
		}
		if (isSetDateSentOn()) {
			if (!first) sb.append(", ");
			sb.append("dateSentOn:");
			sb.append(this.dateSentOn);
			first = false;
		}
		if (isSetHoverNotes()) {
			if (!first) sb.append(", ");
			sb.append("hoverNotes:");
			if (this.hoverNotes == null) {
				sb.append("null");
			} else {
				sb.append(this.hoverNotes);
			}
			first = false;
		}
		if (isSetWorkPhoneNumber()) {
			if (!first) sb.append(", ");
			sb.append("workPhoneNumber:");
			if (this.workPhoneNumber == null) {
				sb.append("null");
			} else {
				sb.append(this.workPhoneNumber);
			}
			first = false;
		}
		if (isSetMobilePhoneNumber()) {
			if (!first) sb.append(", ");
			sb.append("mobilePhoneNumber:");
			if (this.mobilePhoneNumber == null) {
				sb.append("null");
			} else {
				sb.append(this.mobilePhoneNumber);
			}
			first = false;
		}
		if (isSetEmail()) {
			if (!first) sb.append(", ");
			sb.append("email:");
			if (this.email == null) {
				sb.append("null");
			} else {
				sb.append(this.email);
			}
			first = false;
		}
		if (isSetLaneType()) {
			if (!first) sb.append(", ");
			sb.append("laneType:");
			if (this.laneType == null) {
				sb.append("null");
			} else {
				sb.append(this.laneType);
			}
			first = false;
		}
		if (isSetResourceId()) {
			if (!first) sb.append(", ");
			sb.append("resourceId:");
			sb.append(this.resourceId);
			first = false;
		}
		if (isSetCompanyName()) {
			if (!first) sb.append(", ");
			sb.append("companyName:");
			if (this.companyName == null) {
				sb.append("null");
			} else {
				sb.append(this.companyName);
			}
			first = false;
		}
		if (isSetFirstName()) {
			if (!first) sb.append(", ");
			sb.append("firstName:");
			if (this.firstName == null) {
				sb.append("null");
			} else {
				sb.append(this.firstName);
			}
			first = false;
		}
		if (isSetLastName()) {
			if (!first) sb.append(", ");
			sb.append("lastName:");
			if (this.lastName == null) {
				sb.append("null");
			} else {
				sb.append(this.lastName);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
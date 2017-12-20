package com.workmarket.domains.work.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "workResourceTimeTracking")
@Table(name = "work_resource_time_tracking")
@AuditChanges
public class WorkResourceTimeTracking extends DeletableEntity {

	private static final long serialVersionUID = -6157052447453248586L;

	private WorkResource workResource;
	private Calendar checkedInOn;
	private User checkedInBy;
	private Calendar checkedOutOn;
	private User checkedOutBy;
	private Note note;
	private Double latitude_in;
	private Double longitude_in;
	private Double distance_in;
	private Double latitude_out;
	private Double longitude_out;
	private Double distance_out;


	public WorkResourceTimeTracking() {
	}


	public WorkResourceTimeTracking(WorkResource workResource) {
		this.workResource = workResource;
	}


	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "work_resource_id", referencedColumnName = "id")
	public WorkResource getWorkResource() {
		return workResource;
	}


	public void setWorkResource(WorkResource workResource) {
		this.workResource = workResource;
	}


	@Column(name = "checked_in_on")
	public Calendar getCheckedInOn() {
		return checkedInOn;
	}


	public void setCheckedInOn(Calendar checkedInOn) {
		this.checkedInOn = checkedInOn;
	}

	@Transient
	public Boolean isCheckedIn() {
		return (checkedInOn != null);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checked_in_by")
	public User getCheckedInBy() {
		return checkedInBy;
	}


	public void setCheckedInBy(User checkedInBy) {
		this.checkedInBy = checkedInBy;
	}


	@Column(name = "checked_out_on")
	public Calendar getCheckedOutOn() {
		return checkedOutOn;
	}


	public void setCheckedOutOn(Calendar checkedOutOn) {
		this.checkedOutOn = checkedOutOn;
	}

	@Transient
	public Boolean isCheckedOut() {
		return (checkedOutOn != null);
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checked_out_by")
	public User getCheckedOutBy() {
		return checkedOutBy;
	}


	public void setCheckedOutBy(User checkedOutBy) {
		this.checkedOutBy = checkedOutBy;
	}


	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "work_note_id", referencedColumnName = "id")
	public Note getNote() {
		return note;
	}


	public void setNote(Note note) {
		this.note = note;
	}


	@Column(name = "latitude")
	public Double getLatitudeIn() {
		return latitude_in;
	}

	public void setLatitudeIn(Double latitude) {
		this.latitude_in = latitude;
	}


	@Column(name = "longitude")
	public Double getLongitudeIn() {
		return longitude_in;
	}

	public void setLongitudeIn(Double longitude) {
		this.longitude_in = longitude;
	}


	@Column(name = "distance")
	public Double getDistanceIn() {
		return distance_in;
	}

	public void setDistanceIn(Double distance) {
		this.distance_in = distance;
	}

	@Column(name = "latitude_out")
	public Double getLatitudeOut() {
		return latitude_out;
	}

	public void setLatitudeOut(Double latitude) {
		this.latitude_out = latitude;
	}


	@Column(name = "longitude_out")
	public Double getLongitudeOut() {
		return longitude_out;
	}

	public void setLongitudeOut(Double longitude) {
		this.longitude_out = longitude;
	}


	@Column(name = "distance_out")
	public Double getDistanceOut() {
		return distance_out;
	}

	public void setDistanceOut(Double distance) {
		this.distance_out = distance;
	}
}

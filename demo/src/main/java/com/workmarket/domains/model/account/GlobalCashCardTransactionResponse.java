package com.workmarket.domains.model.account;


import java.io.Serializable;

public class GlobalCashCardTransactionResponse {
	/* it must implement serializable otherwise you will get no-args constructor error */
	/* as per this article: http://stackoverflow.com/questions/9621372/xstream-no-args-constructor-error */
	public class GlobalCashCardCardHolderResponse implements Serializable {
		private String keyfield;
		private String cardnumber;
		private String status;
		private String firstname;
		private String lastname;
		private String address;
		private String city;
		private String state;
		private String zipcode;
		private String country;
		private String govid;
		private String govidtype;
		private String goviduser;
		private String govidissuer;
		private String dob;
		private String pinset;

		public String getKeyfield() {
			return keyfield;
		}

		public void setKeyfield(String keyfield) {
			this.keyfield = keyfield;
		}

		public String getCardnumber() {
			return cardnumber;
		}

		public void setCardnumber(String cardnumber) {
			this.cardnumber = cardnumber;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getZipcode() {
			return zipcode;
		}

		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getGovid() {
			return govid;
		}

		public void setGovid(String govid) {
			this.govid = govid;
		}

		public String getGovidtype() {
			return govidtype;
		}

		public void setGovidtype(String govidtype) {
			this.govidtype = govidtype;
		}

		public String getGoviduser() {
			return goviduser;
		}

		public void setGoviduser(String goviduser) {
			this.goviduser = goviduser;
		}


		public String getDob() {
			return dob;
		}

		public void setDob(String dob) {
			this.dob = dob;
		}

		public String getPinset() {
			return pinset;
		}

		public void setPinset(String pinset) {
			this.pinset = pinset;
		}


	}


	private GlobalCashCardCardHolderResponse cardholder;


	private String status;
	private String cardnumber;
	private String firstname;
	private String lastname;
	private String initial;
	private String address;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	private String keyfield;
	private String govid;
	private String govidtype;
	private String govidissuer;
	private String dob;
	private String phone;
	private String email;
	private String tracknum;
	private String responsecode;
	private String custid;

    /* loadCard Related params */

	private String net;
	private String payrollid;
	private String auditno;
	private String description;
	private String transid;

	/* getCardholder related params */
	private String records;

	private String rawResponse;


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCardnumber() {
		return cardnumber;
	}

	public void setCardnumber(String cardnumber) {
		this.cardnumber = cardnumber;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String Initial) {
		this.initial = initial;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getKeyfield() {
		return keyfield;
	}

	public void setKeyfield(String keyfield) {
		this.keyfield = keyfield;
	}

	public String getGovid() {
		return govid;
	}

	public void setGovid(String govid) {
		this.govid = govid;
	}

	public String getGovidtype() {
		return govidtype;
	}

	public void setGovidtype(String govidtype) {
		this.govidtype = govidtype;
	}

	public String getGovidissuer() {
		return govidissuer;
	}

	public void setGovidissuer(String govidissuer) {
		this.govidissuer = govidissuer;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTracknum() {
		return tracknum;
	}

	public void setTracknum(String tracknum) {
		this.tracknum = tracknum;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getPayrollid() {
		return payrollid;
	}

	public void setPayrollid(String payrollid) {
		this.payrollid = payrollid;
	}

	public String getAuditno() {
		return auditno;
	}

	public void setAuditno(String auditno) {
		this.auditno = auditno;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

	public GlobalCashCardCardHolderResponse getCardholder() {
		return cardholder;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}
}

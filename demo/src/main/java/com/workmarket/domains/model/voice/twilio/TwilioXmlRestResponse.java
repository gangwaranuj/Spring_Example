package com.workmarket.domains.model.voice.twilio;

/**
 * Object to serialize Twilio response XML.
 * @see http://www.twilio.com/docs/api/2010-04-01/rest/call
 */
public class TwilioXmlRestResponse {
	public class Call {
		private String Sid;
		private String DateCreated;
		private String DateUpdated;
		private String ParentCallSid;
		private String AccountSid;
		private String To;
		private String ToFormatted;
		private String From;
		private String FromFormatted;
		private String PhoneNumberSid;
		private String Status;
		private String StartTime;
		private String EndTime;
		private String Duration;
		private String Price;
		private String PriceUnit;
		private String Direction;
		private String AnsweredBy;
		private String ApiVersion;
		private String Annotation;
		private String ForwardedFrom;
		private String GroupSid;
		private String CallerName;
		private String Uri;
		private SubresourceURIs SubresourceUris;

		public Call(){

		}
		
		public String getSid() {
			return Sid;
		}
		public void setSid(String Sid) {
			this.Sid = Sid;
		}
		
		public String getDateCreated() {
			return DateCreated;
		}
		public void setDateCreated(String DateCreated) {
			this.DateCreated = DateCreated;
		}
		
		public String getDateUpdated() {
			return DateUpdated;
		}
		public void setDateUpdated(String DateUpdated) {
			this.DateUpdated = DateUpdated;
		}
		
		public String getParentCallSid() {
			return ParentCallSid;
		}
		public void setParentCallSid(String ParentCallSid) {
			this.ParentCallSid = ParentCallSid;
		}
		
		public String getAccountSid() {
			return AccountSid;
		}
		public void setAccountSid(String AccountSid) {
			this.AccountSid = AccountSid;
		}
		
		public String getTo() {
			return To;
		}
		public void setTo(String To) {
			this.To = To;
		}
		
		public String getToFormatted() {
			return ToFormatted;
		}
		public void setToFormatted(String toFormatted) {
			ToFormatted = toFormatted;
		}
		
		public String getFrom() {
			return From;
		}
		public void setFrom(String From) {
			this.From = From;
		}
		
		public String getFromFormatted() {
			return FromFormatted;
		}
		public void setFromFormatted(String fromFormatted) {
			FromFormatted = fromFormatted;
		}
		
		public String getPhoneNumberSid() {
			return PhoneNumberSid;
		}
		public void setPhoneNumberSid(String PhoneNumberSid) {
			this.PhoneNumberSid = PhoneNumberSid;
		}
		
		public String getStatus() {
			return Status;
		}
		public void setStatus(String Status) {
			this.Status = Status;
		}
		
		public String getStartTime() {
			return StartTime;
		}
		public void setStartTime(String StartTime) {
			this.StartTime = StartTime;
		}
		
		public String getEndTime() {
			return EndTime;
		}
		public void setEndTime(String EndTime) {
			this.EndTime = EndTime;
		}
		
		public String getDuration() {
			return Duration;
		}
		public void setDuration(String Duration) {
			this.Duration = Duration;
		}
		
		public String getPrice() {
			return Price;
		}
		public void setPrice(String Price) {
			this.Price = Price;
		}

		public String getPriceUnit() {
			return PriceUnit;
		}

		public void setPriceUnit(String priceUnit) {
			PriceUnit = priceUnit;
		}

		public String getDirection() {
			return Direction;
		}
		public void setDirection(String Direction) {
			this.Direction = Direction;
		}
		
		public String getAnsweredBy() {
			return AnsweredBy;
		}
		public void setAnsweredBy(String AnsweredBy) {
			this.AnsweredBy = AnsweredBy;
		}
		
		public String getApiVersion() {
			return ApiVersion;
		}
		public void setApiVersion(String ApiVersion) {
			this.ApiVersion = ApiVersion;
		}
		
		public String getAnnotation() {
			return Annotation;
		}
		public void setAnnotation(String Annotation) {
			this.Annotation = Annotation;
		}
		
		public String getForwardedFrom() {
			return ForwardedFrom;
		}
		public void setForwardedFrom(String ForwardedFrom) {
			this.ForwardedFrom = ForwardedFrom;
		}
		
		public String getGroupSid() {
			return GroupSid;
		}
		public void setGroupSid(String GroupSid) {
			this.GroupSid = GroupSid;
		}
		
		public String getCallerName() {
			return CallerName;
		}
		public void setCallerName(String CallerName) {
			this.CallerName = CallerName;
		}
		
		public String getUri() {
			return Uri;
		}
		public void setUri(String Uri) {
			this.Uri = Uri;
		}
		
		public SubresourceURIs getSubresourceUris() {
			return SubresourceUris;
		}
		public void setSubresourceUris(SubresourceURIs SubresourceUris) {
			this.SubresourceUris = SubresourceUris;
		}
		
		public class SubresourceURIs {
			private String Notifications;
			private String Recordings;
			
			public String getNotifications() {
				return Notifications;
			}
			public void setNotifications(String Notifications) {
				this.Notifications = Notifications;
			}
			
			public String getRecordings() {
				return Recordings;
			}
			public void setRecordings(String Recordings) {
				this.Recordings = Recordings;
			}
		}
	}
	
	private Call call;
	
	public Call getCall() {
		return call;
	}
	public void setCall(Call call) {
		this.call = call;
	}
}
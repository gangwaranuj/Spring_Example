package com.workmarket.web.forms.webhooks;

import java.io.Serializable;

public class AfterShipWebhookForm implements Serializable {
	private static final long serialVersionUID = 7582823587010349965L;

	private String event;

	private Msg msg;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "AfterShipWebhookForm{" +
			"event='" + event + '\'' +
			", msg=" + msg +
			'}';
	}

	public static class Msg implements Serializable {
		private static final long serialVersionUID = -5068145046151050573L;
		private String tracking_number, tag, slug;

		public String getTracking_number() {
			return tracking_number;
		}

		public void setTracking_number(String tracking_number) {
			this.tracking_number = tracking_number;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getSlug() {
			return slug;
		}

		public void setSlug(String slug) {
			this.slug = slug;
		}

		@Override
		public String toString() {
			return "Msg{" +
				"tracking_number='" + tracking_number + '\'' +
				", tag='" + tag + '\'' +
				", slug='" + slug + '\'' +
				'}';
		}
	}
}

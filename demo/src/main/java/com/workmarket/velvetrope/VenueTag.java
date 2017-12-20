package com.workmarket.velvetrope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagAdapter;

@Component
public class VenueTag extends RequestContextAwareTag {

	@Autowired private AuthenticatedGuestService authenticatedGuestService;

	private Venue venue;
	private boolean bypass = false;
	private RopeTag ropeTag;

	public VenueTag() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	public int doStartTagInternal() throws JspException {
		loadRopeTag();

		if (shouldRender() && shouldEnter()) {
			return render();
		}

		return SKIP_BODY;
	}

	public void setName(Venue venue) {
		this.venue = venue;
	}

	public Venue getVenue() {
		return venue;
	}

	public boolean isBypass() {
		return bypass;
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}

	private int render() {
		ropeTag.setRendered(true);
		return EVAL_BODY_INCLUDE;
	}

	private boolean shouldRender() {
		return !ropeTag.isRendered();
	}

	private boolean shouldEnter() {
		return isBypass() ^ canEnter();
	}

	private boolean canEnter() {
		Guest guest = getGuest();
		return guest != null && guest.canEnter(getVenue());
	}

	private Guest getGuest() {
		return authenticatedGuestService.getGuest();
	}

	private void loadRopeTag() throws JspTagException {
		TagAdapter adapter = (TagAdapter) this.getParent();
		ropeTag = (RopeTag) adapter.getAdaptee();

		if (ropeTag == null) {
			throw new JspTagException("Velvet Rope: A Venue Tag must be nested within a Rope Tag");
		}
	}

}

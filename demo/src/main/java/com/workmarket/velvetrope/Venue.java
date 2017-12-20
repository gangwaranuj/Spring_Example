package com.workmarket.velvetrope;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.zip.CRC32;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties({"providedVenueNames", "providedVenues", "ordinal"})
public enum Venue {
	LOBBY(
		"Lobby",
		"A standard no-op Venue. Useful for Testing.",
		VenueType.SYSTEM
	),

	COMPANY(
		"Company",
		"A venue for features related to companies.",
		VenueType.SYSTEM
	),

	TRANSACTIONAL(
		"Transactional",
		"A Company subscribed to the Transactional Tier. Includes the base Company Tier.",
		VenueType.SYSTEM_PLAN,
		"COMPANY"
	),

	PROFESSIONAL(
		"Professional",
		"A Company subscribed to the Professional Tier. Includes anything available to companies in the Transactional Tier.",
		VenueType.SYSTEM_PLAN,
		"TRANSACTIONAL"
	),

	ENTERPRISE(
		"Enterprise",
		"A Company subscribed to the Enterprise Tier. Includes anything available to companies in the Professional Tier.",
		VenueType.SYSTEM_PLAN,
		"PROFESSIONAL"
	),

	ASSIGNMENTS(
		"Assignments",
		"Provides users with assignment management capabilities."
	),

	EMPLOYEE_WORKER_ROLE(
		"Employee worker",
		"Assign the role to an internal employee to ONLY perform work."
	),

	INTERNAL_NETWORK(
		"Private network employees",
		"Hides the marketplace from a company's employees."
	),

	SALESFORCE_WEBHOOKS(
		"Salesforce Webhooks",
		"Provide access to Salesforce Webhook functionality to allow data to be more easily pushed into Saleforce."
	),

	SHARED_GROUPS(
		"Shared Groups",
		"A venue for features related to groups shared with a Network. Only for companies allowed to share groups."
	),

	SINGLE_SIGN_ON(
		"Single sign on",
		"Provides users with single sign on configuration options."
	),

	OFFLINE_PAY(
		"Offline payment",
		"Allow buyers to create assignments to be paid off platform."
	),

	OFFLINE_PAY_ALL(
		"Pay all assignments off platform",
		"All assignments to be paid off platform."
	),

	PRIVATE_NETWORK(
		"Private network invitations",
		"Hides invited and recruited workers from the marketplace and vice-versa."
	),

	WEBHOOKS(
		"Webhooks",
		"Provide access to Webhook functionality for advanced integrations."
	),

	HIDE_DRUG_TESTS(
		"Hide Drug Tests",
		"Prevents users from being able to order Drug Tests."
	),

	HIDE_BG_CHECKS(
		"Hide Background Checks",
		"Prevents users from being able to order Background Checks."
	),

	HIDE_SCREENINGS(
		"Hide Screening Services",
		"Prevents users from being able to order Screening Services including Drug Tests and Background Checks.",
		"HIDE_DRUG_TESTS",
		"HIDE_BG_CHECKS"
	),

	HIDE_PROF_INSURANCE(
		"Hide Profile Insurance",
		"Hides insurance in user profile settings."
	),

	HIDE_WORKER_SERVICES(
		"Hide Worker Services",
		"Prevents users from being able to view Worker Services."
	),

	HIDE_WORKFEED(
		"Hide WorkFeed",
		"Hides the WorkFeed."
	),

	HIDE_SEARCH_OPT_IN(
		"Hide Worker Search Opt-in",
		"Prevent workers from listing themselves in worker search"
	),

	AVOID_SCHED_CONFLICT(
		"Avoid Schedule Conflicts",
		"Avoid allowing workers to apply for or accept assignments with the same schedule"
	),

	ESIGNATURE(
		"E-Signature",
		"Agreement with vendor must be resolved before enabling this feature for a customer."
	),

	FAST_FUNDS(
		"Fast Funds",
		"Workers for this company's assignments can request Fast Funds."
	),

	MARKETPLACE(
		"Marketplace",
		"Provides employers access to workers in the public marketplace."
	),

	PROFILE_CUSTOM_FIELD(
		"Profile Custom Field",
		"Enables a custom field on the worker profile called &quot;byline&quot; which can only be seen by your own company. Updated via api endpoint."
	);

	public static final long CHECKSUM = getCheckSum();
	private final String displayName;
	private final String description;
	private final Set<String> providedVenueNames = Sets.newHashSet();

	public enum VenueType {
		SYSTEM, // The invisibles, useful in the guts
		SYSTEM_PLAN, // The hardcoded system plans
		INTERNAL_BETA_FEATURE,
		OPEN_SIGNUP_BETA_FEATURE
	}

	private final VenueType venueType;

	Set<Venue> providedVenues;

	Venue(String displayName, String description, VenueType venueType, String... providedVenueNames) {
		this.displayName = displayName;
		this.description = description;
		this.venueType = venueType;
		this.providedVenueNames.addAll(Sets.newHashSet(providedVenueNames));
	}

	Venue(String displayName, String description, String... providedVenueNames) {
		this(displayName, description, null, providedVenueNames);
	}

	public int id() {
		return ordinal() + 1;
	}

	public int mask() {
		int mask = 0;
		for (Venue venue : getProvidedVenues()) {
			mask |= (1 << venue.ordinal());
		}
		return mask;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public VenueType getVenueType() {
		return venueType;
	}

	public boolean isSystemVenue() {
		return venueType == VenueType.SYSTEM;
	}

	public boolean isSystemPlan() {
		return venueType == VenueType.SYSTEM_PLAN;
	}

	public boolean isInternalBetaFeature() {
		return venueType == VenueType.INTERNAL_BETA_FEATURE;
	}

	public boolean isOpenSignUpBetaFeature() {
		return venueType == VenueType.OPEN_SIGNUP_BETA_FEATURE;
	}

	public boolean isFeature() {
		return venueType == null;
	}

	public static Venue[] getBetaFeatureVenueArray() {
		Set<Venue> betaFeatureVenues = getBetaFeatureVenueSet();
		return betaFeatureVenues.toArray(new Venue[betaFeatureVenues.size()]);
	}

	public static Set<Venue> getBetaFeatureVenueSet() {
		Set<VenueType> betaFeatureVenueTypes = ImmutableSet.of(
			VenueType.INTERNAL_BETA_FEATURE,
			VenueType.OPEN_SIGNUP_BETA_FEATURE);

		ImmutableSet.Builder<Venue> venuesBuilder = new ImmutableSet.Builder<>();
		for(VenueType betaFeatureVenueType : betaFeatureVenueTypes) {
			venuesBuilder.addAll(getVenueSetByType(betaFeatureVenueType));
		}
		return venuesBuilder.build();
	}

	public static Set<Venue> getVenueSetByType(VenueType type) {
		ImmutableSet.Builder venues = new ImmutableSet.Builder<>();
		for(Venue venue : Venue.values()) {
			if (venue.getVenueType() == type) {
				venues.add(venue);
			}
		}
		return venues.build();
	}

	public static long getCheckSum() {
		StringBuilder builder = new StringBuilder();
		for (Venue venue : Venue.values()) {
			for (Venue providedVenue : venue.getProvidedVenues()) {
				builder.append(providedVenue.name());
			}
		}

		byte[] bytes = builder.toString().getBytes();
		CRC32 crc = new CRC32();
		crc.update(bytes);

		return crc.getValue();
	}

	public Set<Venue> getProvidedVenues() {
		if (providedVenues == null) {
			providedVenues = Sets.newHashSet();
			loadProvidedVenues(providedVenues, this);
		}

		return providedVenues;
	}

	private void loadProvidedVenues(Set<Venue> providedVenues, Venue venue) {
		// recursive venue graph traversal to collect all provided venues
		providedVenues.add(venue);
		for (String subVenue : venue.providedVenueNames) {
			Venue o = valueOf(subVenue);
			if (!providedVenues.contains(o)) {
				loadProvidedVenues(providedVenues, o);
			}
		}
	}
}

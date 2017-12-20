package com.workmarket.domains.model.postalcode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.StringUtilities.equalsAny;

@Entity(name="country")
@Table(name="country")
public class Country implements Serializable {

	public static final String USA = "USA";
	public static final String US = "US";
	public static final String PR = "PR";
	public static final String PUERTO_RICO = "Puerto Rico";
	public static final String WITHOUTCOUNTRY = "WC";
	public static final String CANADA = "CAN";
	public static final String ISO2_CANADA = "CA";
	public static final String INTERNATIONAL = "ITL";
	public static final Country USA_COUNTRY = newInstance(USA);
	public static final Country CANADA_COUNTRY = newInstance(CANADA);
	public static final Country INTERNATIONAL_COUNTRY = newInstance(INTERNATIONAL);

	public static final List<String> WM_SUPPORTED_COUNTRIES = ImmutableList.of(USA, CANADA);
	public static final List<Country> WM_SUPPORTED_COUNTRIES_OBJECTS = ImmutableList.of(USA_COUNTRY, CANADA_COUNTRY);

	private static final long serialVersionUID = 1L;

	public Country() {}

	public Country(String countryId) {
		this.id = countryId;
	}

	public static Country newInstance(String countryId) {
		return valueOf(countryId);
	}

	private static Country newInstance(CountryMapHelper helper) {
		Country c = new Country(helper.name());
		c.setISO(helper.getIso());
		c.setName(helper.getName());
		c.setISO3(helper.getIso3());
		c.setCcy(helper.getCcy());
		c.setCcyName(helper.getCcyName());
		return c;
	}

	public static Country valueOf(String country) {
		if (StringUtils.isNotBlank(country)) {
			CountryMapHelper c = CountryMapHelper.getCountry(country);
			if (c != null){
				return newInstance(c);
			}
		}
		return newInstance(CountryMapHelper.WC);
	}

	public static boolean isCanada(String postalCode) {
		return postalCode != null && CANADA.equals(getCountry(postalCode));
	}

	public static String getCountry(String postalCode) {
		PostalCode code = PostalCode.fromString(postalCode);
		return (code == null) ? Country.USA : code.getCountry().getId();
	}


	private enum CountryMapHelper {
		USA("United States","US","USA","USD","US Dollar"),
		CAN("Canada","CA","CAN","CAD","Canadian Dollar"),
		AF("Afghanistan","AF","AFG","AFN","Afghani"),
		AL("Albania","AL","ALB","ALL","Lek"),
		DZ("Algeria","DZ","DZA","DZD","Algerian Dinar"),
		AS("American Samoa","AS","ASM","USD","US Dollar"),
		AD("Andorra","AD","AND","EUR","Euro"),
		AO("Angola","AO","AGO","AOA","Kwanza"),
		AI("Anguilla","AI","AIA","XCD","East Caribbean Dollar"),
		AQ("Antarctica","AQ","ATA","",""),
		AG("Antigua and Barbuda","AG","ATG","XCD","East Caribbean Dollar"),
		AR("Argentina","AR","ARG","ARS","Argentine Peso"),
		AM("Armenia","AM","ARM","AMD","Armenian Dram"),
		AW("Aruba","AW","ABW","AWG","Aruban Florin"),
		AU("Australia","AU","AUS","AUD","Australian Dollar"),
		AT("Austria","AT","AUT","EUR","Euro"),
		AZ("Azerbaijan","AZ","AZE","AZN","Azerbaijanian Manat"),
		BS("Bahamas","BS","BHS","BSD","Bahamian Dollar"),
		BH("Bahrain","BH","BHR","BHD","Bahraini Dinar"),
		BD("Bangladesh","BD","BGD","BDT","Taka"),
		BB("Barbados","BB","BRB","BBD","Barbados Dollar"),
		BY("Belarus","BY","BLR","BYR","Belarussian Ruble"),
		BE("Belgium","BE","BEL","EUR","Euro"),
		BZ("Belize","BZ","BLZ","BZD","Belize Dollar"),
		BJ("Benin","BJ","BEN","XOF","CFA Franc BCEAO"),
		BM("Bermuda","BM","BMU","BMD","Bermudian Dollar"),
		BT("Bhutan","BT","BTN","INR","Indian Rupee"),
		BO("Bolivia, Plurinational State of","BO","BOL","BOB","Boliviano"),
		BQ("Bonaire, Sint Eustatius and Saba","BQ","BES","USD","US Dollar"),
		BA("Bosnia and Herzegovina","BA","BIH","BAM","Convertible Mark"),
		BW("Botswana","BW","BWA","BWP","Pula"),
		BV("Bouvet Island","BV","BVT","","578"),
		BR("Brazil","BR","BRA","BRL","Brazilian Real"),
		IO("British Indian Ocean Territory","IO","IOT","","840"),
		BN("Brunei Darussalam","BN","BRN","BND","Brunei Dollar"),
		BG("Bulgaria","BG","BGR","BGN","Bulgarian Lev"),
		BF("Burkina Faso","BF","BFA","XOF","CFA Franc BCEAO"),
		BI("Burundi","BI","BDI","BIF","Burundi Franc"),
		KH("Cambodia","KH","KHM","KHR","Riel"),
		CM("Cameroon","CM","CMR","XAF","CFA Franc BEAC"),
		CV("Cape Verde","CV","CPV","CVE","Cabo Verde Escudo"),
		KY("Cayman Islands","KY","CYM","KYD","Cayman Islands Dollar"),
		CF("Central African Republic","CF","CAF","XAF","CFA Franc BEAC"),
		TD("Chad","TD","TCD","XAF","CFA Franc BEAC"),
		CL("Chile","CL","CHL","CLP","Chilean Peso"),
		CN("China","CN","CHN","CNY","Yuan Renminbi"),
		CX("Christmas Island","CX","CXR","AUD","Australian Dollar"),
		CC("Cocos (Keeling) Islands","CC","CCK","AUD","Australian Dollar"),
		CO("Colombia","CO","COL","COP","Colombian Peso"),
		KM("Comoros","KM","COM","KMF","Comoro Franc"),
		CG("Congo","CG","COG","XAF","CFA Franc BEAC"),
		CD("Congo, the Democratic Republic of the","CD","COD","Yes",""),
		CK("Cook Islands","CK","COK","NZD","New Zealand Dollar"),
		CR("Costa Rica","CR","CRI","CRC","Costa Rican Colon"),
		HR("Croatia","HR","HRV","HRK","Croatian Kuna"),
		CU("Cuba","CU","CUB","CUP","Cuban Peso"),
		CW("Curaçao","CW","CUW","",""),
		CY("Cyprus","CY","CYP","EUR","Euro"),
		CZ("Czech Republic","CZ","CZE","CZK","Czech Koruna"),
		CI("Côte d'Ivoire","CI","CIV","XOF","CFA Franc BCEAO"),
		DK("Denmark","DK","DNK","DKK","Danish Krone"),
		DJ("Djibouti","DJ","DJI","DJF","Djibouti Franc"),
		DM("Dominica","DM","DMA","XCD","East Caribbean Dollar"),
		DO("Dominican Republic","DO","DOM","DOP","Dominican Peso"),
		EC("Ecuador","EC","ECU","USD","US Dollar"),
		EG("Egypt","EG","EGY","EGP","Egyptian Pound"),
		SV("El Salvador","SV","SLV","USD","US Dollar"),
		GQ("Equatorial Guinea","GQ","GNQ","XAF","CFA Franc BEAC"),
		ER("Eritrea","ER","ERI","ERN","Nakfa"),
		EE("Estonia","EE","EST","EUR","Euro"),
		ET("Ethiopia","ET","ETH","ETB","Ethiopian Birr"),
		FK("Falkland Islands (Malvinas)","FK","FLK","FKP","Falkland Islands Pound"),
		FO("Faroe Islands","FO","FRO","DKK","Danish Krone"),
		FJ("Fiji","FJ","FJI","FJD","Fiji Dollar"),
		FI("Finland","FI","FIN","EUR","Euro"),
		FR("France","FR","FRA","EUR","Euro"),
		GF("French Guiana","GF","GUF","EUR","Euro"),
		PF("French Polynesia","PF","PYF","XPF","CFP Franc"),
		TF("French Southern Territories","TF","ATF","","978"),
		GA("Gabon","GA","GAB","XAF","CFA Franc BEAC"),
		GM("Gambia","GM","GMB","GMD","Dalasi"),
		GE("Georgia","GE","GEO","GEL","Lari"),
		DE("Germany","DE","DEU","EUR","Euro"),
		GH("Ghana","GH","GHA","GHS","Ghana Cedi"),
		GI("Gibraltar","GI","GIB","GIP","Gibraltar Pound"),
		GR("Greece","GR","GRC","EUR","Euro"),
		GL("Greenland","GL","GRL","DKK","Danish Krone"),
		GD("Grenada","GD","GRD","XCD","East Caribbean Dollar"),
		GP("Guadeloupe","GP","GLP","EUR","Euro"),
		GU("Guam","GU","GUM","USD","US Dollar"),
		GT("Guatemala","GT","GTM","GTQ","Quetzal"),
		GG("Guernsey","GG","GGY","GBP","Pound Sterling"),
		GN("Guinea","GN","GIN","GNF","Guinea Franc"),
		GW("Guinea-Bissau","GW","GNB","XOF","CFA Franc BCEAO"),
		GY("Guyana","GY","GUY","GYD","Guyana Dollar"),
		HT("Haiti","HT","HTI","USD","US Dollar"),
		HM("Heard Island and McDonald Mcdonald Islands","HM","HMD","","036"),
		VA("Holy See (Vatican City State)","VA","VAT","EUR","Euro"),
		HN("Honduras","HN","HND","HNL","Lempira"),
		HK("Hong Kong","HK","HKG","HKD","Hong Kong Dollar"),
		HU("Hungary","HU","HUN","HUF","Forint"),
		IS("Iceland","IS","ISL","ISK","Iceland Krona"),
		IN("India","IN","IND","INR","Indian Rupee"),
		ID("Indonesia","ID","IDN","IDR","Rupiah"),
		IR("Iran, Islamic Republic of","IR","IRN","IRR","Iranian Rial"),
		IQ("Iraq","IQ","IRQ","IQD","Iraqi Dinar"),
		IE("Ireland","IE","IRL","EUR","Euro"),
		IM("Isle of Man","IM","IMN","GBP","Pound Sterling"),
		IL("Israel","IL","ISR","ILS","New Israeli Sheqel"),
		IT("Italy","IT","ITA","EUR","Euro"),
		JM("Jamaica","JM","JAM","JMD","Jamaican Dollar"),
		JP("Japan","JP","JPN","JPY","Yen"),
		JE("Jersey","JE","JEY","GBP","Pound Sterling"),
		JO("Jordan","JO","JOR","JOD","Jordanian Dinar"),
		KZ("Kazakhstan","KZ","KAZ","KZT","Tenge"),
		KE("Kenya","KE","KEN","KES","Kenyan Shilling"),
		KI("Kiribati","KI","KIR","AUD","Australian Dollar"),
		KP("Korea, Democratic People's Republic of","KP","PRK","KPW","North Korean Won"),
		KR("Korea, Republic of","KR","KOR","KRW","Won"),
		KW("Kuwait","KW","KWT","KWD","Kuwaiti Dinar"),
		KG("Kyrgyzstan","KG","KGZ","KGS","Som"),
		LA("Lao People's Democratic Republic","LA","LAO","LAK","Kip"),
		LV("Latvia","LV","LVA","EUR","Euro"),
		LB("Lebanon","LB","LBN","LBP","Lebanese Pound"),
		LS("Lesotho","LS","LSO","ZAR","Rand"),
		LR("Liberia","LR","LBR","LRD","Liberian Dollar"),
		LY("Libya","LY","LBY","LYD","Libyan Dinar"),
		LI("Liechtenstein","LI","LIE","CHF","Swiss Franc"),
		LT("Lithuania","LT","LTU","EUR","Euro"),
		LU("Luxembourg","LU","LUX","EUR","Euro"),
		MO("Macao","MO","MAC","MOP","Pataca"),
		MK("Macedonia, the Former Yugoslav Republic of","MK","MKD","MKD","Denar"),
		MG("Madagascar","MG","MDG","MGA","Malagasy Ariary"),
		MW("Malawi","MW","MWI","MWK","Kwacha"),
		MY("Malaysia","MY","MYS","MYR","Malaysian Ringgit"),
		MV("Maldives","MV","MDV","MVR","Rufiyaa"),
		ML("Mali","ML","MLI","XOF","CFA Franc BCEAO"),
		MT("Malta","MT","MLT","EUR","Euro"),
		MH("Marshall Islands","MH","MHL","USD","US Dollar"),
		MQ("Martinique","MQ","MTQ","EUR","Euro"),
		MR("Mauritania","MR","MRT","MRO","Ouguiya"),
		MU("Mauritius","MU","MUS","MUR","Mauritius Rupee"),
		YT("Mayotte","YT","MYT","EUR","Euro"),
		MX("Mexico","MX","MEX","MXN","Mexican Peso"),
		FM("Micronesia, Federated States of","FM","FSM","USD","US Dollar"),
		MD("Moldova, Republic of","MD","MDA","MDL","Moldovan Leu"),
		MC("Monaco","MC","MCO","EUR","Euro"),
		MN("Mongolia","MN","MNG","MNT","Tugrik"),
		ME("Montenegro","ME","MNE","EUR","Euro"),
		MS("Montserrat","MS","MSR","XCD","East Caribbean Dollar"),
		MA("Morocco","MA","MAR","MAD","Moroccan Dirham"),
		MZ("Mozambique","MZ","MOZ","MZN","Mozambique Metical"),
		MM("Myanmar","MM","MMR","MMK","Kyat"),
		NA("Namibia","NA","NAM","ZAR","Rand"),
		NR("Nauru","NR","NRU","AUD","Australian Dollar"),
		NP("Nepal","NP","NPL","NPR","Nepalese Rupee"),
		NL("Netherlands","NL","NLD","EUR","Euro"),
		NC("New Caledonia","NC","NCL","XPF","CFP Franc"),
		NZ("New Zealand","NZ","NZL","NZD","New Zealand Dollar"),
		NI("Nicaragua","NI","NIC","NIO","Cordoba Oro"),
		NE("Niger","NE","NER","XOF","CFA Franc BCEAO"),
		NG("Nigeria","NG","NGA","NGN","Naira"),
		NU("Niue","NU","NIU","NZD","New Zealand Dollar"),
		NF("Norfolk Island","NF","NFK","AUD","Australian Dollar"),
		MP("Northern Mariana Islands","MP","MNP","USD","US Dollar"),
		NO("Norway","NO","NOR","NOK","Norwegian Krone"),
		OM("Oman","OM","OMN","OMR","Rial Omani"),
		PK("Pakistan","PK","PAK","PKR","Pakistan Rupee"),
		PW("Palau","PW","PLW","USD","US Dollar"),
		PS("Palestine, State of","PS","PSE","",""),
		PA("Panama","PA","PAN","USD","US Dollar"),
		PG("Papua New Guinea","PG","PNG","PGK","Kina"),
		PY("Paraguay","PY","PRY","PYG","Guarani"),
		PE("Peru","PE","PER","PEN","Nuevo Sol"),
		PH("Philippines","PH","PHL","PHP","Philippine Peso"),
		PN("Pitcairn","PN","PCN","NZD","New Zealand Dollar"),
		PL("Poland","PL","POL","PLN","Zloty"),
		PT("Portugal","PT","PRT","EUR","Euro"),
		PR("Puerto Rico","PR","PRI","USD","US Dollar"),
		QA("Qatar","QA","QAT","QAR","Qatari Rial"),
		RO("Romania","RO","ROU","RON","New Romanian Leu"),
		RU("Russian Federation","RU","RUS","RUB","Russian Ruble"),
		RW("Rwanda","RW","RWA","RWF","Rwanda Franc"),
		RE("Réunion","RE","REU","EUR","Euro"),
		BL("Saint Barthélemy","BL","BLM","","978"),
		SH("Saint Helena, Ascension and Tristan da Cunha","SH","SHN","SHP","Saint Helena Pound"),
		KN("Saint Kitts and Nevis","KN","KNA","XCD","East Caribbean Dollar"),
		LC("Saint Lucia","LC","LCA","XCD","East Caribbean Dollar"),
		MF("Saint Martin (French part)","MF","MAF","","978"),
		PM("Saint Pierre and Miquelon","PM","SPM","EUR","Euro"),
		VC("Saint Vincent and the Grenadines","VC","VCT","XCD","East Caribbean Dollar"),
		WS("Samoa","WS","WSM","WST","Tala"),
		SM("San Marino","SM","SMR","EUR","Euro"),
		ST("Sao Tome and Principe","ST","STP","STD","Dobra"),
		SA("Saudi Arabia","SA","SAU","SAR","Saudi Riyal"),
		SN("Senegal","SN","SEN","XOF","CFA Franc BCEAO"),
		RS("Serbia","RS","SRB","RSD","Serbian Dinar"),
		SC("Seychelles","SC","SYC","SCR","Seychelles Rupee"),
		SL("Sierra Leone","SL","SLE","SLL","Leone"),
		SG("Singapore","SG","SGP","SGD","Singapore Dollar"),
		SX("Sint Maarten (Dutch part)","SX","SXM","",""),
		SK("Slovakia","SK","SVK","EUR","Euro"),
		SI("Slovenia","SI","SVN","EUR","Euro"),
		SB("Solomon Islands","SB","SLB","SBD","Solomon Islands Dollar"),
		SO("Somalia","SO","SOM","SOS","Somali Shilling"),
		ZA("South Africa","ZA","ZAF","ZAR","Rand"),
		GS("South Georgia and the South Sandwich Islands","GS","SGS","",""),
		SS("South Sudan","SS","SSD","Yes",""),
		ES("Spain","ES","ESP","EUR","Euro"),
		LK("Sri Lanka","LK","LKA","LKR","Sri Lanka Rupee"),
		SD("Sudan","SD","SDN","SDG","Sudanese Pound"),
		SR("Suriname","SR","SUR","SRD","Surinam Dollar"),
		SJ("Svalbard and Jan Mayen","SJ","SJM","","578"),
		SZ("Swaziland","SZ","SWZ","SZL","Lilangeni"),
		SE("Sweden","SE","SWE","SEK","Swedish Krona"),
		CH("Switzerland","CH","CHE","CHF","Swiss Franc"),
		SY("Syrian Arab Republic","SY","SYR","SYP","Syrian Pound"),
		TW("Taiwan, Province of China","TW","TWN","TWD","New Taiwan Dollar"),
		TJ("Tajikistan","TJ","TJK","TJS","Somoni"),
		TZ("Tanzania, United Republic of","TZ","TZA","TZS","Tanzanian Shilling"),
		TH("Thailand","TH","THA","THB","Baht"),
		TL("Timor-Leste","TL","TLS","USD","US Dollar"),
		TG("Togo","TG","TGO","XOF","CFA Franc BCEAO"),
		TK("Tokelau","TK","TKL","NZD","New Zealand Dollar"),
		TO("Tonga","TO","TON","TOP","Pa’anga"),
		TT("Trinidad and Tobago","TT","TTO","TTD","Trinidad and Tobago Dollar"),
		TN("Tunisia","TN","TUN","TND","Tunisian Dinar"),
		TR("Turkey","TR","TUR","TRY","Turkish Lira"),
		TM("Turkmenistan","TM","TKM","TMT","Turkmenistan New Manat"),
		TC("Turks and Caicos Islands","TC","TCA","USD","US Dollar"),
		TV("Tuvalu","TV","TUV","AUD","Australian Dollar"),
		UG("Uganda","UG","UGA","UGX","Uganda Shilling"),
		UA("Ukraine","UA","UKR","UAH","Hryvnia"),
		AE("United Arab Emirates","AE","ARE","AED","UAE Dirham"),
		GB("United Kingdom","GB","GBR","GBP","Pound Sterling"),
		UM("United States Minor Outlying Islands","UM","UMI","2","Territories of US"),
		UY("Uruguay","UY","URY","UYU","Peso Uruguayo"),
		UZ("Uzbekistan","UZ","UZB","UZS","Uzbekistan Sum"),
		VU("Vanuatu","VU","VUT","VUV","Vatu"),
		VE("Venezuela, Bolivarian Republic of","VE","VEN","VEF","Bolivar"),
		VN("Viet Nam","VN","VNM","VND","Dong"),
		VG("Virgin Islands, British","VG","VGB","USD","US Dollar"),
		VI("Virgin Islands, U.S.","VI","VIR","USD","US Dollar"),
		WF("Wallis and Futuna","WF","WLF","XPF","CFP Franc"),
		EH("Western Sahara","EH","ESH","MAD","Moroccan Dirham"),
		YE("Yemen","YE","YEM","YER","Yemeni Rial"),
		ZM("Zambia","ZM","ZMB","ZMW","Zambian Kwacha"),
		ZW("Zimbabwe","ZW","ZWE","ZWL","Zimbabwe Dollar"),
		AX("Åland Islands","AX","ALA","EUR","Euro"),
		WC("Without Country","WC", "", "", "");

		private final String name;
		private final String iso;
		private final String iso3;
		private final String ccy;
		private final String ccyName;

		private static final Map<String, CountryMapHelper> lookupByName = Maps.newHashMapWithExpectedSize(CountryMapHelper.values().length);
		private static final Map<String, CountryMapHelper> lookupByIso = Maps.newHashMapWithExpectedSize(CountryMapHelper.values().length);

		static {
			for (CountryMapHelper c : EnumSet.allOf(CountryMapHelper.class)) {
				lookupByName.put(c.getName().toUpperCase(), c);
				lookupByIso.put(c.getIso().toUpperCase(), c);
			}
		}

		CountryMapHelper(String name, String iso, String iso3, String ccy, String ccyName) {
			this.name = name;
			this.iso = iso;
			this.iso3 = iso3;
			this.ccy = ccy;
			this.ccyName = ccyName;
		}

		public String getName() {
			return name;
		}

		public String getIso() {
			return iso;
		}

		public String getIso3() {
			return iso3;
		}

		public String getCcy() {
			return ccy;
		}

		public String getCcyName() {
			return ccyName;
		}

		static CountryMapHelper getCountry(String country) {
			country = country.toUpperCase();
			if (equalsAny(country, USA.toString(), "US")) {
				return CountryMapHelper.USA;
			}
			if (equalsAny(country, CAN.toString(), "CA")) {
				return CountryMapHelper.CAN;
			}
			if (lookupByName.containsKey(country)) {
				return lookupByName.get(country);
			}
			return lookupByIso.get(country);
		}
	}

	private String id;
	private String name;
	private String iso;
	private String iso3;
	private String ccy;
	private String ccyName;

	@Id
	@Column(name = "id", length = 3, nullable = false)
	public String getId() {
		return id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	// @see http://www.iso.org/iso/english_country_names_and_code_elements

	@Column(name = "iso", nullable = false)
	public String getISO() {
		return iso;
	}

	// @see http://data.okfn.org/data/core/country-codes
	// for mapping of country codes to ccy
	@Column(name = "iso3", nullable = true)
	public String getISO3() { return iso3; }

	@Column(name = "ccy", nullable = true)
	public String getCcy() { return ccy; }

	@Column(name = "ccy_name", nullable = true)
	public String getCcyName() { return ccyName; }

	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setISO(String iso) {
		this.iso = iso;
	}
	public void setISO3(String iso3) {
		this.iso3 = iso3;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public void setCcyName(String ccyName) {
		this.ccyName = ccyName;
	}

	@Transient
	public boolean isUSA() {
		return this.getId().equals(USA);
	}

	@Override
	public String toString() {
		return id;
		// @TODO there are places in the code that expect to be able to do a toString
		// on this object and get the id so the below code will break that. That is a bad
		// assumption but I'm not sure how many places that might be so am reverting back
		// to return just the id as that is the safest solution to make sure things don't break.
		// TODO is to fix those places that assume toString is the id
		/*return "Country{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			", iso='" + iso + '\'' +
			", iso3='" + iso3 + '\'' +
			", ccy='" + ccy + '\'' +
			", ccyName='" + ccyName + '\'' +
			'}';*/
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Country)) return false;

		Country country = (Country) o;

		if (!StringUtils.equals(iso, country.iso)) return false;
		if (!StringUtils.equals(name, country.name)) return false;
		if (!StringUtils.equals(iso3, country.iso3)) return false;
		if (!StringUtils.equals(ccy, country.ccy)) return false;
		if (!StringUtils.equals(ccyName, country.ccyName)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + iso.hashCode();
		result = 31 * result + (iso3 != null ? iso3.hashCode() : 0);
		result = 31 * result + (ccy != null ? ccy.hashCode() : 0);
		result = 31 * result + (ccyName != null ? ccyName.hashCode() : 0);
		return result;
	}
}

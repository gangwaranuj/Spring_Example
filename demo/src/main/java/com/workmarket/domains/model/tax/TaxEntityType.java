package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="tax_entity_type")
@Table(name="tax_entity_type")
public class TaxEntityType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String INDIVIDUAL = "individual";			// used by USA and OTHER
	public static final String CORP = "corp";						// used by OTHER and historical data
	public static final String C_CORP = "c_corp";
	public static final String S_CORP = "s_corp";
	public static final String PARTNER = "partner";					// used by USA and OTHER
	public static final String OTHER = "other";
	public static final String LLC_CORPORATION = "llc-corp";		// historical data only
	public static final String LLC_C_CORPORATION = "llc-c-corp";
	public static final String LLC_S_CORPORATION = "llc-s-corp";	// used by USA and OTHER
	public static final String LLC_DISREGARDED = "llc-dis";			// used by USA and OTHER
	public static final String LLC_PARTNERSHIP = "llc-part";
	public static final String TRUST = "trust";
	public static final String EXEMPT = "exempt";					// historical data only
	public static final String NONE = "none";

	public TaxEntityType() {}
	
	public TaxEntityType(String code){
		super(code);
	}

	public TaxEntityType(String code, String description){
        super(code, description);
    }

}

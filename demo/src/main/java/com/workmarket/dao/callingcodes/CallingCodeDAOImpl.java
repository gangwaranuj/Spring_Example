package com.workmarket.dao.callingcodes;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class CallingCodeDAOImpl extends AbstractDAO<CallingCode> implements CallingCodeDAO {
	@Override
	protected Class<CallingCode> getEntityClass() {
		return CallingCode.class;
	}


	@Override
	public List<CallingCode> findAllActiveCallingCodes() {
		return getFactory().getCurrentSession()
				.getNamedQuery("CallingCode.findAllActiveCallingCodes").list();
	}


	@Override
	public CallingCode findCallingCodeById(Long id){
		return (CallingCode) getFactory().getCurrentSession()
				.getNamedQuery("CallingCode.findCallingCodesById")
				.setParameter("id", id).uniqueResult();
	}

	@Override
	public CallingCode findCallingCodeByCallingCodeId(String id) {
		List<CallingCode> result = getFactory().getCurrentSession()
				.getNamedQuery("CallingCode.findCallingCodesByCallingCodesId")
				.setParameter("calling_code_id", id).list();

		return CollectionUtilities.first(result);
	}

	@Override
	public List<String> getAllUniqueActiveCallingCodeIds() {
		return getFactory().getCurrentSession()
				.getNamedQuery("CallingCode.getAllUniqueActiveCallingCodeIds").list();
	}
}
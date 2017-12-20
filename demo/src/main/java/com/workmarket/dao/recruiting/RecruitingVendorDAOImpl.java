package com.workmarket.dao.recruiting;


import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.recruiting.RecruitingVendor;

@Repository
public class RecruitingVendorDAOImpl extends AbstractDAO<RecruitingVendor> implements RecruitingVendorDAO {

	protected Class<RecruitingVendor> getEntityClass() {
        return RecruitingVendor.class;
    }

}

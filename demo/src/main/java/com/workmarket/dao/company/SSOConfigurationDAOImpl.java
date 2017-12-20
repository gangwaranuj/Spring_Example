package com.workmarket.dao.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.company.SSOConfiguration;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class SSOConfigurationDAOImpl extends AbstractDAO<SSOConfiguration> implements SSOConfigurationDAO {

    @Override
    protected Class<?> getEntityClass() {
        return SSOConfiguration.class;
    }
}

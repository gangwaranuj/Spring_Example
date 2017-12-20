package com.workmarket.domains.groups.dao;

import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.dao.company.SSOConfigurationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.company.SSOConfiguration;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SSOConfigurationDAOImplIT extends BaseServiceIT {

    @Autowired private SSOConfigurationDAO ssoConfigurationDAO;
    @Autowired private AclRoleDAO aclRoleDAO;

    @Test
    @Transactional
    public void saveSSOConfiguration() {

        Company company = newCompany();
        AclRole defaultRole = aclRoleDAO.findAllAclRoles().iterator().next();
        SSOConfiguration ssoConfiguration = new SSOConfiguration();
        ssoConfiguration.setCompany(company);
        ssoConfiguration.setDefaultRole(defaultRole);

        ssoConfigurationDAO.saveOrUpdate(ssoConfiguration);

        assertNotNull(ssoConfiguration.getId());
    }

    @Test
    @Transactional
    public void saveAndFetchSSOConfiguration() {

        Company company = newCompany();
        AclRole defaultRole = aclRoleDAO.findAllAclRoles().iterator().next();
        SSOConfiguration ssoConfiguration = new SSOConfiguration();
        ssoConfiguration.setCompany(company);
        ssoConfiguration.setDefaultRole(defaultRole);

        ssoConfigurationDAO.saveOrUpdate(ssoConfiguration);

        assertNotNull(ssoConfiguration.getId());

        SSOConfiguration persistedConfiguration = ssoConfigurationDAO.findBy("company.id", company.getId());

        assertEquals(ssoConfiguration.getDefaultRole().getId(), persistedConfiguration.getDefaultRole().getId());

    }
}

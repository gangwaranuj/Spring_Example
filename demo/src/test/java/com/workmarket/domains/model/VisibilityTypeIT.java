package com.workmarket.domains.model;

import com.google.api.client.util.Lists;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.VisibilityTypeService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VisibilityTypeIT extends BaseServiceIT {

	@Autowired LookupEntityDAO lookupEntityDAO;
	@Autowired VisibilityTypeService visibilityTypeService;

	@Test
	public void isInternal_typeExists() throws Exception {
		VisibilityType type = lookupEntityDAO.findByCode(VisibilityType.class, VisibilityType.INTERNAL);
		assertNotNull(type);
	}

	@Test
	public void isAssignedWorker_typeExists() throws Exception {
		VisibilityType type = lookupEntityDAO.findByCode(VisibilityType.class, VisibilityType.ASSIGNED_WORKER);
		assertNotNull(type);
	}

	@Test
	public void isPublic_typeExists() throws Exception {
		VisibilityType type = lookupEntityDAO.findByCode(VisibilityType.class, VisibilityType.PUBLIC);
		assertNotNull(type);
	}

	@Test
	public void getVisibilitySettings_settingsExist() throws Exception {
		List<VisibilityType> types = visibilityTypeService.getVisibilitySettings();
		assertNotNull(types);

		List<LookupEntity> types2 = Lists.newArrayListWithCapacity(VisibilityType.VISIBILITY_TYPE_CODES.size());
		for (String code : VisibilityType.VISIBILITY_TYPE_CODES) {
			types2.add(lookupEntityDAO.findByCode(VisibilityType.class, code));
		}

		assertTrue(types.containsAll(types2));
	}
}

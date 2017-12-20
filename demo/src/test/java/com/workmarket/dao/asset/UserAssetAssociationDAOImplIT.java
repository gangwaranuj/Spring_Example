package com.workmarket.dao.asset;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserAssetAssociationDAOImplIT extends BaseServiceIT {

	@Autowired UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired AssetManagementService assetManagementService;

	@Test
	@Transactional
	public void findAllActiveUserAssetsByUserAndType_success() throws Exception {
		User user = newWMEmployee();
		AssetDTO assetDTO = newAssetDTO();
		assetDTO.setAssociationType(UserAssetAssociationType.RESUME);

		assetManagementService.storeAssetForUser(assetDTO, user.getId(), true);
		List<UserAssetAssociation> assets = userAssetAssociationDAO.findAllActiveUserAssetsByUserAndType(user.getId(), assetDTO.getAssociationType());
		assertEquals(assets.size(), 1);
	}

}
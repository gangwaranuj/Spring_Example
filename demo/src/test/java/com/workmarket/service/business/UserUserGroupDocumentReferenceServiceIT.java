package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.groups.service.UserGroupBaseIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 12/17/13
 * Time: 9:15 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserUserGroupDocumentReferenceServiceIT extends UserGroupBaseIT {

	@Autowired UserUserGroupDocumentReferenceService userUserGroupDocumentReferenceService;

	User user;

	@Before
	public void before() throws Exception {
		user = newContractorIndependent();
	}

	@Test
	public void findAllDocumentReferencesByUserIdAndUserGroupId_RequirementNotMet_Confirm() throws Exception {
		createGroupWithReqs();

		UserUserGroupDocumentReferencePagination pagination =
			userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(user.getId(), groupWithReqs.getId(), new UserUserGroupDocumentReferencePagination(true));

		assertEquals(0, pagination.getResults().size());
	}

	@Test
	public void findAllDocumentReferencesByUserIdAndUserGroupId_RequirementMet_Confirm() throws Exception {
		createGroupWithReqs();
		saveDocumentReference(null);

		UserUserGroupDocumentReferencePagination pagination =
			userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(user.getId(), groupWithReqs.getId(), new UserUserGroupDocumentReferencePagination(true));

		assertEquals(1, pagination.getResults().size());
	}

	@Test
	public void removeDocumentReference_Succeed() throws Exception {
		createGroupWithReqs();
		saveDocumentReference(null);

		UserUserGroupDocumentReferencePagination pagination = userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(
			user.getId(), groupWithReqs.getId(), new UserUserGroupDocumentReferencePagination(true)
		);
		assertEquals(1, pagination.getResults().size());

		UserUserGroupDocumentReference reference = pagination.getResults().get(0);

		userUserGroupDocumentReferenceService.removeDocumentReference(
			reference.getUser().getId(), reference.getUserGroup().getId(), reference.getRequiredDocument().getId(), reference.getReferencedDocument().getId()
		);

		pagination = userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(
			user.getId(), groupWithReqs.getId(), new UserUserGroupDocumentReferencePagination(true)
		);
		assertEquals(0, pagination.getResults().size());
	}

	@Test
	public void documentExpiration_OneExpired() throws Exception {
		createGroupWithReqs();

		Calendar today = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		saveDocumentReference(sdf.format(today.getTime()));
		saveDocumentReference(sdf.format(tomorrow.getTime()));

		UserUserGroupDocumentReferencePagination pagination = userUserGroupDocumentReferenceService.findAllDocumentReferencesByUserIdAndUserGroupId(
			user.getId(), groupWithReqs.getId(), new UserUserGroupDocumentReferencePagination(true)
		);

		List<UserUserGroupDocumentReference> docs = userUserGroupDocumentReferenceService.findAllDocumentReferencesByDate(today);

		assertEquals(1, countNumberOfExpiredDocsForCurrentUser(docs));
		assertEquals(2, pagination.getResults().size());
	}

	@Test
	public void documentExpiration_NoneExpired() throws Exception {
		Calendar today = Calendar.getInstance();

		List<UserUserGroupDocumentReference> docs = userUserGroupDocumentReferenceService.findAllDocumentReferencesByDate(today);

		assertEquals(0, countNumberOfExpiredDocsForCurrentUser(docs));
	}

	// user is setup in before
	// groupWithReqs is setup in createGroupWithReqs();
	// savedAsset is setup in createGroupWithReqs();
	private void saveDocumentReference(String dateStr) throws Exception {
		AssetDTO dto = newAssetDTO();
		Asset referencedAsset = dto.toAsset();
		assetManagementService.storeAsset(dto, referencedAsset, true);
		dto.setAssetId(referencedAsset.getId());
		assetManagementService.addAssetToUser(dto, user.getId());

		userUserGroupDocumentReferenceService.saveDocumentReference(
				user.getId(),
				groupWithReqs.getId(),
				savedAsset.getId(),
				referencedAsset.getId(),
				dateStr
		);
	}

	private int countNumberOfExpiredDocsForCurrentUser(List<UserUserGroupDocumentReference> docs) {
		int i = 0;
		for (UserUserGroupDocumentReference uugr : docs) {
			if (uugr.getUser().getId().equals(user.getId())) {
				i++;
			}
		}
		return i;
	}
}

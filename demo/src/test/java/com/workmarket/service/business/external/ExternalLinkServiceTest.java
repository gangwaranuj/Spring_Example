package com.workmarket.service.business.external;

import com.workmarket.dao.asset.LinkDAO;
import com.workmarket.dao.asset.UserLinkAssociationDAO;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.service.business.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalLinkServiceTest {

	@InjectMocks ExternalLinkServiceImpl externalLinkService;
	@Mock UserService userService;
	@Mock LinkDAO linkDAO;
	@Mock UserLinkAssociationDAO userLinkAssociationDAO;

	Link link;
	String userNumber;
	User user1;
	UserLinkAssociation userLinkAssociation;

	@Before
	public void setup() {
		link = mock(Link.class);
		userLinkAssociation = mock(UserLinkAssociation.class);
		userNumber = "0000001";
		user1 = mock(User.class);

		when(link.getAvailability()).thenReturn(new AvailabilityType(AvailabilityType.ALL));
		when(link.getName()).thenReturn("Sample");
		when(link.getRemoteUri()).thenReturn("http://www.youtube.com/watch?v=rZBVRw9frhE");
		when(link.getId()).thenReturn(1L);

		when(userLinkAssociation.getDeleted()).thenReturn(false);
		when(userLinkAssociation.getAssetOrder()).thenReturn(0);
		when(userLinkAssociation.getLink()).thenReturn(link);
		when(userLinkAssociation.getUser()).thenReturn(user1);
		when(userLinkAssociation.getAssetOrder()).thenReturn(0);
		when(userLinkAssociation.getId()).thenReturn(1L);

		when(userService.findUserByUserNumber(userNumber)).thenReturn(user1);
		when(userLinkAssociationDAO.get(1L)).thenReturn(userLinkAssociation);
		when(linkDAO.get(1L)).thenReturn(link);
	}

	@Test
	public void test_saveOrUpdateExternalLink() throws Exception {
		externalLinkService.saveOrUpdateExternalLink(link, userNumber, AvailabilityType.ALL ,0);
		Assert.notNull(link.getId());
	}

	@Test
	public void test_saveOrUpdateUserLinkAssociation() throws Exception {
		externalLinkService.saveOrUpdateUserLinkAssociation(userLinkAssociation);
		Assert.notNull(userLinkAssociation.getId());
	}

	@Test
	public void test_saveOrUpdateLink() throws Exception {
		externalLinkService.saveOrUpdateLink(link);
		Assert.notNull(link.getId());
	}


	@Test
	public void findLinkAssociationById() throws Exception {
		UserLinkAssociation association = externalLinkService.findLinkAssociationById(userLinkAssociation.getId());
		Assert.notNull(association);
	}

	@Test
	public void findLinkById() throws Exception {
		Link link = externalLinkService.findLinkById(this.link.getId());
		Assert.notNull(link);
	}
}

package com.workmarket.service.business.external;

import com.workmarket.dao.asset.LinkDAO;
import com.workmarket.dao.asset.UserLinkAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ExternalLinkServiceImpl implements ExternalLinkService {

	@Autowired LinkDAO linkDAO;
	@Autowired UserService userService;
	@Autowired UserLinkAssociationDAO userLinkAssociationDAO;

	@Override
	public void saveOrUpdateExternalLink(Link link, String userNumber, String permission, Integer assetOrder) {
		Assert.notNull(userNumber);
		Assert.notNull(link);
		Assert.notNull(link.getRemoteUri());

		User user = userService.findUserByUserNumber(userNumber);
		Assert.notNull(user);

		linkDAO.saveOrUpdate(link);
		
		Assert.notNull(link.getId());

		UserLinkAssociation userLinkAssociation = new UserLinkAssociation(user, link, assetOrder);

		userLinkAssociationDAO.saveOrUpdate(userLinkAssociation);
	}

	@Override
	public void saveOrUpdateUserLinkAssociation(UserLinkAssociation userLinkAssociation) {
		Assert.notNull(userLinkAssociation);
		userLinkAssociationDAO.saveOrUpdate(userLinkAssociation);
	}

	@Override
	public void saveOrUpdateLink(Link link) {
		Assert.notNull(link);
		linkDAO.saveOrUpdate(link);
	}

	@Override
	public void removeLinkAssociation(Long associationId, String userNumber) {
		Assert.notNull(userNumber);
		Assert.notNull(associationId);

		UserLinkAssociation association = userLinkAssociationDAO.get(associationId);
		Assert.notNull(association);
		userLinkAssociationDAO.delete(association);
	}

	@Override
	public UserLinkAssociation findLinkAssociationById(Long id) {
		Assert.notNull(id);
		UserLinkAssociation association = userLinkAssociationDAO.get(id);

		Assert.notNull(association);
		return association;
	}

	@Override
	public Link findLinkById(Long id) {
		Assert.notNull(id);
		Link link = linkDAO.get(id);

		Assert.notNull(link);
		return link;
	}
}

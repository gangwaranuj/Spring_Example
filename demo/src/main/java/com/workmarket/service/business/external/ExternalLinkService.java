package com.workmarket.service.business.external;

import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.UserLinkAssociation;

public interface ExternalLinkService {

	public void saveOrUpdateExternalLink(Link link, String userNumber, String permission, Integer assetOrder);

	public void saveOrUpdateUserLinkAssociation(UserLinkAssociation userLinkAssociation);

	public void saveOrUpdateLink(Link link);

	public void removeLinkAssociation(Long associationId, String userNumber);

	public UserLinkAssociation findLinkAssociationById(Long id);

	public Link findLinkById(Long id);
}

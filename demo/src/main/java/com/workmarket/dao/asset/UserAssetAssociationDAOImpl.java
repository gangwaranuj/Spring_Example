package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class UserAssetAssociationDAOImpl extends DeletableAbstractDAO<UserAssetAssociation> implements UserAssetAssociationDAO {

	protected Class<UserAssetAssociation> getEntityClass() {
		return UserAssetAssociation.class;
	}

	@Override
	public List<UserAssetAssociation> findAllActiveUserAssetsByUserAndType(Long userId, String assetTypeCode) {
		return findAllActiveUserAssetsByUserAndType(userId, assetTypeCode, ApprovalStatus.APPROVED);
	}

	@SuppressWarnings("unchecked")
	private List<UserAssetAssociation> findAllActiveUserAssetsByUserAndType(Long userId, String assetTypeCode, ApprovalStatus approvalStatus) {

		return getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.byUserStatusAndType")
				.setLong("userId", userId)
				.setInteger("approvalStatus", approvalStatus.ordinal())
				.setString("assetTypeCode", assetTypeCode)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserAssetAssociation> findAllActiveUserAssetsWithAvailabilityByUserAndType(Long userId, String assetTypeCode) {

		return getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.approvedByUserAndTypeWithAvailability")
				.setLong("userId", userId)
				.setString("assetTypeCode", assetTypeCode)
				.list();
	}

	@Override
	public List<UserAssetAssociation> findAllActiveOrderedUserAssetsWithAvailabilityByUserAndType(Long userId, String[] assetTypeCode) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("asset", FetchMode.JOIN)
				.createAlias("asset", "a")
				.setFetchMode("assetType", FetchMode.JOIN)
				.createAlias("assetType", "assetType")
				.setFetchMode("a.availability", FetchMode.JOIN)
				.add(Restrictions.eq("entity.id", userId))
				.add(Restrictions.eq("active", true))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.add(Restrictions.in("assetType.code", assetTypeCode))
				.addOrder(Order.asc("a.order"));


		return criteria.list();
	}

	@Override
	public UserAssetAssociation findUserAssetAssociation(Long userId, Long assetId) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("entity.id", userId))
				.add(Restrictions.eq("asset.id", assetId))
				.setMaxResults(1);

		return (UserAssetAssociation) criteria.uniqueResult();
	}

	@Override
	public Asset findUserAvatarOriginal(Long userId) {
		UserAssetAssociation assetAssociation = (UserAssetAssociation)getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.byUserStatusAndType")
				.setLong("userId", userId)
				.setInteger("approvalStatus", ApprovalStatus.APPROVED.ordinal())
				.setString("assetTypeCode", UserAssetAssociationType.AVATAR)
				.setMaxResults(1).uniqueResult();

		if (assetAssociation != null) {
			return assetAssociation.getAsset();
		}
		return null;
	}

	@Override
	public List<UserAssetAssociation> findUserAssetAssociationAvatarWithAvailability(Long userId) {
		return findAllActiveUserAssetsWithAvailabilityByUserAndType(userId, UserAssetAssociationType.AVATAR);
	}

	@Override
	public UserAssetAssociation findUserAvatars(Long userId) {
		return (UserAssetAssociation)getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.byUserStatusAndType")
				.setLong("userId", userId)
				.setInteger("approvalStatus", ApprovalStatus.APPROVED.ordinal())
				.setString("assetTypeCode", UserAssetAssociationType.AVATAR)
				.setMaxResults(1).uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserAssetAssociation> findUserAvatars(final Collection<Long> userIds) {
		return getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.byUserStatusAndTypeMulti")
				.setParameterList("userIds", userIds)
				.setInteger("approvalStatus", ApprovalStatus.APPROVED.ordinal())
				.setString("assetTypeCode", UserAssetAssociationType.AVATAR)
				.list();
	}

	@Override
	public UserAssetAssociation findPreviousUserAvatars(Long userId) {
		List<UserAssetAssociation> avatars = findAllActiveUserAssetsByUserAndType(userId, UserAssetAssociationType.AVATAR, ApprovalStatus.APPROVED);
		if (avatars.size() > 1) {
			return avatars.get(1);
		}
		return null;
	}

	@Override
	public UserAssetAssociation findBackgroundImage(Long userId) {
		return (UserAssetAssociation)getFactory().getCurrentSession().getNamedQuery("userAssetAssociation.byUserStatusAndType")
				.setLong("userId", userId)
				.setInteger("approvalStatus", ApprovalStatus.APPROVED.ordinal())
				.setString("assetTypeCode", UserAssetAssociationType.BACKGROUND_IMAGE)
				.setMaxResults(1).uniqueResult();
	}
}

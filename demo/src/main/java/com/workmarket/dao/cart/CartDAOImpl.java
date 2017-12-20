package com.workmarket.dao.cart;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.CartDecorator;

@Repository
public class CartDAOImpl extends AbstractDAO<CartDecorator> implements CartDAO {

	@Override
	protected Class<CartDecorator> getEntityClass() {
		return CartDecorator.class;
	}

	@Override
	public CartDecorator getCartByUserNumber(String userNumber) {
		return (CartDecorator) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("userNumber", userNumber))
			.setMaxResults(1)
			.uniqueResult();
	}

}

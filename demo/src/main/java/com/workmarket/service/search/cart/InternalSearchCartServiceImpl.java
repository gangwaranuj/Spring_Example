package com.workmarket.service.search.cart;

import com.google.common.collect.Sets;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.cart.CartDAO;
import com.workmarket.search.model.CartActionData;
import com.workmarket.domains.model.CartDecorator;
import com.workmarket.thrift.search.cart.Cart;
import com.workmarket.thrift.search.cart.CartUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.emptySet;

@Service
public class InternalSearchCartServiceImpl implements InternalSearchCartService {
	
	@Autowired private CartDAO cartDAO;
	@Autowired private UserDAO userDAO;

	@Override
	public CartActionData addUsersToCart(String userId, Collection<String> usersToAdd) {
		CartActionData actionData = new CartActionData();
		CartDecorator cart = getCartByUserNumberCreateNull(userId);
		final Set<CartUser> userSet;
		if (cart.thriftCart().getUsers() == null) {
			userSet = Sets.newHashSetWithExpectedSize(usersToAdd.size());
		} else {
			userSet = Sets.newHashSet(cart.thriftCart().getUsers());
		}

		Collection<String> currentUsersInCart = getCurrentUsersInCart(cart);
		@SuppressWarnings("unchecked")
		Collection<String> usersToGet = CollectionUtils.subtract(usersToAdd, currentUsersInCart);
		Map<String, String> userNames;
		if (usersToGet == null || usersToGet.isEmpty()) {
			userNames = Collections.emptyMap();
		} else {
			userNames = userDAO.findAllUserNamesByUserNumbers(usersToGet);
		}

		for (Entry<String, String> solrUser : userNames.entrySet()) {
			CartUser cartUser = createCartUser(solrUser);
			if (userSet.contains(cartUser)) {
				actionData.addToAlreadyAdded(cartUser.getUserNumber());
			} else {
				cart.thriftCart().addToUsers(cartUser);
				actionData.addToAdded(cartUser.getUserName());
			}
		}
		cartDAO.persist(cart);
		actionData.setCart(cart);
		return actionData;
	}

	private Collection<String> getCurrentUsersInCart(CartDecorator cart) {
		if (cart.thriftCart() == null || !cart.thriftCart().isSetUsers()) {
			return emptySet();
		}
		Set<String> currentUsers = Sets.newHashSetWithExpectedSize(cart.thriftCart().getUsersSize());

		for (CartUser cartUser : cart.thriftCart().getUsers()) {
			currentUsers.add(cartUser.getUserNumber());
		}
		return currentUsers;
	}

	private CartUser createCartUser(Entry<String, String> solrUser) {
		CartUser cartUser = new CartUser();
		cartUser.setUserNumber(solrUser.getKey());
		cartUser.setUserName(solrUser.getValue());
		return cartUser;
	}

	@Override
	public void clearCart(String userId) {
		CartDecorator cart = cartDAO.getCartByUserNumber(userId);
		if (cart == null) {
			// do nothing
			return;
		}
		cart.thriftCart().setUsers(null);
		cartDAO.persist(cart);
	}

	private CartDecorator getCartByUserNumberCreateNull(String userId) {
		CartDecorator cart = cartDAO.getCartByUserNumber(userId);
		if (cart == null) {
			cart = new CartDecorator();
			cart.setUserNumber(userId);
		}
		if (cart.thriftCart() == null) {
			cart.replaceThriftCart(new Cart().setUserNumber(userId));
		}
		return cart;
	}

}

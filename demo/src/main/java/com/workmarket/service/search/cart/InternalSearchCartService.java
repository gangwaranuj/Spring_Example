package com.workmarket.service.search.cart;

import com.workmarket.search.model.CartActionData;

import java.util.Collection;

public interface InternalSearchCartService {

	public CartActionData addUsersToCart(String userId, Collection<String> usersToAdd);

	public void clearCart(String userId);

}

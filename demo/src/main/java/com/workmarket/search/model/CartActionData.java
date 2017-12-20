package com.workmarket.search.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.CartDecorator;
import com.workmarket.thrift.search.cart.SearchCartActionType;
import com.workmarket.thrift.search.cart.SearchCartResponse;
import com.workmarket.thrift.search.cart.SearchCartResult;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CartActionData {

	public CartDecorator cart;
	public Map<SearchCartActionType, Set<String>> userActionResults = Maps.newEnumMap(SearchCartActionType.class);

	public CartDecorator getCart() {
		return cart;
	}

	public void setCart(CartDecorator cart) {
		this.cart = cart;
	}

	public Map<SearchCartActionType, Set<String>> getUserActionResults() {
		return userActionResults;
	}

	public void setUserActionResults(Map<SearchCartActionType, Set<String>> userActionResults) {
		this.userActionResults = userActionResults;
	}

	public void addToRemoved(String userNumber) {
		addToMap(userNumber, SearchCartActionType.REMOVED);
	}

	public void addToAdded(String userNumber) {
		addToMap(userNumber, SearchCartActionType.ADDED);
	}

	public void addToAlreadyAdded(String userNumber) {
		addToMap(userNumber, SearchCartActionType.ALREADY_ADDED);
	}

	public void addToMap(String userNumber, SearchCartActionType actionType) {
		final Set<String> userList;
		if (this.userActionResults.containsValue(actionType)) {
			userList = userActionResults.get(actionType);
		} else {
			userList = Sets.newHashSet();
		}
		userList.add(userNumber);
		userActionResults.put(actionType, userList);
	}

	public SearchCartResponse createSearchCartResponse() {
		SearchCartResponse response = new SearchCartResponse();
		for (Entry<SearchCartActionType, Set<String>> entry : this.userActionResults.entrySet()) {
			SearchCartActionType actionType = entry.getKey();
			for (String userNumber : entry.getValue()) {
				response.addToResult(new SearchCartResult().setActionResult(actionType).setUserNumber(userNumber));
			}
		}
		response.setCurrentCart(this.getCart().thriftCart());
		return response;
	}
}

package com.workmarket.service.thrift.transactional;

import com.google.common.collect.Sets;
import com.workmarket.dao.cart.CartDAO;
import com.workmarket.domains.model.CartDecorator;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.search.model.CartActionData;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.search.cart.InternalSearchCartService;
import com.workmarket.thrift.search.cart.Cart;
import com.workmarket.thrift.search.cart.CartMaxExceededException;
import com.workmarket.thrift.search.cart.CartUser;
import com.workmarket.thrift.search.cart.SearchCart;
import com.workmarket.thrift.search.cart.SearchCartRequest;
import com.workmarket.thrift.search.cart.SearchCartResponse;
import com.workmarket.thrift.search.cart.UserNotFoundException;
import com.workmarket.thrift.work.AddResourcesToWorkResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SearchCartHandler implements SearchCart.Iface {

	private static final Log logger = LogFactory.getLog(SearchCartHandler.class);

	@Autowired private InternalSearchCartService internalSearchCartService;
	@Autowired private CartDAO cartDAO;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private RoutingStrategyService routingStrategyService;


	@Override
	public SearchCartResponse addToCart(SearchCartRequest request) throws CartMaxExceededException {
		logger.debug("adding to cart" + request);
		CartActionData cart = internalSearchCartService.addUsersToCart(request.getCartOwnerNumber(), request.getUserNumbers());
		return cart.createSearchCartResponse();
	}

	@Override
	public void clearCart(String userNumber) {
		logger.debug("clearing cart " + userNumber);
		internalSearchCartService.clearCart(userNumber);
	}


	@Override
	public AddResourcesToWorkResponse pushCartToAssignment(String userNumber, String workNumber) throws UserNotFoundException {
		logger.debug("pushing " + userNumber + "'s cart to assignment number " + workNumber);

		CartDecorator cartDecorator = cartDAO.getCartByUserNumber(userNumber);

		if (cartDecorator == null || cartDecorator.thriftCart() == null) {
			return new AddResourcesToWorkResponse(workNumber);
		}
		Cart cart = cartDecorator.thriftCart();
		if (CollectionUtils.isEmpty(cart.getUsers())) {
			return new AddResourcesToWorkResponse(workNumber);
		}

		WorkRoutingResponseSummary responseSummary;
		try {

			PeopleSearchRoutingStrategy routingStrategy = routingStrategyService.addPeopleSearchRoutingStrategy(workNumber, getUserNumbersFromCart(cart), null, false);
			responseSummary = routingStrategy.getWorkRoutingResponseSummary();
			clearCart(userNumber);
			// For any users that were over the limit, restore them back to the cart
			// and let the user prune the situation at their own discretion.

			if (responseSummary.getResponse().containsKey(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED)) {
				SearchCartRequest addRequest = new SearchCartRequest()
						.setCartOwnerNumber(userNumber)
						.setUserNumbers(Sets.newHashSet(responseSummary.getResponse().get(WorkAuthorizationResponse.MAX_RESOURCES_EXCEEDED)));
				addToCart(addRequest);
			}
		} catch (CartMaxExceededException e) {
			logger.error("cart max exceeded when adding resources " + workNumber, e);
			throw new RuntimeException("cart max exception " + e.getMessage());
		} catch (Exception e) {
			logger.error("general problems when adding resources " + workNumber, e);
			throw new RuntimeException("work request exception " + e.getMessage());
		}

		return new AddResourcesToWorkResponse()
				.setUserMap(responseSummary.getResponse())
				.setWorkNumber(workNumber);
	}

	@Override
	public boolean validateCartForAssign(String userNumber) {
		CartDecorator cartDecorator = cartDAO.getCartByUserNumber(userNumber);
		if (cartDecorator == null || cartDecorator.thriftCart() == null) {
			return false;
		}

		Cart cart = cartDecorator.thriftCart();
		return !(!cart.isSetUsers() || cart.getUsersSize() == 0);
	}

	private Set<String> getUserNumbersFromCart(Cart cart) {
		Set<String> users = Sets.newHashSetWithExpectedSize(cart.getUsersSize());
		for (CartUser user : cart.getUsers()) {
			users.add(user.getUserNumber());
		}
		return users;
	}
}

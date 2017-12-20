package com.workmarket.thrift.search.cart;

public class SearchCart {

	public interface Iface {

		public SearchCartResponse addToCart(SearchCartRequest request) throws CartMaxExceededException, UserNotFoundException;

		public com.workmarket.thrift.work.AddResourcesToWorkResponse pushCartToAssignment(String userNumber, String workNumber) throws UserNotFoundException;

		public void clearCart(String userNumber) throws UserNotFoundException;

		public boolean validateCartForAssign(String userNumber);
	}
}

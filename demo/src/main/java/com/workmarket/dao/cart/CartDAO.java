package com.workmarket.dao.cart;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.CartDecorator;

public interface CartDAO extends DAOInterface<CartDecorator> {

	CartDecorator getCartByUserNumber(String userNumber);

}
package com.workmarket.domains.model;

import com.google.common.collect.Maps;
import com.workmarket.thrift.search.cart.Cart;
import com.workmarket.thrift.search.cart.CartUser;
import com.workmarket.thrift.ThriftUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Cart to be saved in mysql and retrieved.
 *
 * @author krickert
 *
 */
@Entity(name = "thriftCart")
@Table(name = "thrift_cart")
public class CartDecorator {

	private static final Log logger = LogFactory.getLog(CartDecorator.class);

	private long id;
	private String userNumber;
	private Date createdDate = GregorianCalendar.getInstance().getTime();
	private Cart data;
	// transient data
	private Map<String, CartUser> cartData;

	public Cart thriftCart() {
		return data;
	}

	public void replaceThriftCart(Cart data) {
		if (data.getUsers() != null) {
			for (CartUser cartUser : data.getUsers()) {
				if (cartData == null) {
					cartData = Maps.newHashMapWithExpectedSize(data.getUsersSize());
				}
				cartData.put(cartUser.getUserNumber(), cartUser);
			}
		}
		this.data = data;
	}

	public boolean containsUser(String userNumber) {
		if (cartData == null) {
			return false;
		}
		return cartData.containsKey(userNumber);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "user_number", nullable = false, unique = true)
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Column(name = "thrift_cart")
	public byte[] getCart() {
		if (data == null) {
			return null;
		}
		try {
			return ThriftUtilities.serialize(data);
		} catch (Exception e) {
			return null;
		}
	}

	public void setCart(byte[] cartBytes) {
		if (cartBytes == null || cartBytes.length == 0) {
			return;
		}
		try {
			this.data = ThriftUtilities.deserialize(cartBytes, Cart.class);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((userNumber == null) ? 0 : userNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartDecorator other = (CartDecorator) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (id != other.id)
			return false;
		if (userNumber == null) {
			if (other.userNumber != null)
				return false;
		} else if (!userNumber.equals(other.userNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CartDecorator [id=");
		builder.append(id);
		builder.append(", userNumber=");
		builder.append(userNumber);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}

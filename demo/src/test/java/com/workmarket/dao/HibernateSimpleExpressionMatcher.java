package com.workmarket.dao;

import org.hibernate.criterion.SimpleExpression;
import org.mockito.ArgumentMatcher;

/**
 * Created by joshlevine on 5/1/17.
 */
public class HibernateSimpleExpressionMatcher extends ArgumentMatcher<SimpleExpression> {
  private SimpleExpression simpleExpression;

  public HibernateSimpleExpressionMatcher(SimpleExpression simpleExpression) {
    this.simpleExpression = simpleExpression;
  }

  @Override
  public boolean matches(Object argument) {
    if(argument instanceof SimpleExpression) {
      SimpleExpression otherSimpleExpression = (SimpleExpression) argument;
      return this.simpleExpression.toString().equals(otherSimpleExpression.toString());
    }
    return false;
  }
}
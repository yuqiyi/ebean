package org.tests.compositekeys.db;

import javax.persistence.Embeddable;

@Embeddable
public class CaoKey {
  private int customer;

  private int type;

  public int getCustomer() {
    return customer;
  }

  public void setCustomer(int customer) {
    this.customer = customer;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    int hc = 31 * 7 + customer;
    hc = 31 * hc + type;
    return hc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof CaoKey) {
      CaoKey k = (CaoKey) o;
      return k.customer == customer && k.type == type;
    }
    return false;
  }
}

package org.tests.inheritance.bothsides;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public abstract class SourceBase extends WithAutoGeneratedUUID {

  private String name;

  private int pos;

  public SourceBase(String name, int pos) {
    this.name = name;
    this.pos = pos;
  }

  public String getName() {
    return name;
  }

  public int getPos() {
    return pos;
  }
}

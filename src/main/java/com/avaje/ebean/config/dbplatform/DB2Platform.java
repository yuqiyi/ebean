package com.avaje.ebean.config.dbplatform;

import java.sql.Types;

import javax.sql.DataSource;

import com.avaje.ebean.BackgroundExecutor;

/**
 * DB2 specific platform.
 */
public class DB2Platform extends DatabasePlatform {

  public DB2Platform() {
    super();
    this.name = "db2";

    // only support getGeneratedKeys with non-batch JDBC
    // so generally use SEQUENCE instead for H2
    this.sqlLimiter = new Db2SqlLimiter();
    this.dbIdentity.setSupportsGetGeneratedKeys(true);
    this.dbIdentity.setIdType(IdType.IDENTITY);
    this.dbIdentity.setSupportsSequence(true);

    this.openQuote = "\"";
    this.closeQuote = "\"";

    booleanDbType = Types.INTEGER;
    dbTypeMap.put(Types.BOOLEAN, new DbType("smallint default 0"));
    dbTypeMap.put(Types.INTEGER, new DbType("integer"));
    dbTypeMap.put(Types.BIGINT, new DbType("bigint"));
    dbTypeMap.put(Types.REAL, new DbType("float"));
    dbTypeMap.put(Types.DOUBLE, new DbType("float"));
    dbTypeMap.put(Types.SMALLINT, new DbType("smallint"));
    dbTypeMap.put(Types.TINYINT, new DbType("smallint"));
    dbTypeMap.put(Types.DECIMAL, new DbType("decimal", 15));

    this.dbDdlSyntax.setIdentity("generated by default as identity");

    // this.dbDdlSyntax.setDropIfExists("if exists");
    // this.dbDdlSyntax.setDisableReferentialIntegrity("SET REFERENTIAL_INTEGRITY FALSE");
    // this.dbDdlSyntax.setEnableReferentialIntegrity("SET REFERENTIAL_INTEGRITY TRUE");
    // this.dbDdlSyntax.setForeignKeySuffix("on delete restrict on update restrict");
  }

  /**
   * Return a DB2 specific sequence IdGenerator that supports batch fetching
   * sequence values.
   */
  @Override
  public IdGenerator createSequenceIdGenerator(BackgroundExecutor be,
      DataSource ds, String seqName, int batchSize) {

    return new DB2SequenceIdGenerator(be, ds, seqName, batchSize);
  }

}

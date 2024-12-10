package org.example;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import java.util.function.Supplier;

public class JdbiTransactionManager implements TransactionManager {

  private final Jdbi jdbi;

  public JdbiTransactionManager(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public <R> R inTransaction(TransactionIsolationLevel level, Supplier<R> supplier) {
    return jdbi.inTransaction(level, (Handle handle) -> supplier.get());
  }

  @Override
  public void useTransaction(TransactionIsolationLevel level, Runnable runnable) {
    jdbi.useTransaction(level, (Handle handle) -> runnable.run());
  }
}

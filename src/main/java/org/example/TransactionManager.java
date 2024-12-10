package org.example;

import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import java.util.function.Supplier;

public interface TransactionManager {

  <R> R inTransaction(TransactionIsolationLevel level, Supplier<R> supplier);

  void useTransaction(TransactionIsolationLevel level, Runnable runnable);
}

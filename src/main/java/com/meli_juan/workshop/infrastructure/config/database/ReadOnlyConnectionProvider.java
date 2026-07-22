package com.meli_juan.workshop.infrastructure.config.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Component
public class ReadOnlyConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ReadOnlyConnectionProvider.class);
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000;

    private final DataSource readOnlyDataSource;
    private final TaskScheduler taskScheduler;

    public ReadOnlyConnectionProvider(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource,
                                      @Qualifier("readOnlyRetryScheduler") TaskScheduler taskScheduler) {
        this.readOnlyDataSource = readOnlyDataSource;
        this.taskScheduler = taskScheduler;
    }

    @Async
    public CompletableFuture<Connection> getConnectionAsync() {
        CompletableFuture<Connection> future = new CompletableFuture<>();
        attemptConnection(1, future);
        return future;
    }

    private void attemptConnection(int attempt, CompletableFuture<Connection> future) {
        try {
            Connection connection = readOnlyDataSource.getConnection();
            connection.setReadOnly(true);
            log.info("Read-only database connection established successfully on attempt {}", attempt);
            future.complete(connection);
        } catch (SQLException e) {
            log.warn("Read-only connection attempt {}/{} failed: {}", attempt, MAX_RETRIES, e.getMessage());

            if (attempt < MAX_RETRIES) {
                long delay = INITIAL_DELAY_MS * (long) Math.pow(2, attempt - 1);
                log.info("Retrying read-only connection in {} ms...", delay);
                taskScheduler.schedule(() -> attemptConnection(attempt + 1, future), Instant.now().plusMillis(delay));
            } else {
                log.error("Failed to establish read-only database connection after {} attempts", MAX_RETRIES);
                future.completeExceptionally(e);
            }
        }
    }
}

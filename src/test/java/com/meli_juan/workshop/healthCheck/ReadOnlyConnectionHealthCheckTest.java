package com.meli_juan.workshop.healthCheck;

import com.meli_juan.workshop.infrastructure.config.database.ReadOnlyConnectionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadOnlyConnectionHealthCheckTest {

    private static final int FUTURE_TIMEOUT_SECONDS = 5;
    private static final int RETRY_FUTURE_TIMEOUT_SECONDS = 15;
    private static final int EXHAUSTED_RETRIES_FUTURE_TIMEOUT_SECONDS = 30;
    private static final int SUCCESSFUL_ATTEMPT_NUMBER = 3;

    private final ThreadPoolTaskScheduler taskScheduler = createTaskScheduler();

    private static ThreadPoolTaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("test-readonly-retry-");
        scheduler.initialize();
        return scheduler;
    }

    @AfterEach
    void tearDown() {
        taskScheduler.shutdown();
    }

    @Test
    @DisplayName("Should establish read-only connection successfully on first attempt")
    void getConnectionAsync_healthyDataSource_returnsConnection() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DataSource mockDataSource = mock(DataSource.class);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource, taskScheduler);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertNotNull(result);
        verify(mockConnection).setReadOnly(true);
        verify(mockDataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("Should return a connection that is set to read-only mode")
    void getConnectionAsync_healthyDataSource_connectionIsReadOnly() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isReadOnly()).thenReturn(true);
        DataSource mockDataSource = mock(DataSource.class);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource, taskScheduler);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        verify(mockConnection).setReadOnly(true);
        assertTrue(result.isReadOnly());
    }

    @Test
    @DisplayName("Should retry and succeed after transient failures")
    void getConnectionAsync_transientFailures_retriesAndSucceeds() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DataSource mockDataSource = mock(DataSource.class);
        AtomicInteger callCount = new AtomicInteger(0);

        when(mockDataSource.getConnection()).thenAnswer(invocation -> {
            int attempt = callCount.incrementAndGet();
            if (attempt < SUCCESSFUL_ATTEMPT_NUMBER) {
                throw new SQLException("Connection refused (attempt " + attempt + ")");
            }
            return mockConnection;
        });

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource, taskScheduler);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(RETRY_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertNotNull(result);
        verify(mockConnection).setReadOnly(true);
        assertEquals(SUCCESSFUL_ATTEMPT_NUMBER, callCount.get());
    }

    @Test
    @DisplayName("Should fail with exception after exhausting all retry attempts")
    void getConnectionAsync_allAttemptsFail_completesExceptionally() {
        DataSource mockDataSource = mock(DataSource.class);

        try {
            when(mockDataSource.getConnection())
                    .thenThrow(new SQLException("Connection refused"));
        } catch (SQLException e) {
            fail("Mock setup should not throw");
        }

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource, taskScheduler);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        ExecutionException exception = assertThrows(ExecutionException.class,
                () -> future.get(EXHAUSTED_RETRIES_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        assertInstanceOf(SQLException.class, exception.getCause());
    }
}

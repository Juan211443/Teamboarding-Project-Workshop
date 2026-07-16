package com.meli_juan.workshop.healthCheck;

import com.meli_juan.workshop.infrastructure.config.database.ReadOnlyConnectionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Should establish read-only connection successfully on first attempt")
    void getConnectionAsync_healthyDataSource_returnsConnection() throws Exception {
        Connection mockConnection = mock(Connection.class);
        DataSource mockDataSource = mock(DataSource.class);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(5, TimeUnit.SECONDS);

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

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(5, TimeUnit.SECONDS);

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
            if (attempt < 3) {
                throw new SQLException("Connection refused (attempt " + attempt + ")");
            }
            return mockConnection;
        });

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        Connection result = future.get(15, TimeUnit.SECONDS);

        assertNotNull(result);
        verify(mockConnection).setReadOnly(true);
        assertEquals(3, callCount.get());
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

        ReadOnlyConnectionProvider provider = new ReadOnlyConnectionProvider(mockDataSource);
        CompletableFuture<Connection> future = provider.getConnectionAsync();

        ExecutionException exception = assertThrows(ExecutionException.class,
                () -> future.get(30, TimeUnit.SECONDS));

        assertInstanceOf(SQLException.class, exception.getCause());
    }
}

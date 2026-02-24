package me.hektortm.wosCore.database;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDAO {
    List<TableSchema> getTableSchemas(); // Return all tables for this DAO

    default void initialize(DatabaseManager db) throws SQLException {
        try (Connection conn = db.getConnection()) {
            for (TableSchema schema : getTableSchemas()) {
                // Create table if missing
                try (var stmt = conn.createStatement()) {
                    stmt.execute(schema.createTableSQL());
                }
                // Auto-migrate missing columns
                schema.migrate(conn);
            }
        }
    }
}
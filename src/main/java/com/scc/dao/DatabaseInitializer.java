package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Makes sure all tables exist before the menu starts.
 * Tables are created in order so every foreign key points to an existing table.
 */
public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    /** Returns true if the database is ready, false if setup failed. */
    public static boolean initialize() {
        String[] tables = {
                """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles    TEXT NOT NULL,
                    status   TEXT NOT NULL DEFAULT 'PENDING'
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS motorcycles (
                    motorcycle_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plate_number  TEXT NOT NULL UNIQUE,
                    brand         TEXT NOT NULL,
                    model         TEXT NOT NULL,
                    engine_cc     INTEGER,
                    type          TEXT,
                    color         TEXT,
                    year          INTEGER,
                    daily_rate    REAL NOT NULL,
                    status        TEXT NOT NULL DEFAULT 'AVAILABLE',
                    is_active     INTEGER NOT NULL DEFAULT 1
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name      TEXT NOT NULL,
                    last_name       TEXT NOT NULL,
                    contact_number  TEXT,
                    address         TEXT,
                    license_number  TEXT NOT NULL UNIQUE,
                    license_expiry  TIMESTAMP,
                    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_active       INTEGER NOT NULL DEFAULT 1
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS rentals (
                    rental_id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id          INTEGER NOT NULL,
                    motorcycle_id        INTEGER NOT NULL,
                    user_id              INTEGER NOT NULL,
                    rental_date          TIMESTAMP NOT NULL,
                    expected_return_date TIMESTAMP NOT NULL,
                    actual_return_date   TIMESTAMP,
                    daily_rate           REAL NOT NULL,
                    total_amount         REAL NOT NULL,
                    status               TEXT NOT NULL DEFAULT 'RESERVED',
                    FOREIGN KEY (customer_id)   REFERENCES customers(customer_id),
                    FOREIGN KEY (motorcycle_id) REFERENCES motorcycles(motorcycle_id),
                    FOREIGN KEY (user_id)       REFERENCES users(id)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS payments (
                    payment_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                    rental_id      INTEGER NOT NULL,
                    user_id        INTEGER NOT NULL,
                    amount         REAL NOT NULL,
                    payment_type   TEXT NOT NULL,
                    payment_method TEXT,
                    payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id),
                    FOREIGN KEY (user_id)   REFERENCES users(id)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id        INTEGER NOT NULL,
                    action         TEXT NOT NULL,
                    table_affected TEXT,
                    description    TEXT,
                    log_timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """
        };

        // DatabaseConnection.getConnection() already turns PRAGMA foreign_keys ON.
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : tables) {
                stmt.execute(sql);
            }
            migrateOldStatus(conn);
            seedDefaultAdmin(conn);
            return true;
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
            return false;
        }
    }

    /** The status used to be 'ACTIVE'. It is now 'APPROVED', so old rows are converted once. */
    private static void migrateOldStatus(Connection conn) throws SQLException {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "ACTIVE");
            ps.executeUpdate();
        }
    }

    /** First run only: creates an APPROVED admin so someone can approve new accounts. */
    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, 'ADMIN', 'APPROVED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.executeUpdate();
        }
        System.out.println("First run: default admin created (username: admin, password: admin123)");
    }
}
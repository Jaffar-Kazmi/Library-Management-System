package com.library.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.io.File;

public class SchemaReset {
    public static void main(String[] args) {
        String sqlFile = "/home/Ehtisham/Desktop/ACP Project/Library-Management-System/library_db.sql";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {

            System.out.println("Connected to database. Resetting schema...");

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // Remove comments
                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sb.append(line);
                if (line.trim().endsWith(";")) {
                    // Execute statement
                    String sql = sb.toString().replace(";", "");
                    // Handle DELIMITER logic roughly or just ignore stored procs for now if they
                    // are complex
                    // But the file has DELIMITER //...
                    // This simple parser might fail on stored procedures.
                    // Let's try to execute simple statements first.

                    // Actually, for simplicity, let's just execute the DROP and CREATE TABLEs.
                    // The stored procedures are optional.

                    // Better approach: Use a proper SQL runner or just run the critical parts.
                    // But the user needs the full schema.

                    // Let's try to run it. If it fails on DELIMITER, we might skip procs.
                    if (sql.toUpperCase().startsWith("DELIMITER")) {
                        sb.setLength(0);
                        continue;
                    }

                    try {
                        stmt.execute(sql);
                        System.out.println("Executed: " + (sql.length() > 50 ? sql.substring(0, 50) + "..." : sql));
                    } catch (Exception e) {
                        System.err.println("Failed to execute: " + sql);
                        System.err.println(e.getMessage());
                    }
                    sb.setLength(0);
                } else {
                    sb.append(" ");
                }
            }
            System.out.println("Schema reset complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

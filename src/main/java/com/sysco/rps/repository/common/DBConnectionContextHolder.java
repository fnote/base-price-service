package com.sysco.rps.repository.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps the ThreadLocal config of the database in action
 *
 * @author Rohana Kumara
 * @tag Copyright (C) 2019 SYSCO Corp. All Rights Reserved.
 */
public class DBConnectionContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    private static Set<String> enabledDatabases = new HashSet<>();

    /**
     * Set bunit as the db identifier.
     *
     * @param dbName: DB Name
     * @return boolean whether db identifier set successfully
     */
    public static boolean setBusinessUnitDatabaseInstance(String dbName) {

        if (dbName != null && DBConnectionContextHolder.enabledDatabases != null &&
              DBConnectionContextHolder.enabledDatabases.contains(dbName)) {
            contextHolder.set(dbName);
            return true;
        } else {
            return false;
        }
    }

    public static void enableDatabase(String database) {
        DBConnectionContextHolder.enabledDatabases.add(database);
    }

    public static void disableDatabase(String database) {
        DBConnectionContextHolder.enabledDatabases.remove(database);
    }

    public static String getBusinessUnitDatabase() {
        return contextHolder.get();
    }

    public static Set<String> getEnabledDatabases() {
        return enabledDatabases;
    }

    public static void setEnabledDatabases(Set<String> enabledDatabases) {
        DBConnectionContextHolder.enabledDatabases = enabledDatabases;
    }
}

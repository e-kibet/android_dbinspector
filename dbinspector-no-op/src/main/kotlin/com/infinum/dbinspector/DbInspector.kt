@file:Suppress("UNUSED_PARAMETER")

package com.infinum.dbinspector

import com.infinum.dbinspector.logger.Logger

/**
 * _DbInspector_ provides a simple way to view the contents of the in-app database for debugging purposes.
 * There is no need to pull the database from a connected device.
 * This library also supports inspecting of the SQLite databases created by CouchBase Lite out of the box.
 * With this library you can:
 * - preview all application sandbox databases
 * - import single or multiple databases at once
 * - search, delete, rename, copy, share a database
 * - preview tables, views and triggers
 * - preview table or view pragma
 * - delete table contents
 * - drop view or trigger
 * - search table, view or trigger
 * - sort table, view or trigger per column
 * - execute any valid SQL command in editor per database connection
 */
@Suppress("UnusedPrivateMember")
public object DbInspector {

    /**
     * Show a list of databases.
     *
     * @param[logger] Optional logger implementation of [Logger][com.infinum.dbinspector.logger.Logger] interface.
     */
    @JvmStatic
    @JvmOverloads
    public fun show(logger: Logger? = null): Unit = Unit
}

/*
 * This file is generated by jOOQ.
 */
package com.matejuh.demo.generated.tables


import com.matejuh.demo.generated.Public
import com.matejuh.demo.generated.keys.OBJECTIVE_THEMES_PKEY

import java.time.LocalDateTime
import java.util.UUID

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.Name
import org.jooq.Record
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class ObjectiveThemes(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, Record>?,
    aliased: Table<Record>?,
    parameters: Array<Field<*>?>?
): TableImpl<Record>(
    alias,
    Public.PUBLIC,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>public.objective_themes</code>
         */
        val OBJECTIVE_THEMES: ObjectiveThemes = ObjectiveThemes()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>public.objective_themes.id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("id"), SQLDataType.UUID.nullable(false).defaultValue(DSL.field("gen_random_uuid()", SQLDataType.UUID)), this, "")

    /**
     * The column <code>public.objective_themes.space_id</code>.
     */
    val SPACE_ID: TableField<Record, Int?> = createField(DSL.name("space_id"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>public.objective_themes.human_id</code>.
     */
    val HUMAN_ID: TableField<Record, Int?> = createField(DSL.name("human_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "")

    /**
     * The column <code>public.objective_themes.name</code>.
     */
    val NAME: TableField<Record, String?> = createField(DSL.name("name"), SQLDataType.VARCHAR.nullable(false), this, "")

    /**
     * The column <code>public.objective_themes.color_code</code>.
     */
    val COLOR_CODE: TableField<Record, String?> = createField(DSL.name("color_code"), SQLDataType.VARCHAR.nullable(false), this, "")

    /**
     * The column <code>public.objective_themes.created_at</code>.
     */
    val CREATED_AT: TableField<Record, LocalDateTime?> = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>public.objective_themes.updated_at</code>.
     */
    val UPDATED_AT: TableField<Record, LocalDateTime?> = createField(DSL.name("updated_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "")

    private constructor(alias: Name, aliased: Table<Record>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>public.objective_themes</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.objective_themes</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.objective_themes</code> table reference
     */
    constructor(): this(DSL.name("objective_themes"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, Record>): this(Internal.createPathAlias(child, key), child, key, OBJECTIVE_THEMES, null)
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<Record, Int?> = super.getIdentity() as Identity<Record, Int?>
    override fun getPrimaryKey(): UniqueKey<Record> = OBJECTIVE_THEMES_PKEY
    override fun `as`(alias: String): ObjectiveThemes = ObjectiveThemes(DSL.name(alias), this)
    override fun `as`(alias: Name): ObjectiveThemes = ObjectiveThemes(alias, this)
    override fun `as`(alias: Table<*>): ObjectiveThemes = ObjectiveThemes(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): ObjectiveThemes = ObjectiveThemes(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): ObjectiveThemes = ObjectiveThemes(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): ObjectiveThemes = ObjectiveThemes(name.getQualifiedName(), null)
}

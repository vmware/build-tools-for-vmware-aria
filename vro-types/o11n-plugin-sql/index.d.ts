/**
 * Provides CRUD operations for database objects.
 */
declare class SQLDatabaseManager {
	/**
	 * Adds a database object to the plug-in's inventory.
	 * @param item
	 */
	static addDatabase(item: SQLDatabase): SQLDatabase;
	/**
	 * Updates the specified database in the plug-in's inventory.
	 * @param item
	 */
	static updateDatabase(item: SQLDatabase): SQLDatabase;
	/**
	 * Removes a database object from the plug-in's inventory.
	 * @param item
	 */
	static removeDatabase(item: SQLDatabase): SQLDatabase;
	/**
	 * Returns the list of database objects in the plug-in's inventory.
	 */
	static getDatabases(): SQLDatabase[];
	/**
	 * Returns the database object from the plug-in's inventory with a specified name.
	 * @param name
	 * @deprecated removed in vRA 7.6 and above / Aria Automation 8. Use getDatabaseById and getDatabases instead.
	 */
	static getDatabase(name: string): SQLDatabase;
	/**
	 * Returns the database object from the plug-in's inventory with a specified ID.
	 * @param id
	 */
	static getDatabaseById(id: string): SQLDatabase;
	/**
	 * Validates the created databse object if it is able to connect to the real database.
	 * @param database
	 */
	static validateDatabase(database: SQLDatabase): void;
	/**
	 * Returns a list of supported database types.
	 */
	static getSupportedDatabaseTypes(): string[];
	/**
	 * Adds new tables to a database.
	 * @param database
	 * @param tableNames
	 */
	static addTablesToDatabase(database: SQLDatabase, tableNames: string[]): SQLTable[];
	/**
	 * Removes a specified table from a database.
	 * @param table
	 */
	static removeTableFromDatabase(table: SQLTable): SQLTable;
	/**
	 * Generates a forkflow for creation of database record.
	 * @param table
	 * @param category
	 * @param overwriteWorkflow
	 * @param readOnlyColumns
	 */
	static generateCreateWorkflow(table: SQLTable, category: any, overwriteWorkflow: boolean, readOnlyColumns: SQLColumn[]): any;
	/**
	 * Generates a forkflow that reads table records.
	 * @param table
	 * @param category
	 * @param overwriteWorkflow
	 */
	static generateReadWorkflow(table: SQLTable, category: any, overwriteWorkflow: boolean): any;
	/**
	 * Generates a forkflow that deletes table records.
	 * @param table
	 * @param category
	 * @param overwriteWorkflow
	 */
	static generateDeleteWorkflow(table: SQLTable, category: any, overwriteWorkflow: boolean): any;
	/**
	 * Generates a forkflow that updates a table record.
	 * @param table
	 * @param category
	 * @param overwriteWorkflow
	 * @param readOnlyColumns
	 */
	static generateUpdateWorkflow(table: SQLTable, category: any, overwriteWorkflow: boolean, readOnlyColumns: SQLColumn[]): any;
	/**
	 * Converts boolean values to 'Yes' or 'No' strings.
	 * @param source
	 */
	static convertBooleanForPresentation(source: string): string;
	/**
	 * Returns connection URL pattern for a particular database type.
	 * @param existing
	 * @param type
	 */
	static getConnectionUrl(existing: string, type: string): string;
	/**
	 * Returns 'true' if the passed object is a valid number or null. Otherwise, returns 'false'.
	 * @param source
	 */
	static isValidNumberOrEmpty(source: any): boolean;
}

/**
 * Represents a database table.
 */
declare class SQLTable {
	name: string;
	database: any;

	/**
	 * Creates a new active record with primary fields.
	 * @param keyColumns
	 * @param validateUnique
	 */
	createRecord(keyColumns: any, validateUnique: boolean): SQLActiveRecord;
	/**
	 * Reads records by certain search criteria.
	 * @param columns
	 * @param validateUnique
	 */
	readRecords(columns: any, validateUnique: boolean): SQLActiveRecord[];
	/**
	 * Finds a record by certain search criteria.
	 * @param example
	 */
	findUniqueRecord(example: any[]): SQLActiveRecord;
	/**
	 * Updates the record that matches the primary key fields.
	 * @param record
	 * @param values
	 */
	updateRecord(record: SQLActiveRecord, values: any): number;
	/**
	 * Deletes a record by certain search criteria.
	 * @param columns
	 * @param validateUnique
	 */
	deleteRecords(columns: any, validateUnique: boolean): number;
	/**
	 * Get all table columns.
	 */
	getColumns(): SQLColumn[];
}

/**
 * Represents a SQL Database connection.
 */
declare class SQLDatabase {
	readonly id: string;
	name: string;
	type: string;
	connectionURL: string;
	username: string;
	password: string;
	sessionMode: string;
	/**
	 * Returns a list of tables, not added to this database.
	 */
	getUnmappedTableNames(): string[];
	/**
	 * Returns a list of tables, that are added to this database.
	 */
	getMappedTableNames(): string[];
	/**
	 * Returns a list of all tables, that are in this database.
	 */
	getAllTableNames(): string[];
	/**
	 * Returns a list of tables in this database.
	 */
	getTables(): SQLTable[];
	/**
	 * Executes a read custom query on the database and returns the results in an ActiveRecord array
	 * @param customQuery
	 */
	readCustomQuery(customQuery: string): SQLActiveRecord[];
	/**
	 * Execute non-read custom query on the database
	 * @param customQuery
	 */
	executeCustomQuery(customQuery: string): number;
}

/**
 * Represents a SQL Table Column.
 */
declare interface SQLColumn {
	readonly name: string;
	table: any;
	typeName: string;
	typeId: number;
	mandatory: boolean;
}

/**
 * Represents a table record.
 */
declare interface SQLActiveRecord {
	/**
	 * Returns the value of the given property.
	 * @param property
	 */
	getProperty(property: string): any;
	/**
	 * Returns record field names.
	 */
	getFieldNames(): string[];
}

/**
 * JDBC Connection
 */
declare class JDBCConnection {
	/**
	 * Return a connection on a Database connection
	 * ((String)url,(String)username,(String)password)
	 */
	getConnection(url: string, username: string, password: string): Connection;
}

/**
 * Connection on a Database
 */
declare class Connection {
	/**
	 * Creates a Statement object for sending SQL statements
	 * to the database
	 */
	createStatement<T>(): T;
	/**
	 * Creates a PreparedStatement object for sending
	 * parameterized SQL statements to the database
	 */
	prepareStatement(param0: string): PreparedStatement;
	/**
	 * Creates a CallableStatement object for calling
	 * database stored procedures
	 */
	prepareCall(param0: string): PreparedStatement;
	/**
	 * Releases this Connection object's database and JDBC
	 * resources
	 */
	close(): void;
}

/**
 * An object that represents a precompiled SQL statement
 *
 */
declare class PreparedStatement {
	/**
	 * Executes the SQL statement in this PreparedStatement
	 * object, which may be any kind of SQL statement.
	 * Some prepared
	 * statements return multiple results; the execute method
	 * handles
	 * these complex statements as well as the simpler form of
	 * statements
	 * handled by the methods executeQuery and executeUpdate.
	 * The execute
	 * method returns a boolean to indicate the form of the
	 * first result.
	 * You must call either the method getResultSet or getUpdateCount to
	 * retrieve the result; you must call getMoreResults to move to any
	 * subsequent result(s).
	 */
	execute(): boolean;
	/**
	 * Executes the SQL query in this PreparedStatement
	 * object and returns the ResultSet object generated by the query.
	 *
	 */
	executeQuery(): ResultSet;
	/**
	 * Executes the SQL statement in this PreparedStatement
	 * object, which must be an SQL INSERT, UPDATE or DELETE statement;
	 * or an SQL statement that returns nothing, such as a DDL statement.
	 *
	 */
	executeUpdate(): number;
	/**
	 * Sets the designated parameter to the given String
	 * value.
	 * @param parameterIndex
	 * @param x
	 */
	setString(parameterIndex: number, x: any): this;
	/**
	 * Sets the designated parameter to the given Date value.
	 *
	 * @param parameterIndex
	 * @param x
	 */
	setDate(parameterIndex: number, x: any): this;
	/**
	 * Sets the designated parameter to the given Timestamp
	 * value.
	 * @param parameterIndex
	 * @param x
	 */
	setTimestamp(parameterIndex: number, x: any): this;
	/**
	 * Sets the designated parameter to db null.
	 * @param parameterIndex
	 */
	setNull(parameterIndex: number): this;

	/**
	 * Registers the OUT parameter in ordinal position parameterIndex to the JDBC type sqlType.
	 * @param parameterIndex
	 * @param sqlType
	 */
	registerOutParameter(parameterIndex: number, sqlType: number): void;
	/**
	 * Retrieves the value of the designated JDBC INTEGER parameter as an int in the Java programming language.
	 * Not present in the API documentation
	 * @param parameterIndex
	 */
	getInt(parameterIndex: number): number;
	/**
	 * Retrieves the value of the designated JDBC CHAR, VARCHAR, or LONGVARCHAR parameter as a String in the Java programming language.
	 * Not present in the API documentation
	 * @param parameterIndex
	 */
	getString(parameterIndex: number): string;
}

/**
 * Result of an executeQuery
 */
declare class ResultSet {
	/**
	 * Moves the cursor down one row from its current
	 * position.
	 * A ResultSet cursor is initially positioned before the
	 * first row;
	 * the first call to the method next makes the first row
	 * the current
	 * row; the second call makes the second row the current
	 * row, and so
	 * on.
	 * Return true if the new current row is valid; false
	 * if there are no more
	 * rows
	 */
	next(): boolean;
	/**
	 * Moves the cursor to the previous row in this ResultSet
	 * object. Returns true if the cursor is on a valid row; false if it
	 * is off the result set
	 */
	previous(): boolean;
	/**
	 * Moves the cursor to the front of this ResultSet
	 * object, just before the first row. This method has no effect if
	 * the result set contains no rows.
	 */
	beforeFirst(): void;
	/**
	 * Moves the cursor to the first row in this ResultSet
	 * object and returns true if the cursor is on a valid row; false if
	 * there are no rows in the result set
	 * This method can not be use because you can not set the properties
	 * to set up the JDBC connection using the vro SQL Plugin.
	 * @example
	 * Java example
	 * conn.setAutoCommit(false);
	 * Statement s4 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	 *                                     ResultSet.CONCUR_READ_ONLY);
	 */
	first(): boolean;
	/**
	 * Moves the cursor to the last row in this ResultSet
	 * object and returns true if the cursor is on a valid row; false if
	 * there are no rows in the result set
	 */
	last(): boolean;
	/**
	 * Moves the cursor to the end of this ResultSet object,
	 * just after the last row. This method has no effect if the result
	 * set contains no rows.
	 * This method can not be use because you can not set the properties
	 * to set up the JDBC connection using the vro SQL Plugin.
	 * @example
	 * Java example
	 * conn.setAutoCommit(false);
	 * Statement s4 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	 *                                     ResultSet.CONCUR_READ_ONLY);
	 */
	afterLast(): void;
	/**
	 * Reports whether the last column read had a value of
	 * SQL NULL
	 */
	wasNull(): boolean;
	/**
	 * Return the String value of the given column name
	 *
	 * @param columnName
	 */
	getString(columnName: string): string;
	/**
	 * Return the String value of the given column index
	 *
	 * @param columnIndex
	 */
	getStringAt(columnIndex: number): string;
	/**
	 * Return the Number value of the given column name
	 *
	 * @param columnName
	 */
	getNumber(columnName: string): number;
	/**
	 * Return the Number value of the given column index
	 *
	 * @param columnIndex
	 */
	getNumberAt(columnIndex: number): number;
	/**
	 * Return the Date value of the given column name
	 *
	 * @param columnName
	 */
	getDate(columnName: string): Date;
	/**
	 * Return the Date value of the given column index
	 *
	 * @param columnIndex
	 */
	getDateAt(columnIndex: number): Date;
	/**
	 * Return the Timestamp value of the given column name
	 *
	 * @param columnName
	 */
	getTimestamp(columnName: string): Date;
	/**
	 * Return the Timestamp value of the given column index
	 *
	 * @param columnIndex
	 */
	getTimestampAt(columnIndex: number): Date;
	/**
	 * Return the meta data of this ResultSet
	 *
	 */
	getMetaData(): ResultSetMetaData;
}

/**
 * Meta data of ResultSet
 */
declare class ResultSetMetaData {
	/**
	 * Get the number of columns in this ResultSet
	 *
	 */
	getColumnCount(): number;
	/**
	 * Get the name of the column at position index (starting
	 * with 1)
	 * @param index
	 */
	getColumnName(index: number): string;
	/**
	 * Gets the designated column's table name.
	 * @param index
	 */
	getTableName(index: number): string;
}

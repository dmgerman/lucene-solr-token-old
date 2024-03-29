begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|FieldComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
operator|.
name|ExpressionType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import
begin_comment
comment|/**  * Connects to a datasource using a registered JDBC driver and execute a query. The results of  * that query will be returned as tuples. An EOF tuple will indicate that all have been read.  *   * Supported Datatypes  * JDBC Type     | Tuple Type  * --------------|---------------  * String        | String  * Short         | Long  * Integer       | Long  * Long          | Long  * Float         | Double  * Double        | Double  * Boolean       | Boolean  **/
end_comment
begin_class
DECL|class|JDBCStream
specifier|public
class|class
name|JDBCStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
comment|// These are java types that we can directly support as an Object instance. Other supported
comment|// types will require some level of conversion (short -> long, etc...)
comment|// We'll use a static constructor to load this set.
DECL|field|directSupportedTypes
specifier|private
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|directSupportedTypes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|directSupportedTypes
operator|.
name|add
argument_list|(
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|directSupportedTypes
operator|.
name|add
argument_list|(
name|Double
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|directSupportedTypes
operator|.
name|add
argument_list|(
name|Long
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|directSupportedTypes
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Provided as input
DECL|field|driverClassName
specifier|private
name|String
name|driverClassName
decl_stmt|;
DECL|field|connectionUrl
specifier|private
name|String
name|connectionUrl
decl_stmt|;
DECL|field|sqlQuery
specifier|private
name|String
name|sqlQuery
decl_stmt|;
DECL|field|definedSort
specifier|private
name|StreamComparator
name|definedSort
decl_stmt|;
comment|// Internal
DECL|field|connection
specifier|private
name|Connection
name|connection
decl_stmt|;
DECL|field|connectionProperties
specifier|private
name|Properties
name|connectionProperties
decl_stmt|;
DECL|field|statement
specifier|private
name|Statement
name|statement
decl_stmt|;
DECL|field|resultSet
specifier|private
name|ResultSet
name|resultSet
decl_stmt|;
DECL|field|valueSelectors
specifier|private
name|ResultSetValueSelector
index|[]
name|valueSelectors
decl_stmt|;
DECL|field|streamContext
specifier|protected
specifier|transient
name|StreamContext
name|streamContext
decl_stmt|;
DECL|method|JDBCStream
specifier|public
name|JDBCStream
parameter_list|(
name|String
name|connectionUrl
parameter_list|,
name|String
name|sqlQuery
parameter_list|,
name|StreamComparator
name|definedSort
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|connectionUrl
argument_list|,
name|sqlQuery
argument_list|,
name|definedSort
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|JDBCStream
specifier|public
name|JDBCStream
parameter_list|(
name|String
name|connectionUrl
parameter_list|,
name|String
name|sqlQuery
parameter_list|,
name|StreamComparator
name|definedSort
parameter_list|,
name|Properties
name|connectionProperties
parameter_list|,
name|String
name|driverClassName
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|connectionUrl
argument_list|,
name|sqlQuery
argument_list|,
name|definedSort
argument_list|,
name|connectionProperties
argument_list|,
name|driverClassName
argument_list|)
expr_stmt|;
block|}
DECL|method|JDBCStream
specifier|public
name|JDBCStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParams
init|=
name|factory
operator|.
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|connectionUrlExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"connection"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|sqlQueryExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"sql"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|definedSortExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"sort"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|driverClassNameExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"driver"
argument_list|)
decl_stmt|;
comment|// Validate there are no unknown parameters - zkHost and alias are namedParameter so we don't need to count it twice
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// All named params we don't care about will be passed to the driver on connection
name|Properties
name|connectionProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|namedParam
range|:
name|namedParams
control|)
block|{
if|if
condition|(
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"driver"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"connection"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"sql"
argument_list|)
operator|&&
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"sort"
argument_list|)
condition|)
block|{
name|connectionProperties
operator|.
name|put
argument_list|(
name|namedParam
operator|.
name|getName
argument_list|()
argument_list|,
name|namedParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// connectionUrl, required
name|String
name|connectionUrl
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|connectionUrlExpression
operator|&&
name|connectionUrlExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|connectionUrl
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|connectionUrlExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|connectionUrl
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - connection not found"
argument_list|)
argument_list|)
throw|;
block|}
comment|// sql, required
name|String
name|sqlQuery
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sqlQueryExpression
operator|&&
name|sqlQueryExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|sqlQuery
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|sqlQueryExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|sqlQuery
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - sql not found"
argument_list|)
argument_list|)
throw|;
block|}
comment|// definedSort, required
name|StreamComparator
name|definedSort
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sqlQueryExpression
operator|&&
name|sqlQueryExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|definedSort
operator|=
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|definedSortExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldComparator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|definedSort
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - sort not found"
argument_list|)
argument_list|)
throw|;
block|}
comment|// driverClass, optional
name|String
name|driverClass
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|driverClassNameExpression
operator|&&
name|driverClassNameExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|driverClass
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|driverClassNameExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
comment|// We've got all the required items
name|init
argument_list|(
name|connectionUrl
argument_list|,
name|sqlQuery
argument_list|,
name|definedSort
argument_list|,
name|connectionProperties
argument_list|,
name|driverClass
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|connectionUrl
parameter_list|,
name|String
name|sqlQuery
parameter_list|,
name|StreamComparator
name|definedSort
parameter_list|,
name|Properties
name|connectionProperties
parameter_list|,
name|String
name|driverClassName
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|connectionUrl
operator|=
name|connectionUrl
expr_stmt|;
name|this
operator|.
name|sqlQuery
operator|=
name|sqlQuery
expr_stmt|;
name|this
operator|.
name|definedSort
operator|=
name|definedSort
expr_stmt|;
name|this
operator|.
name|connectionProperties
operator|=
name|connectionProperties
expr_stmt|;
name|this
operator|.
name|driverClassName
operator|=
name|driverClassName
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|streamContext
operator|=
name|context
expr_stmt|;
block|}
comment|/**   * Opens the JDBCStream   *   ***/
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|driverClassName
condition|)
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driverClassName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to load JDBC driver for '%s'"
argument_list|,
name|driverClassName
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// See if we can figure out the driver based on the url, if not then tell the user they most likely want to provide the driverClassName.
comment|// Not being able to find a driver generally means the driver has not been loaded.
try|try
block|{
if|if
condition|(
literal|null
operator|==
name|DriverManager
operator|.
name|getDriver
argument_list|(
name|connectionUrl
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"DriverManager.getDriver(url) returned null"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to determine JDBC driver from connection url '%s'. Usually this means the driver is not loaded - you can have JDBCStream try to load it by providing the 'driverClassName' value"
argument_list|,
name|connectionUrl
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|connectionUrl
argument_list|,
name|connectionProperties
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to open JDBC connection to '%s'"
argument_list|,
name|connectionUrl
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to create a statement from JDBC connection '%s'"
argument_list|,
name|connectionUrl
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sqlQuery
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to execute sqlQuery '%s' against JDBC connection '%s'"
argument_list|,
name|sqlQuery
argument_list|,
name|connectionUrl
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
comment|// using the metadata, build selectors for each column
name|valueSelectors
operator|=
name|constructValueSelectors
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to generate value selectors for sqlQuery '%s' against JDBC connection '%s'"
argument_list|,
name|sqlQuery
argument_list|,
name|connectionUrl
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|constructValueSelectors
specifier|private
name|ResultSetValueSelector
index|[]
name|constructValueSelectors
parameter_list|(
name|ResultSetMetaData
name|metadata
parameter_list|)
throws|throws
name|SQLException
block|{
name|ResultSetValueSelector
index|[]
name|valueSelectors
init|=
operator|new
name|ResultSetValueSelector
index|[
name|metadata
operator|.
name|getColumnCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|columnIdx
init|=
literal|0
init|;
name|columnIdx
operator|<
name|metadata
operator|.
name|getColumnCount
argument_list|()
condition|;
operator|++
name|columnIdx
control|)
block|{
specifier|final
name|int
name|columnNumber
init|=
name|columnIdx
operator|+
literal|1
decl_stmt|;
comment|// cause it starts at 1
specifier|final
name|String
name|columnName
init|=
name|metadata
operator|.
name|getColumnName
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
name|String
name|className
init|=
name|metadata
operator|.
name|getColumnClassName
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|directSupportedTypes
operator|.
name|contains
argument_list|(
name|className
argument_list|)
condition|)
block|{
name|valueSelectors
index|[
name|columnIdx
index|]
operator|=
operator|new
name|ResultSetValueSelector
argument_list|()
block|{
specifier|public
name|Object
name|selectValue
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|Object
name|obj
init|=
name|resultSet
operator|.
name|getObject
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|()
block|{
return|return
name|columnName
return|;
block|}
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Short
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|==
name|className
condition|)
block|{
name|valueSelectors
index|[
name|columnIdx
index|]
operator|=
operator|new
name|ResultSetValueSelector
argument_list|()
block|{
specifier|public
name|Object
name|selectValue
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|Short
name|obj
init|=
name|resultSet
operator|.
name|getShort
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|()
block|{
return|return
name|columnName
return|;
block|}
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Integer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|==
name|className
condition|)
block|{
name|valueSelectors
index|[
name|columnIdx
index|]
operator|=
operator|new
name|ResultSetValueSelector
argument_list|()
block|{
specifier|public
name|Object
name|selectValue
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|Integer
name|obj
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|()
block|{
return|return
name|columnName
return|;
block|}
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Float
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|==
name|className
condition|)
block|{
name|valueSelectors
index|[
name|columnIdx
index|]
operator|=
operator|new
name|ResultSetValueSelector
argument_list|()
block|{
specifier|public
name|Object
name|selectValue
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|Float
name|obj
init|=
name|resultSet
operator|.
name|getFloat
argument_list|(
name|columnNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
operator|.
name|doubleValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|()
block|{
return|return
name|columnName
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
return|return
name|valueSelectors
return|;
block|}
comment|/**    *  Closes the JDBCStream    **/
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|resultSet
condition|)
block|{
comment|// it's not required in JDBC that ResultSet implements the isClosed() function
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|statement
operator|&&
operator|!
name|statement
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|connection
operator|&&
operator|!
name|connection
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to properly close JDBCStream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// we have a record
for|for
control|(
name|ResultSetValueSelector
name|selector
range|:
name|valueSelectors
control|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|selector
operator|.
name|getColumnName
argument_list|()
argument_list|,
name|selector
operator|.
name|selectValue
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// we do not have a record
name|fields
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to read next record with error '%s'"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// functionName(collectionName, param1, param2, ..., paramN, sort="comp", [aliases="field=alias,..."])
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// connection url
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"connection"
argument_list|,
name|connectionUrl
argument_list|)
argument_list|)
expr_stmt|;
comment|// sql
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sql"
argument_list|,
name|sqlQuery
argument_list|)
argument_list|)
expr_stmt|;
comment|// sort
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|,
name|definedSort
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// driver class
if|if
condition|(
literal|null
operator|!=
name|driverClassName
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"driver"
argument_list|,
name|driverClassName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// connection properties
if|if
condition|(
literal|null
operator|!=
name|connectionProperties
condition|)
block|{
for|for
control|(
name|String
name|propertyName
range|:
name|connectionProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|propertyName
argument_list|,
name|connectionProperties
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamExplanation
name|explanation
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|explanation
operator|.
name|setFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_SOURCE
argument_list|)
expr_stmt|;
name|StreamExpression
name|expression
init|=
operator|(
name|StreamExpression
operator|)
name|toExpression
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|explanation
operator|.
name|setExpression
argument_list|(
name|expression
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|driverClassName
init|=
name|this
operator|.
name|driverClassName
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|driverClassName
condition|)
block|{
try|try
block|{
name|driverClassName
operator|=
name|DriverManager
operator|.
name|getDriver
argument_list|(
name|connectionUrl
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|driverClassName
operator|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to find driver for connectionUrl='%s'"
argument_list|,
name|connectionUrl
argument_list|)
expr_stmt|;
block|}
block|}
comment|// child is a datastore so add it at this point
name|StreamExplanation
name|child
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|+
literal|"-datastore"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setFunctionName
argument_list|(
literal|"jdbc-source"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setImplementingClass
argument_list|(
name|driverClassName
argument_list|)
expr_stmt|;
name|child
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|DATASTORE
argument_list|)
expr_stmt|;
name|child
operator|.
name|setExpression
argument_list|(
name|sqlQuery
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|addChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
block|}
annotation|@
name|Override
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|TupleStream
argument_list|>
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
comment|// TODO: Need to somehow figure out the sort applied to the incoming data. This is not something you can ask a JDBC stream
comment|// Possibly we can ask the creator to tell us the fields the data is sorted by. This would be duplicate information because
comment|// it's already in the sqlQuery but there's no way we can reliably determine the sort from the query.
return|return
name|definedSort
return|;
block|}
block|}
end_class
begin_interface
DECL|interface|ResultSetValueSelector
interface|interface
name|ResultSetValueSelector
block|{
DECL|method|getColumnName
specifier|public
name|String
name|getColumnName
parameter_list|()
function_decl|;
DECL|method|selectValue
specifier|public
name|Object
name|selectValue
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
end_interface
end_unit

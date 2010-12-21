begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|DeleteUpdateCommand
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
name|update
operator|.
name|MergeIndexesCommand
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
name|update
operator|.
name|RollbackUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorFactory
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
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
name|io
operator|.
name|File
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
name|List
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
begin_comment
comment|/**  *<p>  * Abstract base class for DataImportHandler tests  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|AbstractDataImportHandlerTestCase
specifier|public
specifier|abstract
class|class
name|AbstractDataImportHandlerTestCase
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// remove dataimport.properties
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"solr/conf/dataimport.properties"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Looking for dataimport.properties at: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting dataimport.properties"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete dataimport.properties"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|loadDataConfig
specifier|protected
name|String
name|loadDataConfig
parameter_list|(
name|String
name|dataConfigFileName
parameter_list|)
block|{
try|try
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
return|return
name|SolrWriter
operator|.
name|getResourceAsString
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|dataConfigFileName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|runFullImport
specifier|protected
name|void
name|runFullImport
parameter_list|(
name|String
name|dataConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|runDeltaImport
specifier|protected
name|void
name|runDeltaImport
parameter_list|(
name|String
name|dataConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"delta-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"false"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs a full-import using the given dataConfig and the provided request parameters.    *    * By default, debug=on, clean=true and commit=true are passed which can be overridden.    *    * @param dataConfig the data-config xml as a string    * @param extraParams any extra request parameters needed to be passed to DataImportHandler    * @throws Exception in case of any error    */
DECL|method|runFullImport
specifier|protected
name|void
name|runFullImport
parameter_list|(
name|String
name|dataConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraParams
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"clean"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|putAll
argument_list|(
name|extraParams
argument_list|)
expr_stmt|;
name|NamedList
name|l
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LocalSolrQueryRequest
name|request
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper for creating a Context instance. Useful for testing Transformers    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getContext
specifier|public
specifier|static
name|TestContext
name|getContext
parameter_list|(
name|DataConfig
operator|.
name|Entity
name|parentEntity
parameter_list|,
name|VariableResolverImpl
name|resolver
parameter_list|,
name|DataSource
name|parentDataSource
parameter_list|,
name|String
name|currProcess
parameter_list|,
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
parameter_list|)
block|{
if|if
condition|(
name|resolver
operator|==
literal|null
condition|)
name|resolver
operator|=
operator|new
name|VariableResolverImpl
argument_list|()
expr_stmt|;
specifier|final
name|Context
name|delegate
init|=
operator|new
name|ContextImpl
argument_list|(
name|parentEntity
argument_list|,
name|resolver
argument_list|,
name|parentDataSource
argument_list|,
name|currProcess
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestContext
argument_list|(
name|entityAttrs
argument_list|,
name|delegate
argument_list|,
name|entityFields
argument_list|,
name|parentEntity
operator|==
literal|null
argument_list|)
return|;
block|}
comment|/**    * Strings at even index are keys, odd-index strings are values in the    * returned map    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createMap
specifier|public
specifier|static
name|Map
name|createMap
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|Map
name|result
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
operator|||
name|args
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|result
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
name|result
operator|.
name|put
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createFile
specifier|public
specifier|static
name|File
name|createFile
parameter_list|(
name|File
name|tmpdir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
index|[]
name|content
parameter_list|,
name|boolean
name|changeModifiedTime
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|name
argument_list|)
decl_stmt|;
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileOutputStream
name|f
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|f
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|changeModifiedTime
condition|)
name|file
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|3600000
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
DECL|method|getField
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getField
parameter_list|(
name|String
name|col
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|re
parameter_list|,
name|String
name|srcCol
parameter_list|,
name|String
name|splitBy
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|vals
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"regex"
argument_list|,
name|re
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"sourceColName"
argument_list|,
name|srcCol
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"splitBy"
argument_list|,
name|splitBy
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
DECL|class|TestContext
specifier|static
class|class
name|TestContext
extends|extends
name|Context
block|{
DECL|field|entityAttrs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|Context
name|delegate
decl_stmt|;
DECL|field|entityFields
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|boolean
name|root
decl_stmt|;
DECL|field|script
DECL|field|scriptlang
name|String
name|script
decl_stmt|,
name|scriptlang
decl_stmt|;
DECL|method|TestContext
specifier|public
name|TestContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
parameter_list|,
name|Context
name|delegate
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
parameter_list|,
name|boolean
name|root
parameter_list|)
block|{
name|this
operator|.
name|entityAttrs
operator|=
name|entityAttrs
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|entityFields
operator|=
name|entityFields
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
DECL|method|getEntityAttribute
specifier|public
name|String
name|getEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entityAttrs
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getEntityAttribute
argument_list|(
name|name
argument_list|)
else|:
name|entityAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getResolvedEntityAttribute
specifier|public
name|String
name|getResolvedEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entityAttrs
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|name
argument_list|)
else|:
name|delegate
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|replaceTokens
argument_list|(
name|entityAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAllEntityFields
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getAllEntityFields
parameter_list|()
block|{
return|return
name|entityFields
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getAllEntityFields
argument_list|()
else|:
name|entityFields
return|;
block|}
DECL|method|getVariableResolver
specifier|public
name|VariableResolver
name|getVariableResolver
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getVariableResolver
argument_list|()
return|;
block|}
DECL|method|getDataSource
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getDataSource
argument_list|()
return|;
block|}
DECL|method|isRootEntity
specifier|public
name|boolean
name|isRootEntity
parameter_list|()
block|{
return|return
name|root
return|;
block|}
DECL|method|currentProcess
specifier|public
name|String
name|currentProcess
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|currentProcess
argument_list|()
return|;
block|}
DECL|method|getRequestParameters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRequestParameters
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getRequestParameters
argument_list|()
return|;
block|}
DECL|method|getEntityProcessor
specifier|public
name|EntityProcessor
name|getEntityProcessor
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|setSessionAttribute
specifier|public
name|void
name|setSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|delegate
operator|.
name|setSessionAttribute
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
DECL|method|getSessionAttribute
specifier|public
name|Object
name|getSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getSessionAttribute
argument_list|(
name|name
argument_list|,
name|scope
argument_list|)
return|;
block|}
DECL|method|getParentContext
specifier|public
name|Context
name|getParentContext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getParentContext
argument_list|()
return|;
block|}
DECL|method|getDataSource
specifier|public
name|DataSource
name|getDataSource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getDataSource
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getSolrCore
specifier|public
name|SolrCore
name|getSolrCore
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSolrCore
argument_list|()
return|;
block|}
DECL|method|getStats
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getStats
argument_list|()
return|;
block|}
DECL|method|getScript
specifier|public
name|String
name|getScript
parameter_list|()
block|{
return|return
name|script
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getScript
argument_list|()
else|:
name|script
return|;
block|}
DECL|method|getScriptLanguage
specifier|public
name|String
name|getScriptLanguage
parameter_list|()
block|{
return|return
name|scriptlang
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getScriptLanguage
argument_list|()
else|:
name|scriptlang
return|;
block|}
DECL|method|deleteDoc
specifier|public
name|void
name|deleteDoc
parameter_list|(
name|String
name|id
parameter_list|)
block|{      }
DECL|method|deleteDocByQuery
specifier|public
name|void
name|deleteDocByQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{      }
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|String
name|var
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|resolve
argument_list|(
name|var
argument_list|)
return|;
block|}
DECL|method|replaceTokens
specifier|public
name|String
name|replaceTokens
parameter_list|(
name|String
name|template
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|replaceTokens
argument_list|(
name|template
argument_list|)
return|;
block|}
block|}
DECL|class|TestUpdateRequestProcessorFactory
specifier|public
specifier|static
class|class
name|TestUpdateRequestProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
block|{
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|TestUpdateRequestProcessor
argument_list|(
name|next
argument_list|)
return|;
block|}
block|}
DECL|class|TestUpdateRequestProcessor
specifier|public
specifier|static
class|class
name|TestUpdateRequestProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|finishCalled
specifier|public
specifier|static
name|boolean
name|finishCalled
init|=
literal|false
decl_stmt|;
DECL|field|processAddCalled
specifier|public
specifier|static
name|boolean
name|processAddCalled
init|=
literal|false
decl_stmt|;
DECL|field|processCommitCalled
specifier|public
specifier|static
name|boolean
name|processCommitCalled
init|=
literal|false
decl_stmt|;
DECL|field|processDeleteCalled
specifier|public
specifier|static
name|boolean
name|processDeleteCalled
init|=
literal|false
decl_stmt|;
DECL|field|mergeIndexesCalled
specifier|public
specifier|static
name|boolean
name|mergeIndexesCalled
init|=
literal|false
decl_stmt|;
DECL|field|rollbackCalled
specifier|public
specifier|static
name|boolean
name|rollbackCalled
init|=
literal|false
decl_stmt|;
DECL|method|reset
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|finishCalled
operator|=
literal|false
expr_stmt|;
name|processAddCalled
operator|=
literal|false
expr_stmt|;
name|processCommitCalled
operator|=
literal|false
expr_stmt|;
name|processDeleteCalled
operator|=
literal|false
expr_stmt|;
name|mergeIndexesCalled
operator|=
literal|false
expr_stmt|;
name|rollbackCalled
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|TestUpdateRequestProcessor
specifier|public
name|TestUpdateRequestProcessor
parameter_list|(
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|finishCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|processAddCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|processCommitCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|processDeleteCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|processMergeIndexes
specifier|public
name|void
name|processMergeIndexes
parameter_list|(
name|MergeIndexesCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|mergeIndexesCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|processMergeIndexes
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|processRollback
specifier|public
name|void
name|processRollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|rollbackCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|processRollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

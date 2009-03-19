begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  *<p>  * An implementation for the Context  *</p>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ContextImpl
specifier|public
class|class
name|ContextImpl
extends|extends
name|Context
block|{
DECL|field|entity
specifier|private
name|DataConfig
operator|.
name|Entity
name|entity
decl_stmt|;
DECL|field|parent
specifier|private
name|ContextImpl
name|parent
decl_stmt|;
DECL|field|resolver
specifier|private
name|VariableResolverImpl
name|resolver
decl_stmt|;
DECL|field|ds
specifier|private
name|DataSource
name|ds
decl_stmt|;
DECL|field|currProcess
specifier|private
name|int
name|currProcess
decl_stmt|;
DECL|field|requestParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestParams
decl_stmt|;
DECL|field|dataImporter
specifier|private
name|DataImporter
name|dataImporter
decl_stmt|;
DECL|field|entitySession
DECL|field|globalSession
DECL|field|docSession
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entitySession
decl_stmt|,
name|globalSession
decl_stmt|,
name|docSession
decl_stmt|;
DECL|field|docBuilder
name|DocBuilder
name|docBuilder
decl_stmt|;
DECL|method|ContextImpl
specifier|public
name|ContextImpl
parameter_list|(
name|DataConfig
operator|.
name|Entity
name|entity
parameter_list|,
name|VariableResolverImpl
name|resolver
parameter_list|,
name|DataSource
name|ds
parameter_list|,
name|int
name|currProcess
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|global
parameter_list|,
name|ContextImpl
name|parentContext
parameter_list|,
name|DocBuilder
name|docBuilder
parameter_list|)
block|{
name|this
operator|.
name|entity
operator|=
name|entity
expr_stmt|;
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
name|this
operator|.
name|ds
operator|=
name|ds
expr_stmt|;
name|this
operator|.
name|currProcess
operator|=
name|currProcess
expr_stmt|;
name|this
operator|.
name|docBuilder
operator|=
name|docBuilder
expr_stmt|;
if|if
condition|(
name|docBuilder
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|requestParams
operator|=
name|docBuilder
operator|.
name|requestParameters
operator|.
name|requestParams
expr_stmt|;
name|dataImporter
operator|=
name|docBuilder
operator|.
name|dataImporter
expr_stmt|;
block|}
name|globalSession
operator|=
name|global
expr_stmt|;
name|parent
operator|=
name|parentContext
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
name|entity
operator|==
literal|null
condition|?
literal|null
else|:
name|entity
operator|.
name|allAttributes
operator|.
name|get
argument_list|(
name|name
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
name|entity
operator|==
literal|null
condition|?
name|Collections
operator|.
name|EMPTY_LIST
else|:
name|entity
operator|.
name|allFieldsList
return|;
block|}
DECL|method|getVariableResolver
specifier|public
name|VariableResolver
name|getVariableResolver
parameter_list|()
block|{
return|return
name|resolver
return|;
block|}
DECL|method|getDataSource
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
if|if
condition|(
name|ds
operator|!=
literal|null
condition|)
return|return
name|ds
return|;
if|if
condition|(
name|entity
operator|.
name|dataSrc
operator|==
literal|null
condition|)
block|{
name|entity
operator|.
name|dataSrc
operator|=
name|dataImporter
operator|.
name|getDataSourceInstance
argument_list|(
name|entity
argument_list|,
name|entity
operator|.
name|dataSource
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entity
operator|.
name|dataSrc
operator|!=
literal|null
operator|&&
name|docBuilder
operator|!=
literal|null
operator|&&
name|docBuilder
operator|.
name|verboseDebug
operator|&&
name|currProcess
operator|==
name|Context
operator|.
name|FULL_DUMP
condition|)
block|{
comment|//debug is not yet implemented properly for deltas
return|return
name|DebugLogger
operator|.
name|wrapDs
argument_list|(
name|entity
operator|.
name|dataSrc
argument_list|)
return|;
block|}
return|return
name|entity
operator|.
name|dataSrc
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
name|dataImporter
operator|.
name|getDataSourceInstance
argument_list|(
name|entity
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|isRootEntity
specifier|public
name|boolean
name|isRootEntity
parameter_list|()
block|{
return|return
name|entity
operator|.
name|isDocRoot
return|;
block|}
DECL|method|currentProcess
specifier|public
name|int
name|currentProcess
parameter_list|()
block|{
return|return
name|currProcess
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
name|requestParams
return|;
block|}
DECL|method|getEntityProcessor
specifier|public
name|EntityProcessor
name|getEntityProcessor
parameter_list|()
block|{
return|return
name|entity
operator|==
literal|null
condition|?
literal|null
else|:
name|entity
operator|.
name|processor
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
if|if
condition|(
name|Context
operator|.
name|SCOPE_ENTITY
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
if|if
condition|(
name|entitySession
operator|==
literal|null
condition|)
name|entitySession
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|entitySession
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Context
operator|.
name|SCOPE_GLOBAL
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
if|if
condition|(
name|globalSession
operator|!=
literal|null
condition|)
block|{
name|globalSession
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Context
operator|.
name|SCOPE_DOC
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|docsession
init|=
name|getDocSession
argument_list|()
decl_stmt|;
if|if
condition|(
name|docsession
operator|!=
literal|null
condition|)
name|docsession
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SCOPE_SOLR_CORE
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
if|if
condition|(
name|dataImporter
operator|!=
literal|null
condition|)
name|dataImporter
operator|.
name|getCoreScopeSession
argument_list|()
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|Context
operator|.
name|SCOPE_ENTITY
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
if|if
condition|(
name|entitySession
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|entitySession
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Context
operator|.
name|SCOPE_GLOBAL
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
if|if
condition|(
name|globalSession
operator|!=
literal|null
condition|)
block|{
return|return
name|globalSession
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|Context
operator|.
name|SCOPE_DOC
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|docsession
init|=
name|getDocSession
argument_list|()
decl_stmt|;
if|if
condition|(
name|docsession
operator|!=
literal|null
condition|)
return|return
name|docsession
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|SCOPE_SOLR_CORE
operator|.
name|equals
argument_list|(
name|scope
argument_list|)
condition|)
block|{
return|return
name|dataImporter
operator|==
literal|null
condition|?
literal|null
else|:
name|dataImporter
operator|.
name|getCoreScopeSession
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getParentContext
specifier|public
name|Context
name|getParentContext
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|getDocSession
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDocSession
parameter_list|()
block|{
name|ContextImpl
name|c
init|=
name|this
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|docSession
operator|!=
literal|null
condition|)
return|return
name|c
operator|.
name|docSession
return|;
if|if
condition|(
name|c
operator|.
name|parent
operator|!=
literal|null
condition|)
name|c
operator|=
name|c
operator|.
name|parent
expr_stmt|;
else|else
return|return
literal|null
return|;
block|}
block|}
DECL|method|setDocSession
specifier|public
name|void
name|setDocSession
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|docSession
parameter_list|)
block|{
name|this
operator|.
name|docSession
operator|=
name|docSession
expr_stmt|;
block|}
DECL|method|getSolrCore
specifier|public
name|SolrCore
name|getSolrCore
parameter_list|()
block|{
return|return
name|dataImporter
operator|==
literal|null
condition|?
literal|null
else|:
name|dataImporter
operator|.
name|getCore
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
name|docBuilder
operator|!=
literal|null
condition|?
name|docBuilder
operator|.
name|importStatistics
operator|.
name|getStatsSnapshot
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|emptyMap
argument_list|()
return|;
block|}
DECL|method|getScript
specifier|public
name|String
name|getScript
parameter_list|()
block|{
if|if
condition|(
name|dataImporter
operator|!=
literal|null
condition|)
block|{
name|DataConfig
operator|.
name|Script
name|script
init|=
name|dataImporter
operator|.
name|getConfig
argument_list|()
operator|.
name|script
decl_stmt|;
return|return
name|script
operator|==
literal|null
condition|?
literal|null
else|:
name|script
operator|.
name|text
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getScriptLanguage
specifier|public
name|String
name|getScriptLanguage
parameter_list|()
block|{
if|if
condition|(
name|dataImporter
operator|!=
literal|null
condition|)
block|{
name|DataConfig
operator|.
name|Script
name|script
init|=
name|dataImporter
operator|.
name|getConfig
argument_list|()
operator|.
name|script
decl_stmt|;
return|return
name|script
operator|==
literal|null
condition|?
literal|null
else|:
name|script
operator|.
name|language
return|;
block|}
return|return
literal|null
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
block|{
if|if
condition|(
name|docBuilder
operator|!=
literal|null
condition|)
block|{
name|docBuilder
operator|.
name|writer
operator|.
name|deleteDoc
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteDocByQuery
specifier|public
name|void
name|deleteDocByQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
if|if
condition|(
name|docBuilder
operator|!=
literal|null
condition|)
block|{
name|docBuilder
operator|.
name|writer
operator|.
name|deleteByQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

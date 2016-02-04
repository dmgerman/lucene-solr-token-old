begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|Iterator
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
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|DIHCacheSupport
specifier|public
class|class
name|DIHCacheSupport
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cacheForeignKey
specifier|private
name|String
name|cacheForeignKey
decl_stmt|;
DECL|field|cacheImplName
specifier|private
name|String
name|cacheImplName
decl_stmt|;
DECL|field|queryVsCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DIHCache
argument_list|>
name|queryVsCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queryVsCacheIterator
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|queryVsCacheIterator
decl_stmt|;
DECL|field|dataSourceRowCache
specifier|private
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|dataSourceRowCache
decl_stmt|;
DECL|field|cacheDoKeyLookup
specifier|private
name|boolean
name|cacheDoKeyLookup
decl_stmt|;
DECL|method|DIHCacheSupport
specifier|public
name|DIHCacheSupport
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|cacheImplName
parameter_list|)
block|{
name|this
operator|.
name|cacheImplName
operator|=
name|cacheImplName
expr_stmt|;
name|Relation
name|r
init|=
operator|new
name|Relation
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|cacheDoKeyLookup
operator|=
name|r
operator|.
name|doKeyLookup
expr_stmt|;
name|String
name|cacheKey
init|=
name|r
operator|.
name|primaryKey
decl_stmt|;
name|cacheForeignKey
operator|=
name|r
operator|.
name|foreignKey
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_PRIMARY_KEY
argument_list|,
name|cacheKey
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_FOREIGN_KEY
argument_list|,
name|cacheForeignKey
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_DELETE_PRIOR_DATA
argument_list|,
literal|"true"
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_READ_ONLY
argument_list|,
literal|"false"
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
block|}
DECL|class|Relation
specifier|static
class|class
name|Relation
block|{
DECL|field|doKeyLookup
specifier|protected
specifier|final
name|boolean
name|doKeyLookup
decl_stmt|;
DECL|field|foreignKey
specifier|protected
specifier|final
name|String
name|foreignKey
decl_stmt|;
DECL|field|primaryKey
specifier|protected
specifier|final
name|String
name|primaryKey
decl_stmt|;
DECL|method|Relation
specifier|public
name|Relation
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|String
name|where
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"where"
argument_list|)
decl_stmt|;
name|String
name|cacheKey
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_PRIMARY_KEY
argument_list|)
decl_stmt|;
name|String
name|lookupKey
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_FOREIGN_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheKey
operator|!=
literal|null
operator|&&
name|lookupKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"'cacheKey' is specified for the entity "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" but 'cacheLookup' is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|where
operator|==
literal|null
operator|&&
name|cacheKey
operator|==
literal|null
condition|)
block|{
name|doKeyLookup
operator|=
literal|false
expr_stmt|;
name|primaryKey
operator|=
literal|null
expr_stmt|;
name|foreignKey
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|splits
init|=
name|where
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|primaryKey
operator|=
name|splits
index|[
literal|0
index|]
expr_stmt|;
name|foreignKey
operator|=
name|splits
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|primaryKey
operator|=
name|cacheKey
expr_stmt|;
name|foreignKey
operator|=
name|lookupKey
expr_stmt|;
block|}
name|doKeyLookup
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Relation "
operator|+
name|primaryKey
operator|+
literal|"="
operator|+
name|foreignKey
return|;
block|}
block|}
DECL|method|instantiateCache
specifier|private
name|DIHCache
name|instantiateCache
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|DIHCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|DIHCache
argument_list|>
name|cacheClass
init|=
name|DocBuilder
operator|.
name|loadClass
argument_list|(
name|cacheImplName
argument_list|,
name|context
operator|.
name|getSolrCore
argument_list|()
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|DIHCache
argument_list|>
name|constr
init|=
name|cacheClass
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
name|cache
operator|=
name|constr
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to load Cache implementation:"
operator|+
name|cacheImplName
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|cache
return|;
block|}
DECL|method|initNewParent
specifier|public
name|void
name|initNewParent
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|dataSourceRowCache
operator|=
literal|null
expr_stmt|;
name|queryVsCacheIterator
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DIHCache
argument_list|>
name|entry
range|:
name|queryVsCache
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|queryVsCacheIterator
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|destroyAll
specifier|public
name|void
name|destroyAll
parameter_list|()
block|{
if|if
condition|(
name|queryVsCache
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DIHCache
name|cache
range|:
name|queryVsCache
operator|.
name|values
argument_list|()
control|)
block|{
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
name|queryVsCache
operator|=
literal|null
expr_stmt|;
name|dataSourceRowCache
operator|=
literal|null
expr_stmt|;
name|cacheForeignKey
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    *<p>    * Get all the rows from the datasource for the given query and cache them    *</p>    */
DECL|method|populateCache
specifier|public
name|void
name|populateCache
parameter_list|(
name|String
name|query
parameter_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
init|=
literal|null
decl_stmt|;
name|DIHCache
name|cache
init|=
name|queryVsCache
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|aRow
operator|=
name|getNextFromCache
argument_list|(
name|query
argument_list|,
name|rowIterator
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|add
argument_list|(
name|aRow
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNextFromCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getNextFromCache
parameter_list|(
name|String
name|query
parameter_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|rowIterator
operator|.
name|hasNext
argument_list|()
condition|)
return|return
name|rowIterator
operator|.
name|next
argument_list|()
return|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"getNextFromCache() failed for query '"
operator|+
name|query
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getCacheData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getCacheData
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|query
parameter_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
parameter_list|)
block|{
if|if
condition|(
name|cacheDoKeyLookup
condition|)
block|{
return|return
name|getIdCacheData
argument_list|(
name|context
argument_list|,
name|query
argument_list|,
name|rowIterator
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getSimpleCacheData
argument_list|(
name|context
argument_list|,
name|query
argument_list|,
name|rowIterator
argument_list|)
return|;
block|}
block|}
comment|/**    * If the where clause is present the cache is sql Vs Map of key Vs List of    * Rows.    *     * @param query    *          the query string for which cached data is to be returned    *     * @return the cached row corresponding to the given query after all variables    *         have been resolved    */
DECL|method|getIdCacheData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getIdCacheData
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|query
parameter_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
parameter_list|)
block|{
name|Object
name|key
init|=
name|context
operator|.
name|resolve
argument_list|(
name|cacheForeignKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
literal|"The cache lookup value : "
operator|+
name|cacheForeignKey
operator|+
literal|" is resolved to be null in the entity :"
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|dataSourceRowCache
operator|==
literal|null
condition|)
block|{
name|DIHCache
name|cache
init|=
name|queryVsCache
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
name|instantiateCache
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|queryVsCache
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|cache
argument_list|)
expr_stmt|;
name|populateCache
argument_list|(
name|query
argument_list|,
name|rowIterator
argument_list|)
expr_stmt|;
block|}
name|dataSourceRowCache
operator|=
name|cache
operator|.
name|iterator
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
comment|/**    * If where clause is not present the cache is a Map of query vs List of Rows.    *     * @param query    *          string for which cached row is to be returned    *     * @return the cached row corresponding to the given query    */
DECL|method|getSimpleCacheData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSimpleCacheData
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|query
parameter_list|,
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
parameter_list|)
block|{
if|if
condition|(
name|dataSourceRowCache
operator|==
literal|null
condition|)
block|{
name|DIHCache
name|cache
init|=
name|queryVsCache
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
name|instantiateCache
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|queryVsCache
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|cache
argument_list|)
expr_stmt|;
name|populateCache
argument_list|(
name|query
argument_list|,
name|rowIterator
argument_list|)
expr_stmt|;
name|queryVsCacheIterator
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|cache
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|cacheIter
init|=
name|queryVsCacheIterator
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|dataSourceRowCache
operator|=
name|cacheIter
expr_stmt|;
block|}
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
DECL|method|getFromRowCacheTransformed
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFromRowCacheTransformed
parameter_list|()
block|{
if|if
condition|(
name|dataSourceRowCache
operator|==
literal|null
operator|||
operator|!
name|dataSourceRowCache
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|dataSourceRowCache
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|dataSourceRowCache
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|r
return|;
block|}
comment|/**    *<p>    * Specify the class for the cache implementation    *</p>    */
DECL|field|CACHE_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_IMPL
init|=
literal|"cacheImpl"
decl_stmt|;
comment|/**    *<p>    * If the cache supports persistent data, set to "true" to delete any prior    * persisted data before running the entity.    *</p>    */
DECL|field|CACHE_DELETE_PRIOR_DATA
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_DELETE_PRIOR_DATA
init|=
literal|"cacheDeletePriorData"
decl_stmt|;
comment|/**    *<p>    * Specify the Foreign Key from the parent entity to join on. Use if the cache    * is on a child entity.    *</p>    */
DECL|field|CACHE_FOREIGN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_FOREIGN_KEY
init|=
literal|"cacheLookup"
decl_stmt|;
comment|/**    *<p>    * Specify the Primary Key field from this Entity to map the input records    * with    *</p>    */
DECL|field|CACHE_PRIMARY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_PRIMARY_KEY
init|=
literal|"cacheKey"
decl_stmt|;
comment|/**    *<p>    * If true, a pre-existing cache is re-opened for read-only access.    *</p>    */
DECL|field|CACHE_READ_ONLY
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_READ_ONLY
init|=
literal|"cacheReadOnly"
decl_stmt|;
block|}
end_class
end_unit

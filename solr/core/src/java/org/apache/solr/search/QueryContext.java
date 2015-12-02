begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|IdentityHashMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
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
name|SolrRequestInfo
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
begin_comment
comment|/*  * Bridge between old style context and a real class.  * This is currently slightly more heavy weight than necessary because of the need to inherit from IdentityHashMap rather than  * instantiate it on demand (and the need to put "searcher" in the map)  * @lucene.experimental  */
end_comment
begin_class
DECL|class|QueryContext
specifier|public
class|class
name|QueryContext
extends|extends
name|IdentityHashMap
implements|implements
name|Closeable
block|{
comment|// private IdentityHashMap map;  // we are the map for now (for compat w/ ValueSource)
DECL|field|searcher
specifier|private
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|indexSearcher
specifier|private
specifier|final
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|closeHooks
specifier|private
name|IdentityHashMap
argument_list|<
name|Closeable
argument_list|,
name|String
argument_list|>
name|closeHooks
decl_stmt|;
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
comment|// migrated from ValueSource
DECL|method|newContext
specifier|public
specifier|static
name|QueryContext
name|newContext
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|QueryContext
name|context
init|=
operator|new
name|QueryContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
name|context
return|;
block|}
DECL|method|QueryContext
specifier|public
name|QueryContext
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
operator|instanceof
name|SolrIndexSearcher
condition|?
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
else|:
literal|null
expr_stmt|;
name|indexSearcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|put
argument_list|(
literal|"searcher"
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// see ValueSource.newContext()  // TODO: move check to "get"?
block|}
DECL|method|searcher
specifier|public
name|SolrIndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|searcher
return|;
block|}
DECL|method|indexSearcher
specifier|public
name|IndexSearcher
name|indexSearcher
parameter_list|()
block|{
return|return
name|indexSearcher
return|;
block|}
comment|/***  implementations obtained via inheritance   public Object get(Object key) {     return map.get(key);   }    public Object put(Object key, Object val) {     if (map == null) {       map = new IdentityHashMap();     }     return map.put(key, val);   }   ***/
DECL|method|addCloseHook
specifier|public
name|void
name|addCloseHook
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
if|if
condition|(
name|closeHooks
operator|==
literal|null
condition|)
block|{
name|closeHooks
operator|=
operator|new
name|IdentityHashMap
argument_list|<
name|Closeable
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// for now, defer closing until the end of the request
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
operator|.
name|addCloseHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|closeHooks
operator|.
name|put
argument_list|(
name|closeable
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCloseHook
specifier|public
name|boolean
name|removeCloseHook
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
return|return
name|closeHooks
operator|.
name|remove
argument_list|(
name|closeable
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/** Don't call close explicitly!  This will be automatically closed at the end of the request */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closeHooks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Closeable
name|hook
range|:
name|closeHooks
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|hook
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Exception during close hook"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|closeHooks
operator|=
literal|null
expr_stmt|;
comment|// map = null;
block|}
block|}
end_class
end_unit

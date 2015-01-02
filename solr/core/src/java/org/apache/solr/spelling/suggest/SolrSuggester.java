begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|util
operator|.
name|Collection
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
name|List
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
name|spell
operator|.
name|Dictionary
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|util
operator|.
name|Accountable
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
name|util
operator|.
name|IOUtils
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
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CloseHook
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
name|search
operator|.
name|SolrIndexSearcher
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
comment|/**   * Responsible for loading the lookup and dictionary Implementations specified by   * the SolrConfig.   * Interacts (query/build/reload) with Lucene Suggesters through {@link Lookup} and  * {@link Dictionary}  * */
end_comment
begin_class
DECL|class|SolrSuggester
specifier|public
class|class
name|SolrSuggester
implements|implements
name|Accountable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrSuggester
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Name used when an unnamed suggester config is passed */
DECL|field|DEFAULT_DICT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DICT_NAME
init|=
literal|"default"
decl_stmt|;
comment|/** Label to identify the name of the suggester */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
comment|/** Location of the source data - either a path to a file, or null for the    * current IndexReader.    * */
DECL|field|LOCATION
specifier|public
specifier|static
specifier|final
name|String
name|LOCATION
init|=
literal|"sourceLocation"
decl_stmt|;
comment|/** Fully-qualified class of the {@link Lookup} implementation. */
DECL|field|LOOKUP_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|LOOKUP_IMPL
init|=
literal|"lookupImpl"
decl_stmt|;
comment|/** Fully-qualified class of the {@link Dictionary} implementation */
DECL|field|DICTIONARY_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|DICTIONARY_IMPL
init|=
literal|"dictionaryImpl"
decl_stmt|;
comment|/**    * Name of the location where to persist the dictionary. If this location    * is relative then the data will be stored under the core's dataDir. If this    * is null the storing will be disabled.    */
DECL|field|STORE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|STORE_DIR
init|=
literal|"storeDir"
decl_stmt|;
DECL|field|EMPTY_RESULT
specifier|static
name|SuggesterResult
name|EMPTY_RESULT
init|=
operator|new
name|SuggesterResult
argument_list|()
decl_stmt|;
DECL|field|sourceLocation
specifier|private
name|String
name|sourceLocation
decl_stmt|;
DECL|field|storeDir
specifier|private
name|File
name|storeDir
decl_stmt|;
DECL|field|dictionary
specifier|private
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|lookup
specifier|private
name|Lookup
name|lookup
decl_stmt|;
DECL|field|lookupImpl
specifier|private
name|String
name|lookupImpl
decl_stmt|;
DECL|field|dictionaryImpl
specifier|private
name|String
name|dictionaryImpl
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|factory
specifier|private
name|LookupFactory
name|factory
decl_stmt|;
DECL|field|dictionaryFactory
specifier|private
name|DictionaryFactory
name|dictionaryFactory
decl_stmt|;
comment|/**     * Uses the<code>config</code> and the<code>core</code> to initialize the underlying     * Lucene suggester    * */
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"init: "
operator|+
name|config
argument_list|)
expr_stmt|;
comment|// read the config
name|name
operator|=
name|config
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
operator|!=
literal|null
condition|?
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
else|:
name|DEFAULT_DICT_NAME
expr_stmt|;
name|sourceLocation
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|LOCATION
argument_list|)
expr_stmt|;
name|lookupImpl
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|LOOKUP_IMPL
argument_list|)
expr_stmt|;
name|dictionaryImpl
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|DICTIONARY_IMPL
argument_list|)
expr_stmt|;
name|String
name|store
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|STORE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookupImpl
operator|==
literal|null
condition|)
block|{
name|lookupImpl
operator|=
name|LookupFactory
operator|.
name|DEFAULT_FILE_BASED_DICT
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"No "
operator|+
name|LOOKUP_IMPL
operator|+
literal|" parameter was provided falling back to "
operator|+
name|lookupImpl
argument_list|)
expr_stmt|;
block|}
comment|// initialize appropriate lookup instance
name|factory
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|lookupImpl
argument_list|,
name|LookupFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|lookup
operator|=
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|core
operator|.
name|addCloseHook
argument_list|(
operator|new
name|CloseHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|lookup
operator|!=
literal|null
operator|&&
name|lookup
operator|instanceof
name|Closeable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Closeable
operator|)
name|lookup
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not close the suggester lookup."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{}
block|}
argument_list|)
expr_stmt|;
comment|// if store directory is provided make it or load up the lookup with its content
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|storeDir
operator|=
operator|new
name|File
argument_list|(
name|store
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|storeDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|storeDir
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|storeDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|storeDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|storeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// attempt reload of the stored lookup
try|try
block|{
name|lookup
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|factory
operator|.
name|storeFileName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Loading stored lookup data failed, possibly not cached yet"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// dictionary configuration
if|if
condition|(
name|dictionaryImpl
operator|==
literal|null
condition|)
block|{
name|dictionaryImpl
operator|=
operator|(
name|sourceLocation
operator|==
literal|null
operator|)
condition|?
name|DictionaryFactory
operator|.
name|DEFAULT_INDEX_BASED_DICT
else|:
name|DictionaryFactory
operator|.
name|DEFAULT_FILE_BASED_DICT
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"No "
operator|+
name|DICTIONARY_IMPL
operator|+
literal|" parameter was provided falling back to "
operator|+
name|dictionaryImpl
argument_list|)
expr_stmt|;
block|}
name|dictionaryFactory
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|dictionaryImpl
argument_list|,
name|DictionaryFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|dictionaryFactory
operator|.
name|setParams
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dictionary loaded with params: "
operator|+
name|config
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
comment|/** Build the underlying Lucene Suggester */
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"build()"
argument_list|)
expr_stmt|;
name|dictionary
operator|=
name|dictionaryFactory
operator|.
name|create
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeDir
operator|!=
literal|null
condition|)
block|{
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|factory
operator|.
name|storeFileName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lookup
operator|.
name|store
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|target
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Store Lookup build failed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored suggest data to: "
operator|+
name|target
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Reloads the underlying Lucene Suggester */
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"reload()"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictionary
operator|==
literal|null
operator|&&
name|storeDir
operator|!=
literal|null
condition|)
block|{
comment|// this may be a firstSearcher event, try loading it
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|factory
operator|.
name|storeFileName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|lookup
operator|.
name|load
argument_list|(
name|is
argument_list|)
condition|)
block|{
return|return;
comment|// loaded ok
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"load failed, need to build Lookup again"
argument_list|)
expr_stmt|;
block|}
comment|// loading was unsuccessful - build it again
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/** Returns suggestions based on the {@link SuggesterOptions} passed */
DECL|method|getSuggestions
specifier|public
name|SuggesterResult
name|getSuggestions
parameter_list|(
name|SuggesterOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getSuggestions: "
operator|+
name|options
operator|.
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Lookup is null - invoke suggest.build first"
argument_list|)
expr_stmt|;
return|return
name|EMPTY_RESULT
return|;
block|}
name|SuggesterResult
name|res
init|=
operator|new
name|SuggesterResult
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|suggestions
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|options
operator|.
name|token
argument_list|,
literal|false
argument_list|,
name|options
operator|.
name|count
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|getName
argument_list|()
argument_list|,
name|options
operator|.
name|token
operator|.
name|toString
argument_list|()
argument_list|,
name|suggestions
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/** Returns the unique name of the suggester */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|lookup
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|lookup
operator|.
name|getChildResources
argument_list|()
return|;
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
literal|"SolrSuggester [ name="
operator|+
name|name
operator|+
literal|", "
operator|+
literal|"sourceLocation="
operator|+
name|sourceLocation
operator|+
literal|", "
operator|+
literal|"storeDir="
operator|+
operator|(
operator|(
name|storeDir
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|storeDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|)
operator|+
literal|", "
operator|+
literal|"lookupImpl="
operator|+
name|lookupImpl
operator|+
literal|", "
operator|+
literal|"dictionaryImpl="
operator|+
name|dictionaryImpl
operator|+
literal|", "
operator|+
literal|"sizeInBytes="
operator|+
operator|(
operator|(
name|lookup
operator|!=
literal|null
operator|)
condition|?
name|String
operator|.
name|valueOf
argument_list|(
name|ramBytesUsed
argument_list|()
argument_list|)
else|:
literal|"0"
operator|)
operator|+
literal|" ]"
return|;
block|}
block|}
end_class
end_unit

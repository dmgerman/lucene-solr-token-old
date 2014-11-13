begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|*
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|DocIdSetIterator
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
name|BytesRefBuilder
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
name|handler
operator|.
name|RequestHandlerBase
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
name|handler
operator|.
name|RequestHandlerUtils
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaField
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
name|util
operator|.
name|VersionedFile
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Obtains float field values from an external file.  *  * @see org.apache.solr.schema.ExternalFileField  * @see org.apache.solr.schema.ExternalFileFieldReloader  */
end_comment
begin_class
DECL|class|FileFloatSource
specifier|public
class|class
name|FileFloatSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|private
name|SchemaField
name|field
decl_stmt|;
DECL|field|keyField
specifier|private
specifier|final
name|SchemaField
name|keyField
decl_stmt|;
DECL|field|defVal
specifier|private
specifier|final
name|float
name|defVal
decl_stmt|;
DECL|field|dataDir
specifier|private
specifier|final
name|String
name|dataDir
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
name|FileFloatSource
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates a new FileFloatSource    * @param field the source's SchemaField    * @param keyField the field to use as a key    * @param defVal the default value to use if a field has no entry in the external file    * @param datadir the directory in which to look for the external file    */
DECL|method|FileFloatSource
specifier|public
name|FileFloatSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|SchemaField
name|keyField
parameter_list|,
name|float
name|defVal
parameter_list|,
name|String
name|datadir
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|keyField
operator|=
name|keyField
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|defVal
expr_stmt|;
name|this
operator|.
name|dataDir
operator|=
name|datadir
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"float("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|off
init|=
name|readerContext
operator|.
name|docBase
decl_stmt|;
name|IndexReaderContext
name|topLevelContext
init|=
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|float
index|[]
name|arr
init|=
name|getCachedFloats
argument_list|(
name|topLevelContext
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|FloatDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
index|[
name|doc
operator|+
name|off
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
comment|// TODO: keep track of missing values
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|FileFloatSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|FileFloatSource
name|other
init|=
operator|(
name|FileFloatSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|keyField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|defVal
operator|==
name|other
operator|.
name|defVal
operator|&&
name|this
operator|.
name|dataDir
operator|.
name|equals
argument_list|(
name|other
operator|.
name|dataDir
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|FileFloatSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FileFloatSource(field="
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|",keyField="
operator|+
name|keyField
operator|.
name|getName
argument_list|()
operator|+
literal|",defVal="
operator|+
name|defVal
operator|+
literal|",dataDir="
operator|+
name|dataDir
operator|+
literal|")"
return|;
block|}
comment|/**    * Remove all cached entries.  Values are lazily loaded next time getValues() is    * called.    */
DECL|method|resetCache
specifier|public
specifier|static
name|void
name|resetCache
parameter_list|()
block|{
name|floatCache
operator|.
name|resetCache
argument_list|()
expr_stmt|;
block|}
comment|/**    * Refresh the cache for an IndexReader.  The new values are loaded in the background    * and then swapped in, so queries against the cache should not block while the reload    * is happening.    * @param reader the IndexReader whose cache needs refreshing    */
DECL|method|refreshCache
specifier|public
name|void
name|refreshCache
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Refreshing FileFloatSource cache for field {}"
argument_list|,
name|this
operator|.
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|floatCache
operator|.
name|refresh
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"FileFloatSource cache for field {} reloaded"
argument_list|,
name|this
operator|.
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCachedFloats
specifier|private
specifier|final
name|float
index|[]
name|getCachedFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|(
name|float
index|[]
operator|)
name|floatCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
DECL|field|floatCache
specifier|static
name|Cache
name|floatCache
init|=
operator|new
name|Cache
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
block|{
return|return
name|getFloats
argument_list|(
operator|(
operator|(
name|Entry
operator|)
name|key
operator|)
operator|.
name|ffs
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Internal cache. (from lucene FieldCache) */
DECL|class|Cache
specifier|abstract
specifier|static
class|class
name|Cache
block|{
DECL|field|readerCache
specifier|private
specifier|final
name|Map
name|readerCache
init|=
operator|new
name|WeakHashMap
argument_list|()
decl_stmt|;
DECL|method|createValue
specifier|protected
specifier|abstract
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
function_decl|;
DECL|method|refresh
specifier|public
name|void
name|refresh
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
block|{
name|Object
name|refreshedValues
init|=
name|createValue
argument_list|(
name|reader
argument_list|,
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|Map
name|innerCache
init|=
operator|(
name|Map
operator|)
name|readerCache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerCache
operator|==
literal|null
condition|)
block|{
name|innerCache
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|readerCache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|innerCache
argument_list|)
expr_stmt|;
block|}
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|refreshedValues
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
block|{
name|Map
name|innerCache
decl_stmt|;
name|Object
name|value
decl_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|=
operator|(
name|Map
operator|)
name|readerCache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerCache
operator|==
literal|null
condition|)
block|{
name|innerCache
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|readerCache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|innerCache
argument_list|)
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|innerCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|CreationPlaceholder
argument_list|()
expr_stmt|;
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|CreationPlaceholder
condition|)
block|{
synchronized|synchronized
init|(
name|value
init|)
block|{
name|CreationPlaceholder
name|progress
init|=
operator|(
name|CreationPlaceholder
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|progress
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|progress
operator|.
name|value
operator|=
name|createValue
argument_list|(
name|reader
argument_list|,
name|key
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|progress
operator|.
name|value
argument_list|)
expr_stmt|;
name|onlyForTesting
operator|=
name|progress
operator|.
name|value
expr_stmt|;
block|}
block|}
return|return
name|progress
operator|.
name|value
return|;
block|}
block|}
return|return
name|value
return|;
block|}
DECL|method|resetCache
specifier|public
name|void
name|resetCache
parameter_list|()
block|{
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
comment|// Map.clear() is optional and can throw UnsipportedOperationException,
comment|// but readerCache is WeakHashMap and it supports clear().
name|readerCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|onlyForTesting
specifier|static
name|Object
name|onlyForTesting
decl_stmt|;
comment|// set to the last value
DECL|class|CreationPlaceholder
specifier|static
specifier|final
class|class
name|CreationPlaceholder
block|{
DECL|field|value
name|Object
name|value
decl_stmt|;
block|}
comment|/** Expert: Every composite-key in the internal cache is of this type. */
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|ffs
specifier|final
name|FileFloatSource
name|ffs
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|FileFloatSource
name|ffs
parameter_list|)
block|{
name|this
operator|.
name|ffs
operator|=
name|ffs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Entry
operator|)
condition|)
return|return
literal|false
return|;
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
decl_stmt|;
return|return
name|ffs
operator|.
name|equals
argument_list|(
name|other
operator|.
name|ffs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ffs
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|method|getFloats
specifier|private
specifier|static
name|float
index|[]
name|getFloats
parameter_list|(
name|FileFloatSource
name|ffs
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|float
index|[]
name|vals
init|=
operator|new
name|float
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|ffs
operator|.
name|defVal
operator|!=
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|vals
argument_list|,
name|ffs
operator|.
name|defVal
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|is
decl_stmt|;
name|String
name|fname
init|=
literal|"external_"
operator|+
name|ffs
operator|.
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|is
operator|=
name|VersionedFile
operator|.
name|getLatestFile
argument_list|(
name|ffs
operator|.
name|dataDir
argument_list|,
name|fname
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// log, use defaults
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"Error opening external value source file: "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|idName
init|=
name|ffs
operator|.
name|keyField
operator|.
name|getName
argument_list|()
decl_stmt|;
name|FieldType
name|idType
init|=
name|ffs
operator|.
name|keyField
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// warning: lucene's termEnum.skipTo() is not optimized... it simply does a next()
comment|// because of this, simply ask the reader for a new termEnum rather than
comment|// trying to use skipTo()
name|List
argument_list|<
name|String
argument_list|>
name|notFound
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|notFoundCount
init|=
literal|0
decl_stmt|;
name|int
name|otherErrors
init|=
literal|0
decl_stmt|;
name|char
name|delimiter
init|=
literal|'='
decl_stmt|;
name|BytesRefBuilder
name|internalKey
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|idName
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
comment|// removing deleted docs shouldn't matter
comment|// final Bits liveDocs = MultiFields.getLiveDocs(reader);
for|for
control|(
name|String
name|line
init|;
operator|(
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|int
name|delimIndex
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
name|delimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|delimIndex
operator|<
literal|0
condition|)
continue|continue;
name|int
name|endIndex
init|=
name|line
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|delimIndex
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|line
operator|.
name|substring
argument_list|(
name|delimIndex
operator|+
literal|1
argument_list|,
name|endIndex
argument_list|)
decl_stmt|;
name|float
name|fval
decl_stmt|;
try|try
block|{
name|idType
operator|.
name|readableToIndexed
argument_list|(
name|key
argument_list|,
name|internalKey
argument_list|)
expr_stmt|;
name|fval
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|++
name|otherErrors
operator|<=
literal|10
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"Error loading external value source + fileName + "
operator|+
name|e
operator|+
operator|(
name|otherErrors
operator|<
literal|10
condition|?
literal|""
else|:
literal|"\tSkipping future errors for this file."
operator|)
argument_list|)
expr_stmt|;
block|}
continue|continue;
comment|// go to next line in file.. leave values as default.
block|}
if|if
condition|(
operator|!
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|internalKey
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|notFoundCount
operator|<
literal|10
condition|)
block|{
comment|// collect first 10 not found for logging
name|notFound
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|notFoundCount
operator|++
expr_stmt|;
continue|continue;
block|}
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|vals
index|[
name|doc
index|]
operator|=
name|fval
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// log, use defaults
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"Error loading external value source: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// swallow exceptions on close so we don't override any
comment|// exceptions that happened in the loop
try|try
block|{
name|r
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
block|{}
block|}
name|SolrCore
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"Loaded external value source "
operator|+
name|fname
operator|+
operator|(
name|notFoundCount
operator|==
literal|0
condition|?
literal|""
else|:
literal|" :"
operator|+
name|notFoundCount
operator|+
literal|" missing keys "
operator|+
name|notFound
operator|)
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
DECL|class|ReloadCacheRequestHandler
specifier|public
specifier|static
class|class
name|ReloadCacheRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ReloadCacheRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|FileFloatSource
operator|.
name|resetCache
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"readerCache has been reset."
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|null
argument_list|)
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|RequestHandlerUtils
operator|.
name|handleCommit
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Reload readerCache request handler"
return|;
block|}
block|}
block|}
end_class
end_unit

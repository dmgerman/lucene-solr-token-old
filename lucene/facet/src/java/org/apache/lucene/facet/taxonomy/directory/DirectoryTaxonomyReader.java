begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
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
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|Consts
operator|.
name|LoadFullPathOnly
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
name|index
operator|.
name|CorruptIndexException
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|DocsEnum
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|MultiFields
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
name|store
operator|.
name|Directory
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
name|BytesRef
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
name|lucene
operator|.
name|util
operator|.
name|collections
operator|.
name|LRUHashMap
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link TaxonomyReader} which retrieves stored taxonomy information from a  * {@link Directory}.  *<P>  * Reading from the on-disk index on every method call is too slow, so this  * implementation employs caching: Some methods cache recent requests and their  * results, while other methods prefetch all the data into memory and then  * provide answers directly from in-memory tables. See the documentation of  * individual methods for comments on their performance.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DirectoryTaxonomyReader
specifier|public
class|class
name|DirectoryTaxonomyReader
extends|extends
name|TaxonomyReader
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DirectoryTaxonomyReader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_CACHE_VALUE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE_VALUE
init|=
literal|4000
decl_stmt|;
DECL|field|taxoWriter
specifier|private
specifier|final
name|DirectoryTaxonomyWriter
name|taxoWriter
decl_stmt|;
DECL|field|taxoEpoch
specifier|private
specifier|final
name|long
name|taxoEpoch
decl_stmt|;
comment|// used in doOpenIfChanged
DECL|field|indexReader
specifier|private
specifier|final
name|DirectoryReader
name|indexReader
decl_stmt|;
comment|// TODO: test DoubleBarrelLRUCache and consider using it instead
DECL|field|ordinalCache
specifier|private
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ordinalCache
decl_stmt|;
DECL|field|categoryCache
specifier|private
name|LRUHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|categoryCache
decl_stmt|;
DECL|field|taxoArrays
specifier|private
specifier|volatile
name|ParallelTaxonomyArrays
name|taxoArrays
decl_stmt|;
DECL|field|delimiter
specifier|private
name|char
name|delimiter
init|=
name|Consts
operator|.
name|DEFAULT_DELIMITER
decl_stmt|;
comment|/**    * Called only from {@link #doOpenIfChanged()}. If the taxonomy has been    * recreated, you should pass {@code null} as the caches and parent/children    * arrays.    */
DECL|method|DirectoryTaxonomyReader
name|DirectoryTaxonomyReader
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|,
name|DirectoryTaxonomyWriter
name|taxoWriter
parameter_list|,
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ordinalCache
parameter_list|,
name|LRUHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|categoryCache
parameter_list|,
name|ParallelTaxonomyArrays
name|taxoArrays
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
name|this
operator|.
name|taxoWriter
operator|=
name|taxoWriter
expr_stmt|;
name|this
operator|.
name|taxoEpoch
operator|=
name|taxoWriter
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|taxoWriter
operator|.
name|getTaxonomyEpoch
argument_list|()
expr_stmt|;
comment|// use the same instance of the cache, note the protective code in getOrdinal and getPath
name|this
operator|.
name|ordinalCache
operator|=
name|ordinalCache
operator|==
literal|null
condition|?
operator|new
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
else|:
name|ordinalCache
expr_stmt|;
name|this
operator|.
name|categoryCache
operator|=
name|categoryCache
operator|==
literal|null
condition|?
operator|new
name|LRUHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
else|:
name|categoryCache
expr_stmt|;
name|this
operator|.
name|taxoArrays
operator|=
name|taxoArrays
operator|!=
literal|null
condition|?
operator|new
name|ParallelTaxonomyArrays
argument_list|(
name|indexReader
argument_list|,
name|taxoArrays
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
comment|/**    * Open for reading a taxonomy stored in a given {@link Directory}.    *     * @param directory    *          The {@link Directory} in which the taxonomy resides.    * @throws CorruptIndexException    *           if the Taxonomy is corrupt.    * @throws IOException    *           if another error occurred.    */
DECL|method|DirectoryTaxonomyReader
specifier|public
name|DirectoryTaxonomyReader
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|indexReader
operator|=
name|openIndexReader
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|taxoWriter
operator|=
literal|null
expr_stmt|;
name|taxoEpoch
operator|=
operator|-
literal|1
expr_stmt|;
comment|// These are the default cache sizes; they can be configured after
comment|// construction with the cache's setMaxSize() method
name|ordinalCache
operator|=
operator|new
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
expr_stmt|;
name|categoryCache
operator|=
operator|new
name|LRUHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Opens a {@link DirectoryTaxonomyReader} over the given    * {@link DirectoryTaxonomyWriter} (for NRT).    *     * @param taxoWriter    *          The {@link DirectoryTaxonomyWriter} from which to obtain newly    *          added categories, in real-time.    */
DECL|method|DirectoryTaxonomyReader
specifier|public
name|DirectoryTaxonomyReader
parameter_list|(
name|DirectoryTaxonomyWriter
name|taxoWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|taxoWriter
operator|=
name|taxoWriter
expr_stmt|;
name|taxoEpoch
operator|=
name|taxoWriter
operator|.
name|getTaxonomyEpoch
argument_list|()
expr_stmt|;
name|indexReader
operator|=
name|openIndexReader
argument_list|(
name|taxoWriter
operator|.
name|getInternalIndexWriter
argument_list|()
argument_list|)
expr_stmt|;
comment|// These are the default cache sizes; they can be configured after
comment|// construction with the cache's setMaxSize() method
name|ordinalCache
operator|=
operator|new
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
expr_stmt|;
name|categoryCache
operator|=
operator|new
name|LRUHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_CACHE_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|getLabel
specifier|private
name|String
name|getLabel
parameter_list|(
name|int
name|catID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// Since the cache is shared with DTR instances allocated from
comment|// doOpenIfChanged, we need to ensure that the ordinal is one that this DTR
comment|// instance recognizes. Therefore we do this check up front, before we hit
comment|// the cache.
if|if
condition|(
name|catID
operator|<
literal|0
operator|||
name|catID
operator|>=
name|indexReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO: can we use an int-based hash impl, such as IntToObjectMap,
comment|// wrapped as LRU?
name|Integer
name|catIDInteger
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|catID
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|categoryCache
init|)
block|{
name|String
name|res
init|=
name|categoryCache
operator|.
name|get
argument_list|(
name|catIDInteger
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
return|return
name|res
return|;
block|}
block|}
specifier|final
name|LoadFullPathOnly
name|loader
init|=
operator|new
name|LoadFullPathOnly
argument_list|()
decl_stmt|;
name|indexReader
operator|.
name|document
argument_list|(
name|catID
argument_list|,
name|loader
argument_list|)
expr_stmt|;
name|String
name|ret
init|=
name|loader
operator|.
name|getFullPath
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|categoryCache
init|)
block|{
name|categoryCache
operator|.
name|put
argument_list|(
name|catIDInteger
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|initTaxoArrays
specifier|private
specifier|synchronized
name|void
name|initTaxoArrays
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|taxoArrays
operator|==
literal|null
condition|)
block|{
comment|// according to Java Concurrency in Practice, this might perform better on
comment|// some JVMs, because the array initialization doesn't happen on the
comment|// volatile member.
name|ParallelTaxonomyArrays
name|tmpArrays
init|=
operator|new
name|ParallelTaxonomyArrays
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|taxoArrays
operator|=
name|tmpArrays
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoArrays
operator|=
literal|null
expr_stmt|;
comment|// do not clear() the caches, as they may be used by other DTR instances.
name|ordinalCache
operator|=
literal|null
expr_stmt|;
name|categoryCache
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Implements the opening of a new {@link DirectoryTaxonomyReader} instance if    * the taxonomy has changed.    *     *<p>    *<b>NOTE:</b> the returned {@link DirectoryTaxonomyReader} shares the    * ordinal and category caches with this reader. This is not expected to cause    * any issues, unless the two instances continue to live. The reader    * guarantees that the two instances cannot affect each other in terms of    * correctness of the caches, however if the size of the cache is changed    * through {@link #setCacheSize(int)}, it will affect both reader instances.    */
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
name|DirectoryTaxonomyReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|r2
decl_stmt|;
if|if
condition|(
name|taxoWriter
operator|==
literal|null
condition|)
block|{
comment|// not NRT
name|r2
operator|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// NRT
name|r2
operator|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|indexReader
argument_list|,
name|taxoWriter
operator|.
name|getInternalIndexWriter
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r2
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// no changes, nothing to do
block|}
comment|// check if the taxonomy was recreated
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|boolean
name|recreated
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|taxoWriter
operator|==
literal|null
condition|)
block|{
comment|// not NRT, check epoch from commit data
name|String
name|t1
init|=
name|indexReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|DirectoryTaxonomyWriter
operator|.
name|INDEX_EPOCH
argument_list|)
decl_stmt|;
name|String
name|t2
init|=
name|r2
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|DirectoryTaxonomyWriter
operator|.
name|INDEX_EPOCH
argument_list|)
decl_stmt|;
if|if
condition|(
name|t1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|t2
operator|!=
literal|null
condition|)
block|{
name|recreated
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|t1
operator|.
name|equals
argument_list|(
name|t2
argument_list|)
condition|)
block|{
comment|// t1 != null and t2 cannot be null b/c DirTaxoWriter always puts the commit data.
comment|// it's ok to use String.equals because we require the two epoch values to be the same.
name|recreated
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// NRT, compare current taxoWriter.epoch() vs the one that was given at construction
if|if
condition|(
name|taxoEpoch
operator|!=
name|taxoWriter
operator|.
name|getTaxonomyEpoch
argument_list|()
condition|)
block|{
name|recreated
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|final
name|DirectoryTaxonomyReader
name|newtr
decl_stmt|;
if|if
condition|(
name|recreated
condition|)
block|{
comment|// if recreated, do not reuse anything from this instace. the information
comment|// will be lazily computed by the new instance when needed.
name|newtr
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|r2
argument_list|,
name|taxoWriter
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newtr
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|r2
argument_list|,
name|taxoWriter
argument_list|,
name|ordinalCache
argument_list|,
name|categoryCache
argument_list|,
name|taxoArrays
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|newtr
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|r2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|openIndexReader
specifier|protected
name|DirectoryReader
name|openIndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
return|;
block|}
DECL|method|openIndexReader
specifier|protected
name|DirectoryReader
name|openIndexReader
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Expert: returns the underlying {@link DirectoryReader} instance that is    * used by this {@link TaxonomyReader}.    */
DECL|method|getInternalIndexReader
name|DirectoryReader
name|getInternalIndexReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|indexReader
return|;
block|}
annotation|@
name|Override
DECL|method|getParallelTaxonomyArrays
specifier|public
name|ParallelTaxonomyArrays
name|getParallelTaxonomyArrays
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|taxoArrays
operator|==
literal|null
condition|)
block|{
name|initTaxoArrays
argument_list|()
expr_stmt|;
block|}
return|return
name|taxoArrays
return|;
block|}
annotation|@
name|Override
DECL|method|getCommitUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCommitUserData
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|indexReader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinal
specifier|public
name|int
name|getOrdinal
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|categoryPath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|ROOT_ORDINAL
return|;
block|}
name|String
name|path
init|=
name|categoryPath
operator|.
name|toString
argument_list|(
name|delimiter
argument_list|)
decl_stmt|;
comment|// First try to find the answer in the LRU cache:
synchronized|synchronized
init|(
name|ordinalCache
init|)
block|{
name|Integer
name|res
init|=
name|ordinalCache
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|res
operator|.
name|intValue
argument_list|()
operator|<
name|indexReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// Since the cache is shared with DTR instances allocated from
comment|// doOpenIfChanged, we need to ensure that the ordinal is one that
comment|// this DTR instance recognizes.
return|return
name|res
operator|.
name|intValue
argument_list|()
return|;
block|}
else|else
block|{
comment|// if we get here, it means that the category was found in the cache,
comment|// but is not recognized by this TR instance. Therefore there's no
comment|// need to continue search for the path on disk, because we won't find
comment|// it there too.
return|return
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
return|;
block|}
block|}
block|}
comment|// If we're still here, we have a cache miss. We need to fetch the
comment|// value from disk, and then also put it in the cache:
name|int
name|ret
init|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
decl_stmt|;
name|DocsEnum
name|docs
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|indexReader
argument_list|,
literal|null
argument_list|,
name|Consts
operator|.
name|FULL
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|path
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
operator|&&
name|docs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|ret
operator|=
name|docs
operator|.
name|docID
argument_list|()
expr_stmt|;
comment|// we only store the fact that a category exists, not its inexistence.
comment|// This is required because the caches are shared with new DTR instances
comment|// that are allocated from doOpenIfChanged. Therefore, if we only store
comment|// information about found categories, we cannot accidently tell a new
comment|// generation of DTR that a category does not exist.
synchronized|synchronized
init|(
name|ordinalCache
init|)
block|{
name|ordinalCache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|ret
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getParent
specifier|public
name|int
name|getParent
parameter_list|(
name|int
name|ordinal
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|CategoryPath
name|getPath
parameter_list|(
name|int
name|ordinal
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// TODO (Facet): Currently, the LRU cache we use (getCategoryCache) holds
comment|// strings with delimiters, not CategoryPath objects, so even if
comment|// we have a cache hit, we need to process the string and build a new
comment|// CategoryPath object every time. What is preventing us from putting
comment|// the actual CategoryPath object in the cache is the fact that these
comment|// objects are mutable. So we should create an immutable (read-only)
comment|// interface that CategoryPath implements, and this method should
comment|// return this interface, not the writable CategoryPath.
name|String
name|label
init|=
name|getLabel
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|CategoryPath
argument_list|(
name|label
argument_list|,
name|delimiter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|boolean
name|getPath
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|CategoryPath
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|label
init|=
name|getLabel
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|indexReader
operator|.
name|numDocs
argument_list|()
return|;
block|}
comment|/**    * setCacheSize controls the maximum allowed size of each of the caches    * used by {@link #getPath(int)} and {@link #getOrdinal(CategoryPath)}.    *<P>    * Currently, if the given size is smaller than the current size of    * a cache, it will not shrink, and rather we be limited to its current    * size.    * @param size the new maximum cache size, in number of entries.    */
DECL|method|setCacheSize
specifier|public
name|void
name|setCacheSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|categoryCache
init|)
block|{
name|categoryCache
operator|.
name|setMaxSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|ordinalCache
init|)
block|{
name|ordinalCache
operator|.
name|setMaxSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * setDelimiter changes the character that the taxonomy uses in its    * internal storage as a delimiter between category components. Do not    * use this method unless you really know what you are doing.    *<P>    * If you do use this method, make sure you call it before any other    * methods that actually queries the taxonomy. Moreover, make sure you    * always pass the same delimiter for all LuceneTaxonomyWriter and    * LuceneTaxonomyReader objects you create.    */
DECL|method|setDelimiter
specifier|public
name|void
name|setDelimiter
parameter_list|(
name|char
name|delimiter
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|upperl
init|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upperl
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|CategoryPath
name|category
init|=
name|this
operator|.
name|getPath
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|category
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
operator|+
literal|": NULL!! \n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|category
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
operator|+
literal|": EMPTY STRING!! \n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
name|i
operator|+
literal|": "
operator|+
name|category
operator|.
name|toString
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|logger
operator|.
name|log
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

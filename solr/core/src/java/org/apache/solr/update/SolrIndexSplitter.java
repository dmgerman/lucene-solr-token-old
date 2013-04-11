begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|AtomicReader
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
name|AtomicReaderContext
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
name|Fields
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
name|FilterAtomicReader
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
name|IndexReader
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
name|Terms
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
name|TermsEnum
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
name|Bits
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
name|CharsRef
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
name|OpenBitSet
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
name|cloud
operator|.
name|DocRouter
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
name|Hash
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
name|search
operator|.
name|SolrIndexSearcher
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
name|RefCounted
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
name|IOException
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
name|List
import|;
end_import
begin_class
DECL|class|SolrIndexSplitter
specifier|public
class|class
name|SolrIndexSplitter
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexSplitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|field
name|SchemaField
name|field
decl_stmt|;
DECL|field|ranges
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
decl_stmt|;
DECL|field|rangesArr
name|DocRouter
operator|.
name|Range
index|[]
name|rangesArr
decl_stmt|;
comment|// same as ranges list, but an array for extra speed in inner loops
DECL|field|paths
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
DECL|field|cores
name|List
argument_list|<
name|SolrCore
argument_list|>
name|cores
decl_stmt|;
DECL|field|numPieces
name|int
name|numPieces
decl_stmt|;
DECL|field|currPartition
name|int
name|currPartition
init|=
literal|0
decl_stmt|;
DECL|method|SolrIndexSplitter
specifier|public
name|SolrIndexSplitter
parameter_list|(
name|SplitIndexCommand
name|cmd
parameter_list|)
block|{
name|field
operator|=
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|ranges
operator|=
name|cmd
operator|.
name|ranges
expr_stmt|;
name|paths
operator|=
name|cmd
operator|.
name|paths
expr_stmt|;
name|cores
operator|=
name|cmd
operator|.
name|cores
expr_stmt|;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
name|numPieces
operator|=
name|paths
operator|!=
literal|null
condition|?
name|paths
operator|.
name|size
argument_list|()
else|:
name|cores
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numPieces
operator|=
name|ranges
operator|.
name|size
argument_list|()
expr_stmt|;
name|rangesArr
operator|=
name|ranges
operator|.
name|toArray
argument_list|(
operator|new
name|DocRouter
operator|.
name|Range
index|[
name|ranges
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|split
specifier|public
name|void
name|split
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OpenBitSet
index|[]
argument_list|>
name|segmentDocSets
init|=
operator|new
name|ArrayList
argument_list|<
name|OpenBitSet
index|[]
argument_list|>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SolrIndexSplitter: partitions="
operator|+
name|numPieces
operator|+
literal|" segments="
operator|+
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicReaderContext
name|readerContext
range|:
name|leaves
control|)
block|{
assert|assert
name|readerContext
operator|.
name|ordInParent
operator|==
name|segmentDocSets
operator|.
name|size
argument_list|()
assert|;
comment|// make sure we're going in order
name|OpenBitSet
index|[]
name|docSets
init|=
name|split
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
name|segmentDocSets
operator|.
name|add
argument_list|(
name|docSets
argument_list|)
expr_stmt|;
block|}
comment|// would it be more efficient to write segment-at-a-time to each new index?
comment|// - need to worry about number of open descriptors
comment|// - need to worry about if IW.addIndexes does a sync or not...
comment|// - would be more efficient on the read side, but prob less efficient merging
name|IndexReader
index|[]
name|subReaders
init|=
operator|new
name|IndexReader
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|partitionNumber
init|=
literal|0
init|;
name|partitionNumber
operator|<
name|numPieces
condition|;
name|partitionNumber
operator|++
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrIndexSplitter: partition #"
operator|+
name|partitionNumber
operator|+
operator|(
name|ranges
operator|!=
literal|null
condition|?
literal|" range="
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|segmentNumber
init|=
literal|0
init|;
name|segmentNumber
operator|<
name|subReaders
operator|.
name|length
condition|;
name|segmentNumber
operator|++
control|)
block|{
name|subReaders
index|[
name|segmentNumber
index|]
operator|=
operator|new
name|LiveDocsReader
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|segmentNumber
argument_list|)
argument_list|,
name|segmentDocSets
operator|.
name|get
argument_list|(
name|segmentNumber
argument_list|)
index|[
name|partitionNumber
index|]
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|iwRef
init|=
literal|null
decl_stmt|;
name|IndexWriter
name|iw
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|SolrCore
name|subCore
init|=
name|cores
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
decl_stmt|;
name|iwRef
operator|=
name|subCore
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getIndexWriter
argument_list|(
name|subCore
argument_list|)
expr_stmt|;
name|iw
operator|=
name|iwRef
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
name|core
init|=
name|searcher
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|paths
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
decl_stmt|;
name|iw
operator|=
name|SolrIndexWriter
operator|.
name|create
argument_list|(
literal|"SplittingIndexWriter"
operator|+
name|partitionNumber
operator|+
operator|(
name|ranges
operator|!=
literal|null
condition|?
literal|" "
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|partitionNumber
argument_list|)
else|:
literal|""
operator|)
argument_list|,
name|path
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
literal|true
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// This merges the subreaders and will thus remove deletions (i.e. no optimize needed)
name|iw
operator|.
name|addIndexes
argument_list|(
name|subReaders
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|iwRef
operator|!=
literal|null
condition|)
block|{
name|iwRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|iw
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|iw
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|split
name|OpenBitSet
index|[]
name|split
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|AtomicReader
name|reader
init|=
name|readerContext
operator|.
name|reader
argument_list|()
decl_stmt|;
name|OpenBitSet
index|[]
name|docSets
init|=
operator|new
name|OpenBitSet
index|[
name|numPieces
index|]
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
name|docSets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docSets
index|[
name|i
index|]
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
name|fields
operator|.
name|terms
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
return|return
name|docSets
return|;
name|BytesRef
name|term
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
break|break;
comment|// figure out the hash for the term
comment|// TODO: hook in custom hashes (or store hashes)
comment|// TODO: performance implications of using indexedToReadable?
name|CharsRef
name|ref
init|=
operator|new
name|CharsRef
argument_list|(
name|term
operator|.
name|length
argument_list|)
decl_stmt|;
name|ref
operator|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|int
name|hash
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|ref
argument_list|,
name|ref
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|length
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
name|docSets
index|[
name|currPartition
index|]
operator|.
name|fastSet
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|currPartition
operator|=
operator|(
name|currPartition
operator|+
literal|1
operator|)
operator|%
name|numPieces
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rangesArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// inner-loop: use array here for extra speed.
if|if
condition|(
name|rangesArr
index|[
name|i
index|]
operator|.
name|includes
argument_list|(
name|hash
argument_list|)
condition|)
block|{
name|docSets
index|[
name|i
index|]
operator|.
name|fastSet
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|docSets
return|;
block|}
comment|// change livedocs on the reader to delete those docs we don't want
DECL|class|LiveDocsReader
specifier|static
class|class
name|LiveDocsReader
extends|extends
name|FilterAtomicReader
block|{
DECL|field|liveDocs
specifier|final
name|OpenBitSet
name|liveDocs
decl_stmt|;
DECL|field|numDocs
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|method|LiveDocsReader
specifier|public
name|LiveDocsReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|OpenBitSet
name|liveDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
operator|(
name|int
operator|)
name|liveDocs
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|liveDocs
return|;
block|}
block|}
block|}
end_class
end_unit

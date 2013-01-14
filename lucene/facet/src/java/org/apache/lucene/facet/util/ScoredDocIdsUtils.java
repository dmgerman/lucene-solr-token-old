begin_unit
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|util
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
name|Arrays
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
name|search
operator|.
name|ScoredDocIDs
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
name|search
operator|.
name|ScoredDocIDsIterator
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
name|DocIdSet
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
name|FixedBitSet
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
name|OpenBitSetDISI
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Utility methods for Scored Doc IDs.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ScoredDocIdsUtils
specifier|public
class|class
name|ScoredDocIdsUtils
block|{
comment|/**    * Create a complement of the input set. The returned {@link ScoredDocIDs}    * does not contain any scores, which makes sense given that the complementing    * documents were not scored.    *     * Note: the complement set does NOT contain doc ids which are noted as deleted by the given reader    *     * @param docids to be complemented.    * @param reader holding the number of documents& information about deletions.    */
DECL|method|getComplementSet
specifier|public
specifier|final
specifier|static
name|ScoredDocIDs
name|getComplementSet
parameter_list|(
specifier|final
name|ScoredDocIDs
name|docids
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|docids
operator|.
name|getDocIDs
argument_list|()
decl_stmt|;
specifier|final
name|FixedBitSet
name|complement
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|instanceof
name|FixedBitSet
condition|)
block|{
comment|// That is the most common case, if ScoredDocIdsCollector was used.
name|complement
operator|=
operator|(
operator|(
name|FixedBitSet
operator|)
name|docIdSet
operator|)
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|complement
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iter
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|maxDoc
condition|)
block|{
name|complement
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|complement
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|clearDeleted
argument_list|(
name|reader
argument_list|,
name|complement
argument_list|)
expr_stmt|;
return|return
name|createScoredDocIds
argument_list|(
name|complement
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
comment|/** Clear all deleted documents from a given open-bit-set according to a given reader */
DECL|method|clearDeleted
specifier|private
specifier|static
name|void
name|clearDeleted
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|FixedBitSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If there are no deleted docs
if|if
condition|(
operator|!
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
return|return;
comment|// return immediately
block|}
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|r
operator|.
name|maxDoc
argument_list|()
operator|+
name|context
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
block|{
comment|// skip this segment
continue|continue;
block|}
if|if
condition|(
operator|!
name|r
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
comment|// skip all docs that belong to this reader as it has no deletions
while|while
condition|(
operator|(
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|maxDoc
condition|)
block|{}
continue|continue;
block|}
name|Bits
name|liveDocs
init|=
name|r
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
do|do
block|{
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
operator|-
name|context
operator|.
name|docBase
argument_list|)
condition|)
block|{
name|set
operator|.
name|clear
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|(
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|maxDoc
condition|)
do|;
block|}
block|}
comment|/**    * Create a subset of an existing ScoredDocIDs object.    *     * @param allDocIds orginal set    * @param sampleSet Doc Ids of the subset.    */
DECL|method|createScoredDocIDsSubset
specifier|public
specifier|static
specifier|final
name|ScoredDocIDs
name|createScoredDocIDsSubset
parameter_list|(
specifier|final
name|ScoredDocIDs
name|allDocIds
parameter_list|,
specifier|final
name|int
index|[]
name|sampleSet
parameter_list|)
throws|throws
name|IOException
block|{
comment|// sort so that we can scan docs in order
specifier|final
name|int
index|[]
name|docids
init|=
name|sampleSet
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|docids
argument_list|)
expr_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|docids
operator|.
name|length
index|]
decl_stmt|;
comment|// fetch scores and compute size
name|ScoredDocIDsIterator
name|it
init|=
name|allDocIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|next
argument_list|()
operator|&&
name|n
operator|<
name|docids
operator|.
name|length
condition|)
block|{
name|int
name|doc
init|=
name|it
operator|.
name|getDocID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|docids
index|[
name|n
index|]
condition|)
block|{
name|scores
index|[
name|n
index|]
operator|=
name|it
operator|.
name|getScore
argument_list|()
expr_stmt|;
operator|++
name|n
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|size
init|=
name|n
decl_stmt|;
return|return
operator|new
name|ScoredDocIDs
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
block|{
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
while|while
condition|(
name|next
operator|<
name|size
operator|&&
name|docids
index|[
name|next
operator|++
index|]
operator|<
name|target
condition|)
block|{                 }
return|return
name|next
operator|==
name|size
condition|?
name|NO_MORE_DOCS
else|:
name|docids
index|[
name|next
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docids
index|[
name|next
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
if|if
condition|(
operator|++
name|next
operator|>=
name|size
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
return|return
name|docids
index|[
name|next
index|]
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
return|return
operator|++
name|next
operator|<
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|scores
index|[
name|next
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|docids
index|[
name|next
index|]
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a {@link ScoredDocIDs} which returns document IDs all non-deleted doc ids     * according to the given reader.     * The returned set contains the range of [0 .. reader.maxDoc ) doc ids    */
DECL|method|createAllDocsScoredDocIDs
specifier|public
specifier|static
specifier|final
name|ScoredDocIDs
name|createAllDocsScoredDocIDs
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
return|return
operator|new
name|AllLiveDocsScoredDocIDs
argument_list|(
name|reader
argument_list|)
return|;
block|}
return|return
operator|new
name|AllDocsScoredDocIDs
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/**    * Create a ScoredDocIDs out of a given docIdSet and the total number of documents in an index      */
DECL|method|createScoredDocIds
specifier|public
specifier|static
specifier|final
name|ScoredDocIDs
name|createScoredDocIds
parameter_list|(
specifier|final
name|DocIdSet
name|docIdSet
parameter_list|,
specifier|final
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|new
name|ScoredDocIDs
argument_list|()
block|{
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
block|{
return|return
name|docIdSet
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSetIterator
name|docIterator
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
try|try
block|{
return|return
name|docIterator
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|DEFAULT_SCORE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|docIterator
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
comment|// lazy size computation
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
name|OpenBitSetDISI
name|openBitSetDISI
decl_stmt|;
try|try
block|{
name|openBitSetDISI
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|docIdSet
operator|.
name|iterator
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|size
operator|=
operator|(
name|int
operator|)
name|openBitSetDISI
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
return|;
block|}
comment|/**    * All docs ScoredDocsIDs - this one is simply an 'all 1' bitset. Used when    * there are no deletions in the index and we wish to go through each and    * every document    */
DECL|class|AllDocsScoredDocIDs
specifier|private
specifier|static
class|class
name|AllDocsScoredDocIDs
implements|implements
name|ScoredDocIDs
block|{
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AllDocsScoredDocIDs
specifier|public
name|AllDocsScoredDocIDs
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIDs
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
block|{
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|<=
name|next
condition|)
block|{
name|target
operator|=
name|next
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|next
operator|=
name|target
operator|>=
name|maxDoc
condition|?
name|NO_MORE_DOCS
else|:
name|target
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
operator|++
name|next
operator|<
name|maxDoc
condition|?
name|next
else|:
name|NO_MORE_DOCS
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
block|{
try|try
block|{
specifier|final
name|DocIdSetIterator
name|iter
init|=
name|getDocIDs
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
try|try
block|{
return|return
name|iter
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// cannot happen
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|DEFAULT_SCORE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|iter
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// cannot happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * An All-docs bitset which has '0' for deleted documents and '1' for the    * rest. Useful for iterating over all 'live' documents in a given index.    *<p>    * NOTE: this class would work for indexes with no deletions at all,    * although it is recommended to use {@link AllDocsScoredDocIDs} to ease    * the performance cost of validating isDeleted() on each and every docId    */
DECL|class|AllLiveDocsScoredDocIDs
specifier|private
specifier|static
specifier|final
class|class
name|AllLiveDocsScoredDocIDs
implements|implements
name|ScoredDocIDs
block|{
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|reader
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|method|AllLiveDocsScoredDocIDs
name|AllLiveDocsScoredDocIDs
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|reader
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIDs
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
block|{
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|final
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|private
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|>
name|next
condition|)
block|{
name|next
operator|=
name|target
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
do|do
block|{
operator|++
name|next
expr_stmt|;
block|}
do|while
condition|(
name|next
operator|<
name|maxDoc
operator|&&
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|next
argument_list|)
condition|)
do|;
return|return
name|next
operator|<
name|maxDoc
condition|?
name|next
else|:
name|NO_MORE_DOCS
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
block|{
try|try
block|{
specifier|final
name|DocIdSetIterator
name|iter
init|=
name|getDocIDs
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
try|try
block|{
return|return
name|iter
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// cannot happen
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|DEFAULT_SCORE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|iter
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// cannot happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit

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
name|IOException
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
name|ExitableDirectoryReader
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
name|LeafReader
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|Term
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|search
operator|.
name|Filter
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
name|Query
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
name|TermQuery
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/** @lucene.experimental */
end_comment
begin_class
DECL|class|DocSetUtil
specifier|public
class|class
name|DocSetUtil
block|{
comment|/** The cut-off point for small sets (SortedIntDocSet) vs large sets (BitDocSet) */
DECL|method|smallSetSize
specifier|public
specifier|static
name|int
name|smallSetSize
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|(
name|maxDoc
operator|>>
literal|6
operator|)
operator|+
literal|5
return|;
comment|// The +5 is for better test coverage for small sets
block|}
comment|/**    * Iterates DocSets to test for equality - slow and for testing purposes only.    * @lucene.internal    */
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|DocSet
name|a
parameter_list|,
name|DocSet
name|b
parameter_list|)
block|{
name|DocIterator
name|iter1
init|=
name|a
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIterator
name|iter2
init|=
name|b
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|boolean
name|n1
init|=
name|iter1
operator|.
name|hasNext
argument_list|()
decl_stmt|;
name|boolean
name|n2
init|=
name|iter2
operator|.
name|hasNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|n1
operator|!=
name|n2
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|n1
condition|)
return|return
literal|true
return|;
comment|// made it to end
name|int
name|d1
init|=
name|iter1
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|d2
init|=
name|iter2
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|d1
operator|!=
name|d2
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|// implementers of DocSetProducer should not call this with themselves or it will result in an infinite loop
DECL|method|createDocSet
specifier|public
specifier|static
name|DocSet
name|createDocSet
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|DocSet
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|Filter
name|luceneFilter
init|=
name|filter
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|luceneFilter
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|DocSet
name|set
init|=
name|createDocSet
argument_list|(
name|searcher
argument_list|,
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|)
decl_stmt|;
comment|// assert equals(set, createDocSetGeneric(searcher, query));
return|return
name|set
return|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|DocSetProducer
condition|)
block|{
name|DocSet
name|set
init|=
operator|(
operator|(
name|DocSetProducer
operator|)
name|query
operator|)
operator|.
name|createDocSet
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
comment|// assert equals(set, createDocSetGeneric(searcher, query));
return|return
name|set
return|;
block|}
return|return
name|createDocSetGeneric
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|// code to produce docsets for non-docsetproducer queries
DECL|method|createDocSetGeneric
specifier|public
specifier|static
name|DocSet
name|createDocSetGeneric
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|DocSetCollector
name|collector
init|=
operator|new
name|DocSetCollector
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
comment|// This may throw an ExitableDirectoryReader.ExitingReaderException
comment|// but we should not catch it here, as we don't know how this DocSet will be used (it could be negated before use) or cached.
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|getDocSet
argument_list|()
return|;
block|}
DECL|method|createDocSet
specifier|public
specifier|static
name|DocSet
name|createDocSet
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|DirectoryReader
name|reader
init|=
name|searcher
operator|.
name|getRawReader
argument_list|()
decl_stmt|;
comment|// raw reader to avoid extra wrapping overhead
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|smallSetSize
init|=
name|smallSetSize
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|term
operator|.
name|field
argument_list|()
decl_stmt|;
name|BytesRef
name|termVal
init|=
name|term
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|int
name|maxCount
init|=
literal|0
decl_stmt|;
name|int
name|firstReader
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|PostingsEnum
index|[]
name|postList
init|=
operator|new
name|PostingsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// use array for slightly higher scanning cost, but fewer memory allocations
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
assert|assert
name|leaves
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|ord
argument_list|)
operator|==
name|ctx
assert|;
name|LeafReader
name|r
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Fields
name|f
init|=
name|r
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Terms
name|t
init|=
name|f
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
continue|continue;
comment|// field is missing
name|TermsEnum
name|te
init|=
name|t
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|te
operator|.
name|seekExact
argument_list|(
name|termVal
argument_list|)
condition|)
block|{
name|maxCount
operator|+=
name|te
operator|.
name|docFreq
argument_list|()
expr_stmt|;
name|postList
index|[
name|ctx
operator|.
name|ord
index|]
operator|=
name|te
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstReader
operator|<
literal|0
condition|)
name|firstReader
operator|=
name|ctx
operator|.
name|ord
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxCount
operator|==
literal|0
condition|)
block|{
return|return
name|DocSet
operator|.
name|EMPTY
return|;
block|}
if|if
condition|(
name|maxCount
operator|<=
name|smallSetSize
condition|)
block|{
return|return
name|createSmallSet
argument_list|(
name|leaves
argument_list|,
name|postList
argument_list|,
name|maxCount
argument_list|,
name|firstReader
argument_list|)
return|;
block|}
return|return
name|createBigSet
argument_list|(
name|leaves
argument_list|,
name|postList
argument_list|,
name|maxDoc
argument_list|,
name|firstReader
argument_list|)
return|;
block|}
DECL|method|createSmallSet
specifier|private
specifier|static
name|DocSet
name|createSmallSet
parameter_list|(
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|,
name|PostingsEnum
index|[]
name|postList
parameter_list|,
name|int
name|maxPossible
parameter_list|,
name|int
name|firstReader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|maxPossible
index|]
decl_stmt|;
name|int
name|sz
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|firstReader
init|;
name|i
operator|<
name|postList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PostingsEnum
name|postings
init|=
name|postList
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|postings
operator|==
literal|null
condition|)
continue|continue;
name|LeafReaderContext
name|ctx
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|int
name|base
init|=
name|ctx
operator|.
name|docBase
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|subId
init|=
name|postings
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|subId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|subId
argument_list|)
condition|)
continue|continue;
name|int
name|globalId
init|=
name|subId
operator|+
name|base
decl_stmt|;
name|docs
index|[
name|sz
operator|++
index|]
operator|=
name|globalId
expr_stmt|;
block|}
block|}
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|docs
argument_list|,
name|sz
argument_list|)
return|;
block|}
DECL|method|createBigSet
specifier|private
specifier|static
name|DocSet
name|createBigSet
parameter_list|(
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|,
name|PostingsEnum
index|[]
name|postList
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|int
name|firstReader
parameter_list|)
throws|throws
name|IOException
block|{
name|long
index|[]
name|bits
init|=
operator|new
name|long
index|[
name|FixedBitSet
operator|.
name|bits2words
argument_list|(
name|maxDoc
argument_list|)
index|]
decl_stmt|;
name|int
name|sz
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|firstReader
init|;
name|i
operator|<
name|postList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PostingsEnum
name|postings
init|=
name|postList
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|postings
operator|==
literal|null
condition|)
continue|continue;
name|LeafReaderContext
name|ctx
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|int
name|base
init|=
name|ctx
operator|.
name|docBase
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|subId
init|=
name|postings
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|subId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|subId
argument_list|)
condition|)
continue|continue;
name|int
name|globalId
init|=
name|subId
operator|+
name|base
decl_stmt|;
name|bits
index|[
name|globalId
operator|>>
literal|6
index|]
operator||=
operator|(
literal|1L
operator|<<
name|globalId
operator|)
expr_stmt|;
name|sz
operator|++
expr_stmt|;
block|}
block|}
name|BitDocSet
name|docSet
init|=
operator|new
name|BitDocSet
argument_list|(
operator|new
name|FixedBitSet
argument_list|(
name|bits
argument_list|,
name|maxDoc
argument_list|)
argument_list|,
name|sz
argument_list|)
decl_stmt|;
name|int
name|smallSetSize
init|=
name|smallSetSize
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|<
name|smallSetSize
condition|)
block|{
comment|// make this optional?
name|DocSet
name|smallSet
init|=
name|toSmallSet
argument_list|(
name|docSet
argument_list|)
decl_stmt|;
comment|// assert equals(docSet, smallSet);
return|return
name|smallSet
return|;
block|}
return|return
name|docSet
return|;
block|}
DECL|method|toSmallSet
specifier|public
specifier|static
name|DocSet
name|toSmallSet
parameter_list|(
name|BitDocSet
name|bitSet
parameter_list|)
block|{
name|int
name|sz
init|=
name|bitSet
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|FixedBitSet
name|bs
init|=
name|bitSet
operator|.
name|getBits
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
operator|-
literal|1
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
expr_stmt|;
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
block|}
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|docs
argument_list|)
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|search
operator|.
name|ComplexExplanation
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
name|Explanation
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
name|Scorer
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
name|Weight
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
name|BytesRefHash
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
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_class
DECL|class|TermsIncludingScoreQuery
class|class
name|TermsIncludingScoreQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|multipleValuesPerDocument
specifier|final
name|boolean
name|multipleValuesPerDocument
decl_stmt|;
DECL|field|terms
specifier|final
name|BytesRefHash
name|terms
decl_stmt|;
DECL|field|scores
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|ords
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|originalQuery
specifier|final
name|Query
name|originalQuery
decl_stmt|;
DECL|field|unwrittenOriginalQuery
specifier|final
name|Query
name|unwrittenOriginalQuery
decl_stmt|;
DECL|method|TermsIncludingScoreQuery
name|TermsIncludingScoreQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|BytesRefHash
name|terms
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|Query
name|originalQuery
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
name|multipleValuesPerDocument
operator|=
name|multipleValuesPerDocument
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|originalQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|terms
operator|.
name|sort
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|unwrittenOriginalQuery
operator|=
name|originalQuery
expr_stmt|;
block|}
DECL|method|TermsIncludingScoreQuery
specifier|private
name|TermsIncludingScoreQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|BytesRefHash
name|terms
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|int
index|[]
name|ords
parameter_list|,
name|Query
name|originalQuery
parameter_list|,
name|Query
name|unwrittenOriginalQuery
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
name|multipleValuesPerDocument
operator|=
name|multipleValuesPerDocument
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|originalQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
name|this
operator|.
name|unwrittenOriginalQuery
operator|=
name|unwrittenOriginalQuery
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"TermsIncludingScoreQuery{field=%s;originalQuery=%s}"
argument_list|,
name|field
argument_list|,
name|unwrittenOriginalQuery
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|originalQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|originalQueryRewrite
init|=
name|originalQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|originalQueryRewrite
operator|!=
name|originalQuery
condition|)
block|{
name|Query
name|rewritten
init|=
operator|new
name|TermsIncludingScoreQuery
argument_list|(
name|field
argument_list|,
name|multipleValuesPerDocument
argument_list|,
name|terms
argument_list|,
name|scores
argument_list|,
name|ords
argument_list|,
name|originalQueryRewrite
argument_list|,
name|originalQuery
argument_list|)
decl_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|originalWeight
init|=
name|originalQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|()
block|{
specifier|private
name|TermsEnum
name|segmentTermsEnum
decl_stmt|;
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|SVInnerScorer
name|scorer
init|=
operator|(
name|SVInnerScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
condition|)
block|{
return|return
name|scorer
operator|.
name|explain
argument_list|()
return|;
block|}
block|}
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"Not a match"
argument_list|)
return|;
block|}
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|TermsIncludingScoreQuery
operator|.
name|this
return|;
block|}
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|originalWeight
operator|.
name|getValueForNormalization
argument_list|()
operator|*
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|getBoost
argument_list|()
operator|*
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|getBoost
argument_list|()
return|;
block|}
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|originalWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
operator|*
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|segmentTermsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|segmentTermsEnum
argument_list|)
expr_stmt|;
if|if
condition|(
name|multipleValuesPerDocument
condition|)
block|{
return|return
operator|new
name|MVInnerScorer
argument_list|(
name|this
argument_list|,
name|acceptDocs
argument_list|,
name|segmentTermsEnum
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SVInnerScorer
argument_list|(
name|this
argument_list|,
name|acceptDocs
argument_list|,
name|segmentTermsEnum
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
comment|// This impl assumes that the 'join' values are used uniquely per doc per field. Used for one to many relations.
DECL|class|SVInnerScorer
class|class
name|SVInnerScorer
extends|extends
name|Scorer
block|{
DECL|field|spare
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|acceptDocs
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|termsEnum
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|docsEnum
name|DocsEnum
name|docsEnum
decl_stmt|;
DECL|field|reuse
name|DocsEnum
name|reuse
decl_stmt|;
DECL|field|scoreUpto
name|int
name|scoreUpto
decl_stmt|;
DECL|method|SVInnerScorer
name|SVInnerScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scores
index|[
name|ords
index|[
name|scoreUpto
index|]
index|]
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|score
argument_list|()
argument_list|,
literal|"Score based on join value "
operator|+
name|termsEnum
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docsEnum
operator|!=
literal|null
condition|?
name|docsEnum
operator|.
name|docID
argument_list|()
else|:
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
name|int
name|docId
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|docsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
return|return
name|docId
return|;
block|}
block|}
do|do
block|{
if|if
condition|(
name|upto
operator|==
name|terms
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
name|scoreUpto
operator|=
name|upto
expr_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|status
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|upto
operator|++
index|]
argument_list|,
name|spare
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|docsEnum
operator|=
name|reuse
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|reuse
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|docsEnum
operator|==
literal|null
condition|)
do|;
return|return
name|docsEnum
operator|.
name|nextDoc
argument_list|()
return|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docId
decl_stmt|;
do|do
block|{
name|docId
operator|=
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|<
name|target
condition|)
block|{
name|int
name|tempDocId
init|=
name|docsEnum
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|tempDocId
operator|==
name|target
condition|)
block|{
name|docId
operator|=
name|tempDocId
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|docId
operator|==
name|target
condition|)
block|{
break|break;
block|}
name|docsEnum
operator|=
literal|null
expr_stmt|;
comment|// goto the next ord.
block|}
do|while
condition|(
name|docId
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
do|;
return|return
name|docId
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
comment|// This impl that tracks whether a docid has already been emitted. This check makes sure that docs aren't emitted
comment|// twice for different join values. This means that the first encountered join value determines the score of a document
comment|// even if other join values yield a higher score.
DECL|class|MVInnerScorer
class|class
name|MVInnerScorer
extends|extends
name|SVInnerScorer
block|{
DECL|field|alreadyEmittedDocs
specifier|final
name|FixedBitSet
name|alreadyEmittedDocs
decl_stmt|;
DECL|method|MVInnerScorer
name|MVInnerScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|acceptDocs
argument_list|,
name|termsEnum
argument_list|)
expr_stmt|;
name|alreadyEmittedDocs
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
name|int
name|docId
decl_stmt|;
do|do
block|{
name|docId
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
block|}
do|while
condition|(
name|alreadyEmittedDocs
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
do|;
if|if
condition|(
name|docId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|docsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|alreadyEmittedDocs
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
do|do
block|{
if|if
condition|(
name|upto
operator|==
name|terms
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
name|scoreUpto
operator|=
name|upto
expr_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|status
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|upto
operator|++
index|]
argument_list|,
name|spare
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|docsEnum
operator|=
name|reuse
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|reuse
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|docsEnum
operator|==
literal|null
condition|)
do|;
name|int
name|docId
decl_stmt|;
do|do
block|{
name|docId
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
block|}
do|while
condition|(
name|alreadyEmittedDocs
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
do|;
if|if
condition|(
name|docId
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|docsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|alreadyEmittedDocs
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit

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
name|BulkScorer
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
name|LeafCollector
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
name|BitSetIterator
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
annotation|@
name|Override
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
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsIncludingScoreQuery
name|other
init|=
operator|(
name|TermsIncludingScoreQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|unwrittenOriginalQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|unwrittenOriginalQuery
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|+=
name|prime
operator|*
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|+=
name|prime
operator|*
name|unwrittenOriginalQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
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
operator|!=
literal|null
condition|)
block|{
name|segmentTermsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|segmentTermsEnum
argument_list|)
expr_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
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
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|segmentTermsEnum
operator|.
name|seekExact
argument_list|(
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|i
index|]
argument_list|,
name|spare
argument_list|)
argument_list|)
condition|)
block|{
name|docsEnum
operator|=
name|segmentTermsEnum
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
if|if
condition|(
name|docsEnum
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
condition|)
block|{
specifier|final
name|float
name|score
init|=
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|scores
index|[
name|ords
index|[
name|i
index|]
index|]
decl_stmt|;
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|score
argument_list|,
literal|"Score based on join value "
operator|+
name|segmentTermsEnum
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
block|}
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
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
comment|// what is the runtime...seems ok?
specifier|final
name|long
name|cost
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|*
name|terms
operator|.
name|size
argument_list|()
decl_stmt|;
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
name|MVInOrderScorer
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
argument_list|,
name|cost
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SVInOrderScorer
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
argument_list|,
name|cost
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
DECL|class|SVInOrderScorer
class|class
name|SVInOrderScorer
extends|extends
name|Scorer
block|{
DECL|field|matchingDocsIterator
specifier|final
name|DocIdSetIterator
name|matchingDocsIterator
decl_stmt|;
DECL|field|scores
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|field|currentDoc
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SVInOrderScorer
name|SVInOrderScorer
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
parameter_list|,
name|long
name|cost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|FixedBitSet
name|matchingDocs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|this
operator|.
name|scores
operator|=
operator|new
name|float
index|[
name|maxDoc
index|]
expr_stmt|;
name|fillDocsAndScores
argument_list|(
name|matchingDocs
argument_list|,
name|acceptDocs
argument_list|,
name|termsEnum
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchingDocsIterator
operator|=
operator|new
name|BitSetIterator
argument_list|(
name|matchingDocs
argument_list|,
name|cost
argument_list|)
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
DECL|method|fillDocsAndScores
specifier|protected
name|void
name|fillDocsAndScores
parameter_list|(
name|FixedBitSet
name|matchingDocs
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|i
index|]
argument_list|,
name|spare
argument_list|)
argument_list|)
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|scores
index|[
name|ords
index|[
name|i
index|]
index|]
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|matchingDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// In the case the same doc is also related to a another doc, a score might be overwritten. I think this
comment|// can only happen in a many-to-many relation
name|scores
index|[
name|doc
index|]
operator|=
name|score
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
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
name|currentDoc
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentDoc
operator|=
name|matchingDocsIterator
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|currentDoc
operator|=
name|matchingDocsIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
block|}
comment|// This scorer deals with the fact that a document can have more than one score from multiple related documents.
DECL|class|MVInOrderScorer
class|class
name|MVInOrderScorer
extends|extends
name|SVInOrderScorer
block|{
DECL|method|MVInOrderScorer
name|MVInOrderScorer
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
parameter_list|,
name|long
name|cost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|acceptDocs
argument_list|,
name|termsEnum
argument_list|,
name|maxDoc
argument_list|,
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillDocsAndScores
specifier|protected
name|void
name|fillDocsAndScores
parameter_list|(
name|FixedBitSet
name|matchingDocs
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|ords
index|[
name|i
index|]
argument_list|,
name|spare
argument_list|)
argument_list|)
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|TermsIncludingScoreQuery
operator|.
name|this
operator|.
name|scores
index|[
name|ords
index|[
name|i
index|]
index|]
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
control|)
block|{
comment|// I prefer this:
comment|/*if (scores[doc]< score) {               scores[doc] = score;               matchingDocs.set(doc);             }*/
comment|// But this behaves the same as MVInnerScorer and only then the tests will pass:
if|if
condition|(
operator|!
name|matchingDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|scores
index|[
name|doc
index|]
operator|=
name|score
expr_stmt|;
name|matchingDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class
end_unit

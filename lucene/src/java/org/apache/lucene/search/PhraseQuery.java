begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|Explanation
operator|.
name|IDFExplanation
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
name|ToStringUtils
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
begin_comment
comment|/** A Query that matches documents containing a particular sequence of terms.  * A PhraseQuery is built by QueryParser for input like<code>"new york"</code>.  *   *<p>This query may be combined with other terms or queries with a {@link BooleanQuery}.  */
end_comment
begin_class
DECL|class|PhraseQuery
specifier|public
class|class
name|PhraseQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
name|ArrayList
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|field|positions
specifier|private
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|positions
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|field|maxPosition
specifier|private
name|int
name|maxPosition
init|=
literal|0
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
literal|0
decl_stmt|;
comment|/** Constructs an empty phrase query. */
DECL|method|PhraseQuery
specifier|public
name|PhraseQuery
parameter_list|()
block|{}
comment|/** Sets the number of other words permitted between words in query phrase.     If zero, then this is an exact phrase search.  For larger values this works     like a<code>WITHIN</code> or<code>NEAR</code> operator.<p>The slop is in fact an edit-distance, where the units correspond to     moves of terms in the query phrase out of position.  For example, to switch     the order of two words requires two moves (the first move places the words     atop one another), so to permit re-orderings of phrases, the slop must be     at least two.<p>More exact matches are scored higher than sloppier matches, thus search     results are sorted by exactness.<p>The slop is zero by default, requiring exact matches.*/
DECL|method|setSlop
specifier|public
name|void
name|setSlop
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|slop
operator|=
name|s
expr_stmt|;
block|}
comment|/** Returns the slop.  See setSlop(). */
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/**    * Adds a term to the end of the query phrase.    * The relative position of the term is the one immediately after the last term added.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|int
name|position
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|position
operator|=
name|positions
operator|.
name|get
argument_list|(
name|positions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
name|add
argument_list|(
name|term
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a term to the end of the query phrase.    * The relative position of the term within the phrase is specified explicitly.    * This allows e.g. phrases with more than one term at the same position    * or phrases with gaps (e.g. in connection with stopwords).    *     * @param term    * @param position    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All phrase terms must be in the same field: "
operator|+
name|term
argument_list|)
throw|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|positions
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|position
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|position
operator|>
name|maxPosition
condition|)
name|maxPosition
operator|=
name|position
expr_stmt|;
block|}
comment|/** Returns the set of terms in this phrase. */
DECL|method|getTerms
specifier|public
name|Term
index|[]
name|getTerms
parameter_list|()
block|{
return|return
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Returns the relative positions of terms in this phrase.    */
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|positions
operator|.
name|size
argument_list|()
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
name|positions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|terms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tq
return|;
block|}
else|else
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
DECL|class|PostingsAndFreq
specifier|static
class|class
name|PostingsAndFreq
implements|implements
name|Comparable
argument_list|<
name|PostingsAndFreq
argument_list|>
block|{
DECL|field|postings
specifier|final
name|DocsAndPositionsEnum
name|postings
decl_stmt|;
DECL|field|docFreq
specifier|final
name|int
name|docFreq
decl_stmt|;
DECL|field|position
specifier|final
name|int
name|position
decl_stmt|;
DECL|method|PostingsAndFreq
specifier|public
name|PostingsAndFreq
parameter_list|(
name|DocsAndPositionsEnum
name|postings
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|this
operator|.
name|postings
operator|=
name|postings
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|PostingsAndFreq
name|other
parameter_list|)
block|{
return|return
name|docFreq
operator|-
name|other
operator|.
name|docFreq
return|;
block|}
block|}
DECL|class|PhraseWeight
specifier|private
class|class
name|PhraseWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|value
specifier|private
name|float
name|value
decl_stmt|;
DECL|field|idf
specifier|private
name|float
name|idf
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|field|idfExp
specifier|private
name|IDFExplanation
name|idfExp
decl_stmt|;
DECL|method|PhraseWeight
specifier|public
name|PhraseWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|similarity
operator|=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|idfExp
operator|=
name|similarity
operator|.
name|idfExplain
argument_list|(
name|terms
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|idf
operator|=
name|idfExp
operator|.
name|getIdf
argument_list|()
expr_stmt|;
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
literal|"weight("
operator|+
name|PhraseQuery
operator|.
name|this
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|PhraseQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
block|{
name|queryWeight
operator|=
name|idf
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// compute query weight
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
comment|// square it
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|queryNorm
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
expr_stmt|;
comment|// normalize query weight
name|value
operator|=
name|queryWeight
operator|*
name|idf
expr_stmt|;
comment|// idf for document
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// optimize zero-term case
return|return
literal|null
return|;
name|PostingsAndFreq
index|[]
name|postingsFreqs
init|=
operator|new
name|PostingsAndFreq
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Term
name|t
init|=
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|postingsEnum
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|delDocs
argument_list|,
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|t
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// PhraseQuery on a field that did not index
comment|// positions.
if|if
condition|(
name|postingsEnum
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|reader
argument_list|,
name|delDocs
argument_list|,
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|t
operator|.
name|bytes
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// term does exist, but has no positions
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|t
operator|.
name|field
argument_list|()
operator|+
literal|"\" was indexed with Field.omitTermFreqAndPositions=true; cannot run PhraseQuery (term="
operator|+
name|t
operator|.
name|text
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// term does not exist
return|return
literal|null
return|;
block|}
block|}
name|postingsFreqs
index|[
name|i
index|]
operator|=
operator|new
name|PostingsAndFreq
argument_list|(
name|postingsEnum
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|t
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sort by increasing docFreq order
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|postingsFreqs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
block|{
comment|// optimize exact case
name|ExactPhraseScorer
name|s
init|=
operator|new
name|ExactPhraseScorer
argument_list|(
name|this
argument_list|,
name|postingsFreqs
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|noDocs
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|s
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|SloppyPhraseScorer
argument_list|(
name|this
argument_list|,
name|postingsFreqs
argument_list|,
name|similarity
argument_list|,
name|slop
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|StringBuilder
name|docFreqs
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
name|docFreqs
operator|.
name|append
argument_list|(
name|idfExp
operator|.
name|explain
argument_list|()
argument_list|)
expr_stmt|;
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
name|i
operator|!=
literal|0
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|Term
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
name|Explanation
name|idfExpl
init|=
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
literal|"idf("
operator|+
name|field
operator|+
literal|":"
operator|+
name|docFreqs
operator|+
literal|")"
argument_list|)
decl_stmt|;
comment|// explain query weight
name|Explanation
name|queryExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|queryExpl
operator|.
name|setDescription
argument_list|(
literal|"queryWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|boostExpl
init|=
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|boostExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|queryNormExpl
init|=
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
decl_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|queryNormExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|setValue
argument_list|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|queryNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|queryExpl
argument_list|)
expr_stmt|;
comment|// explain field weight
name|Explanation
name|fieldExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|fieldExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldWeight("
operator|+
name|field
operator|+
literal|":"
operator|+
name|query
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Scorer
name|scorer
init|=
operator|(
name|Scorer
operator|)
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no matching docs"
argument_list|)
return|;
block|}
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|d
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|float
name|phraseFreq
decl_stmt|;
if|if
condition|(
name|d
operator|==
name|doc
condition|)
block|{
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
block|{
name|phraseFreq
operator|=
operator|(
operator|(
name|ExactPhraseScorer
operator|)
name|scorer
operator|)
operator|.
name|currentFreq
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|phraseFreq
operator|=
operator|(
operator|(
name|SloppyPhraseScorer
operator|)
name|scorer
operator|)
operator|.
name|currentFreq
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|phraseFreq
operator|=
literal|0.0f
expr_stmt|;
block|}
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|similarity
operator|.
name|tf
argument_list|(
name|phraseFreq
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(phraseFreq="
operator|+
name|phraseFreq
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|tfExplanation
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|fieldNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fieldNorms
init|=
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|float
name|fieldNorm
init|=
name|fieldNorms
operator|!=
literal|null
condition|?
name|similarity
operator|.
name|decodeNormValue
argument_list|(
name|fieldNorms
index|[
name|doc
index|]
argument_list|)
else|:
literal|1.0f
decl_stmt|;
name|fieldNormExpl
operator|.
name|setValue
argument_list|(
name|fieldNorm
argument_list|)
expr_stmt|;
name|fieldNormExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldNorm(field="
operator|+
name|field
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|fieldNormExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setValue
argument_list|(
name|tfExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|fieldExpl
argument_list|)
expr_stmt|;
comment|// combine them
name|result
operator|.
name|setValue
argument_list|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|1.0f
condition|)
return|return
name|fieldExpl
return|;
return|return
name|result
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
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize one-term case
name|Term
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Query
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|termQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|termQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
return|return
operator|new
name|PhraseWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
comment|/**    * @see org.apache.lucene.search.Query#extractTerms(Set)    */
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
name|queryTerms
parameter_list|)
block|{
name|queryTerms
operator|.
name|addAll
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|String
index|[]
name|pieces
init|=
operator|new
name|String
index|[
name|maxPosition
operator|+
literal|1
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|pos
init|=
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|pieces
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|s
operator|=
operator|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|s
operator|+
literal|"|"
operator|+
operator|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
name|pieces
index|[
name|pos
index|]
operator|=
name|s
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pieces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
name|pieces
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|slop
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
name|PhraseQuery
operator|)
condition|)
return|return
literal|false
return|;
name|PhraseQuery
name|other
init|=
operator|(
name|PhraseQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
operator|(
name|this
operator|.
name|slop
operator|==
name|other
operator|.
name|slop
operator|)
operator|&&
name|this
operator|.
name|terms
operator|.
name|equals
argument_list|(
name|other
operator|.
name|terms
argument_list|)
operator|&&
name|this
operator|.
name|positions
operator|.
name|equals
argument_list|(
name|other
operator|.
name|positions
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|slop
operator|^
name|terms
operator|.
name|hashCode
argument_list|()
operator|^
name|positions
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit

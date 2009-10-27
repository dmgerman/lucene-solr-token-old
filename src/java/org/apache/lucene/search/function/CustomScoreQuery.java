begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|Searcher
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
name|Similarity
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
begin_comment
comment|/**  * Query that sets document score as a programmatic function of several (sub) scores:  *<ol>  *<li>the score of its subQuery (any query)</li>  *<li>(optional) the score of its ValueSourceQuery (or queries).  *        For most simple/convenient use cases this query is likely to be a   *        {@link org.apache.lucene.search.function.FieldScoreQuery FieldScoreQuery}</li>  *</ol>  * Subclasses can modify the computation by overriding {@link #customScore(int, float, float)}.  *   *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  */
end_comment
begin_class
DECL|class|CustomScoreQuery
specifier|public
class|class
name|CustomScoreQuery
extends|extends
name|Query
block|{
DECL|field|subQuery
specifier|private
name|Query
name|subQuery
decl_stmt|;
DECL|field|valSrcQueries
specifier|private
name|ValueSourceQuery
index|[]
name|valSrcQueries
decl_stmt|;
comment|// never null (empty array if there are no valSrcQueries).
DECL|field|strict
specifier|private
name|boolean
name|strict
init|=
literal|false
decl_stmt|;
comment|// if true, valueSource part of query does not take part in weights normalization.
comment|/**    * Create a CustomScoreQuery over input subQuery.    * @param subQuery the sub query whose scored is being customed. Must not be null.     */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|)
block|{
name|this
argument_list|(
name|subQuery
argument_list|,
operator|new
name|ValueSourceQuery
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a CustomScoreQuery over input subQuery and a {@link ValueSourceQuery}.    * @param subQuery the sub query whose score is being customized. Must not be null.    * @param valSrcQuery a value source query whose scores are used in the custom score    * computation. For most simple/convenient use case this would be a     * {@link org.apache.lucene.search.function.FieldScoreQuery FieldScoreQuery}.    * This parameter is optional - it can be null.    */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|ValueSourceQuery
name|valSrcQuery
parameter_list|)
block|{
name|this
argument_list|(
name|subQuery
argument_list|,
name|valSrcQuery
operator|!=
literal|null
condition|?
comment|// don't want an array that contains a single null..
operator|new
name|ValueSourceQuery
index|[]
block|{
name|valSrcQuery
block|}
else|:
operator|new
name|ValueSourceQuery
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a CustomScoreQuery over input subQuery and a {@link ValueSourceQuery}.    * @param subQuery the sub query whose score is being customized. Must not be null.    * @param valSrcQueries value source queries whose scores are used in the custom score    * computation. For most simple/convenient use case these would be     * {@link org.apache.lucene.search.function.FieldScoreQuery FieldScoreQueries}.    * This parameter is optional - it can be null or even an empty array.    */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|ValueSourceQuery
name|valSrcQueries
index|[]
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|subQuery
operator|=
name|subQuery
expr_stmt|;
name|this
operator|.
name|valSrcQueries
operator|=
name|valSrcQueries
operator|!=
literal|null
condition|?
name|valSrcQueries
else|:
operator|new
name|ValueSourceQuery
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|subQuery
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"<subquery> must not be null!"
argument_list|)
throw|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#rewrite(org.apache.lucene.index.IndexReader) */
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
name|subQuery
operator|=
name|subQuery
operator|.
name|rewrite
argument_list|(
name|reader
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcQueries
index|[
name|i
index|]
operator|=
operator|(
name|ValueSourceQuery
operator|)
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#extractTerms(java.util.Set) */
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
name|subQuery
operator|.
name|extractTerms
argument_list|(
name|terms
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#clone() */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|CustomScoreQuery
name|clone
init|=
operator|(
name|CustomScoreQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|subQuery
operator|=
operator|(
name|Query
operator|)
name|subQuery
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|valSrcQueries
operator|=
operator|new
name|ValueSourceQuery
index|[
name|valSrcQueries
operator|.
name|length
index|]
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clone
operator|.
name|valSrcQueries
index|[
name|i
index|]
operator|=
operator|(
name|ValueSourceQuery
operator|)
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
comment|/* (non-Javadoc) @see org.apache.lucene.search.Query#toString(java.lang.String) */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|strict
condition|?
literal|" STRICT"
else|:
literal|""
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|+
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
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
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CustomScoreQuery
name|other
init|=
operator|(
name|CustomScoreQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getBoost
argument_list|()
operator|!=
name|other
operator|.
name|getBoost
argument_list|()
operator|||
operator|!
name|this
operator|.
name|subQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|subQuery
argument_list|)
operator|||
name|this
operator|.
name|valSrcQueries
operator|.
name|length
operator|!=
name|other
operator|.
name|valSrcQueries
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO simplify with Arrays.deepEquals() once moving to Java 1.5
if|if
condition|(
operator|!
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|other
operator|.
name|valSrcQueries
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|valSrcHash
init|=
literal|0
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO simplify with Arrays.deepHashcode() once moving to Java 1.5
name|valSrcHash
operator|+=
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|subQuery
operator|.
name|hashCode
argument_list|()
operator|+
name|valSrcHash
operator|)
operator|^
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compute a custom score by the subQuery score and a number of     * ValueSourceQuery scores.    *<p>     * Subclasses can override this method to modify the custom score.      *<p>    * If your custom scoring is different than the default herein you     * should override at least one of the two customScore() methods.    * If the number of ValueSourceQueries is always&lt; 2 it is     * sufficient to override the other     * {@link #customScore(int, float, float) customScore()}     * method, which is simpler.     *<p>    * The default computation herein is a multiplication of given scores:    *<pre>    *     ModifiedScore = valSrcScore * valSrcScores[0] * valSrcScores[1] * ...    *</pre>    *     * @param doc id of scored doc.     * @param subQueryScore score of that doc by the subQuery.    * @param valSrcScores scores of that doc by the ValueSourceQuery.    * @return custom score.    */
DECL|method|customScore
specifier|public
name|float
name|customScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|subQueryScore
parameter_list|,
name|float
name|valSrcScores
index|[]
parameter_list|)
block|{
if|if
condition|(
name|valSrcScores
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|customScore
argument_list|(
name|doc
argument_list|,
name|subQueryScore
argument_list|,
name|valSrcScores
index|[
literal|0
index|]
argument_list|)
return|;
block|}
if|if
condition|(
name|valSrcScores
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|customScore
argument_list|(
name|doc
argument_list|,
name|subQueryScore
argument_list|,
literal|1
argument_list|)
return|;
block|}
name|float
name|score
init|=
name|subQueryScore
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
name|valSrcScores
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|score
operator|*=
name|valSrcScores
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
comment|/**    * Compute a custom score by the subQuery score and the ValueSourceQuery score.    *<p>     * Subclasses can override this method to modify the custom score.    *<p>    * If your custom scoring is different than the default herein you     * should override at least one of the two customScore() methods.    * If the number of ValueSourceQueries is always&lt; 2 it is     * sufficient to override this customScore() method, which is simpler.     *<p>    * The default computation herein is a multiplication of the two scores:    *<pre>    *     ModifiedScore = subQueryScore * valSrcScore    *</pre>    *     * @param doc id of scored doc.     * @param subQueryScore score of that doc by the subQuery.    * @param valSrcScore score of that doc by the ValueSourceQuery.    * @return custom score.    */
DECL|method|customScore
specifier|public
name|float
name|customScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|subQueryScore
parameter_list|,
name|float
name|valSrcScore
parameter_list|)
block|{
return|return
name|subQueryScore
operator|*
name|valSrcScore
return|;
block|}
comment|/**    * Explain the custom score.    * Whenever overriding {@link #customScore(int, float, float[])},     * this method should also be overridden to provide the correct explanation    * for the part of the custom scoring.    *      * @param doc doc being explained.    * @param subQueryExpl explanation for the sub-query part.    * @param valSrcExpls explanation for the value source part.    * @return an explanation for the custom score    */
DECL|method|customExplain
specifier|public
name|Explanation
name|customExplain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|,
name|Explanation
name|valSrcExpls
index|[]
parameter_list|)
block|{
if|if
condition|(
name|valSrcExpls
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|customExplain
argument_list|(
name|doc
argument_list|,
name|subQueryExpl
argument_list|,
name|valSrcExpls
index|[
literal|0
index|]
argument_list|)
return|;
block|}
if|if
condition|(
name|valSrcExpls
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
name|float
name|valSrcScore
init|=
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
name|valSrcExpls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcScore
operator|*=
name|valSrcExpls
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|(
name|valSrcScore
operator|*
name|subQueryExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"custom score: product of:"
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
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
name|valSrcExpls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|exp
operator|.
name|addDetail
argument_list|(
name|valSrcExpls
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|exp
return|;
block|}
comment|/**    * Explain the custom score.    * Whenever overriding {@link #customScore(int, float, float)},     * this method should also be overridden to provide the correct explanation    * for the part of the custom scoring.    *      * @param doc doc being explained.    * @param subQueryExpl explanation for the sub-query part.    * @param valSrcExpl explanation for the value source part.    * @return an explanation for the custom score    */
DECL|method|customExplain
specifier|public
name|Explanation
name|customExplain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|,
name|Explanation
name|valSrcExpl
parameter_list|)
block|{
name|float
name|valSrcScore
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|valSrcExpl
operator|!=
literal|null
condition|)
block|{
name|valSrcScore
operator|*=
name|valSrcExpl
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|(
name|valSrcScore
operator|*
name|subQueryExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"custom score: product of:"
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
argument_list|)
expr_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|valSrcExpl
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
comment|//=========================== W E I G H T ============================
DECL|class|CustomWeight
specifier|private
class|class
name|CustomWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
name|Similarity
name|similarity
decl_stmt|;
DECL|field|subQueryWeight
name|Weight
name|subQueryWeight
decl_stmt|;
DECL|field|valSrcWeights
name|Weight
index|[]
name|valSrcWeights
decl_stmt|;
DECL|field|qStrict
name|boolean
name|qStrict
decl_stmt|;
DECL|method|CustomWeight
specifier|public
name|CustomWeight
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
name|this
operator|.
name|subQueryWeight
operator|=
name|subQuery
operator|.
name|weight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|valSrcWeights
operator|=
operator|new
name|Weight
index|[
name|valSrcQueries
operator|.
name|length
index|]
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
name|valSrcQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|valSrcWeights
index|[
name|i
index|]
operator|=
name|valSrcQueries
index|[
name|i
index|]
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|qStrict
operator|=
name|strict
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#getQuery() */
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|CustomScoreQuery
operator|.
name|this
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#getValue() */
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#sumOfSquaredWeights() */
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
name|subQueryWeight
operator|.
name|sumOfSquaredWeights
argument_list|()
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
name|valSrcWeights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|qStrict
condition|)
block|{
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|sumOfSquaredWeights
argument_list|()
expr_stmt|;
comment|// do not include ValueSource part in the query normalization
block|}
else|else
block|{
name|sum
operator|+=
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|sumOfSquaredWeights
argument_list|()
expr_stmt|;
block|}
block|}
name|sum
operator|*=
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// boost each sub-weight
return|return
name|sum
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#normalize(float) */
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|norm
operator|*=
name|getBoost
argument_list|()
expr_stmt|;
comment|// incorporate boost
name|subQueryWeight
operator|.
name|normalize
argument_list|(
name|norm
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
name|valSrcWeights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|qStrict
condition|)
block|{
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|normalize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// do not normalize the ValueSource part
block|}
else|else
block|{
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// Pass true for "scoresDocsInOrder", because we
comment|// require in-order scoring, even if caller does not,
comment|// since we call advance on the valSrcScorers.  Pass
comment|// false for "topScorer" because we will not invoke
comment|// score(Collector) on these scorers:
name|Scorer
name|subQueryScorer
init|=
name|subQueryWeight
operator|.
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
name|subQueryScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Scorer
index|[]
name|valSrcScorers
init|=
operator|new
name|Scorer
index|[
name|valSrcWeights
operator|.
name|length
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
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcScorers
index|[
name|i
index|]
operator|=
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
name|topScorer
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CustomScorer
argument_list|(
name|similarity
argument_list|,
name|reader
argument_list|,
name|this
argument_list|,
name|subQueryScorer
argument_list|,
name|valSrcScorers
argument_list|)
return|;
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
name|explain
init|=
name|doExplain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
decl_stmt|;
return|return
name|explain
operator|==
literal|null
condition|?
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no matching docs"
argument_list|)
else|:
name|doExplain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
return|;
block|}
DECL|method|doExplain
specifier|private
name|Explanation
name|doExplain
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
name|subQueryExpl
init|=
name|subQueryWeight
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subQueryExpl
operator|.
name|isMatch
argument_list|()
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
comment|// match
name|Explanation
index|[]
name|valSrcExpls
init|=
operator|new
name|Explanation
index|[
name|valSrcWeights
operator|.
name|length
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
name|valSrcWeights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcExpls
index|[
name|i
index|]
operator|=
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|Explanation
name|customExp
init|=
name|customExplain
argument_list|(
name|doc
argument_list|,
name|subQueryExpl
argument_list|,
name|valSrcExpls
argument_list|)
decl_stmt|;
name|float
name|sc
init|=
name|getValue
argument_list|()
operator|*
name|customExp
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Explanation
name|res
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
name|CustomScoreQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|)
decl_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
name|customExp
argument_list|)
expr_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getValue
argument_list|()
argument_list|,
literal|"queryBoost"
argument_list|)
argument_list|)
expr_stmt|;
comment|// actually using the q boost as q weight (== weight value)
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|//=========================== S C O R E R ============================
comment|/**    * A scorer that applies a (callback) function on scores of the subQuery.    */
DECL|class|CustomScorer
specifier|private
class|class
name|CustomScorer
extends|extends
name|Scorer
block|{
DECL|field|weight
specifier|private
specifier|final
name|CustomWeight
name|weight
decl_stmt|;
DECL|field|qWeight
specifier|private
specifier|final
name|float
name|qWeight
decl_stmt|;
DECL|field|subQueryScorer
specifier|private
name|Scorer
name|subQueryScorer
decl_stmt|;
DECL|field|valSrcScorers
specifier|private
name|Scorer
index|[]
name|valSrcScorers
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|vScores
specifier|private
name|float
name|vScores
index|[]
decl_stmt|;
comment|// reused in score() to avoid allocating this array for each doc
comment|// constructor
DECL|method|CustomScorer
specifier|private
name|CustomScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|CustomWeight
name|w
parameter_list|,
name|Scorer
name|subQueryScorer
parameter_list|,
name|Scorer
index|[]
name|valSrcScorers
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|qWeight
operator|=
name|w
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|subQueryScorer
operator|=
name|subQueryScorer
expr_stmt|;
name|this
operator|.
name|valSrcScorers
operator|=
name|valSrcScorers
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|vScores
operator|=
operator|new
name|float
index|[
name|valSrcScorers
operator|.
name|length
index|]
expr_stmt|;
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
name|int
name|doc
init|=
name|subQueryScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
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
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcScorers
index|[
name|i
index|]
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
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
name|subQueryScorer
operator|.
name|docID
argument_list|()
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Scorer#score() */
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vScores
index|[
name|i
index|]
operator|=
name|valSrcScorers
index|[
name|i
index|]
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
return|return
name|qWeight
operator|*
name|customScore
argument_list|(
name|subQueryScorer
operator|.
name|docID
argument_list|()
argument_list|,
name|subQueryScorer
operator|.
name|score
argument_list|()
argument_list|,
name|vScores
argument_list|)
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
name|int
name|doc
init|=
name|subQueryScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
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
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcScorers
index|[
name|i
index|]
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
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
return|return
operator|new
name|CustomWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
comment|/**    * Checks if this is strict custom scoring.    * In strict custom scoring, the ValueSource part does not participate in weight normalization.    * This may be useful when one wants full control over how scores are modified, and does     * not care about normalizing by the ValueSource part.    * One particular case where this is useful if for testing this query.       *<P>    * Note: only has effect when the ValueSource part is not null.    */
DECL|method|isStrict
specifier|public
name|boolean
name|isStrict
parameter_list|()
block|{
return|return
name|strict
return|;
block|}
comment|/**    * Set the strict mode of this query.     * @param strict The strict mode to set.    * @see #isStrict()    */
DECL|method|setStrict
specifier|public
name|void
name|setStrict
parameter_list|(
name|boolean
name|strict
parameter_list|)
block|{
name|this
operator|.
name|strict
operator|=
name|strict
expr_stmt|;
block|}
comment|/**    * A short name of this query, used in {@link #toString(String)}.    */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"custom"
return|;
block|}
block|}
end_class
end_unit

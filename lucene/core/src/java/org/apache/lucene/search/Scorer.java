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
begin_comment
comment|/**  * Expert: Common scoring functionality for different types of queries.  *  *<p>  * A<code>Scorer</code> iterates over documents matching a  * query in increasing order of doc Id.  *</p>  *<p>  * Document scores are computed using a given<code>Similarity</code>  * implementation.  *</p>  *  *<p><b>NOTE</b>: The values Float.Nan,  * Float.NEGATIVE_INFINITY and Float.POSITIVE_INFINITY are  * not valid scores.  Certain collectors (eg {@link  * TopScoreDocCollector}) will not properly collect hits  * with these scores.  */
end_comment
begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
extends|extends
name|DocIdSetIterator
block|{
DECL|field|weight
specifier|protected
specifier|final
name|Weight
name|weight
decl_stmt|;
comment|/**    * Constructs a Scorer    * @param weight The scorers<code>Weight</code>.    */
DECL|method|Scorer
specifier|protected
name|Scorer
parameter_list|(
name|Weight
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|this
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
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: Collects matching documents in a range. Hook for optimization.    * Note,<code>firstDocID</code> is added to ensure that {@link #nextDoc()}    * was called before this method.    *     * @param collector    *          The collector to which all matching documents are passed.    * @param max    *          Do not score documents past this.    * @param firstDocID    *          The first document ID (ensures {@link #nextDoc()} is called before    *          this method.    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|firstDocID
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|max
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|doc
operator|!=
name|NO_MORE_DOCS
return|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} or {@link #advance(int)}    * is called the first time, or when called from within    * {@link Collector#collect}.    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns number of matches for the current document.    *  This returns a float (not int) because    *  SloppyPhraseScorer discounts its freq according to how    *  "sloppy" the match was.    *    * @lucene.experimental */
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|this
operator|+
literal|" does not implement freq()"
argument_list|)
throw|;
block|}
comment|/** returns parent Weight    * @lucene.experimental    */
DECL|method|getWeight
specifier|public
name|Weight
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
comment|/** Returns child sub-scorers    * @lucene.experimental */
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/** a child Scorer and its relationship to its parent.    * the meaning of the relationship depends upon the parent query.     * @lucene.experimental */
DECL|class|ChildScorer
specifier|public
specifier|static
class|class
name|ChildScorer
block|{
DECL|field|child
specifier|public
specifier|final
name|Scorer
name|child
decl_stmt|;
DECL|field|relationship
specifier|public
specifier|final
name|String
name|relationship
decl_stmt|;
DECL|method|ChildScorer
specifier|public
name|ChildScorer
parameter_list|(
name|Scorer
name|child
parameter_list|,
name|String
name|relationship
parameter_list|)
block|{
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
name|this
operator|.
name|relationship
operator|=
name|relationship
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|FieldInvertState
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
name|Norm
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
begin_comment
comment|/** Expert: Default scoring implementation. */
end_comment
begin_class
DECL|class|DefaultSimilarity
specifier|public
class|class
name|DefaultSimilarity
extends|extends
name|TFIDFSimilarity
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|DefaultSimilarity
specifier|public
name|DefaultSimilarity
parameter_list|()
block|{}
comment|/** Implemented as<code>overlap / maxOverlap</code>. */
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|overlap
operator|/
operator|(
name|float
operator|)
name|maxOverlap
return|;
block|}
comment|/** Implemented as<code>1/sqrt(sumOfSquaredWeights)</code>. */
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|sumOfSquaredWeights
argument_list|)
argument_list|)
return|;
block|}
comment|/** Implemented as    *<code>state.getBoost()*lengthNorm(numTerms)</code>, where    *<code>numTerms</code> is {@link FieldInvertState#getLength()} if {@link    *  #setDiscountOverlaps} is false, else it's {@link    *  FieldInvertState#getLength()} - {@link    *  FieldInvertState#getNumOverlap()}.    *    *  @lucene.experimental */
annotation|@
name|Override
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
specifier|final
name|int
name|numTerms
decl_stmt|;
if|if
condition|(
name|discountOverlaps
condition|)
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
expr_stmt|;
else|else
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
expr_stmt|;
return|return
name|state
operator|.
name|getBoost
argument_list|()
operator|*
operator|(
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|numTerms
argument_list|)
argument_list|)
operator|)
return|;
block|}
comment|/** Implemented as<code>sqrt(freq)</code>. */
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|freq
argument_list|)
return|;
block|}
comment|/** Implemented as<code>1 / (distance + 1)</code>. */
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
operator|/
operator|(
name|distance
operator|+
literal|1
operator|)
return|;
block|}
comment|/** The default implementation returns<code>1</code> */
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
comment|/** Implemented as<code>log(numDocs/(docFreq+1)) + 1</code>. */
annotation|@
name|Override
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|long
name|docFreq
parameter_list|,
name|long
name|numDocs
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|numDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|docFreq
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
return|;
block|}
comment|/**     * True if overlap tokens (tokens with a position of increment of zero) are    * discounted from the document's length.    */
DECL|field|discountOverlaps
specifier|protected
name|boolean
name|discountOverlaps
init|=
literal|true
decl_stmt|;
comment|/** Determines whether overlap tokens (Tokens with    *  0 position increment) are ignored when computing    *  norm.  By default this is true, meaning overlap    *  tokens do not count when computing norms.    *    *  @lucene.experimental    *    *  @see #computeNorm    */
DECL|method|setDiscountOverlaps
specifier|public
name|void
name|setDiscountOverlaps
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|discountOverlaps
operator|=
name|v
expr_stmt|;
block|}
comment|/**    * Returns true if overlap tokens are discounted from the document's length.     * @see #setDiscountOverlaps     */
DECL|method|getDiscountOverlaps
specifier|public
name|boolean
name|getDiscountOverlaps
parameter_list|()
block|{
return|return
name|discountOverlaps
return|;
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
literal|"DefaultSimilarity"
return|;
block|}
block|}
end_class
end_unit

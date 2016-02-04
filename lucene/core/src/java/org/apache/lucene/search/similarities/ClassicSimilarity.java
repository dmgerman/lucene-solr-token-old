begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SmallFloat
import|;
end_import
begin_comment
comment|/**  * Expert: Default scoring implementation which {@link #encodeNormValue(float)  * encodes} norm values as a single byte before being stored. At search time,  * the norm byte value is read from the index  * {@link org.apache.lucene.store.Directory directory} and  * {@link #decodeNormValue(long) decoded} back to a float<i>norm</i> value.  * This encoding/decoding, while reducing index size, comes with the price of  * precision loss - it is not guaranteed that<i>decode(encode(x)) = x</i>. For  * instance,<i>decode(encode(0.89)) = 0.875</i>.  *<p>  * Compression of norm values to a single byte saves memory at search time,  * because once a field is referenced at search time, its norms - for all  * documents - are maintained in memory.  *<p>  * The rationale supporting such lossy compression of norm values is that given  * the difficulty (and inaccuracy) of users to express their true information  * need by a query, only big differences matter.<br>  *&nbsp;<br>  * Last, note that search time is too late to modify this<i>norm</i> part of  * scoring, e.g. by using a different {@link Similarity} for search.  */
end_comment
begin_class
DECL|class|ClassicSimilarity
specifier|public
class|class
name|ClassicSimilarity
extends|extends
name|TFIDFSimilarity
block|{
comment|/** Cache of decoded bytes. */
DECL|field|NORM_TABLE
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|NORM_TABLE
init|=
operator|new
name|float
index|[
literal|256
index|]
decl_stmt|;
static|static
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
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|NORM_TABLE
index|[
name|i
index|]
operator|=
name|SmallFloat
operator|.
name|byte315ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Sole constructor: parameter-free */
DECL|method|ClassicSimilarity
specifier|public
name|ClassicSimilarity
parameter_list|()
block|{}
comment|/** Implemented as<code>overlap / maxOverlap</code>. */
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|/**    * Encodes a normalization factor for storage in an index.    *<p>    * The encoding uses a three-bit mantissa, a five-bit exponent, and the    * zero-exponent point at 15, thus representing values from around 7x10^9 to    * 2x10^-9 with about one significant decimal digit of accuracy. Zero is also    * represented. Negative numbers are rounded up to zero. Values too large to    * represent are rounded down to the largest representable value. Positive    * values too small to represent are rounded up to the smallest positive    * representable value.    *     * @see org.apache.lucene.document.Field#setBoost(float)    * @see org.apache.lucene.util.SmallFloat    */
annotation|@
name|Override
DECL|method|encodeNormValue
specifier|public
specifier|final
name|long
name|encodeNormValue
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Decodes the norm value, assuming it is a single byte.    *     * @see #encodeNormValue(float)    */
annotation|@
name|Override
DECL|method|decodeNormValue
specifier|public
specifier|final
name|float
name|decodeNormValue
parameter_list|(
name|long
name|norm
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
call|(
name|int
call|)
argument_list|(
name|norm
operator|&
literal|0xFF
argument_list|)
index|]
return|;
comment|//& 0xFF maps negative bytes to positive above 127
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
comment|/** Implemented as<code>log((docCount+1)/(docFreq+1)) + 1</code>. */
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
name|docCount
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
operator|(
name|docCount
operator|+
literal|1
operator|)
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
literal|"ClassicSimilarity"
return|;
block|}
block|}
end_class
end_unit

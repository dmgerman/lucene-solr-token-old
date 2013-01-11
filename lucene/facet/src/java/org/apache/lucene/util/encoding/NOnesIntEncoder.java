begin_unit
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|IntsRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A variation of {@link FourFlagsIntEncoder} which translates the data as  * follows:  *<ul>  *<li>Values&ge; 2 are trnalsated to<code>value+1</code> (2&rArr; 3, 3  *&rArr; 4 and so forth).  *<li>Any<code>N</code> occurrences of 1 are encoded as a single 2.  *<li>Otherwise, each 1 is encoded as 1.  *</ul>  *<p>  * Encoding examples:  *<ul>  *<li>N = 4: the data 1,1,1,1,1 is translated to: 2, 1  *<li>N = 3: the data 1,2,3,4,1,1,1,1,5 is translated to 1,3,4,5,2,1,6  *</ul>  *<b>NOTE:</b> this encoder does not support values&le; 0 and  * {@link Integer#MAX_VALUE}. 0 is not supported because it's not supported by  * {@link FourFlagsIntEncoder} and {@link Integer#MAX_VALUE} because this  * encoder translates N to N+1, which will cause an overflow and  * {@link Integer#MAX_VALUE} will become a negative number, which is not  * supported as well.<br>  * This does not mean you cannot encode {@link Integer#MAX_VALUE}. If it is not  * the first value to encode, and you wrap this encoder with  * {@link DGapIntEncoder}, then the value that will be sent to this encoder will  * be<code>MAX_VAL - prev</code>.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|NOnesIntEncoder
specifier|public
class|class
name|NOnesIntEncoder
extends|extends
name|FourFlagsIntEncoder
block|{
DECL|field|internalBuffer
specifier|private
specifier|final
name|IntsRef
name|internalBuffer
decl_stmt|;
comment|/** Number of consecutive '1's to be translated into single target value '2'. */
DECL|field|n
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
comment|/**    * Constructs an encoder with a given value of N (N: Number of consecutive    * '1's to be translated into single target value '2').    */
DECL|method|NOnesIntEncoder
specifier|public
name|NOnesIntEncoder
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|internalBuffer
operator|=
operator|new
name|IntsRef
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|()
block|{
name|internalBuffer
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEncode
specifier|protected
name|void
name|doEncode
parameter_list|(
name|IntsRef
name|values
parameter_list|,
name|BytesRef
name|buf
parameter_list|,
name|int
name|upto
parameter_list|)
block|{
comment|// make sure the internal buffer is large enough
if|if
condition|(
name|values
operator|.
name|length
operator|>
name|internalBuffer
operator|.
name|ints
operator|.
name|length
condition|)
block|{
name|internalBuffer
operator|.
name|grow
argument_list|(
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|int
name|onesCounter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|values
operator|.
name|offset
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|int
name|value
init|=
name|values
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
comment|// every N 1's should be encoded as '2'
if|if
condition|(
operator|++
name|onesCounter
operator|==
name|n
condition|)
block|{
name|internalBuffer
operator|.
name|ints
index|[
name|internalBuffer
operator|.
name|length
operator|++
index|]
operator|=
literal|2
expr_stmt|;
name|onesCounter
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// there might have been 1's that we need to encode
while|while
condition|(
name|onesCounter
operator|>
literal|0
condition|)
block|{
operator|--
name|onesCounter
expr_stmt|;
name|internalBuffer
operator|.
name|ints
index|[
name|internalBuffer
operator|.
name|length
operator|++
index|]
operator|=
literal|1
expr_stmt|;
block|}
comment|// encode value as value+1
name|internalBuffer
operator|.
name|ints
index|[
name|internalBuffer
operator|.
name|length
operator|++
index|]
operator|=
name|value
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|// there might have been 1's that we need to encode
while|while
condition|(
name|onesCounter
operator|>
literal|0
condition|)
block|{
operator|--
name|onesCounter
expr_stmt|;
name|internalBuffer
operator|.
name|ints
index|[
name|internalBuffer
operator|.
name|length
operator|++
index|]
operator|=
literal|1
expr_stmt|;
block|}
name|super
operator|.
name|doEncode
argument_list|(
name|internalBuffer
argument_list|,
name|buf
argument_list|,
name|internalBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createMatchingDecoder
specifier|public
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
block|{
return|return
operator|new
name|NOnesIntDecoder
argument_list|(
name|n
argument_list|)
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
literal|"NOnes ("
operator|+
name|n
operator|+
literal|") ("
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit

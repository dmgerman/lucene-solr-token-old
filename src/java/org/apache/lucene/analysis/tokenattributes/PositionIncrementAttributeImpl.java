begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|Serializable
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
name|analysis
operator|.
name|TokenStream
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
name|AttributeImpl
import|;
end_import
begin_comment
comment|/** The positionIncrement determines the position of this token  * relative to the previous Token in a {@link TokenStream}, used in phrase  * searching.  *  *<p>The default value is one.  *  *<p>Some common uses for this are:<ul>  *  *<li>Set it to zero to put multiple terms in the same position.  This is  * useful if, e.g., a word has multiple stems.  Searches for phrases  * including either stem will match.  In this case, all but the first stem's  * increment should be set to zero: the increment of the first instance  * should be one.  Repeating a token with an increment of zero can also be  * used to boost the scores of matches on that token.  *  *<li>Set it to values greater than one to inhibit exact phrase matches.  * If, for example, one does not want phrases to match across removed stop  * words, then one could build a stop word filter that removes stop words and  * also sets the increment to the number of stop words removed before each  * non-stop word.  Then exact phrase queries will only match when the terms  * occur with no intervening stop words.  *  *</ul>  *   *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  *   * @see org.apache.lucene.index.TermPositions  */
end_comment
begin_class
DECL|class|PositionIncrementAttributeImpl
specifier|public
class|class
name|PositionIncrementAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|PositionIncrementAttribute
implements|,
name|Cloneable
implements|,
name|Serializable
block|{
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
comment|/** Set the position increment. The default value is one.    *    * @param positionIncrement the distance from the prior term    */
DECL|method|setPositionIncrement
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|positionIncrement
parameter_list|)
block|{
if|if
condition|(
name|positionIncrement
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Increment must be zero or greater: "
operator|+
name|positionIncrement
argument_list|)
throw|;
name|this
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
block|}
comment|/** Returns the position increment of this Token.    * @see #setPositionIncrement    */
DECL|method|getPositionIncrement
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|positionIncrement
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|positionIncrement
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|PositionIncrementAttributeImpl
condition|)
block|{
return|return
name|positionIncrement
operator|==
operator|(
operator|(
name|PositionIncrementAttributeImpl
operator|)
name|other
operator|)
operator|.
name|positionIncrement
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|positionIncrement
return|;
block|}
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|PositionIncrementAttribute
name|t
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

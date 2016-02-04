begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/** Represents int[], as a slice (offset + length) into an  *  existing int[].  The {@link #ints} member should never be null; use  *  {@link #EMPTY_INTS} if necessary.  *  *  @lucene.internal */
end_comment
begin_class
DECL|class|IntsRef
specifier|public
specifier|final
class|class
name|IntsRef
implements|implements
name|Comparable
argument_list|<
name|IntsRef
argument_list|>
implements|,
name|Cloneable
block|{
comment|/** An empty integer array for convenience */
DECL|field|EMPTY_INTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|EMPTY_INTS
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
comment|/** The contents of the IntsRef. Should never be {@code null}. */
DECL|field|ints
specifier|public
name|int
index|[]
name|ints
decl_stmt|;
comment|/** Offset of first valid integer. */
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
comment|/** Length of used ints. */
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
comment|/** Create a IntsRef with {@link #EMPTY_INTS} */
DECL|method|IntsRef
specifier|public
name|IntsRef
parameter_list|()
block|{
name|ints
operator|=
name|EMPTY_INTS
expr_stmt|;
block|}
comment|/**     * Create a IntsRef pointing to a new array of size<code>capacity</code>.    * Offset and length will both be zero.    */
DECL|method|IntsRef
specifier|public
name|IntsRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|ints
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/** This instance will directly reference ints w/o making a copy.    * ints should not be null.    */
DECL|method|IntsRef
specifier|public
name|IntsRef
parameter_list|(
name|int
index|[]
name|ints
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|ints
operator|=
name|ints
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
assert|assert
name|isValid
argument_list|()
assert|;
block|}
comment|/**    * Returns a shallow clone of this instance (the underlying ints are    *<b>not</b> copied and will be shared by both the returned object and this    * object.    *     * @see #deepCopyOf    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IntsRef
name|clone
parameter_list|()
block|{
return|return
operator|new
name|IntsRef
argument_list|(
name|ints
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
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
literal|0
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
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
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|IntsRef
condition|)
block|{
return|return
name|this
operator|.
name|intsEquals
argument_list|(
operator|(
name|IntsRef
operator|)
name|other
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|intsEquals
specifier|public
name|boolean
name|intsEquals
parameter_list|(
name|IntsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
name|other
operator|.
name|length
condition|)
block|{
name|int
name|otherUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
index|[]
name|otherInts
init|=
name|other
operator|.
name|ints
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|upto
init|=
name|offset
init|;
name|upto
operator|<
name|end
condition|;
name|upto
operator|++
operator|,
name|otherUpto
operator|++
control|)
block|{
if|if
condition|(
name|ints
index|[
name|upto
index|]
operator|!=
name|otherInts
index|[
name|otherUpto
index|]
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
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Signed int order comparison */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|IntsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|0
return|;
specifier|final
name|int
index|[]
name|aInts
init|=
name|this
operator|.
name|ints
decl_stmt|;
name|int
name|aUpto
init|=
name|this
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
index|[]
name|bInts
init|=
name|other
operator|.
name|ints
decl_stmt|;
name|int
name|bUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|length
argument_list|,
name|other
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aInt
init|=
name|aInts
index|[
name|aUpto
operator|++
index|]
decl_stmt|;
name|int
name|bInt
init|=
name|bInts
index|[
name|bUpto
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|aInt
operator|>
name|bInt
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|aInt
operator|<
name|bInt
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|this
operator|.
name|length
operator|-
name|other
operator|.
name|length
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
name|offset
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|ints
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Creates a new IntsRef that points to a copy of the ints from     *<code>other</code>    *<p>    * The returned IntsRef will have a length of other.length    * and an offset of zero.    */
DECL|method|deepCopyOf
specifier|public
specifier|static
name|IntsRef
name|deepCopyOf
parameter_list|(
name|IntsRef
name|other
parameter_list|)
block|{
return|return
operator|new
name|IntsRef
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|other
operator|.
name|ints
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
argument_list|)
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**     * Performs internal consistency checks.    * Always returns true (or throws IllegalStateException)     */
DECL|method|isValid
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
if|if
condition|(
name|ints
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ints is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is negative: "
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|>
name|ints
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is out of bounds: "
operator|+
name|length
operator|+
literal|",ints.length="
operator|+
name|ints
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset is negative: "
operator|+
name|offset
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|>
name|ints
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset out of bounds: "
operator|+
name|offset
operator|+
literal|",ints.length="
operator|+
name|ints
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length is negative: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|>
name|ints
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length out of bounds: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
operator|+
literal|",ints.length="
operator|+
name|ints
operator|.
name|length
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit

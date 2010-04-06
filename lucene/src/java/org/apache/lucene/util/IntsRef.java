begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** Represents int[], as a slice (offset + length) into an  *  existing int[].  *  *  @lucene.internal */
end_comment
begin_class
DECL|class|IntsRef
specifier|public
specifier|final
class|class
name|IntsRef
block|{
DECL|field|ints
specifier|public
name|int
index|[]
name|ints
decl_stmt|;
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
DECL|method|IntsRef
specifier|public
name|IntsRef
parameter_list|()
block|{   }
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
block|}
DECL|method|IntsRef
specifier|public
name|IntsRef
parameter_list|(
name|IntsRef
name|other
parameter_list|)
block|{
name|copy
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|IntsRef
argument_list|(
name|this
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
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|IntsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|ints
operator|==
literal|null
condition|)
block|{
name|ints
operator|=
operator|new
name|int
index|[
name|other
operator|.
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|ints
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ints
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|ints
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|ints
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
if|if
condition|(
name|ints
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|ints
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ints
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

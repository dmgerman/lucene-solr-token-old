begin_unit
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A collection of bit utilities.  */
end_comment
begin_class
DECL|class|BitUtil
class|class
name|BitUtil
block|{
comment|/**      * The set of least-significant bits for a given<code>byte</code>.<code>-1</code>      * is used if no bits are set (so as to not be confused with "index of zero"      * meaning that the least significant bit is the 0th (1st) bit).      *      * @see #leastSignificantBit(long)      */
DECL|field|LEAST_SIGNIFICANT_BIT
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|LEAST_SIGNIFICANT_BIT
init|=
block|{
operator|-
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|5
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|6
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|5
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|7
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|5
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|6
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|5
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
comment|/**      * Computes the least-significant bit of the specified<code>long</code>      * that is set to<code>1</code>. Zero-indexed.      *      * @param  value the<code>long</code> whose least-significant bit is desired.      * @return the least-significant bit of the specified<code>long</code>.      *<code>-1</code> is returned if there are no bits set.      */
comment|// REF:  http://stackoverflow.com/questions/757059/position-of-least-significant-bit-that-is-set
comment|// REF:  http://www-graphics.stanford.edu/~seander/bithacks.html
DECL|method|leastSignificantBit
specifier|public
specifier|static
name|int
name|leastSignificantBit
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0L
condition|)
return|return
operator|-
literal|1
comment|/*by contract*/
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|0
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|8
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|16
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|24
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFFFFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|32
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|32
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFFFFFFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|40
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|40
return|;
if|if
condition|(
operator|(
name|value
operator|&
literal|0xFFFFFFFFFFFFFFL
operator|)
operator|!=
literal|0
condition|)
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|48
operator|)
operator|&
literal|0xFF
argument_list|)
index|]
operator|+
literal|48
return|;
return|return
name|LEAST_SIGNIFICANT_BIT
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|>>>
literal|56
operator|)
operator|&
literal|0xFFL
argument_list|)
index|]
operator|+
literal|56
return|;
block|}
block|}
end_class
end_unit

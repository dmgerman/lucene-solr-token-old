begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|Arrays
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
name|FieldInfo
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
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
name|Accountable
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
name|RamUsageEstimator
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
name|fst
operator|.
name|Outputs
import|;
end_import
begin_comment
comment|/**  * An FST {@link Outputs} implementation for   * {@link FSTTermsWriter}.  *  * @lucene.experimental  */
end_comment
begin_comment
comment|// NOTE: outputs should be per-field, since
end_comment
begin_comment
comment|// longsSize is fixed for each field
end_comment
begin_class
DECL|class|FSTTermOutputs
class|class
name|FSTTermOutputs
extends|extends
name|Outputs
argument_list|<
name|FSTTermOutputs
operator|.
name|TermData
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|private
specifier|final
specifier|static
name|TermData
name|NO_OUTPUT
init|=
operator|new
name|TermData
argument_list|()
decl_stmt|;
comment|//private static boolean TEST = false;
DECL|field|hasPos
specifier|private
specifier|final
name|boolean
name|hasPos
decl_stmt|;
DECL|field|longsSize
specifier|private
specifier|final
name|int
name|longsSize
decl_stmt|;
comment|/**     * Represents the metadata for one term.    * On an FST, only long[] part is 'shared' and pushed towards root.    * byte[] and term stats will be kept on deeper arcs.    */
DECL|class|TermData
specifier|static
class|class
name|TermData
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|TermData
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|longs
name|long
index|[]
name|longs
decl_stmt|;
DECL|field|bytes
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
DECL|field|totalTermFreq
name|long
name|totalTermFreq
decl_stmt|;
DECL|method|TermData
name|TermData
parameter_list|()
block|{
name|this
operator|.
name|longs
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|totalTermFreq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|TermData
name|TermData
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|long
name|totalTermFreq
parameter_list|)
block|{
name|this
operator|.
name|longs
operator|=
name|longs
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|this
operator|.
name|totalTermFreq
operator|=
name|totalTermFreq
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|ramBytesUsed
init|=
name|BASE_RAM_BYTES_USED
decl_stmt|;
if|if
condition|(
name|longs
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|longs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|// NOTE: actually, FST nodes are seldom
comment|// identical when outputs on their arcs
comment|// aren't NO_OUTPUTs.
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|longs
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|end
init|=
name|longs
operator|.
name|length
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|-=
name|longs
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|hash
operator|=
operator|-
name|hash
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|bytes
operator|.
name|length
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|+=
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|hash
operator|+=
name|docFreq
operator|+
name|totalTermFreq
expr_stmt|;
return|return
name|hash
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
literal|"FSTTermOutputs$TermData longs="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|longs
argument_list|)
operator|+
literal|" bytes="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|bytes
argument_list|)
operator|+
literal|" docFreq="
operator|+
name|docFreq
operator|+
literal|" totalTermFreq="
operator|+
name|totalTermFreq
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
name|other_
parameter_list|)
block|{
if|if
condition|(
name|other_
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|other_
operator|instanceof
name|FSTTermOutputs
operator|.
name|TermData
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermData
name|other
init|=
operator|(
name|TermData
operator|)
name|other_
decl_stmt|;
return|return
name|statsEqual
argument_list|(
name|this
argument_list|,
name|other
argument_list|)
operator|&&
name|longsEqual
argument_list|(
name|this
argument_list|,
name|other
argument_list|)
operator|&&
name|bytesEqual
argument_list|(
name|this
argument_list|,
name|other
argument_list|)
return|;
block|}
block|}
DECL|method|FSTTermOutputs
specifier|protected
name|FSTTermOutputs
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|longsSize
parameter_list|)
block|{
name|this
operator|.
name|hasPos
operator|=
operator|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
operator|)
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|(
name|TermData
name|output
parameter_list|)
block|{
return|return
name|output
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//
comment|// The return value will be the smaller one, when these two are
comment|// 'comparable', i.e.
comment|// 1. every value in t1 is not larger than in t2, or
comment|// 2. every value in t1 is not smaller than t2.
comment|//
DECL|method|common
specifier|public
name|TermData
name|common
parameter_list|(
name|TermData
name|t1
parameter_list|,
name|TermData
name|t2
parameter_list|)
block|{
comment|//if (TEST) System.out.print("common("+t1+", "+t2+") = ");
if|if
condition|(
name|t1
operator|==
name|NO_OUTPUT
operator|||
name|t2
operator|==
name|NO_OUTPUT
condition|)
block|{
comment|//if (TEST) System.out.println("ret:"+NO_OUTPUT);
return|return
name|NO_OUTPUT
return|;
block|}
assert|assert
name|t1
operator|.
name|longs
operator|.
name|length
operator|==
name|t2
operator|.
name|longs
operator|.
name|length
assert|;
name|long
index|[]
name|min
init|=
name|t1
operator|.
name|longs
decl_stmt|,
name|max
init|=
name|t2
operator|.
name|longs
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|TermData
name|ret
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|longsSize
operator|&&
name|min
index|[
name|pos
index|]
operator|==
name|max
index|[
name|pos
index|]
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|longsSize
condition|)
block|{
comment|// unequal long[]
if|if
condition|(
name|min
index|[
name|pos
index|]
operator|>
name|max
index|[
name|pos
index|]
condition|)
block|{
name|min
operator|=
name|t2
operator|.
name|longs
expr_stmt|;
name|max
operator|=
name|t1
operator|.
name|longs
expr_stmt|;
block|}
comment|// check whether strictly smaller
while|while
condition|(
name|pos
operator|<
name|longsSize
operator|&&
name|min
index|[
name|pos
index|]
operator|<=
name|max
index|[
name|pos
index|]
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|longsSize
operator|||
name|allZero
argument_list|(
name|min
argument_list|)
condition|)
block|{
comment|// not comparable or all-zero
name|ret
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
operator|new
name|TermData
argument_list|(
name|min
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// equal long[]
if|if
condition|(
name|statsEqual
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
operator|&&
name|bytesEqual
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
condition|)
block|{
name|ret
operator|=
name|t1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|allZero
argument_list|(
name|min
argument_list|)
condition|)
block|{
name|ret
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
operator|new
name|TermData
argument_list|(
name|min
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|//if (TEST) System.out.println("ret:"+ret);
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|TermData
name|subtract
parameter_list|(
name|TermData
name|t1
parameter_list|,
name|TermData
name|t2
parameter_list|)
block|{
comment|//if (TEST) System.out.print("subtract("+t1+", "+t2+") = ");
if|if
condition|(
name|t2
operator|==
name|NO_OUTPUT
condition|)
block|{
comment|//if (TEST) System.out.println("ret:"+t1);
return|return
name|t1
return|;
block|}
assert|assert
name|t1
operator|.
name|longs
operator|.
name|length
operator|==
name|t2
operator|.
name|longs
operator|.
name|length
assert|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|long
name|diff
init|=
literal|0
decl_stmt|;
name|long
index|[]
name|share
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|longsSize
condition|)
block|{
name|share
index|[
name|pos
index|]
operator|=
name|t1
operator|.
name|longs
index|[
name|pos
index|]
operator|-
name|t2
operator|.
name|longs
index|[
name|pos
index|]
expr_stmt|;
name|diff
operator|+=
name|share
index|[
name|pos
index|]
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
name|TermData
name|ret
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
operator|&&
name|statsEqual
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
operator|&&
name|bytesEqual
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
condition|)
block|{
name|ret
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
operator|new
name|TermData
argument_list|(
name|share
argument_list|,
name|t1
operator|.
name|bytes
argument_list|,
name|t1
operator|.
name|docFreq
argument_list|,
name|t1
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|//if (TEST) System.out.println("ret:"+ret);
return|return
name|ret
return|;
block|}
comment|// TODO: if we refactor a 'addSelf(TermData other)',
comment|// we can gain about 5~7% for fuzzy queries, however this also
comment|// means we are putting too much stress on FST Outputs decoding?
annotation|@
name|Override
DECL|method|add
specifier|public
name|TermData
name|add
parameter_list|(
name|TermData
name|t1
parameter_list|,
name|TermData
name|t2
parameter_list|)
block|{
comment|//if (TEST) System.out.print("add("+t1+", "+t2+") = ");
if|if
condition|(
name|t1
operator|==
name|NO_OUTPUT
condition|)
block|{
comment|//if (TEST) System.out.println("ret:"+t2);
return|return
name|t2
return|;
block|}
elseif|else
if|if
condition|(
name|t2
operator|==
name|NO_OUTPUT
condition|)
block|{
comment|//if (TEST) System.out.println("ret:"+t1);
return|return
name|t1
return|;
block|}
assert|assert
name|t1
operator|.
name|longs
operator|.
name|length
operator|==
name|t2
operator|.
name|longs
operator|.
name|length
assert|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|long
index|[]
name|accum
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|longsSize
condition|)
block|{
name|accum
index|[
name|pos
index|]
operator|=
name|t1
operator|.
name|longs
index|[
name|pos
index|]
operator|+
name|t2
operator|.
name|longs
index|[
name|pos
index|]
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
name|TermData
name|ret
decl_stmt|;
if|if
condition|(
name|t2
operator|.
name|bytes
operator|!=
literal|null
operator|||
name|t2
operator|.
name|docFreq
operator|>
literal|0
condition|)
block|{
name|ret
operator|=
operator|new
name|TermData
argument_list|(
name|accum
argument_list|,
name|t2
operator|.
name|bytes
argument_list|,
name|t2
operator|.
name|docFreq
argument_list|,
name|t2
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
operator|new
name|TermData
argument_list|(
name|accum
argument_list|,
name|t1
operator|.
name|bytes
argument_list|,
name|t1
operator|.
name|docFreq
argument_list|,
name|t1
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|//if (TEST) System.out.println("ret:"+ret);
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TermData
name|data
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|hasPos
operator|||
name|data
operator|.
name|totalTermFreq
operator|==
operator|-
literal|1
assert|;
name|int
name|bit0
init|=
name|allZero
argument_list|(
name|data
operator|.
name|longs
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|int
name|bit1
init|=
operator|(
operator|(
name|data
operator|.
name|bytes
operator|==
literal|null
operator|||
name|data
operator|.
name|bytes
operator|.
name|length
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
operator|<<
literal|1
decl_stmt|;
name|int
name|bit2
init|=
operator|(
operator|(
name|data
operator|.
name|docFreq
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
operator|<<
literal|2
decl_stmt|;
name|int
name|bits
init|=
name|bit0
operator||
name|bit1
operator||
name|bit2
decl_stmt|;
if|if
condition|(
name|bit1
operator|>
literal|0
condition|)
block|{
comment|// determine extra length
if|if
condition|(
name|data
operator|.
name|bytes
operator|.
name|length
operator|<
literal|32
condition|)
block|{
name|bits
operator||=
operator|(
name|data
operator|.
name|bytes
operator|.
name|length
operator|<<
literal|3
operator|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|data
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bits
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bit0
operator|>
literal|0
condition|)
block|{
comment|// not all-zero case
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|longsSize
condition|;
name|pos
operator|++
control|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|data
operator|.
name|longs
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bit1
operator|>
literal|0
condition|)
block|{
comment|// bytes exists
name|out
operator|.
name|writeBytes
argument_list|(
name|data
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bit2
operator|>
literal|0
condition|)
block|{
comment|// stats exist
if|if
condition|(
name|hasPos
condition|)
block|{
if|if
condition|(
name|data
operator|.
name|docFreq
operator|==
name|data
operator|.
name|totalTermFreq
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|data
operator|.
name|docFreq
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|data
operator|.
name|docFreq
operator|<<
literal|1
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|data
operator|.
name|totalTermFreq
operator|-
name|data
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|data
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|TermData
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|null
decl_stmt|;
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
name|long
name|totalTermFreq
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|bits
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
decl_stmt|;
name|int
name|bit0
init|=
name|bits
operator|&
literal|1
decl_stmt|;
name|int
name|bit1
init|=
name|bits
operator|&
literal|2
decl_stmt|;
name|int
name|bit2
init|=
name|bits
operator|&
literal|4
decl_stmt|;
name|int
name|bytesSize
init|=
operator|(
name|bits
operator|>>>
literal|3
operator|)
decl_stmt|;
if|if
condition|(
name|bit1
operator|>
literal|0
operator|&&
name|bytesSize
operator|==
literal|0
condition|)
block|{
comment|// determine extra length
name|bytesSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bit0
operator|>
literal|0
condition|)
block|{
comment|// not all-zero case
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|longsSize
condition|;
name|pos
operator|++
control|)
block|{
name|longs
index|[
name|pos
index|]
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bit1
operator|>
literal|0
condition|)
block|{
comment|// bytes exists
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytesSize
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytesSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bit2
operator|>
literal|0
condition|)
block|{
comment|// stats exist
name|int
name|code
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasPos
condition|)
block|{
name|totalTermFreq
operator|=
name|docFreq
operator|=
name|code
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|totalTermFreq
operator|+=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|docFreq
operator|=
name|code
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TermData
argument_list|(
name|longs
argument_list|,
name|bytes
argument_list|,
name|docFreq
argument_list|,
name|totalTermFreq
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|skipOutput
specifier|public
name|void
name|skipOutput
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bits
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
decl_stmt|;
name|int
name|bit0
init|=
name|bits
operator|&
literal|1
decl_stmt|;
name|int
name|bit1
init|=
name|bits
operator|&
literal|2
decl_stmt|;
name|int
name|bit2
init|=
name|bits
operator|&
literal|4
decl_stmt|;
name|int
name|bytesSize
init|=
operator|(
name|bits
operator|>>>
literal|3
operator|)
decl_stmt|;
if|if
condition|(
name|bit1
operator|>
literal|0
operator|&&
name|bytesSize
operator|==
literal|0
condition|)
block|{
comment|// determine extra length
name|bytesSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bit0
operator|>
literal|0
condition|)
block|{
comment|// not all-zero case
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|longsSize
condition|;
name|pos
operator|++
control|)
block|{
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bit1
operator|>
literal|0
condition|)
block|{
comment|// bytes exists
name|in
operator|.
name|skipBytes
argument_list|(
name|bytesSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bit2
operator|>
literal|0
condition|)
block|{
comment|// stats exist
name|int
name|code
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasPos
operator|&&
operator|(
name|code
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|TermData
name|getNoOutput
parameter_list|()
block|{
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|TermData
name|data
parameter_list|)
block|{
return|return
name|data
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|statsEqual
specifier|static
name|boolean
name|statsEqual
parameter_list|(
specifier|final
name|TermData
name|t1
parameter_list|,
specifier|final
name|TermData
name|t2
parameter_list|)
block|{
return|return
name|t1
operator|.
name|docFreq
operator|==
name|t2
operator|.
name|docFreq
operator|&&
name|t1
operator|.
name|totalTermFreq
operator|==
name|t2
operator|.
name|totalTermFreq
return|;
block|}
DECL|method|bytesEqual
specifier|static
name|boolean
name|bytesEqual
parameter_list|(
specifier|final
name|TermData
name|t1
parameter_list|,
specifier|final
name|TermData
name|t2
parameter_list|)
block|{
if|if
condition|(
name|t1
operator|.
name|bytes
operator|==
literal|null
operator|&&
name|t2
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|t1
operator|.
name|bytes
operator|!=
literal|null
operator|&&
name|t2
operator|.
name|bytes
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|t1
operator|.
name|bytes
argument_list|,
name|t2
operator|.
name|bytes
argument_list|)
return|;
block|}
DECL|method|longsEqual
specifier|static
name|boolean
name|longsEqual
parameter_list|(
specifier|final
name|TermData
name|t1
parameter_list|,
specifier|final
name|TermData
name|t2
parameter_list|)
block|{
if|if
condition|(
name|t1
operator|.
name|longs
operator|==
literal|null
operator|&&
name|t2
operator|.
name|longs
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|t1
operator|.
name|longs
operator|!=
literal|null
operator|&&
name|t2
operator|.
name|longs
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|t1
operator|.
name|longs
argument_list|,
name|t2
operator|.
name|longs
argument_list|)
return|;
block|}
DECL|method|allZero
specifier|static
name|boolean
name|allZero
parameter_list|(
specifier|final
name|long
index|[]
name|l
parameter_list|)
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
name|l
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|l
index|[
name|i
index|]
operator|!=
literal|0
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
block|}
end_class
end_unit

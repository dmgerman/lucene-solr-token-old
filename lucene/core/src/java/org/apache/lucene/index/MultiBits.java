begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|Bits
import|;
end_import
begin_comment
comment|/**  * Concatenates multiple Bits together, on every lookup.  *  *<p><b>NOTE</b>: This is very costly, as every lookup must  * do a binary search to locate the right sub-reader.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiBits
specifier|final
class|class
name|MultiBits
implements|implements
name|Bits
block|{
DECL|field|subs
specifier|private
specifier|final
name|Bits
index|[]
name|subs
decl_stmt|;
comment|// length is 1+subs.length (the last entry has the maxDoc):
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|defaultValue
specifier|private
specifier|final
name|boolean
name|defaultValue
decl_stmt|;
DECL|method|MultiBits
specifier|public
name|MultiBits
parameter_list|(
name|Bits
index|[]
name|subs
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
assert|assert
name|starts
operator|.
name|length
operator|==
literal|1
operator|+
name|subs
operator|.
name|length
assert|;
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
block|}
DECL|method|checkLength
specifier|private
name|boolean
name|checkLength
parameter_list|(
name|int
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|starts
index|[
literal|1
operator|+
name|reader
index|]
operator|-
name|starts
index|[
name|reader
index|]
decl_stmt|;
assert|assert
name|doc
operator|-
name|starts
index|[
name|reader
index|]
operator|<
name|length
operator|:
literal|"doc="
operator|+
name|doc
operator|+
literal|" reader="
operator|+
name|reader
operator|+
literal|" starts[reader]="
operator|+
name|starts
index|[
name|reader
index|]
operator|+
literal|" length="
operator|+
name|length
assert|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|reader
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|starts
argument_list|)
decl_stmt|;
assert|assert
name|reader
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|Bits
name|bits
init|=
name|subs
index|[
name|reader
index|]
decl_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
else|else
block|{
assert|assert
name|checkLength
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
assert|;
return|return
name|bits
operator|.
name|get
argument_list|(
name|doc
operator|-
name|starts
index|[
name|reader
index|]
argument_list|)
return|;
block|}
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
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|subs
operator|.
name|length
operator|+
literal|" subs: "
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subs
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"s="
operator|+
name|starts
index|[
name|i
index|]
operator|+
literal|" l=null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"s="
operator|+
name|starts
index|[
name|i
index|]
operator|+
literal|" l="
operator|+
name|subs
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|+
literal|" b="
operator|+
name|subs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|b
operator|.
name|append
argument_list|(
literal|" end="
operator|+
name|starts
index|[
name|subs
operator|.
name|length
index|]
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Represents a sub-Bits from     * {@link MultiBits#getMatchingSub(org.apache.lucene.index.ReaderSlice) getMatchingSub()}.    */
DECL|class|SubResult
specifier|public
specifier|final
specifier|static
class|class
name|SubResult
block|{
DECL|field|matches
specifier|public
name|boolean
name|matches
decl_stmt|;
DECL|field|result
specifier|public
name|Bits
name|result
decl_stmt|;
block|}
comment|/**    * Returns a sub-Bits matching the provided<code>slice</code>    *<p>    * Because<code>null</code> usually has a special meaning for    * Bits (e.g. no deleted documents), you must check    * {@link SubResult#matches} instead to ensure the sub was     * actually found.    */
DECL|method|getMatchingSub
specifier|public
name|SubResult
name|getMatchingSub
parameter_list|(
name|ReaderSlice
name|slice
parameter_list|)
block|{
name|int
name|reader
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|slice
operator|.
name|start
argument_list|,
name|starts
argument_list|)
decl_stmt|;
assert|assert
name|reader
operator|!=
operator|-
literal|1
assert|;
assert|assert
name|reader
operator|<
name|subs
operator|.
name|length
operator|:
literal|"slice="
operator|+
name|slice
operator|+
literal|" starts[-1]="
operator|+
name|starts
index|[
name|starts
operator|.
name|length
operator|-
literal|1
index|]
assert|;
specifier|final
name|SubResult
name|subResult
init|=
operator|new
name|SubResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|starts
index|[
name|reader
index|]
operator|==
name|slice
operator|.
name|start
operator|&&
name|starts
index|[
literal|1
operator|+
name|reader
index|]
operator|==
name|slice
operator|.
name|start
operator|+
name|slice
operator|.
name|length
condition|)
block|{
name|subResult
operator|.
name|matches
operator|=
literal|true
expr_stmt|;
name|subResult
operator|.
name|result
operator|=
name|subs
index|[
name|reader
index|]
expr_stmt|;
block|}
else|else
block|{
name|subResult
operator|.
name|matches
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|subResult
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|starts
index|[
name|starts
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
block|}
end_class
end_unit

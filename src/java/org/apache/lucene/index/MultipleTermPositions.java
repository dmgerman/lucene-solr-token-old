begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PriorityQueue
import|;
end_import
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * Allows you to iterate over the {@link TermPositions} for multiple {@link Term}s as  * a single {@link TermPositions}.  *  */
end_comment
begin_class
DECL|class|MultipleTermPositions
specifier|public
class|class
name|MultipleTermPositions
implements|implements
name|TermPositions
block|{
DECL|class|TermPositionsQueue
specifier|private
specifier|static
specifier|final
class|class
name|TermPositionsQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|TermPositionsQueue
name|TermPositionsQueue
parameter_list|(
name|List
name|termPositions
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|termPositions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|termPositions
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TermPositions
name|tp
init|=
operator|(
name|TermPositions
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|tp
operator|.
name|next
argument_list|()
condition|)
name|put
argument_list|(
name|tp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|peek
specifier|final
name|TermPositions
name|peek
parameter_list|()
block|{
return|return
operator|(
name|TermPositions
operator|)
name|top
argument_list|()
return|;
block|}
DECL|method|lessThan
specifier|public
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|a
operator|)
operator|.
name|doc
argument_list|()
operator|<
operator|(
operator|(
name|TermPositions
operator|)
name|b
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
block|}
DECL|class|IntQueue
specifier|private
specifier|static
specifier|final
class|class
name|IntQueue
block|{
DECL|field|_arraySize
specifier|private
name|int
name|_arraySize
init|=
literal|16
decl_stmt|;
DECL|field|_index
specifier|private
name|int
name|_index
init|=
literal|0
decl_stmt|;
DECL|field|_lastIndex
specifier|private
name|int
name|_lastIndex
init|=
literal|0
decl_stmt|;
DECL|field|_array
specifier|private
name|int
index|[]
name|_array
init|=
operator|new
name|int
index|[
name|_arraySize
index|]
decl_stmt|;
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|_lastIndex
operator|==
name|_arraySize
condition|)
name|growArray
argument_list|()
expr_stmt|;
name|_array
index|[
name|_lastIndex
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
DECL|method|next
specifier|final
name|int
name|next
parameter_list|()
block|{
return|return
name|_array
index|[
name|_index
operator|++
index|]
return|;
block|}
DECL|method|sort
specifier|final
name|void
name|sort
parameter_list|()
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|_array
argument_list|,
name|_index
argument_list|,
name|_lastIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|final
name|void
name|clear
parameter_list|()
block|{
name|_index
operator|=
literal|0
expr_stmt|;
name|_lastIndex
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|size
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
operator|(
name|_lastIndex
operator|-
name|_index
operator|)
return|;
block|}
DECL|method|growArray
specifier|private
name|void
name|growArray
parameter_list|()
block|{
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|_arraySize
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|_array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|_arraySize
argument_list|)
expr_stmt|;
name|_array
operator|=
name|newArray
expr_stmt|;
name|_arraySize
operator|*=
literal|2
expr_stmt|;
block|}
block|}
DECL|field|_doc
specifier|private
name|int
name|_doc
decl_stmt|;
DECL|field|_freq
specifier|private
name|int
name|_freq
decl_stmt|;
DECL|field|_termPositionsQueue
specifier|private
name|TermPositionsQueue
name|_termPositionsQueue
decl_stmt|;
DECL|field|_posList
specifier|private
name|IntQueue
name|_posList
decl_stmt|;
comment|/**    * Creates a new<code>MultipleTermPositions</code> instance.    *     * @exception IOException    */
DECL|method|MultipleTermPositions
specifier|public
name|MultipleTermPositions
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|List
name|termPositions
init|=
operator|new
name|LinkedList
argument_list|()
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|termPositions
operator|.
name|add
argument_list|(
name|indexReader
operator|.
name|termPositions
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|_termPositionsQueue
operator|=
operator|new
name|TermPositionsQueue
argument_list|(
name|termPositions
argument_list|)
expr_stmt|;
name|_posList
operator|=
operator|new
name|IntQueue
argument_list|()
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|_termPositionsQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|_posList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|_doc
operator|=
name|_termPositionsQueue
operator|.
name|peek
argument_list|()
operator|.
name|doc
argument_list|()
expr_stmt|;
name|TermPositions
name|tp
decl_stmt|;
do|do
block|{
name|tp
operator|=
name|_termPositionsQueue
operator|.
name|peek
argument_list|()
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
name|tp
operator|.
name|freq
argument_list|()
condition|;
name|i
operator|++
control|)
name|_posList
operator|.
name|add
argument_list|(
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tp
operator|.
name|next
argument_list|()
condition|)
name|_termPositionsQueue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
else|else
block|{
name|_termPositionsQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|tp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|_termPositionsQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|_termPositionsQueue
operator|.
name|peek
argument_list|()
operator|.
name|doc
argument_list|()
operator|==
name|_doc
condition|)
do|;
name|_posList
operator|.
name|sort
argument_list|()
expr_stmt|;
name|_freq
operator|=
name|_posList
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|nextPosition
specifier|public
specifier|final
name|int
name|nextPosition
parameter_list|()
block|{
return|return
name|_posList
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|skipTo
specifier|public
specifier|final
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|_termPositionsQueue
operator|.
name|peek
argument_list|()
operator|!=
literal|null
operator|&&
name|target
operator|>
name|_termPositionsQueue
operator|.
name|peek
argument_list|()
operator|.
name|doc
argument_list|()
condition|)
block|{
name|TermPositions
name|tp
init|=
operator|(
name|TermPositions
operator|)
name|_termPositionsQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|tp
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
name|_termPositionsQueue
operator|.
name|put
argument_list|(
name|tp
argument_list|)
expr_stmt|;
else|else
name|tp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|next
argument_list|()
return|;
block|}
DECL|method|doc
specifier|public
specifier|final
name|int
name|doc
parameter_list|()
block|{
return|return
name|_doc
return|;
block|}
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
return|return
name|_freq
return|;
block|}
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|_termPositionsQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
operator|(
operator|(
name|TermPositions
operator|)
name|_termPositionsQueue
operator|.
name|pop
argument_list|()
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|int
index|[]
name|arg0
parameter_list|,
name|int
index|[]
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|getPayloadLength
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|getPayload
specifier|public
name|byte
index|[]
name|getPayload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    *    * @return false    */
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit

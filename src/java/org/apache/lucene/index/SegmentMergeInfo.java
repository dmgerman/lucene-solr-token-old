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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitVector
import|;
end_import
begin_class
DECL|class|SegmentMergeInfo
specifier|final
class|class
name|SegmentMergeInfo
block|{
DECL|field|term
name|Term
name|term
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
DECL|field|termEnum
name|TermEnum
name|termEnum
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|postings
name|TermPositions
name|postings
decl_stmt|;
DECL|field|docMap
name|int
index|[]
name|docMap
init|=
literal|null
decl_stmt|;
comment|// maps around deleted docs
DECL|method|SegmentMergeInfo
name|SegmentMergeInfo
parameter_list|(
name|int
name|b
parameter_list|,
name|TermEnum
name|te
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|=
name|b
expr_stmt|;
name|reader
operator|=
name|r
expr_stmt|;
name|termEnum
operator|=
name|te
expr_stmt|;
name|term
operator|=
name|te
operator|.
name|term
argument_list|()
expr_stmt|;
name|postings
operator|=
name|reader
operator|.
name|termPositions
argument_list|()
expr_stmt|;
comment|// build array which maps document numbers around deletions
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|docMap
operator|=
operator|new
name|int
index|[
name|maxDoc
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|isDeleted
argument_list|(
name|i
argument_list|)
condition|)
name|docMap
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
else|else
name|docMap
index|[
name|i
index|]
operator|=
name|j
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|next
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
block|{
name|term
operator|=
name|termEnum
operator|.
name|term
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|term
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|postings
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

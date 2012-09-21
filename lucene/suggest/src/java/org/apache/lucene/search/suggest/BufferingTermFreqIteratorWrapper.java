begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|Comparator
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
name|search
operator|.
name|spell
operator|.
name|TermFreqIterator
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
name|ArrayUtil
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
begin_comment
comment|/**  * This wrapper buffers incoming elements.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BufferingTermFreqIteratorWrapper
specifier|public
class|class
name|BufferingTermFreqIteratorWrapper
implements|implements
name|TermFreqIterator
block|{
comment|// TODO keep this for now
comment|/** buffered term entries */
DECL|field|entries
specifier|protected
name|BytesRefList
name|entries
init|=
operator|new
name|BytesRefList
argument_list|()
decl_stmt|;
comment|/** current buffer position */
DECL|field|curPos
specifier|protected
name|int
name|curPos
init|=
operator|-
literal|1
decl_stmt|;
comment|/** buffered weights, parallel with {@link #entries} */
DECL|field|freqs
specifier|protected
name|long
index|[]
name|freqs
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
comment|/** Creates a new iterator, buffering entries from the specified iterator */
DECL|method|BufferingTermFreqIteratorWrapper
specifier|public
name|BufferingTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|comp
operator|=
name|source
operator|.
name|getComparator
argument_list|()
expr_stmt|;
name|BytesRef
name|spare
decl_stmt|;
name|int
name|freqIndex
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|source
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|entries
operator|.
name|append
argument_list|(
name|spare
argument_list|)
expr_stmt|;
if|if
condition|(
name|freqIndex
operator|>=
name|freqs
operator|.
name|length
condition|)
block|{
name|freqs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|freqs
argument_list|,
name|freqs
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|freqs
index|[
name|freqIndex
operator|++
index|]
operator|=
name|source
operator|.
name|weight
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|freqs
index|[
name|curPos
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|curPos
operator|<
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
name|entries
operator|.
name|get
argument_list|(
name|spare
argument_list|,
name|curPos
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comp
return|;
block|}
block|}
end_class
end_unit

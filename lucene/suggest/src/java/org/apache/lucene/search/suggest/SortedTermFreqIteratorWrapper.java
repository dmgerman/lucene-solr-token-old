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
name|File
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
name|search
operator|.
name|suggest
operator|.
name|fst
operator|.
name|Sort
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
name|suggest
operator|.
name|fst
operator|.
name|Sort
operator|.
name|ByteSequencesReader
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
name|suggest
operator|.
name|fst
operator|.
name|Sort
operator|.
name|ByteSequencesWriter
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * This wrapper buffers incoming elements and makes sure they are sorted based on given comparator.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SortedTermFreqIteratorWrapper
specifier|public
class|class
name|SortedTermFreqIteratorWrapper
implements|implements
name|TermFreqIterator
block|{
DECL|field|source
specifier|private
specifier|final
name|TermFreqIterator
name|source
decl_stmt|;
DECL|field|tempInput
specifier|private
name|File
name|tempInput
decl_stmt|;
DECL|field|tempSorted
specifier|private
name|File
name|tempSorted
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|ByteSequencesReader
name|reader
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|weight
specifier|private
name|long
name|weight
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
comment|/**     * Calls {@link #SortedTermFreqIteratorWrapper(TermFreqIterator, Comparator, boolean)     * SortedTermFreqIteratorWrapper(source, comparator, false)}    */
DECL|method|SortedTermFreqIteratorWrapper
specifier|public
name|SortedTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|source
argument_list|,
name|comparator
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new sorted wrapper. if<code>compareRawBytes</code> is true, then    * only the bytes (not the weight) will be used for comparison.    */
DECL|method|SortedTermFreqIteratorWrapper
specifier|public
name|SortedTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|,
name|boolean
name|compareRawBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|sort
argument_list|(
name|compareRawBytes
condition|?
name|comparator
else|:
operator|new
name|BytesOnlyComparator
argument_list|(
name|this
operator|.
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|read
argument_list|(
name|scratch
argument_list|)
condition|)
block|{
name|weight
operator|=
name|decode
argument_list|(
name|scratch
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|scratch
return|;
block|}
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
name|done
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|comparator
return|;
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
DECL|method|sort
specifier|private
name|Sort
operator|.
name|ByteSequencesReader
name|sort
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|prefix
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
name|Sort
operator|.
name|defaultTempDir
argument_list|()
decl_stmt|;
name|tempInput
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|".input"
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|tempSorted
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|".sorted"
argument_list|,
name|directory
argument_list|)
expr_stmt|;
specifier|final
name|Sort
operator|.
name|ByteSequencesWriter
name|writer
init|=
operator|new
name|Sort
operator|.
name|ByteSequencesWriter
argument_list|(
name|tempInput
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|BytesRef
name|spare
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|output
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
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
name|encode
argument_list|(
name|writer
argument_list|,
name|output
argument_list|,
name|buffer
argument_list|,
name|spare
argument_list|,
name|source
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|Sort
argument_list|(
name|comparator
argument_list|)
operator|.
name|sort
argument_list|(
name|tempInput
argument_list|,
name|tempSorted
argument_list|)
expr_stmt|;
name|ByteSequencesReader
name|reader
init|=
operator|new
name|Sort
operator|.
name|ByteSequencesReader
argument_list|(
name|tempSorted
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|close
specifier|private
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|tempInput
operator|!=
literal|null
condition|)
block|{
name|tempInput
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tempSorted
operator|!=
literal|null
condition|)
block|{
name|tempSorted
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|BytesOnlyComparator
specifier|private
specifier|final
specifier|static
class|class
name|BytesOnlyComparator
implements|implements
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|other
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|other
decl_stmt|;
DECL|field|leftScratch
specifier|private
specifier|final
name|BytesRef
name|leftScratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|rightScratch
specifier|private
specifier|final
name|BytesRef
name|rightScratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|BytesOnlyComparator
specifier|public
name|BytesOnlyComparator
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|other
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|left
parameter_list|,
name|BytesRef
name|right
parameter_list|)
block|{
name|wrap
argument_list|(
name|leftScratch
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|wrap
argument_list|(
name|rightScratch
argument_list|,
name|right
argument_list|)
expr_stmt|;
return|return
name|other
operator|.
name|compare
argument_list|(
name|leftScratch
argument_list|,
name|rightScratch
argument_list|)
return|;
block|}
DECL|method|wrap
specifier|private
name|void
name|wrap
parameter_list|(
name|BytesRef
name|wrapper
parameter_list|,
name|BytesRef
name|source
parameter_list|)
block|{
name|wrapper
operator|.
name|bytes
operator|=
name|source
operator|.
name|bytes
expr_stmt|;
name|wrapper
operator|.
name|offset
operator|=
name|source
operator|.
name|offset
expr_stmt|;
name|wrapper
operator|.
name|length
operator|=
name|source
operator|.
name|length
operator|-
literal|8
expr_stmt|;
block|}
block|}
comment|/** encodes an entry (bytes+weight) to the provided writer */
DECL|method|encode
specifier|protected
name|void
name|encode
parameter_list|(
name|ByteSequencesWriter
name|writer
parameter_list|,
name|ByteArrayDataOutput
name|output
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|BytesRef
name|spare
parameter_list|,
name|long
name|weight
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spare
operator|.
name|length
operator|+
literal|8
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|spare
operator|.
name|length
operator|+
literal|8
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** decodes the weight at the current position */
DECL|method|decode
specifier|protected
name|long
name|decode
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|ByteArrayDataInput
name|tmpInput
parameter_list|)
block|{
name|tmpInput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tmpInput
operator|.
name|skipBytes
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|8
argument_list|)
expr_stmt|;
comment|// suggestion + separator
name|scratch
operator|.
name|length
operator|-=
literal|8
expr_stmt|;
comment|// sep + long
return|return
name|tmpInput
operator|.
name|readLong
argument_list|()
return|;
block|}
block|}
end_class
end_unit

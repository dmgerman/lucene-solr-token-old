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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|BytesRefHash
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
name|LuceneTestCase
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
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestTermFreqIterator
specifier|public
class|class
name|TestTermFreqIterator
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|TermFreqArrayIterator
name|iterator
init|=
operator|new
name|TermFreqArrayIterator
argument_list|(
operator|new
name|TermFreq
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|TermFreqIterator
name|wrapper
init|=
operator|new
name|SortedTermFreqIteratorWrapper
argument_list|(
name|iterator
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|=
operator|new
name|UnsortedTermFreqIteratorWrapper
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTerms
specifier|public
name|void
name|testTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
else|:
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
decl_stmt|;
name|TreeMap
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
name|sorted
init|=
operator|new
name|TreeMap
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
name|TermFreq
index|[]
name|unsorted
init|=
operator|new
name|TermFreq
index|[
name|num
index|]
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|key
decl_stmt|;
do|do
block|{
name|key
operator|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|sorted
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
do|;
name|long
name|value
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|sorted
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|unsorted
index|[
name|i
index|]
operator|=
operator|new
name|TermFreq
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// test the sorted iterator wrapper
name|TermFreqIterator
name|wrapper
init|=
operator|new
name|SortedTermFreqIteratorWrapper
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|unsorted
argument_list|)
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
argument_list|>
name|expected
init|=
name|sorted
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|expected
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|expected
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|wrapper
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// test the unsorted iterator wrapper
name|wrapper
operator|=
operator|new
name|UnsortedTermFreqIteratorWrapper
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|unsorted
argument_list|)
argument_list|)
expr_stmt|;
name|TreeMap
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
name|actual
init|=
operator|new
name|TreeMap
argument_list|<
name|BytesRef
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|BytesRef
name|key
decl_stmt|;
while|while
condition|(
operator|(
name|key
operator|=
name|wrapper
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|long
name|value
init|=
name|wrapper
operator|.
name|weight
argument_list|()
decl_stmt|;
name|actual
operator|.
name|put
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|sorted
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|testRaw
specifier|public
name|void
name|testRaw
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
init|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
decl_stmt|;
name|BytesRefHash
name|sorted
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
name|TermFreq
index|[]
name|unsorted
init|=
operator|new
name|TermFreq
index|[
name|num
index|]
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
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|spare
decl_stmt|;
name|long
name|weight
decl_stmt|;
do|do
block|{
name|spare
operator|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
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
name|weight
operator|=
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|sorted
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
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
argument_list|)
operator|<
literal|0
condition|)
do|;
name|unsorted
index|[
name|i
index|]
operator|=
operator|new
name|TermFreq
argument_list|(
name|spare
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
comment|// test the sorted iterator wrapper
name|TermFreqIterator
name|wrapper
init|=
operator|new
name|SortedTermFreqIteratorWrapper
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|unsorted
argument_list|)
argument_list|,
name|comparator
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
index|[]
name|sort
init|=
name|sorted
operator|.
name|sort
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|sorted
operator|.
name|size
argument_list|()
decl_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|sorted
operator|.
name|get
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|spare
operator|.
name|length
operator|-=
literal|8
expr_stmt|;
comment|// sub the long value
name|assertEquals
argument_list|(
name|spare
argument_list|,
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|spare
operator|.
name|offset
operator|=
name|spare
operator|.
name|offset
operator|+
name|spare
operator|.
name|length
expr_stmt|;
name|spare
operator|.
name|length
operator|=
literal|8
expr_stmt|;
name|assertEquals
argument_list|(
name|asLong
argument_list|(
name|spare
argument_list|)
argument_list|,
name|wrapper
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|wrapper
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|asLong
specifier|public
specifier|static
name|long
name|asLong
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|asIntInternal
argument_list|(
name|b
argument_list|,
name|b
operator|.
name|offset
argument_list|)
operator|<<
literal|32
operator|)
operator||
name|asIntInternal
argument_list|(
name|b
argument_list|,
name|b
operator|.
name|offset
operator|+
literal|4
argument_list|)
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
DECL|method|asIntInternal
specifier|private
specifier|static
name|int
name|asIntInternal
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|b
operator|.
name|bytes
index|[
name|pos
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
block|}
end_class
end_unit

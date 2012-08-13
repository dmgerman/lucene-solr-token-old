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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|MergedIterator
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
DECL|class|TestPrefixCodedTerms
specifier|public
class|class
name|TestPrefixCodedTerms
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|PrefixCodedTerms
operator|.
name|Builder
name|b
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|PrefixCodedTerms
name|pb
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|pb
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOne
specifier|public
name|void
name|testOne
parameter_list|()
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bogus"
argument_list|)
decl_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|b
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
name|pb
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iterator
init|=
name|pb
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
block|{
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|nterms
init|=
name|atLeast
argument_list|(
literal|10000
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
name|nterms
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|PrefixCodedTerms
operator|.
name|Builder
name|b
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|ref
range|:
name|terms
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|PrefixCodedTerms
name|pb
init|=
name|b
operator|.
name|finish
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Term
argument_list|>
name|expected
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|pb
control|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|next
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|expected
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testMergeEmpty
specifier|public
name|void
name|testMergeEmpty
parameter_list|()
block|{
name|List
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
name|subs
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Term
argument_list|>
name|merged
init|=
operator|new
name|MergedIterator
argument_list|<
name|Term
argument_list|>
argument_list|(
name|subs
operator|.
name|toArray
argument_list|(
operator|new
name|Iterator
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|subs
operator|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
operator|.
name|finish
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
operator|.
name|finish
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|merged
operator|=
operator|new
name|MergedIterator
argument_list|<
name|Term
argument_list|>
argument_list|(
name|subs
operator|.
name|toArray
argument_list|(
operator|new
name|Iterator
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testMergeOne
specifier|public
name|void
name|testMergeOne
parameter_list|()
block|{
name|Term
name|t1
init|=
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|b1
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
name|pb1
init|=
name|b1
operator|.
name|finish
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|b2
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|add
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
name|pb2
init|=
name|b2
operator|.
name|finish
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|pb1
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|pb2
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Term
argument_list|>
name|merged
init|=
operator|new
name|MergedIterator
argument_list|<
name|Term
argument_list|>
argument_list|(
name|subs
operator|.
name|toArray
argument_list|(
operator|new
name|Iterator
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t1
argument_list|,
name|merged
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t2
argument_list|,
name|merged
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testMergeRandom
specifier|public
name|void
name|testMergeRandom
parameter_list|()
block|{
name|PrefixCodedTerms
name|pb
index|[]
init|=
operator|new
name|PrefixCodedTerms
index|[
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|superSet
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
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
name|pb
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|nterms
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nterms
condition|;
name|j
operator|++
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|superSet
operator|.
name|addAll
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|b
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|ref
range|:
name|terms
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|pb
index|[
name|i
index|]
operator|=
name|b
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|>
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
name|pb
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|pb
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Term
argument_list|>
name|expected
init|=
name|superSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Term
argument_list|>
name|actual
init|=
operator|new
name|MergedIterator
argument_list|<
name|Term
argument_list|>
argument_list|(
name|subs
operator|.
name|toArray
argument_list|(
operator|new
name|Iterator
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|actual
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|next
argument_list|()
argument_list|,
name|actual
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|expected
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

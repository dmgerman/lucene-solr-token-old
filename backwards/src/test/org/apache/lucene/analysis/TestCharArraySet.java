begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_class
DECL|class|TestCharArraySet
specifier|public
class|class
name|TestCharArraySet
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEST_STOP_WORDS
specifier|static
specifier|final
name|String
index|[]
name|TEST_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"an"
block|,
literal|"and"
block|,
literal|"are"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"for"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"no"
block|,
literal|"not"
block|,
literal|"of"
block|,
literal|"on"
block|,
literal|"or"
block|,
literal|"such"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"then"
block|,
literal|"there"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"to"
block|,
literal|"was"
block|,
literal|"will"
block|,
literal|"with"
block|}
decl_stmt|;
DECL|method|testRehash
specifier|public
name|void
name|testRehash
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|cas
init|=
operator|new
name|CharArraySet
argument_list|(
literal|0
argument_list|,
literal|true
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
name|TEST_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|cas
operator|.
name|add
argument_list|(
name|TEST_STOP_WORDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_STOP_WORDS
operator|.
name|length
argument_list|,
name|cas
operator|.
name|size
argument_list|()
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
name|TEST_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|assertTrue
argument_list|(
name|cas
operator|.
name|contains
argument_list|(
name|TEST_STOP_WORDS
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonZeroOffset
specifier|public
name|void
name|testNonZeroOffset
parameter_list|()
block|{
name|String
index|[]
name|words
init|=
block|{
literal|"Hello"
block|,
literal|"World"
block|,
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|}
decl_stmt|;
name|char
index|[]
name|findme
init|=
literal|"xthisy"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|words
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
operator|new
name|String
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test unmodifiable
name|set
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
operator|new
name|String
argument_list|(
name|findme
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testObjectContains
specifier|public
name|void
name|testObjectContains
parameter_list|()
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Integer
name|val
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test unmodifiable
name|set
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* clear() is now supported in 3.1, so disable this test in BW   public void testClear(){     CharArraySet set=new CharArraySet(10,true);     set.addAll(Arrays.asList(TEST_STOP_WORDS));     assertEquals("Not all words added", TEST_STOP_WORDS.length, set.size());     try{       set.clear();       fail("remove is not supported");     }catch (UnsupportedOperationException e) {       // expected       assertEquals("Not all words added", TEST_STOP_WORDS.length, set.size());     }   }   */
DECL|method|testModifyOnUnmodifiable
specifier|public
name|void
name|testModifyOnUnmodifiable
parameter_list|()
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TEST_STOP_WORDS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|set
operator|.
name|size
argument_list|()
decl_stmt|;
name|set
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Set size changed due to unmodifiableSet call"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|NOT_IN_SET
init|=
literal|"SirGallahad"
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Test String already exists in set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|set
operator|.
name|add
argument_list|(
name|NOT_IN_SET
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|add
argument_list|(
name|NOT_IN_SET
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|add
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Changed unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|add
argument_list|(
operator|(
name|Object
operator|)
name|NOT_IN_SET
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|removeAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TEST_STOP_WORDS
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|retainAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|NOT_IN_SET
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
literal|"Size of unmodifiable set has changed"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|NOT_IN_SET
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable set"
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|NOT_IN_SET
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TEST_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|TEST_STOP_WORDS
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnmodifiableSet
specifier|public
name|void
name|testUnmodifiableSet
parameter_list|()
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TEST_STOP_WORDS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|set
operator|.
name|size
argument_list|()
decl_stmt|;
name|set
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Set size changed due to unmodifiableSet call"
argument_list|,
name|size
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"can not make null unmodifiable"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit

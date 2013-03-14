begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
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
name|facet
operator|.
name|FacetTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestCategoryPath
specifier|public
class|class
name|TestCategoryPath
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
comment|// When the category is empty, we expect an empty string
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|// one category (so no delimiter needed)
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|// more than one category (so no delimiter needed)
name|assertEquals
argument_list|(
literal|"hello/world"
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetComponent
specifier|public
name|void
name|testGetComponent
parameter_list|()
block|{
name|String
index|[]
name|components
init|=
operator|new
name|String
index|[
name|atLeast
argument_list|(
literal|10
argument_list|)
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
name|components
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|components
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|components
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
name|components
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|cp
operator|.
name|components
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelimiterConstructor
specifier|public
name|void
name|testDelimiterConstructor
parameter_list|()
block|{
name|CategoryPath
name|p
init|=
operator|new
name|CategoryPath
argument_list|(
literal|""
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|length
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|toString
argument_list|(
literal|'@'
argument_list|)
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"hi/there"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|toString
argument_list|(
literal|'@'
argument_list|)
argument_list|,
literal|"hi@there"
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"how/are/you/doing?"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|length
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|toString
argument_list|(
literal|'@'
argument_list|)
argument_list|,
literal|"how@are@you@doing?"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultConstructor
specifier|public
name|void
name|testDefaultConstructor
parameter_list|()
block|{
comment|// test that the default constructor (no parameters) currently
comment|// defaults to creating an object with a 0 initial capacity.
comment|// If we change this default later, we also need to change this
comment|// test.
name|CategoryPath
name|p
init|=
name|CategoryPath
operator|.
name|EMPTY
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|p
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubPath
specifier|public
name|void
name|testSubPath
parameter_list|()
block|{
specifier|final
name|CategoryPath
name|p
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"hi"
argument_list|,
literal|"there"
argument_list|,
literal|"man"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|CategoryPath
name|p1
init|=
name|p
operator|.
name|subpath
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hi/there"
argument_list|,
name|p1
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|p1
operator|=
name|p
operator|.
name|subpath
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|p1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hi"
argument_list|,
name|p1
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|p1
operator|=
name|p
operator|.
name|subpath
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|p1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|p1
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|// with all the following lengths, the prefix should be the whole path
name|int
index|[]
name|lengths
init|=
block|{
literal|3
block|,
operator|-
literal|1
block|,
literal|4
block|}
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
name|lengths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|p1
operator|=
name|p
operator|.
name|subpath
argument_list|(
name|lengths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|p1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hi/there/man"
argument_list|,
name|p1
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
argument_list|,
name|p1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|equals
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"hi"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|equals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHashCode
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|hashCode
argument_list|()
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|hashCode
argument_list|()
operator|==
operator|new
name|CategoryPath
argument_list|(
literal|"hi"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLongHashCode
specifier|public
name|void
name|testLongHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|longHashCode
argument_list|()
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|longHashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|longHashCode
argument_list|()
operator|==
operator|new
name|CategoryPath
argument_list|(
literal|"hi"
argument_list|)
operator|.
name|longHashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|longHashCode
argument_list|()
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
operator|.
name|longHashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArrayConstructor
specifier|public
name|void
name|testArrayConstructor
parameter_list|()
block|{
name|CategoryPath
name|p
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|,
literal|"yo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|p
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello/world/yo"
argument_list|,
name|p
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCharsNeededForFullPath
specifier|public
name|void
name|testCharsNeededForFullPath
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|CategoryPath
operator|.
name|EMPTY
operator|.
name|fullPathLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|components
init|=
block|{
literal|"hello"
block|,
literal|"world"
block|,
literal|"yo"
block|}
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|components
argument_list|)
decl_stmt|;
name|int
name|expectedCharsNeeded
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|comp
range|:
name|components
control|)
block|{
name|expectedCharsNeeded
operator|+=
name|comp
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|expectedCharsNeeded
operator|+=
name|cp
operator|.
name|length
operator|-
literal|1
expr_stmt|;
comment|// delimiter chars
name|assertEquals
argument_list|(
name|expectedCharsNeeded
argument_list|,
name|cp
operator|.
name|fullPathLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyToCharArray
specifier|public
name|void
name|testCopyToCharArray
parameter_list|()
block|{
name|CategoryPath
name|p
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|,
literal|"yo"
argument_list|)
decl_stmt|;
name|char
index|[]
name|charArray
init|=
operator|new
name|char
index|[
name|p
operator|.
name|fullPathLength
argument_list|()
index|]
decl_stmt|;
name|int
name|numCharsCopied
init|=
name|p
operator|.
name|copyFullPath
argument_list|(
name|charArray
argument_list|,
literal|0
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|fullPathLength
argument_list|()
argument_list|,
name|numCharsCopied
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello.world.yo"
argument_list|,
operator|new
name|String
argument_list|(
name|charArray
argument_list|,
literal|0
argument_list|,
name|numCharsCopied
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompareTo
specifier|public
name|void
name|testCompareTo
parameter_list|()
block|{
name|CategoryPath
name|p
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a/b/c/d"
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|CategoryPath
name|pother
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a/b/c/d"
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|pother
operator|.
name|compareTo
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|compareTo
argument_list|(
name|pother
argument_list|)
argument_list|)
expr_stmt|;
name|pother
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|""
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pother
operator|.
name|compareTo
argument_list|(
name|p
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|compareTo
argument_list|(
name|pother
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|pother
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"a/b_/c/d"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pother
operator|.
name|compareTo
argument_list|(
name|p
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|compareTo
argument_list|(
name|pother
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|pother
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"a/b/c"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pother
operator|.
name|compareTo
argument_list|(
name|p
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|compareTo
argument_list|(
name|pother
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|pother
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"a/b/c/e"
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pother
operator|.
name|compareTo
argument_list|(
name|p
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|compareTo
argument_list|(
name|pother
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyNullComponents
specifier|public
name|void
name|testEmptyNullComponents
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-4724: CategoryPath should not allow empty or null components
name|String
index|[]
index|[]
name|components_tests
init|=
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"test"
block|}
block|,
comment|// empty in the beginning
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|""
block|}
block|,
comment|// empty in the end
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|""
block|,
literal|"foo"
block|}
block|,
comment|// empty in the middle
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|"test"
block|}
block|,
comment|// null at the beginning
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|null
block|}
block|,
comment|// null in the end
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|null
block|,
literal|"foo"
block|}
block|,
comment|// null in the middle
block|}
decl_stmt|;
for|for
control|(
name|String
index|[]
name|components
range|:
name|components_tests
control|)
block|{
try|try
block|{
name|assertNotNull
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"empty or null components should not be allowed: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
name|String
index|[]
name|path_tests
init|=
operator|new
name|String
index|[]
block|{
literal|"/test"
block|,
comment|// empty in the beginning
literal|"test//foo"
block|,
comment|// empty in the middle
block|}
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|path_tests
control|)
block|{
try|try
block|{
name|assertNotNull
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"empty or null components should not be allowed: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
comment|// a trailing path separator is produces only one component
name|assertNotNull
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"test/"
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidDelimChar
specifier|public
name|void
name|testInvalidDelimChar
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure CategoryPath doesn't silently corrupt:
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|100
index|]
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"foo/bar"
argument_list|)
decl_stmt|;
try|try
block|{
name|cp
operator|.
name|toString
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|cp
operator|.
name|copyFullPath
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
name|cp
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"abc"
argument_list|,
literal|"foo/bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|cp
operator|.
name|toString
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|cp
operator|.
name|copyFullPath
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
name|cp
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"foo:bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|cp
operator|.
name|toString
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|cp
operator|.
name|copyFullPath
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|':'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
name|cp
operator|=
operator|new
name|CategoryPath
argument_list|(
literal|"abc"
argument_list|,
literal|"foo:bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|cp
operator|.
name|toString
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|cp
operator|.
name|copyFullPath
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|':'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit

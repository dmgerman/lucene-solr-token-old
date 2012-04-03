begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|CharBufferTest
specifier|public
class|class
name|CharBufferTest
extends|extends
name|TestCase
block|{
DECL|method|testCreate
specifier|public
name|void
name|testCreate
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|cb
operator|=
operator|new
name|CharBuffer
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be possible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|cb
operator|=
operator|new
name|CharBuffer
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendChar
specifier|public
name|void
name|testAppendChar
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|expected
init|=
literal|""
decl_stmt|;
for|for
control|(
name|char
name|c
init|=
literal|'a'
init|;
name|c
operator|<
literal|'z'
condition|;
name|c
operator|++
control|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|expected
operator|+=
name|c
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|()
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppendCharArray
specifier|public
name|void
name|testAppendCharArray
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|char
index|[]
name|abcd
init|=
literal|"abcd"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|""
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|abcd
argument_list|)
expr_stmt|;
name|expected
operator|+=
literal|"abcd"
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppendString
specifier|public
name|void
name|testAppendString
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|abcd
init|=
literal|"abcd"
decl_stmt|;
name|String
name|expected
init|=
literal|""
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|abcd
argument_list|)
expr_stmt|;
name|expected
operator|+=
name|abcd
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppendStringBuffer
specifier|public
name|void
name|testAppendStringBuffer
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|StringBuffer
name|abcd
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|String
name|expected
init|=
literal|""
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|abcd
argument_list|)
expr_stmt|;
name|expected
operator|+=
literal|"abcd"
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppendCharBuffer
specifier|public
name|void
name|testAppendCharBuffer
parameter_list|()
block|{
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CharBuffer
name|abcd
init|=
operator|new
name|CharBuffer
argument_list|(
literal|17
argument_list|)
decl_stmt|;
name|abcd
operator|.
name|append
argument_list|(
literal|"abcd"
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|""
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|abcd
argument_list|)
expr_stmt|;
name|expected
operator|+=
literal|"abcd"
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testShrink
specifier|public
name|void
name|testShrink
parameter_list|()
block|{
name|String
name|data
init|=
literal|"123456789012345678901234567890"
decl_stmt|;
name|CharBuffer
name|cb
init|=
operator|new
name|CharBuffer
argument_list|(
name|data
operator|.
name|length
argument_list|()
operator|+
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|()
operator|+
literal|100
argument_list|,
name|cb
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|()
operator|+
literal|100
argument_list|,
name|cb
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|()
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|shrink
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|()
argument_list|,
name|cb
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|()
argument_list|,
name|cb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//-- the following test cases have been adapted from the HttpComponents project
comment|//-- written by Oleg Kalnichevski
DECL|method|testSimpleAppend
specifier|public
name|void
name|testSimpleAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer
init|=
operator|new
name|CharBuffer
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|buffer
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|b1
init|=
name|buffer
operator|.
name|getCharacters
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|tmp
init|=
operator|new
name|char
index|[]
block|{
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|}
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|buffer
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|b2
init|=
name|buffer
operator|.
name|getCharacters
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|b2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|b2
operator|.
name|length
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
name|tmp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|tmp
index|[
name|i
index|]
argument_list|,
name|b2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"1234"
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|buffer
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendString2
specifier|public
name|void
name|testAppendString2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer
init|=
operator|new
name|CharBuffer
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"stuff"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" and more stuff"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"stuff and more stuff"
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendNull
specifier|public
name|void
name|testAppendNull
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer
init|=
operator|new
name|CharBuffer
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|StringBuffer
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|CharBuffer
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|char
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendCharArrayBuffer
specifier|public
name|void
name|testAppendCharArrayBuffer
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer1
init|=
operator|new
name|CharBuffer
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|buffer1
operator|.
name|append
argument_list|(
literal|" and more stuff"
argument_list|)
expr_stmt|;
name|CharBuffer
name|buffer2
init|=
operator|new
name|CharBuffer
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|buffer2
operator|.
name|append
argument_list|(
literal|"stuff"
argument_list|)
expr_stmt|;
name|buffer2
operator|.
name|append
argument_list|(
name|buffer1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"stuff and more stuff"
argument_list|,
name|buffer2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendSingleChar
specifier|public
name|void
name|testAppendSingleChar
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer
init|=
operator|new
name|CharBuffer
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'1'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'2'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'3'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'4'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'5'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'6'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456"
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testProvideCapacity
specifier|public
name|void
name|testProvideCapacity
parameter_list|()
throws|throws
name|Exception
block|{
name|CharBuffer
name|buffer
init|=
operator|new
name|CharBuffer
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|provideCapacity
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|provideCapacity
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|buffer
operator|.
name|capacity
argument_list|()
operator|>=
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

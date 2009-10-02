begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestTermAttributeImpl
specifier|public
class|class
name|TestTermAttributeImpl
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestTermAttributeImpl
specifier|public
name|TestTermAttributeImpl
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testResize
specifier|public
name|void
name|testResize
parameter_list|()
block|{
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
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
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|.
name|resizeTermBuffer
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|<=
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGrow
specifier|public
name|void
name|testGrow
parameter_list|()
block|{
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ab"
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1048576
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1179654
argument_list|,
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// now as a string, first variant
name|t
operator|=
operator|new
name|TermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"ab"
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1048576
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1179654
argument_list|,
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// now as a string, second variant
name|t
operator|=
operator|new
name|TermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"ab"
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1048576
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1179654
argument_list|,
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Test for slow growth to a long term
name|t
operator|=
operator|new
name|TermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"a"
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
literal|20000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|20000
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20167
argument_list|,
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Test for slow growth to a long term
name|t
operator|=
operator|new
name|TermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"a"
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
literal|20000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|20000
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20167
argument_list|,
name|t
operator|.
name|termBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|b
init|=
block|{
literal|'a'
block|,
literal|'l'
block|,
literal|'o'
block|,
literal|'h'
block|,
literal|'a'
block|}
decl_stmt|;
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"term=aloha"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
literal|"hi there"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"term=hi there"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMixedStringArray
specifier|public
name|void
name|testMixedStringArray
parameter_list|()
throws|throws
name|Exception
block|{
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|termLength
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
literal|"hello2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|termLength
argument_list|()
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
literal|"hello3"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
literal|"hello3"
argument_list|)
expr_stmt|;
comment|// Make sure if we get the buffer and change a character
comment|// that term() reflects the change
name|char
index|[]
name|buffer
init|=
name|t
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
literal|'o'
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
literal|"hollo3"
argument_list|)
expr_stmt|;
block|}
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
throws|throws
name|Exception
block|{
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
name|t
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|TermAttributeImpl
name|copy
init|=
operator|(
name|TermAttributeImpl
operator|)
name|TestSimpleAttributeImpls
operator|.
name|assertCloneIsEqual
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
name|copy
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|termBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|TermAttributeImpl
name|t1a
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content1a
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t1a
operator|.
name|setTermBuffer
argument_list|(
name|content1a
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|TermAttributeImpl
name|t1b
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content1b
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t1b
operator|.
name|setTermBuffer
argument_list|(
name|content1b
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|TermAttributeImpl
name|t2
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content2
init|=
literal|"hello2"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t2
operator|.
name|setTermBuffer
argument_list|(
name|content2
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t1a
operator|.
name|equals
argument_list|(
name|t1b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t1a
operator|.
name|equals
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t2
operator|.
name|equals
argument_list|(
name|t1b
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCopyTo
specifier|public
name|void
name|testCopyTo
parameter_list|()
throws|throws
name|Exception
block|{
name|TermAttributeImpl
name|t
init|=
operator|new
name|TermAttributeImpl
argument_list|()
decl_stmt|;
name|TermAttributeImpl
name|copy
init|=
operator|(
name|TermAttributeImpl
operator|)
name|TestSimpleAttributeImpls
operator|.
name|assertCopyIsEqual
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|copy
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|TermAttributeImpl
argument_list|()
expr_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
name|t
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|copy
operator|=
operator|(
name|TermAttributeImpl
operator|)
name|TestSimpleAttributeImpls
operator|.
name|assertCopyIsEqual
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|term
argument_list|()
argument_list|,
name|copy
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|termBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

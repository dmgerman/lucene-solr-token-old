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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockTokenizer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|TestUtil
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_class
DECL|class|TestPackedTokenAttributeImpl
specifier|public
class|class
name|TestPackedTokenAttributeImpl
extends|extends
name|LuceneTestCase
block|{
comment|/* the CharTermAttributeStuff is tested by TestCharTermAttributeImpl */
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
throws|throws
name|Exception
block|{
name|PackedTokenAttributeImpl
name|t
init|=
operator|new
name|PackedTokenAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|copyBuffer
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
name|buffer
argument_list|()
decl_stmt|;
name|PackedTokenAttributeImpl
name|copy
init|=
name|TestCharTermAttributeImpl
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
name|toString
argument_list|()
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|buffer
argument_list|()
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
name|PackedTokenAttributeImpl
name|t
init|=
operator|new
name|PackedTokenAttributeImpl
argument_list|()
decl_stmt|;
name|PackedTokenAttributeImpl
name|copy
init|=
name|TestCharTermAttributeImpl
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|PackedTokenAttributeImpl
argument_list|()
expr_stmt|;
name|t
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|copyBuffer
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
name|buffer
argument_list|()
decl_stmt|;
name|copy
operator|=
name|TestCharTermAttributeImpl
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
name|toString
argument_list|()
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|buffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPackedTokenAttributeFactory
specifier|public
name|void
name|testPackedTokenAttributeFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|TokenStream
operator|.
name|DEFAULT_TOKEN_ATTRIBUTE_FACTORY
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
name|MockTokenizer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|ts
operator|)
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foo bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CharTermAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PackedTokenAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OffsetAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PackedTokenAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PositionIncrementAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PackedTokenAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TypeAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PackedTokenAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FlagsAttribute is not implemented by FlagsAttributeImpl"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|FlagsAttributeImpl
argument_list|)
expr_stmt|;
block|}
DECL|method|testAttributeReflection
specifier|public
name|void
name|testAttributeReflection
parameter_list|()
throws|throws
name|Exception
block|{
name|PackedTokenAttributeImpl
name|t
init|=
operator|new
name|PackedTokenAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setOffset
argument_list|(
literal|6
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionLength
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|t
operator|.
name|setType
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
name|t
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#term"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#bytes"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#startOffset"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#endOffset"
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionIncrement"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionLength"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TypeAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#type"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

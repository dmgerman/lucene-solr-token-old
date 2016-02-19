begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package
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
name|analysis
operator|.
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
begin_comment
comment|/**  * Simple tests to ensure the simple truncation filter factory is working.  */
end_comment
begin_class
DECL|class|TestTruncateTokenFilterFactory
specifier|public
class|class
name|TestTruncateTokenFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
comment|/**    * Ensure the filter actually truncates text.    */
DECL|method|testTruncating
specifier|public
name|void
name|testTruncating
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"abcdefg 1234567 ABCDEFG abcde abc 12345 123"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|stream
operator|)
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Truncate"
argument_list|,
name|TruncateTokenFilterFactory
operator|.
name|PREFIX_LENGTH_KEY
argument_list|,
literal|"5"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abcde"
block|,
literal|"12345"
block|,
literal|"ABCDE"
block|,
literal|"abcde"
block|,
literal|"abc"
block|,
literal|"12345"
block|,
literal|"123"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that bogus arguments result in exception    */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|tokenFilterFactory
argument_list|(
literal|"Truncate"
argument_list|,
name|TruncateTokenFilterFactory
operator|.
name|PREFIX_LENGTH_KEY
argument_list|,
literal|"5"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameter(s):"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that negative prefix length result in exception    */
DECL|method|testNonPositivePrefixLengthArgument
specifier|public
name|void
name|testNonPositivePrefixLengthArgument
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|tokenFilterFactory
argument_list|(
literal|"Truncate"
argument_list|,
name|TruncateTokenFilterFactory
operator|.
name|PREFIX_LENGTH_KEY
argument_list|,
literal|"-5"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|TruncateTokenFilterFactory
operator|.
name|PREFIX_LENGTH_KEY
operator|+
literal|" parameter must be a positive number: -5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

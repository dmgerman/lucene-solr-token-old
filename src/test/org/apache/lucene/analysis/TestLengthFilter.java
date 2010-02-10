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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
begin_class
DECL|class|TestLengthFilter
specifier|public
class|class
name|TestLengthFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"short toolong evenmuchlongertext a ab toolong foo"
argument_list|)
argument_list|)
decl_stmt|;
name|LengthFilter
name|filter
init|=
operator|new
name|LengthFilter
argument_list|(
name|stream
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"short"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

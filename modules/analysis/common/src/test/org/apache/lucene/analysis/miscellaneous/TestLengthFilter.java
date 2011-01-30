begin_unit
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
name|*
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
DECL|method|testFilterNoPosIncr
specifier|public
name|void
name|testFilterNoPosIncr
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
literal|false
argument_list|,
name|stream
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"ab"
block|,
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterWithPosIncr
specifier|public
name|void
name|testFilterWithPosIncr
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
literal|true
argument_list|,
name|stream
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"ab"
block|,
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

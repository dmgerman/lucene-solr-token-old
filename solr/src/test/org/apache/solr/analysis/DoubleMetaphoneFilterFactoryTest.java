begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|TermAttribute
import|;
end_import
begin_class
DECL|class|DoubleMetaphoneFilterFactoryTest
specifier|public
class|class
name|DoubleMetaphoneFilterFactoryTest
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|DoubleMetaphoneFilterFactory
name|factory
init|=
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|TokenStream
name|inputStream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"international"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filteredStream
init|=
name|factory
operator|.
name|create
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DoubleMetaphoneFilter
operator|.
name|class
argument_list|,
name|filteredStream
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filteredStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"international"
block|,
literal|"ANTR"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSettingSizeAndInject
specifier|public
name|void
name|testSettingSizeAndInject
parameter_list|()
throws|throws
name|Exception
block|{
name|DoubleMetaphoneFilterFactory
name|factory
init|=
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|put
argument_list|(
literal|"inject"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|put
argument_list|(
literal|"maxCodeLength"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|TokenStream
name|inputStream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"international"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filteredStream
init|=
name|factory
operator|.
name|create
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DoubleMetaphoneFilter
operator|.
name|class
argument_list|,
name|filteredStream
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filteredStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ANTRNXNL"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that reset() removes any state (buffered tokens)    */
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|DoubleMetaphoneFilterFactory
name|factory
init|=
operator|new
name|DoubleMetaphoneFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|TokenStream
name|inputStream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"international"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filteredStream
init|=
name|factory
operator|.
name|create
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filteredStream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DoubleMetaphoneFilter
operator|.
name|class
argument_list|,
name|filteredStream
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filteredStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"international"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|filteredStream
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// ensure there are no more tokens, such as ANTRNXNL
name|assertFalse
argument_list|(
name|filteredStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

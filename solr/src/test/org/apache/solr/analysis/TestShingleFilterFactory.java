begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Simple tests to ensure the Shingle filter factory works.  */
end_comment
begin_class
DECL|class|TestShingleFilterFactory
specifier|public
class|class
name|TestShingleFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test the defaults    */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
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
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is"
block|,
literal|"is"
block|,
literal|"is a"
block|,
literal|"a"
block|,
literal|"a test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with unigrams disabled    */
DECL|method|testNoUnigrams
specifier|public
name|void
name|testNoUnigrams
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
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is"
block|,
literal|"is a"
block|,
literal|"a test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with a higher max shingle size    */
DECL|method|testMaxShingleSize
specifier|public
name|void
name|testMaxShingleSize
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
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is"
block|,
literal|"this is a"
block|,
literal|"is"
block|,
literal|"is a"
block|,
literal|"is a test"
block|,
literal|"a"
block|,
literal|"a test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

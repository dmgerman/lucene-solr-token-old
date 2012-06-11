begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
begin_comment
comment|/**  * Simple tests to ensure this factory is working  */
end_comment
begin_class
DECL|class|TestPatternReplaceFilterFactory
specifier|public
class|class
name|TestPatternReplaceFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testReplaceAll
specifier|public
name|void
name|testReplaceAll
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|PatternReplaceFilterFactory
name|factory
init|=
operator|new
name|PatternReplaceFilterFactory
argument_list|()
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
literal|"pattern"
argument_list|,
literal|"a*b"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"replacement"
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-foo-foo-foo-"
block|,
literal|"-"
block|,
literal|"c-"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

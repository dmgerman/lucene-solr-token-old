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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
begin_class
DECL|class|LengthFilterTest
specifier|public
class|class
name|LengthFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|LengthFilterFactory
name|factory
init|=
operator|new
name|LengthFilterFactory
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
name|LengthFilterFactory
operator|.
name|MIN_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|LengthFilterFactory
operator|.
name|MAX_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// default: args.put("enablePositionIncrements", "false");
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|test
init|=
literal|"foo foobar super-duper-trooper"
decl_stmt|;
name|TokenStream
name|stream
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
name|test
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
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|LengthFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|LengthFilterFactory
operator|.
name|MIN_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|LengthFilterFactory
operator|.
name|MAX_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|stream
operator|=
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
name|test
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
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
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

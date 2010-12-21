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
begin_comment
comment|/** basic tests for {@link ICUTokenizerFactory} **/
end_comment
begin_class
DECL|class|TestICUTokenizerFactory
specifier|public
class|class
name|TestICUTokenizerFactory
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testMixedText
specifier|public
name|void
name|testMixedText
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
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ  This is a test àºàº§à»àº²àºàº­àº"
argument_list|)
decl_stmt|;
name|ICUTokenizerFactory
name|factory
init|=
operator|new
name|ICUTokenizerFactory
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
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
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|,
literal|"àºàº§à»àº²"
block|,
literal|"àºàº­àº"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

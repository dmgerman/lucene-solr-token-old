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
name|TokenStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
import|;
end_import
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
name|Collections
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
comment|/**  * Simple tests for {@link JapaneseKatakanaStemFilterFactory}  */
end_comment
begin_class
DECL|class|TestJapaneseKatakanaStemFilterFactory
specifier|public
class|class
name|TestJapaneseKatakanaStemFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testKatakanaStemming
specifier|public
name|void
name|testKatakanaStemming
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokenizerArgs
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|tokenizerFactory
operator|.
name|init
argument_list|(
name|tokenizerArgs
argument_list|)
expr_stmt|;
name|tokenizerFactory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tokenStream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æå¾æ¥ãã¼ãã£ã¼ã«è¡ãäºå®ããããå³æ¸é¤¨ã§è³æãã³ãã¼ãã¾ããã"
argument_list|)
argument_list|)
decl_stmt|;
name|JapaneseKatakanaStemFilterFactory
name|filterFactory
init|=
operator|new
name|JapaneseKatakanaStemFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filterArgs
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|filterFactory
operator|.
name|init
argument_list|(
name|filterArgs
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filterFactory
operator|.
name|create
argument_list|(
name|tokenStream
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æå¾æ¥"
block|,
literal|"ãã¼ãã£"
block|,
literal|"ã«"
block|,
literal|"è¡ã"
block|,
literal|"äºå®"
block|,
literal|"ã"
block|,
literal|"ãã"
block|,
comment|// ãã¼ãã£ã¼ should be stemmed
literal|"å³æ¸é¤¨"
block|,
literal|"ã§"
block|,
literal|"è³æ"
block|,
literal|"ã"
block|,
literal|"ã³ãã¼"
block|,
literal|"ã"
block|,
literal|"ã¾ã"
block|,
literal|"ã"
block|}
comment|// ã³ãã¼ should not be stemmed
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

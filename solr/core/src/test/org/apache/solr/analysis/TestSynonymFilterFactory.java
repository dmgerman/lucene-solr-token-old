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
name|ByteArrayInputStream
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
name|InputStream
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
name|Arrays
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
name|List
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
name|synonym
operator|.
name|SynonymFilter
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
name|Version
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
name|common
operator|.
name|ResourceLoader
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
begin_class
DECL|class|TestSynonymFilterFactory
specifier|public
class|class
name|TestSynonymFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
comment|/** test that we can parse and use the solr syn file */
DECL|method|testSynonyms
specifier|public
name|void
name|testSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymFilterFactory
name|factory
init|=
operator|new
name|SynonymFilterFactory
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
name|putAll
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
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
literal|"GB"
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
name|assertTrue
argument_list|(
name|ts
operator|instanceof
name|SynonymFilter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GB"
block|,
literal|"gib"
block|,
literal|"gigabyte"
block|,
literal|"gigabytes"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** test that we can parse and use the solr syn file, with the old impl    * @deprecated Remove this test in Lucene 5.0 */
annotation|@
name|Deprecated
DECL|method|testSynonymsOld
specifier|public
name|void
name|testSynonymsOld
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymFilterFactory
name|factory
init|=
operator|new
name|SynonymFilterFactory
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
literal|"luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_33
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
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
literal|"GB"
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
name|assertTrue
argument_list|(
name|ts
operator|instanceof
name|SlowSynonymFilter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GB"
block|,
literal|"gib"
block|,
literal|"gigabyte"
block|,
literal|"gigabytes"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** test multiword offsets with the old impl    * @deprecated Remove this test in Lucene 5.0 */
annotation|@
name|Deprecated
DECL|method|testMultiwordOffsetsOld
specifier|public
name|void
name|testMultiwordOffsetsOld
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymFilterFactory
name|factory
init|=
operator|new
name|SynonymFilterFactory
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
literal|"luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_33
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|"national hockey league, nhl"
argument_list|)
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
literal|"national hockey league"
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
comment|// WTF?
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"national"
block|,
literal|"nhl"
block|,
literal|"hockey"
block|,
literal|"league"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|22
block|,
literal|22
block|,
literal|22
block|,
literal|22
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** if the synonyms are completely empty, test that we still analyze correctly */
DECL|method|testEmptySynonyms
specifier|public
name|void
name|testEmptySynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymFilterFactory
name|factory
init|=
operator|new
name|SynonymFilterFactory
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
name|putAll
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// empty file!
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
literal|"GB"
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
literal|"GB"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|StringMockSolrResourceLoader
specifier|private
class|class
name|StringMockSolrResourceLoader
implements|implements
name|ResourceLoader
block|{
DECL|field|text
name|String
name|text
decl_stmt|;
DECL|method|StringMockSolrResourceLoader
name|StringMockSolrResourceLoader
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
DECL|method|getLines
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|text
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit

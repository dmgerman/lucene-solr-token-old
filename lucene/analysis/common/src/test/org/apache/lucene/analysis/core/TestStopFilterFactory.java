begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|util
operator|.
name|CharArraySet
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|TestStopFilterFactory
specifier|public
class|class
name|TestStopFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testInform
specifier|public
name|void
name|testInform
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"loader is null and it shouldn't be"
argument_list|,
name|loader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|StopFilterFactory
name|factory
init|=
operator|new
name|StopFilterFactory
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
literal|"words"
argument_list|,
literal|"stop-1.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
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
name|loader
argument_list|)
expr_stmt|;
name|CharArraySet
name|words
init|=
name|factory
operator|.
name|getStopWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|StopFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"words"
argument_list|,
literal|"stop-1.txt, stop-2.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
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
name|loader
argument_list|)
expr_stmt|;
name|words
operator|=
name|factory
operator|.
name|getStopWords
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|StopFilterFactory
argument_list|()
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"words"
argument_list|,
literal|"stop-snowball.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
literal|"snowball"
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
name|loader
argument_list|)
expr_stmt|;
name|words
operator|=
name|factory
operator|.
name|getStopWords
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|words
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"he"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"him"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"his"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"himself"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"she"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"her"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"hers"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"herself"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

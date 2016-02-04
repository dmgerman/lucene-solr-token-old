begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.hy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hy
package|;
end_package
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|util
operator|.
name|CharArraySet
import|;
end_import
begin_class
DECL|class|TestArmenianAnalyzer
specifier|public
class|class
name|TestArmenianAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** This test fails with NPE when the     * stopwords file is missing in classpath */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|ArmenianAnalyzer
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** test stopwords and stemming */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|ArmenianAnalyzer
argument_list|()
decl_stmt|;
comment|// stemming
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Õ¡ÖÕ®Õ«Õ¾"
argument_list|,
literal|"Õ¡ÖÕ®"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Õ¡ÖÕ®Õ«Õ¾Õ¶Õ¥Ö"
argument_list|,
literal|"Õ¡ÖÕ®"
argument_list|)
expr_stmt|;
comment|// stopword
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Õ§"
argument_list|,
operator|new
name|String
index|[]
block|{ }
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** test use of exclusion set */
DECL|method|testExclude
specifier|public
name|void
name|testExclude
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|exclusionSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|asSet
argument_list|(
literal|"Õ¡ÖÕ®Õ«Õ¾Õ¶Õ¥Ö"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|ArmenianAnalyzer
argument_list|(
name|ArmenianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Õ¡ÖÕ®Õ«Õ¾Õ¶Õ¥Ö"
argument_list|,
literal|"Õ¡ÖÕ®Õ«Õ¾Õ¶Õ¥Ö"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Õ¡ÖÕ®Õ«Õ¾"
argument_list|,
literal|"Õ¡ÖÕ®"
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|ArmenianAnalyzer
argument_list|()
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

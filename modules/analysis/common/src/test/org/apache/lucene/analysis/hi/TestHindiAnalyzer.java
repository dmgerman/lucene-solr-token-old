begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hi
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Tests the HindiAnalyzer  */
end_comment
begin_class
DECL|class|TestHindiAnalyzer
specifier|public
class|class
name|TestHindiAnalyzer
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
name|HindiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|HindiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// two ways to write 'hindi' itself.
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"à¤¹à¤¿à¤¨à¥à¤¦à¥"
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦à¥"
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusionSet
specifier|public
name|void
name|testExclusionSet
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|exclusionSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|asSet
argument_list|(
literal|"à¤¹à¤¿à¤à¤¦à¥"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|HindiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|HindiAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦à¥"
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦à¥"
argument_list|)
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
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|HindiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

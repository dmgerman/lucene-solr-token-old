begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.uima.ae
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
operator|.
name|ae
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngine
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|resource
operator|.
name|ResourceInitializationException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
comment|/**  * TestCase for {@link OverridingParamsAEProvider}  */
end_comment
begin_class
DECL|class|OverridingParamsAEProviderTest
specifier|public
class|class
name|OverridingParamsAEProviderTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testNullMapInitialization
specifier|public
name|void
name|testNullMapInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|ResourceInitializationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|AEProvider
name|aeProvider
init|=
operator|new
name|OverridingParamsAEProvider
argument_list|(
literal|"/uima/TestEntityAnnotatorAE.xml"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|aeProvider
operator|.
name|getAE
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyMapInitialization
specifier|public
name|void
name|testEmptyMapInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|AEProvider
name|aeProvider
init|=
operator|new
name|OverridingParamsAEProvider
argument_list|(
literal|"/uima/TestEntityAnnotatorAE.xml"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|AnalysisEngine
name|analysisEngine
init|=
name|aeProvider
operator|.
name|getAE
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|analysisEngine
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOverridingParamsInitialization
specifier|public
name|void
name|testOverridingParamsInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|runtimeParameters
operator|.
name|put
argument_list|(
literal|"ngramsize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|AEProvider
name|aeProvider
init|=
operator|new
name|OverridingParamsAEProvider
argument_list|(
literal|"/uima/AggregateSentenceAE.xml"
argument_list|,
name|runtimeParameters
argument_list|)
decl_stmt|;
name|AnalysisEngine
name|analysisEngine
init|=
name|aeProvider
operator|.
name|getAE
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|analysisEngine
argument_list|)
expr_stmt|;
name|Object
name|parameterValue
init|=
name|analysisEngine
operator|.
name|getConfigParameterValue
argument_list|(
literal|"ngramsize"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parameterValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|3
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|parameterValue
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

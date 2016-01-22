begin_unit
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
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
name|search
operator|.
name|similarities
operator|.
name|DFISimilarity
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
name|search
operator|.
name|similarities
operator|.
name|IndependenceChiSquared
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Tests {@link DFISimilarityFactory}  */
end_comment
begin_class
DECL|class|TestDFISimilarityFactory
specifier|public
class|class
name|TestDFISimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-dfi.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * dfi with no parameters    */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFISimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|DFISimilarity
name|dfi
init|=
operator|(
name|DFISimilarity
operator|)
name|sim
decl_stmt|;
name|assertTrue
argument_list|(
name|dfi
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dfi
operator|.
name|getIndependence
argument_list|()
operator|instanceof
name|IndependenceChiSquared
argument_list|)
expr_stmt|;
block|}
comment|/**    * dfi with discountOverlaps parameter set to false    */
DECL|method|testParameters
specifier|public
name|void
name|testParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_params"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFISimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|DFISimilarity
name|dfr
init|=
operator|(
name|DFISimilarity
operator|)
name|sim
decl_stmt|;
name|assertFalse
argument_list|(
name|dfr
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

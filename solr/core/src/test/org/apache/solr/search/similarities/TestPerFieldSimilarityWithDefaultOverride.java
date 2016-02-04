begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
operator|.
name|SweetSpotSimilarity
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
name|BM25Similarity
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
comment|/**  * Tests per-field similarity support in the schema when SchemaSimilarityFactory is explicitly  * configured to use a custom default sim for field types that do not override it.  * @see TestPerFieldSimilarity  */
end_comment
begin_class
DECL|class|TestPerFieldSimilarityWithDefaultOverride
specifier|public
class|class
name|TestPerFieldSimilarityWithDefaultOverride
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
literal|"schema-sim-default-override.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where the sim is specified directly */
DECL|method|testDirect
specifier|public
name|void
name|testDirect
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|getSimilarity
argument_list|(
literal|"sim1text"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testDirectDynamic
specifier|public
name|void
name|testDirectDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|getSimilarity
argument_list|(
literal|"text_sim1"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where a configurable sim factory is explicitly defined */
DECL|method|testDirectFactory
specifier|public
name|void
name|testDirectFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|MockConfigurableSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"sim2text"
argument_list|,
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
name|sim
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testDirectFactoryDynamic
specifier|public
name|void
name|testDirectFactoryDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|MockConfigurableSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_sim2"
argument_list|,
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
name|sim
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where no similarity is specified */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|MockConfigurableSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"sim3text"
argument_list|,
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
name|sim
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testDefaultsDynamic
specifier|public
name|void
name|testDefaultsDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|MockConfigurableSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_sim3"
argument_list|,
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
name|sim
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test a field that does not exist */
DECL|method|testNonexistent
specifier|public
name|void
name|testNonexistent
parameter_list|()
throws|throws
name|Exception
block|{
name|MockConfigurableSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_sim3"
argument_list|,
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
name|sim
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

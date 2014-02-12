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
name|DefaultSimilarity
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
name|junit
operator|.
name|After
import|;
end_import
begin_comment
comment|/**  * Verifies that the default behavior of the implicit {@link DefaultSimilarityFactory}   * (ie: no similarity configured in schema.xml at all) is consistnent with   * expectations based on the luceneMatchVersion  * @see<a href="https://issues.apache.org/jira/browse/SOLR-5561">SOLR-5561</a>  */
end_comment
begin_class
DECL|class|TestNonDefinedSimilarityFactory
specifier|public
class|class
name|TestNonDefinedSimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|After
DECL|method|cleanup
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
DECL|method|testCurrent
specifier|public
name|void
name|testCurrent
parameter_list|()
throws|throws
name|Exception
block|{
comment|// no sys prop set, rely on LUCENE_CURRENT
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
name|DefaultSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|DefaultSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|sim
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test47
specifier|public
name|void
name|test47
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_47
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
name|DefaultSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|DefaultSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|sim
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test46
specifier|public
name|void
name|test46
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_46
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
name|DefaultSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|DefaultSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|sim
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
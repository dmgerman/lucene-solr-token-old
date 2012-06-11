begin_unit
begin_package
DECL|package|org.apache.lucene.facet.example
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
package|;
end_package
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
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|ExampleResult
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
name|facet
operator|.
name|example
operator|.
name|association
operator|.
name|AssociationMain
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Test that the association example works as expected. This test helps to  * verify that examples code is alive!  */
end_comment
begin_class
DECL|class|TestAssociationExample
specifier|public
class|class
name|TestAssociationExample
extends|extends
name|LuceneTestCase
block|{
DECL|field|EXPECTED_INT_SUM_RESULTS
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|EXPECTED_INT_SUM_RESULTS
init|=
block|{
literal|4
block|,
literal|2
block|}
decl_stmt|;
DECL|field|EXPECTED_FLOAT_SUM_RESULTS
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|EXPECTED_FLOAT_SUM_RESULTS
init|=
block|{
literal|1.62
block|,
literal|0.34
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testAssociationExamples
specifier|public
name|void
name|testAssociationExamples
parameter_list|()
throws|throws
name|Exception
block|{
name|assertExampleResult
argument_list|(
operator|new
name|AssociationMain
argument_list|()
operator|.
name|runSumIntAssociationSample
argument_list|()
argument_list|,
name|EXPECTED_INT_SUM_RESULTS
argument_list|)
expr_stmt|;
name|assertExampleResult
argument_list|(
operator|new
name|AssociationMain
argument_list|()
operator|.
name|runSumFloatAssociationSample
argument_list|()
argument_list|,
name|EXPECTED_FLOAT_SUM_RESULTS
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExampleResult
specifier|private
name|void
name|assertExampleResult
parameter_list|(
name|ExampleResult
name|res
parameter_list|,
name|double
index|[]
name|expectedResults
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Null result!"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Null facet result!"
argument_list|,
name|res
operator|.
name|getFacetResults
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of results!"
argument_list|,
literal|1
argument_list|,
name|res
operator|.
name|getFacetResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facets!"
argument_list|,
literal|2
argument_list|,
name|res
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNumValidDescendants
argument_list|()
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|?
extends|extends
name|FacetResultNode
argument_list|>
name|it
init|=
name|res
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|getSubResults
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|fResNode
range|:
name|it
control|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong result for facet "
operator|+
name|fResNode
operator|.
name|getLabel
argument_list|()
argument_list|,
name|expectedResults
index|[
name|i
operator|++
index|]
argument_list|,
name|fResNode
operator|.
name|getValue
argument_list|()
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

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
name|adaptive
operator|.
name|AdaptiveMain
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Test that the adaptive example works as expected. This test helps to verify  * that examples code is alive!  */
end_comment
begin_class
DECL|class|TestAdaptiveExample
specifier|public
class|class
name|TestAdaptiveExample
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testAdaptive
specifier|public
name|void
name|testAdaptive
parameter_list|()
throws|throws
name|Exception
block|{
name|ExampleResult
name|res
init|=
operator|new
name|AdaptiveMain
argument_list|()
operator|.
name|runSample
argument_list|()
decl_stmt|;
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
literal|3
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
block|}
block|}
end_class
end_unit

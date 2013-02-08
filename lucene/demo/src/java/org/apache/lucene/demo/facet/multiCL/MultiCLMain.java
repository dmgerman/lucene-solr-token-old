begin_unit
begin_package
DECL|package|org.apache.lucene.demo.facet.multiCL
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
operator|.
name|multiCL
package|;
end_package
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|demo
operator|.
name|facet
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
name|demo
operator|.
name|facet
operator|.
name|ExampleUtils
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
name|FacetResult
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Driver for the multi sample.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiCLMain
specifier|public
class|class
name|MultiCLMain
block|{
comment|/** Sole constructor. */
DECL|method|MultiCLMain
specifier|public
name|MultiCLMain
parameter_list|()
block|{}
comment|/**    * Executes the multi sample.    *     * @throws Exception    *             on error (no detailed exception handling here for sample    *             simplicity    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|MultiCLMain
argument_list|()
operator|.
name|runSample
argument_list|()
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
comment|/** Runs the multi sample and returns the facet results */
DECL|method|runSample
specifier|public
name|ExampleResult
name|runSample
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create Directories for the search index and for the taxonomy index
name|Directory
name|indexDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// index the sample documents
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"index the sample documents..."
argument_list|)
expr_stmt|;
name|MultiCLIndexer
operator|.
name|index
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"search the sample documents..."
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetRes
init|=
name|MultiCLSearcher
operator|.
name|searchWithFacets
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|,
name|MultiCLIndexer
operator|.
name|MULTI_IPARAMS
argument_list|)
decl_stmt|;
name|ExampleResult
name|res
init|=
operator|new
name|ExampleResult
argument_list|()
decl_stmt|;
name|res
operator|.
name|setFacetResults
argument_list|(
name|facetRes
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit

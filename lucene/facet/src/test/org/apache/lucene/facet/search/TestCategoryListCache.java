begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|MatchAllDocsQuery
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetTestBase
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|cache
operator|.
name|CategoryListCache
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
name|cache
operator|.
name|CategoryListData
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
name|params
operator|.
name|CountFacetRequest
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
name|params
operator|.
name|FacetRequest
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
name|params
operator|.
name|FacetSearchParams
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
name|FacetResult
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
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestCategoryListCache
specifier|public
class|class
name|TestCategoryListCache
extends|extends
name|FacetTestBase
block|{
DECL|method|TestCategoryListCache
specifier|public
name|TestCategoryListCache
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|initIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|closeAll
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoClCache
specifier|public
name|void
name|testNoClCache
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorrectClCache
specifier|public
name|void
name|testCorrectClCache
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWrongClCache
specifier|public
name|void
name|testWrongClCache
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|boolean
name|withCache
parameter_list|,
name|boolean
name|plantWrongData
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
name|truth
init|=
name|facetCountsTruth
argument_list|()
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|(
name|CategoryPath
operator|)
name|truth
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
comment|// any category path will do for this test
name|FacetIndexingParams
name|iParams
init|=
name|FacetIndexingParams
operator|.
name|ALL_PARENTS
decl_stmt|;
specifier|final
name|CategoryListCache
name|clCache
decl_stmt|;
if|if
condition|(
name|withCache
condition|)
block|{
comment|//let's use a cached cl data
name|CategoryListParams
name|clp
init|=
operator|new
name|CategoryListParams
argument_list|()
decl_stmt|;
comment|// default term ok as only single list
name|clCache
operator|=
operator|new
name|CategoryListCache
argument_list|()
expr_stmt|;
name|clCache
operator|.
name|loadAndRegister
argument_list|(
name|clp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|plantWrongData
condition|)
block|{
comment|// let's mess up the cached data and then expect a wrong result...
name|messCachedData
argument_list|(
name|clCache
argument_list|,
name|clp
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|clCache
operator|=
literal|null
expr_stmt|;
block|}
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|req
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
name|cp
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FacetSearchParams
name|sParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|req
argument_list|,
name|iParams
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|CategoryListCache
name|getCategoryListCache
parameter_list|()
block|{
return|return
name|clCache
return|;
block|}
block|}
decl_stmt|;
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|(
name|sParams
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
try|try
block|{
name|assertCountsAndCardinality
argument_list|(
name|truth
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Correct results not expected when wrong data was cached"
argument_list|,
name|plantWrongData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Wrong results not expected unless wrong data was cached"
argument_list|,
name|withCache
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong results not expected unless wrong data was cached"
argument_list|,
name|plantWrongData
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Mess the cached data for this {@link CategoryListParams} */
DECL|method|messCachedData
specifier|private
name|void
name|messCachedData
parameter_list|(
name|CategoryListCache
name|clCache
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|)
block|{
specifier|final
name|CategoryListData
name|cld
init|=
name|clCache
operator|.
name|get
argument_list|(
name|clp
argument_list|)
decl_stmt|;
name|CategoryListData
name|badCld
init|=
operator|new
name|CategoryListData
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CategoryListIterator
name|iterator
parameter_list|(
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|CategoryListIterator
name|it
init|=
name|cld
operator|.
name|iterator
argument_list|(
name|partition
argument_list|)
decl_stmt|;
return|return
operator|new
name|CategoryListIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|it
operator|.
name|skipTo
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextCategory
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|res
init|=
name|it
operator|.
name|nextCategory
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|res
return|;
block|}
return|return
name|res
operator|>
literal|1
condition|?
name|res
operator|-
literal|1
else|:
name|res
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|it
operator|.
name|init
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|clCache
operator|.
name|register
argument_list|(
name|clp
argument_list|,
name|badCld
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

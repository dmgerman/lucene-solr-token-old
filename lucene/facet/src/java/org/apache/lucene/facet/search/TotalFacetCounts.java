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
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
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
name|Arrays
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|aggregator
operator|.
name|Aggregator
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
name|aggregator
operator|.
name|CountingAggregator
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
name|taxonomy
operator|.
name|CategoryPath
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
name|TaxonomyReader
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
name|util
operator|.
name|PartitionsUtils
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
name|util
operator|.
name|ScoredDocIdsUtils
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Maintain Total Facet Counts per partition, for given parameters:  *<ul>   *<li>Index reader of an index</li>  *<li>Taxonomy index reader</li>  *<li>Facet indexing params (and particularly the category list params)</li>  *<li></li>  *</ul>  * The total facet counts are maintained as an array of arrays of integers,   * where a separate array is kept for each partition.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|TotalFacetCounts
specifier|public
class|class
name|TotalFacetCounts
block|{
comment|/** total facet counts per partition: totalCounts[partition][ordinal%partitionLength] */
DECL|field|totalCounts
specifier|private
name|int
index|[]
index|[]
name|totalCounts
init|=
literal|null
decl_stmt|;
DECL|field|taxonomy
specifier|private
specifier|final
name|TaxonomyReader
name|taxonomy
decl_stmt|;
DECL|field|facetIndexingParams
specifier|private
specifier|final
name|FacetIndexingParams
name|facetIndexingParams
decl_stmt|;
DECL|field|atomicGen4Test
specifier|private
specifier|final
specifier|static
name|AtomicInteger
name|atomicGen4Test
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/** Creation type for test purposes */
DECL|enum|CreationType
DECL|enum constant|Computed
DECL|enum constant|Loaded
enum|enum
name|CreationType
block|{
name|Computed
block|,
name|Loaded
block|}
comment|// for testing
DECL|field|gen4test
specifier|final
name|int
name|gen4test
decl_stmt|;
DECL|field|createType4test
specifier|final
name|CreationType
name|createType4test
decl_stmt|;
comment|/**     * Construct by key - from index Directory or by recomputing.    */
DECL|method|TotalFacetCounts
specifier|private
name|TotalFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxonomy
parameter_list|,
name|FacetIndexingParams
name|facetIndexingParams
parameter_list|,
name|int
index|[]
index|[]
name|counts
parameter_list|,
name|CreationType
name|createType4Test
parameter_list|)
block|{
name|this
operator|.
name|taxonomy
operator|=
name|taxonomy
expr_stmt|;
name|this
operator|.
name|facetIndexingParams
operator|=
name|facetIndexingParams
expr_stmt|;
name|this
operator|.
name|totalCounts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|createType4test
operator|=
name|createType4Test
expr_stmt|;
name|this
operator|.
name|gen4test
operator|=
name|atomicGen4Test
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Fill a partition's array with the TotalCountsArray values.    * @param partitionArray array to fill    * @param partition number of required partition     */
DECL|method|fillTotalCountsForPartition
specifier|public
name|void
name|fillTotalCountsForPartition
parameter_list|(
name|int
index|[]
name|partitionArray
parameter_list|,
name|int
name|partition
parameter_list|)
block|{
name|int
name|partitionSize
init|=
name|partitionArray
operator|.
name|length
decl_stmt|;
name|int
index|[]
name|countArray
init|=
name|totalCounts
index|[
name|partition
index|]
decl_stmt|;
if|if
condition|(
name|countArray
operator|==
literal|null
condition|)
block|{
name|countArray
operator|=
operator|new
name|int
index|[
name|partitionSize
index|]
expr_stmt|;
name|totalCounts
index|[
name|partition
index|]
operator|=
name|countArray
expr_stmt|;
block|}
name|int
name|length
init|=
name|Math
operator|.
name|min
argument_list|(
name|partitionSize
argument_list|,
name|countArray
operator|.
name|length
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|countArray
argument_list|,
literal|0
argument_list|,
name|partitionArray
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the total count of an input category    * @param ordinal ordinal of category whose total count is required     */
DECL|method|getTotalCount
specifier|public
name|int
name|getTotalCount
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|int
name|partition
init|=
name|PartitionsUtils
operator|.
name|partitionNumber
argument_list|(
name|facetIndexingParams
argument_list|,
name|ordinal
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|ordinal
operator|%
name|PartitionsUtils
operator|.
name|partitionSize
argument_list|(
name|facetIndexingParams
argument_list|,
name|taxonomy
argument_list|)
decl_stmt|;
return|return
name|totalCounts
index|[
name|partition
index|]
index|[
name|offset
index|]
return|;
block|}
DECL|method|loadFromFile
specifier|static
name|TotalFacetCounts
name|loadFromFile
parameter_list|(
name|File
name|inputFile
parameter_list|,
name|TaxonomyReader
name|taxonomy
parameter_list|,
name|FacetIndexingParams
name|facetIndexingParams
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|inputFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|int
index|[]
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|dis
operator|.
name|readInt
argument_list|()
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|counts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|size
init|=
name|dis
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
name|counts
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|counts
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|size
condition|;
name|j
operator|++
control|)
block|{
name|counts
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|TotalFacetCounts
argument_list|(
name|taxonomy
argument_list|,
name|facetIndexingParams
argument_list|,
name|counts
argument_list|,
name|CreationType
operator|.
name|Loaded
argument_list|)
return|;
block|}
finally|finally
block|{
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|storeToFile
specifier|static
name|void
name|storeToFile
parameter_list|(
name|File
name|outputFile
parameter_list|,
name|TotalFacetCounts
name|tfc
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|dos
operator|.
name|writeInt
argument_list|(
name|tfc
operator|.
name|totalCounts
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
index|[]
name|counts
range|:
name|tfc
operator|.
name|totalCounts
control|)
block|{
if|if
condition|(
name|counts
operator|==
literal|null
condition|)
block|{
name|dos
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dos
operator|.
name|writeInt
argument_list|(
name|counts
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
range|:
name|counts
control|)
block|{
name|dos
operator|.
name|writeInt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// needed because FacetSearchParams do not allow empty FacetRequests
DECL|field|DUMMY_REQ
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|DUMMY_REQ
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|FacetRequest
index|[]
block|{
operator|new
name|CountFacetRequest
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
argument_list|,
literal|1
argument_list|)
block|}
argument_list|)
decl_stmt|;
DECL|method|compute
specifier|static
name|TotalFacetCounts
name|compute
parameter_list|(
specifier|final
name|IndexReader
name|indexReader
parameter_list|,
specifier|final
name|TaxonomyReader
name|taxonomy
parameter_list|,
specifier|final
name|FacetIndexingParams
name|facetIndexingParams
parameter_list|,
specifier|final
name|CategoryListCache
name|clCache
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|partitionSize
init|=
name|PartitionsUtils
operator|.
name|partitionSize
argument_list|(
name|facetIndexingParams
argument_list|,
name|taxonomy
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|counts
init|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|taxonomy
operator|.
name|getSize
argument_list|()
operator|/
operator|(
name|float
operator|)
name|partitionSize
argument_list|)
index|]
index|[
name|partitionSize
index|]
decl_stmt|;
name|FacetSearchParams
name|newSearchParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|DUMMY_REQ
argument_list|,
name|facetIndexingParams
argument_list|)
decl_stmt|;
comment|//createAllListsSearchParams(facetIndexingParams,  this.totalCounts);
name|FacetsAccumulator
name|fe
init|=
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|newSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomy
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|HashMap
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
name|getCategoryListMap
parameter_list|(
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
name|Aggregator
name|aggregator
init|=
operator|new
name|CountingAggregator
argument_list|(
name|counts
index|[
name|partition
index|]
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|facetIndexingParams
operator|.
name|getAllCategoryListParams
argument_list|()
control|)
block|{
specifier|final
name|CategoryListIterator
name|cli
init|=
name|clIteraor
argument_list|(
name|clCache
argument_list|,
name|clp
argument_list|,
name|indexReader
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|cli
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
decl_stmt|;
name|fe
operator|.
name|setComplementThreshold
argument_list|(
name|FacetsAccumulator
operator|.
name|DISABLE_COMPLEMENT
argument_list|)
expr_stmt|;
name|fe
operator|.
name|accumulate
argument_list|(
name|ScoredDocIdsUtils
operator|.
name|createAllDocsScoredDocIDs
argument_list|(
name|indexReader
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TotalFacetCounts
argument_list|(
name|taxonomy
argument_list|,
name|facetIndexingParams
argument_list|,
name|counts
argument_list|,
name|CreationType
operator|.
name|Computed
argument_list|)
return|;
block|}
DECL|method|clIteraor
specifier|static
name|CategoryListIterator
name|clIteraor
parameter_list|(
name|CategoryListCache
name|clCache
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|clCache
operator|!=
literal|null
condition|)
block|{
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
if|if
condition|(
name|cld
operator|!=
literal|null
condition|)
block|{
return|return
name|cld
operator|.
name|iterator
argument_list|(
name|partition
argument_list|)
return|;
block|}
block|}
return|return
name|clp
operator|.
name|createCategoryListIterator
argument_list|(
name|indexReader
argument_list|,
name|partition
argument_list|)
return|;
block|}
block|}
end_class
end_unit

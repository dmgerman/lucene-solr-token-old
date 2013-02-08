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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|complements
operator|.
name|TotalFacetCounts
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
name|complements
operator|.
name|TotalFacetCountsCache
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
name|partitions
operator|.
name|IntermediateFacetResult
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
name|partitions
operator|.
name|PartitionsFacetResultsHandler
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
name|FacetRequest
operator|.
name|ResultMode
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
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|AtomicReaderContext
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
name|IntsRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Standard implementation for {@link FacetsAccumulator}, utilizing partitions to save on memory.  *<p>  * Why partitions? Because if there are say 100M categories out of which   * only top K are required, we must first compute value for all 100M categories  * (going over all documents) and only then could we select top K.   * This is made easier on memory by working in partitions of distinct categories:   * Once a values for a partition are found, we take the top K for that   * partition and work on the next partition, them merge the top K of both,   * and so forth, thereby computing top K with RAM needs for the size of   * a single partition rather than for the size of all the 100M categories.  *<p>  * Decision on partitions size is done at indexing time, and the facet information  * for each partition is maintained separately.  *<p>  *<u>Implementation detail:</u> Since facets information of each partition is   * maintained in a separate "category list", we can be more efficient  * at search time, because only the facet info for a single partition   * need to be read while processing that partition.   *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|StandardFacetsAccumulator
specifier|public
class|class
name|StandardFacetsAccumulator
extends|extends
name|FacetsAccumulator
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|StandardFacetsAccumulator
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Default threshold for using the complements optimization.    * If accumulating facets for a document set larger than this ratio of the index size than     * perform the complement optimization.    * @see #setComplementThreshold(double) for more info on the complements optimization.      */
DECL|field|DEFAULT_COMPLEMENT_THRESHOLD
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_COMPLEMENT_THRESHOLD
init|=
literal|0.6
decl_stmt|;
comment|/**    * Passing this to {@link #setComplementThreshold(double)} will disable using complement optimization.    */
DECL|field|DISABLE_COMPLEMENT
specifier|public
specifier|static
specifier|final
name|double
name|DISABLE_COMPLEMENT
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
comment|//> 1 actually
comment|/**    * Passing this to {@link #setComplementThreshold(double)} will force using complement optimization.    */
DECL|field|FORCE_COMPLEMENT
specifier|public
specifier|static
specifier|final
name|double
name|FORCE_COMPLEMENT
init|=
literal|0
decl_stmt|;
comment|//<=0
DECL|field|partitionSize
specifier|protected
name|int
name|partitionSize
decl_stmt|;
DECL|field|maxPartitions
specifier|protected
name|int
name|maxPartitions
decl_stmt|;
DECL|field|isUsingComplements
specifier|protected
name|boolean
name|isUsingComplements
decl_stmt|;
DECL|field|totalFacetCounts
specifier|private
name|TotalFacetCounts
name|totalFacetCounts
decl_stmt|;
DECL|field|accumulateGuard
specifier|private
name|Object
name|accumulateGuard
decl_stmt|;
DECL|field|complementThreshold
specifier|private
name|double
name|complementThreshold
decl_stmt|;
DECL|method|StandardFacetsAccumulator
specifier|public
name|StandardFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
name|this
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
operator|new
name|FacetArrays
argument_list|(
name|PartitionsUtils
operator|.
name|partitionSize
argument_list|(
name|searchParams
operator|.
name|indexingParams
argument_list|,
name|taxonomyReader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|StandardFacetsAccumulator
specifier|public
name|StandardFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|super
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
comment|// can only be computed later when docids size is known
name|isUsingComplements
operator|=
literal|false
expr_stmt|;
name|partitionSize
operator|=
name|PartitionsUtils
operator|.
name|partitionSize
argument_list|(
name|searchParams
operator|.
name|indexingParams
argument_list|,
name|taxonomyReader
argument_list|)
expr_stmt|;
name|maxPartitions
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|this
operator|.
name|taxonomyReader
operator|.
name|getSize
argument_list|()
operator|/
operator|(
name|double
operator|)
name|partitionSize
argument_list|)
expr_stmt|;
name|accumulateGuard
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
comment|// TODO: this should be removed once we clean the API
DECL|method|accumulate
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|)
throws|throws
name|IOException
block|{
comment|// synchronize to prevent calling two accumulate()'s at the same time.
comment|// We decided not to synchronize the method because that might mislead
comment|// users to feel encouraged to call this method simultaneously.
synchronized|synchronized
init|(
name|accumulateGuard
init|)
block|{
comment|// only now we can compute this
name|isUsingComplements
operator|=
name|shouldComplement
argument_list|(
name|docids
argument_list|)
expr_stmt|;
if|if
condition|(
name|isUsingComplements
condition|)
block|{
try|try
block|{
name|totalFacetCounts
operator|=
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
operator|.
name|getTotalCounts
argument_list|(
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
name|searchParams
operator|.
name|indexingParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalFacetCounts
operator|!=
literal|null
condition|)
block|{
name|docids
operator|=
name|ScoredDocIdsUtils
operator|.
name|getComplementSet
argument_list|(
name|docids
argument_list|,
name|indexReader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|isUsingComplements
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// TODO (Facet): this exception is thrown from TotalCountsKey if the
comment|// IndexReader used does not support getVersion(). We should re-think
comment|// this: is this tiny detail worth disabling total counts completely
comment|// for such readers? Currently, it's not supported by Parallel and
comment|// MultiReader, which might be problematic for several applications.
comment|// We could, for example, base our "isCurrent" logic on something else
comment|// than the reader's version. Need to think more deeply about it.
if|if
condition|(
name|logger
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|logger
operator|.
name|log
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|,
literal|"IndexReader used does not support completents: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|isUsingComplements
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|logger
operator|.
name|log
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|,
literal|"Failed to load/calculate total counts (complement counting disabled): "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// silently fail if for some reason failed to load/save from/to dir
name|isUsingComplements
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// give up: this should not happen!
throw|throw
operator|new
name|IOException
argument_list|(
literal|"PANIC: Got unexpected exception while trying to get/calculate total counts"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|docids
operator|=
name|actualDocsToAccumulate
argument_list|(
name|docids
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|FacetRequest
argument_list|,
name|IntermediateFacetResult
argument_list|>
name|fr2tmpRes
init|=
operator|new
name|HashMap
argument_list|<
name|FacetRequest
argument_list|,
name|IntermediateFacetResult
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|part
init|=
literal|0
init|;
name|part
operator|<
name|maxPartitions
condition|;
name|part
operator|++
control|)
block|{
comment|// fill arrays from category lists
name|fillArraysForPartition
argument_list|(
name|docids
argument_list|,
name|facetArrays
argument_list|,
name|part
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|part
operator|*
name|partitionSize
decl_stmt|;
comment|// for each partition we go over all requests and handle
comment|// each, where the request maintains the merged result.
comment|// In this implementation merges happen after each partition,
comment|// but other impl could merge only at the end.
specifier|final
name|HashSet
argument_list|<
name|FacetRequest
argument_list|>
name|handledRequests
init|=
operator|new
name|HashSet
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
comment|// Handle and merge only facet requests which were not already handled.
if|if
condition|(
name|handledRequests
operator|.
name|add
argument_list|(
name|fr
argument_list|)
condition|)
block|{
name|PartitionsFacetResultsHandler
name|frHndlr
init|=
name|createFacetResultsHandler
argument_list|(
name|fr
argument_list|)
decl_stmt|;
name|IntermediateFacetResult
name|res4fr
init|=
name|frHndlr
operator|.
name|fetchPartitionResult
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|IntermediateFacetResult
name|oldRes
init|=
name|fr2tmpRes
operator|.
name|get
argument_list|(
name|fr
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldRes
operator|!=
literal|null
condition|)
block|{
name|res4fr
operator|=
name|frHndlr
operator|.
name|mergeResults
argument_list|(
name|oldRes
argument_list|,
name|res4fr
argument_list|)
expr_stmt|;
block|}
name|fr2tmpRes
operator|.
name|put
argument_list|(
name|fr
argument_list|,
name|res4fr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|facetArrays
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
comment|// gather results from all requests into a list for returning them
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|PartitionsFacetResultsHandler
name|frHndlr
init|=
name|createFacetResultsHandler
argument_list|(
name|fr
argument_list|)
decl_stmt|;
name|IntermediateFacetResult
name|tmpResult
init|=
name|fr2tmpRes
operator|.
name|get
argument_list|(
name|fr
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpResult
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// do not add a null to the list.
block|}
name|FacetResult
name|facetRes
init|=
name|frHndlr
operator|.
name|renderFacetResult
argument_list|(
name|tmpResult
argument_list|)
decl_stmt|;
comment|// final labeling if allowed (because labeling is a costly operation)
name|frHndlr
operator|.
name|labelResult
argument_list|(
name|facetRes
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|facetRes
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
comment|/** check if all requests are complementable */
DECL|method|mayComplement
specifier|protected
name|boolean
name|mayComplement
parameter_list|()
block|{
for|for
control|(
name|FacetRequest
name|freq
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|freq
operator|instanceof
name|CountFacetRequest
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createFacetResultsHandler
specifier|protected
name|PartitionsFacetResultsHandler
name|createFacetResultsHandler
parameter_list|(
name|FacetRequest
name|fr
parameter_list|)
block|{
if|if
condition|(
name|fr
operator|.
name|getResultMode
argument_list|()
operator|==
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
condition|)
block|{
return|return
operator|new
name|TopKInEachNodeHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TopKFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
block|}
comment|/**    * Set the actual set of documents over which accumulation should take place.    *<p>    * Allows to override the set of documents to accumulate for. Invoked just    * before actual accumulating starts. From this point that set of documents    * remains unmodified. Default implementation just returns the input    * unchanged.    *     * @param docids    *          candidate documents to accumulate for    * @return actual documents to accumulate for    */
DECL|method|actualDocsToAccumulate
specifier|protected
name|ScoredDocIDs
name|actualDocsToAccumulate
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docids
return|;
block|}
comment|/** Check if it is worth to use complements */
DECL|method|shouldComplement
specifier|protected
name|boolean
name|shouldComplement
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|)
block|{
return|return
name|mayComplement
argument_list|()
operator|&&
operator|(
name|docids
operator|.
name|size
argument_list|()
operator|>
name|indexReader
operator|.
name|numDocs
argument_list|()
operator|*
name|getComplementThreshold
argument_list|()
operator|)
return|;
block|}
comment|/**    * Iterate over the documents for this partition and fill the facet arrays with the correct    * count/complement count/value.    */
DECL|method|fillArraysForPartition
specifier|private
specifier|final
name|void
name|fillArraysForPartition
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isUsingComplements
condition|)
block|{
name|initArraysByTotalCounts
argument_list|(
name|facetArrays
argument_list|,
name|partition
argument_list|,
name|docids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|facetArrays
operator|.
name|free
argument_list|()
expr_stmt|;
comment|// to get a cleared array for this partition
block|}
name|HashMap
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
name|categoryLists
init|=
name|getCategoryListMap
argument_list|(
name|facetArrays
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
comment|// a reasonable start capacity for most common apps
for|for
control|(
name|Entry
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
name|entry
range|:
name|categoryLists
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|ScoredDocIDsIterator
name|iterator
init|=
name|docids
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|CategoryListIterator
name|categoryListIter
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Aggregator
name|aggregator
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|AtomicReaderContext
argument_list|>
name|contexts
init|=
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|AtomicReaderContext
name|current
init|=
literal|null
decl_stmt|;
name|int
name|maxDoc
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|docID
init|=
name|iterator
operator|.
name|getDocID
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|>=
name|maxDoc
condition|)
block|{
name|boolean
name|iteratorDone
init|=
literal|false
decl_stmt|;
do|do
block|{
comment|// find the segment which contains this document
if|if
condition|(
operator|!
name|contexts
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"ScoredDocIDs contains documents outside this reader's segments !?"
argument_list|)
throw|;
block|}
name|current
operator|=
name|contexts
operator|.
name|next
argument_list|()
expr_stmt|;
name|maxDoc
operator|=
name|current
operator|.
name|docBase
operator|+
name|current
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|docID
operator|<
name|maxDoc
condition|)
block|{
comment|// segment has docs, check if it has categories
name|boolean
name|validSegment
init|=
name|categoryListIter
operator|.
name|setNextReader
argument_list|(
name|current
argument_list|)
decl_stmt|;
name|validSegment
operator|&=
name|aggregator
operator|.
name|setNextReader
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validSegment
condition|)
block|{
comment|// if categoryList or aggregtor say it's an invalid segment, skip all docs
while|while
condition|(
name|docID
operator|<
name|maxDoc
operator|&&
name|iterator
operator|.
name|next
argument_list|()
condition|)
block|{
name|docID
operator|=
name|iterator
operator|.
name|getDocID
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docID
operator|<
name|maxDoc
condition|)
block|{
name|iteratorDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|docID
operator|>=
name|maxDoc
condition|)
do|;
if|if
condition|(
name|iteratorDone
condition|)
block|{
comment|// iterator finished, terminate the loop
break|break;
block|}
block|}
name|docID
operator|-=
name|current
operator|.
name|docBase
expr_stmt|;
name|categoryListIter
operator|.
name|getOrdinals
argument_list|(
name|docID
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
if|if
condition|(
name|ordinals
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// document does not have category ordinals
block|}
name|aggregator
operator|.
name|aggregate
argument_list|(
name|docID
argument_list|,
name|iterator
operator|.
name|getScore
argument_list|()
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Init arrays for partition by total counts, optionally applying a factor */
DECL|method|initArraysByTotalCounts
specifier|private
specifier|final
name|void
name|initArraysByTotalCounts
parameter_list|(
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|partition
parameter_list|,
name|int
name|nAccumulatedDocs
parameter_list|)
block|{
name|int
index|[]
name|intArray
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|totalFacetCounts
operator|.
name|fillTotalCountsForPartition
argument_list|(
name|intArray
argument_list|,
name|partition
argument_list|)
expr_stmt|;
name|double
name|totalCountsFactor
init|=
name|getTotalCountsFactor
argument_list|()
decl_stmt|;
comment|// fix total counts, but only if the effect of this would be meaningful.
if|if
condition|(
name|totalCountsFactor
operator|<
literal|0.99999
condition|)
block|{
name|int
name|delta
init|=
name|nAccumulatedDocs
operator|+
literal|1
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
name|intArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|intArray
index|[
name|i
index|]
operator|*=
name|totalCountsFactor
expr_stmt|;
comment|// also translate to prevent loss of non-positive values
comment|// due to complement sampling (ie if sampled docs all decremented a certain category).
name|intArray
index|[
name|i
index|]
operator|+=
name|delta
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Expert: factor by which counts should be multiplied when initializing    * the count arrays from total counts.    * Default implementation for this returns 1, which is a no op.      * @return a factor by which total counts should be multiplied    */
DECL|method|getTotalCountsFactor
specifier|protected
name|double
name|getTotalCountsFactor
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**    * Create an {@link Aggregator} and a {@link CategoryListIterator} for each    * and every {@link FacetRequest}. Generating a map, matching each    * categoryListIterator to its matching aggregator.    *<p>    * If two CategoryListIterators are served by the same aggregator, a single    * aggregator is returned for both.    *     *<b>NOTE:</b>If a given category list iterator is needed with two different    * aggregators (e.g counting and association) - an exception is thrown as this    * functionality is not supported at this time.    */
DECL|method|getCategoryListMap
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
name|HashMap
argument_list|<
name|CategoryListIterator
argument_list|,
name|Aggregator
argument_list|>
name|categoryLists
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
name|FacetIndexingParams
name|indexingParams
init|=
name|searchParams
operator|.
name|indexingParams
decl_stmt|;
for|for
control|(
name|FacetRequest
name|facetRequest
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|Aggregator
name|categoryAggregator
init|=
name|facetRequest
operator|.
name|createAggregator
argument_list|(
name|isUsingComplements
argument_list|,
name|facetArrays
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
name|CategoryListIterator
name|cli
init|=
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|facetRequest
operator|.
name|categoryPath
argument_list|)
operator|.
name|createCategoryListIterator
argument_list|(
name|partition
argument_list|)
decl_stmt|;
comment|// get the aggregator
name|Aggregator
name|old
init|=
name|categoryLists
operator|.
name|put
argument_list|(
name|cli
argument_list|,
name|categoryAggregator
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
operator|!
name|old
operator|.
name|equals
argument_list|(
name|categoryAggregator
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Overriding existing category list with different aggregator"
argument_list|)
throw|;
block|}
comment|// if the aggregator is the same we're covered
block|}
return|return
name|categoryLists
return|;
block|}
annotation|@
name|Override
DECL|method|accumulate
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|accumulate
argument_list|(
operator|new
name|MatchingDocsAsScoredDocIDs
argument_list|(
name|matchingDocs
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the complement threshold.    * @see #setComplementThreshold(double)    */
DECL|method|getComplementThreshold
specifier|public
name|double
name|getComplementThreshold
parameter_list|()
block|{
return|return
name|complementThreshold
return|;
block|}
comment|/**    * Set the complement threshold.    * This threshold will dictate whether the complements optimization is applied.    * The optimization is to count for less documents. It is useful when the same     * FacetSearchParams are used for varying sets of documents. The first time     * complements is used the "total counts" are computed - counting for all the     * documents in the collection. Then, only the complementing set of documents    * is considered, and used to decrement from the overall counts, thereby     * walking through less documents, which is faster.    *<p>    * For the default settings see {@link #DEFAULT_COMPLEMENT_THRESHOLD}.    *<p>    * To forcing complements in all cases pass {@link #FORCE_COMPLEMENT}.    * This is mostly useful for testing purposes, as forcing complements when only     * tiny fraction of available documents match the query does not make sense and     * would incur performance degradations.    *<p>    * To disable complements pass {@link #DISABLE_COMPLEMENT}.    * @param complementThreshold the complement threshold to set    * @see #getComplementThreshold()    */
DECL|method|setComplementThreshold
specifier|public
name|void
name|setComplementThreshold
parameter_list|(
name|double
name|complementThreshold
parameter_list|)
block|{
name|this
operator|.
name|complementThreshold
operator|=
name|complementThreshold
expr_stmt|;
block|}
comment|/** Returns true if complements are enabled. */
DECL|method|isUsingComplements
specifier|public
name|boolean
name|isUsingComplements
parameter_list|()
block|{
return|return
name|isUsingComplements
return|;
block|}
block|}
end_class
end_unit

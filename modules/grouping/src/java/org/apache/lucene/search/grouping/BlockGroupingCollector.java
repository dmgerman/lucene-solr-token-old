begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|IndexWriter
import|;
end_import
begin_comment
comment|// javadocs
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
name|Collector
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
name|DocIdSetIterator
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
name|FieldComparator
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
name|Filter
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
name|Scorer
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
name|Sort
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
name|SortField
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
name|TopDocs
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
name|TopDocsCollector
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
name|TopFieldCollector
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
name|TopScoreDocCollector
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
name|Weight
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
name|ArrayUtil
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/** BlockGroupingCollector performs grouping with a  *  single pass collector, as long as you are grouping by a  *  doc block field, ie all documents sharing a given group  *  value were indexed as a doc block using the atomic  *  {@link IndexWriter#addDocuments} or {@link  *  IndexWriter#updateDocuments} API.  *  *<p>This results in faster performance (~25% faster QPS)  *  than the two-pass grouping collectors, with the tradeoff  *  being that the documents in each group must always be  *  indexed as a block.  This collector also fills in  *  TopGroups.totalGroupCount without requiring the separate  *  {@link org.apache.lucene.search.grouping.term.TermAllGroupsCollector}.  However, this collector does  *  not fill in the groupValue of each group; this field  *  will always be null.  *  *<p><b>NOTE</b>: this collector makes no effort to verify  *  the docs were in fact indexed as a block, so it's up to  *  you to ensure this was the case.  *  *<p>See {@link org.apache.lucene.search.grouping} for more  *  details including a full code example.</p>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BlockGroupingCollector
specifier|public
class|class
name|BlockGroupingCollector
extends|extends
name|Collector
block|{
DECL|field|pendingSubDocs
specifier|private
name|int
index|[]
name|pendingSubDocs
decl_stmt|;
DECL|field|pendingSubScores
specifier|private
name|float
index|[]
name|pendingSubScores
decl_stmt|;
DECL|field|subDocUpto
specifier|private
name|int
name|subDocUpto
decl_stmt|;
DECL|field|groupSort
specifier|private
specifier|final
name|Sort
name|groupSort
decl_stmt|;
DECL|field|topNGroups
specifier|private
specifier|final
name|int
name|topNGroups
decl_stmt|;
DECL|field|lastDocPerGroup
specifier|private
specifier|final
name|Filter
name|lastDocPerGroup
decl_stmt|;
comment|// TODO: specialize into 2 classes, static "create" method:
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|comparators
specifier|private
specifier|final
name|FieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reversed
specifier|private
specifier|final
name|int
index|[]
name|reversed
decl_stmt|;
DECL|field|compIDXEnd
specifier|private
specifier|final
name|int
name|compIDXEnd
decl_stmt|;
DECL|field|bottomSlot
specifier|private
name|int
name|bottomSlot
decl_stmt|;
DECL|field|queueFull
specifier|private
name|boolean
name|queueFull
decl_stmt|;
DECL|field|currentReaderContext
specifier|private
name|AtomicReaderContext
name|currentReaderContext
decl_stmt|;
DECL|field|topGroupDoc
specifier|private
name|int
name|topGroupDoc
decl_stmt|;
DECL|field|totalHitCount
specifier|private
name|int
name|totalHitCount
decl_stmt|;
DECL|field|totalGroupCount
specifier|private
name|int
name|totalGroupCount
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|groupEndDocID
specifier|private
name|int
name|groupEndDocID
decl_stmt|;
DECL|field|lastDocPerGroupBits
specifier|private
name|DocIdSetIterator
name|lastDocPerGroupBits
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|groupQueue
specifier|private
specifier|final
name|GroupQueue
name|groupQueue
decl_stmt|;
DECL|field|groupCompetes
specifier|private
name|boolean
name|groupCompetes
decl_stmt|;
DECL|class|FakeScorer
specifier|private
specifier|final
specifier|static
class|class
name|FakeScorer
extends|extends
name|Scorer
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|method|FakeScorer
specifier|public
name|FakeScorer
parameter_list|()
block|{
name|super
argument_list|(
operator|(
name|Weight
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|OneGroup
specifier|private
specifier|static
specifier|final
class|class
name|OneGroup
block|{
DECL|field|readerContext
name|AtomicReaderContext
name|readerContext
decl_stmt|;
comment|//int groupOrd;
DECL|field|topGroupDoc
name|int
name|topGroupDoc
decl_stmt|;
DECL|field|docs
name|int
index|[]
name|docs
decl_stmt|;
DECL|field|scores
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|comparatorSlot
name|int
name|comparatorSlot
decl_stmt|;
block|}
comment|// Sorts by groupSort.  Not static -- uses comparators, reversed
DECL|class|GroupQueue
specifier|private
specifier|final
class|class
name|GroupQueue
extends|extends
name|PriorityQueue
argument_list|<
name|OneGroup
argument_list|>
block|{
DECL|method|GroupQueue
specifier|public
name|GroupQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|OneGroup
name|group1
parameter_list|,
specifier|final
name|OneGroup
name|group2
parameter_list|)
block|{
comment|//System.out.println("    ltcheck");
assert|assert
name|group1
operator|!=
name|group2
assert|;
assert|assert
name|group1
operator|.
name|comparatorSlot
operator|!=
name|group2
operator|.
name|comparatorSlot
assert|;
specifier|final
name|int
name|numComparators
init|=
name|comparators
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|numComparators
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compare
argument_list|(
name|group1
operator|.
name|comparatorSlot
argument_list|,
name|group2
operator|.
name|comparatorSlot
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
comment|// Short circuit
return|return
name|c
operator|>
literal|0
return|;
block|}
block|}
comment|// Break ties by docID; lower docID is always sorted first
return|return
name|group1
operator|.
name|topGroupDoc
operator|>
name|group2
operator|.
name|topGroupDoc
return|;
block|}
block|}
comment|// Called when we transition to another group; if the
comment|// group is competitive we insert into the group queue
DECL|method|processGroup
specifier|private
name|void
name|processGroup
parameter_list|()
block|{
name|totalGroupCount
operator|++
expr_stmt|;
comment|//System.out.println("    processGroup ord=" + lastGroupOrd + " competes=" + groupCompetes + " count=" + subDocUpto + " groupDoc=" + topGroupDoc);
if|if
condition|(
name|groupCompetes
condition|)
block|{
if|if
condition|(
operator|!
name|queueFull
condition|)
block|{
comment|// Startup transient: always add a new OneGroup
specifier|final
name|OneGroup
name|og
init|=
operator|new
name|OneGroup
argument_list|()
decl_stmt|;
name|og
operator|.
name|count
operator|=
name|subDocUpto
expr_stmt|;
name|og
operator|.
name|topGroupDoc
operator|=
name|docBase
operator|+
name|topGroupDoc
expr_stmt|;
name|og
operator|.
name|docs
operator|=
name|pendingSubDocs
expr_stmt|;
name|pendingSubDocs
operator|=
operator|new
name|int
index|[
literal|10
index|]
expr_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
name|og
operator|.
name|scores
operator|=
name|pendingSubScores
expr_stmt|;
name|pendingSubScores
operator|=
operator|new
name|float
index|[
literal|10
index|]
expr_stmt|;
block|}
name|og
operator|.
name|readerContext
operator|=
name|currentReaderContext
expr_stmt|;
comment|//og.groupOrd = lastGroupOrd;
name|og
operator|.
name|comparatorSlot
operator|=
name|bottomSlot
expr_stmt|;
specifier|final
name|OneGroup
name|bottomGroup
init|=
name|groupQueue
operator|.
name|add
argument_list|(
name|og
argument_list|)
decl_stmt|;
comment|//System.out.println("      ADD group=" + getGroupString(lastGroupOrd) + " newBottom=" + getGroupString(bottomGroup.groupOrd));
name|queueFull
operator|=
name|groupQueue
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
expr_stmt|;
if|if
condition|(
name|queueFull
condition|)
block|{
comment|// Queue just became full; now set the real bottom
comment|// in the comparators:
name|bottomSlot
operator|=
name|bottomGroup
operator|.
name|comparatorSlot
expr_stmt|;
comment|//System.out.println("    set bottom=" + bottomSlot);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("     QUEUE FULL");
block|}
else|else
block|{
comment|// Queue not full yet -- just advance bottomSlot:
name|bottomSlot
operator|=
name|groupQueue
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Replace bottom element in PQ and then updateTop
specifier|final
name|OneGroup
name|og
init|=
name|groupQueue
operator|.
name|top
argument_list|()
decl_stmt|;
assert|assert
name|og
operator|!=
literal|null
assert|;
name|og
operator|.
name|count
operator|=
name|subDocUpto
expr_stmt|;
name|og
operator|.
name|topGroupDoc
operator|=
name|docBase
operator|+
name|topGroupDoc
expr_stmt|;
comment|// Swap pending docs
specifier|final
name|int
index|[]
name|savDocs
init|=
name|og
operator|.
name|docs
decl_stmt|;
name|og
operator|.
name|docs
operator|=
name|pendingSubDocs
expr_stmt|;
name|pendingSubDocs
operator|=
name|savDocs
expr_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
comment|// Swap pending scores
specifier|final
name|float
index|[]
name|savScores
init|=
name|og
operator|.
name|scores
decl_stmt|;
name|og
operator|.
name|scores
operator|=
name|pendingSubScores
expr_stmt|;
name|pendingSubScores
operator|=
name|savScores
expr_stmt|;
block|}
name|og
operator|.
name|readerContext
operator|=
name|currentReaderContext
expr_stmt|;
comment|//og.groupOrd = lastGroupOrd;
name|bottomSlot
operator|=
name|groupQueue
operator|.
name|updateTop
argument_list|()
operator|.
name|comparatorSlot
expr_stmt|;
comment|//System.out.println("    set bottom=" + bottomSlot);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|subDocUpto
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Create the single pass collector.    *    *  @param groupSort The {@link Sort} used to sort the    *    groups.  The top sorted document within each group    *    according to groupSort, determines how that group    *    sorts against other groups.  This must be non-null,    *    ie, if you want to groupSort by relevance use    *    Sort.RELEVANCE.    *  @param topNGroups How many top groups to keep.    *  @param needsScores true if the collected documents    *    require scores, either because relevance is included    *    in the withinGroupSort or because you plan to pass true    *    for either getSscores or getMaxScores to {@link    *    #getTopGroups}    *  @param lastDocPerGroup a {@link Filter} that marks the    *    last document in each group.    */
DECL|method|BlockGroupingCollector
specifier|public
name|BlockGroupingCollector
parameter_list|(
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|Filter
name|lastDocPerGroup
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topNGroups
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"topNGroups must be>= 1 (got "
operator|+
name|topNGroups
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|groupQueue
operator|=
operator|new
name|GroupQueue
argument_list|(
name|topNGroups
argument_list|)
expr_stmt|;
name|pendingSubDocs
operator|=
operator|new
name|int
index|[
literal|10
index|]
expr_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
name|pendingSubScores
operator|=
operator|new
name|float
index|[
literal|10
index|]
expr_stmt|;
block|}
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|this
operator|.
name|lastDocPerGroup
operator|=
name|lastDocPerGroup
expr_stmt|;
comment|// TODO: allow null groupSort to mean "by relevance",
comment|// and specialize it?
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
name|this
operator|.
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
name|compIDXEnd
operator|=
name|comparators
operator|.
name|length
operator|-
literal|1
expr_stmt|;
name|reversed
operator|=
operator|new
name|int
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|i
index|]
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
name|topNGroups
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|reversed
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
block|}
block|}
comment|// TODO: maybe allow no sort on retrieving groups?  app
comment|// may want to simply process docs in the group itself?
comment|// typically they will be presented as a "single" result
comment|// in the UI?
comment|/** Returns the grouped results.  Returns null if the    *  number of groups collected is<= groupOffset.    *    *<p><b>NOTE</b>: This collector is unable to compute    *  the groupValue per group so it will always be null.    *  This is normally not a problem, as you can obtain the    *  value just like you obtain other values for each    *  matching document (eg, via stored fields, via    *  FieldCache, etc.)    *    *  @param withinGroupSort The {@link Sort} used to sort    *    documents within each group.  Passing null is    *    allowed, to sort by relevance.    *  @param groupOffset Which group to start from    *  @param withinGroupOffset Which document to start from    *    within each group    *  @param maxDocsPerGroup How many top documents to keep    *     within each group.    *  @param fillSortFields If true then the Comparable    *     values for the sort fields will be set    */
DECL|method|getTopGroups
specifier|public
name|TopGroups
name|getTopGroups
parameter_list|(
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|groupOffset
parameter_list|,
name|int
name|withinGroupOffset
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (queueFull) {
comment|//System.out.println("getTopGroups groupOffset=" + groupOffset + " topNGroups=" + topNGroups);
comment|//}
if|if
condition|(
name|subDocUpto
operator|!=
literal|0
condition|)
block|{
name|processGroup
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|groupOffset
operator|>=
name|groupQueue
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|totalGroupedHitCount
init|=
literal|0
decl_stmt|;
specifier|final
name|FakeScorer
name|fakeScorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|GroupDocs
argument_list|<
name|Object
argument_list|>
index|[]
name|groups
init|=
operator|new
name|GroupDocs
index|[
name|groupQueue
operator|.
name|size
argument_list|()
operator|-
name|groupOffset
index|]
decl_stmt|;
for|for
control|(
name|int
name|downTo
init|=
name|groupQueue
operator|.
name|size
argument_list|()
operator|-
name|groupOffset
operator|-
literal|1
init|;
name|downTo
operator|>=
literal|0
condition|;
name|downTo
operator|--
control|)
block|{
specifier|final
name|OneGroup
name|og
init|=
name|groupQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// At this point we hold all docs w/ in each group,
comment|// unsorted; we now sort them:
specifier|final
name|TopDocsCollector
name|collector
decl_stmt|;
if|if
condition|(
name|withinGroupSort
operator|==
literal|null
condition|)
block|{
comment|// Sort by score
if|if
condition|(
operator|!
name|needsScores
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot sort by relevance within group: needsScores=false"
argument_list|)
throw|;
block|}
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|maxDocsPerGroup
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Sort by fields
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|fillSortFields
argument_list|,
name|needsScores
argument_list|,
name|needsScores
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|setScorer
argument_list|(
name|fakeScorer
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setNextReader
argument_list|(
name|og
operator|.
name|readerContext
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docIDX
init|=
literal|0
init|;
name|docIDX
operator|<
name|og
operator|.
name|count
condition|;
name|docIDX
operator|++
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|og
operator|.
name|docs
index|[
name|docIDX
index|]
decl_stmt|;
name|fakeScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
name|fakeScorer
operator|.
name|score
operator|=
name|og
operator|.
name|scores
index|[
name|docIDX
index|]
expr_stmt|;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|totalGroupedHitCount
operator|+=
name|og
operator|.
name|count
expr_stmt|;
specifier|final
name|Object
index|[]
name|groupSortValues
decl_stmt|;
if|if
condition|(
name|fillSortFields
condition|)
block|{
name|groupSortValues
operator|=
operator|new
name|Comparable
index|[
name|comparators
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|sortFieldIDX
init|=
literal|0
init|;
name|sortFieldIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|sortFieldIDX
operator|++
control|)
block|{
name|groupSortValues
index|[
name|sortFieldIDX
index|]
operator|=
name|comparators
index|[
name|sortFieldIDX
index|]
operator|.
name|value
argument_list|(
name|og
operator|.
name|comparatorSlot
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|groupSortValues
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|TopDocs
name|topDocs
init|=
name|collector
operator|.
name|topDocs
argument_list|(
name|withinGroupOffset
argument_list|,
name|maxDocsPerGroup
argument_list|)
decl_stmt|;
name|groups
index|[
name|downTo
index|]
operator|=
operator|new
name|GroupDocs
argument_list|<
name|Object
argument_list|>
argument_list|(
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|og
operator|.
name|count
argument_list|,
name|topDocs
operator|.
name|scoreDocs
argument_list|,
literal|null
argument_list|,
name|groupSortValues
argument_list|)
expr_stmt|;
block|}
comment|/*     while (groupQueue.size() != 0) {       final OneGroup og = groupQueue.pop();       //System.out.println("  leftover: og ord=" + og.groupOrd + " count=" + og.count);       totalGroupedHitCount += og.count;     }     */
return|return
operator|new
name|TopGroups
argument_list|<
name|Object
argument_list|>
argument_list|(
operator|new
name|TopGroups
argument_list|<
name|Object
argument_list|>
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|withinGroupSort
operator|==
literal|null
condition|?
literal|null
else|:
name|withinGroupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|groups
argument_list|)
argument_list|,
name|totalGroupCount
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|FieldComparator
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// System.out.println("C " + doc);
if|if
condition|(
name|doc
operator|>
name|groupEndDocID
condition|)
block|{
comment|// Group changed
if|if
condition|(
name|subDocUpto
operator|!=
literal|0
condition|)
block|{
name|processGroup
argument_list|()
expr_stmt|;
block|}
name|groupEndDocID
operator|=
name|lastDocPerGroupBits
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//System.out.println("  adv " + groupEndDocID + " " + lastDocPerGroupBits);
name|subDocUpto
operator|=
literal|0
expr_stmt|;
name|groupCompetes
operator|=
operator|!
name|queueFull
expr_stmt|;
block|}
name|totalHitCount
operator|++
expr_stmt|;
comment|// Always cache doc/score within this group:
if|if
condition|(
name|subDocUpto
operator|==
name|pendingSubDocs
operator|.
name|length
condition|)
block|{
name|pendingSubDocs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingSubDocs
argument_list|)
expr_stmt|;
block|}
name|pendingSubDocs
index|[
name|subDocUpto
index|]
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
if|if
condition|(
name|subDocUpto
operator|==
name|pendingSubScores
operator|.
name|length
condition|)
block|{
name|pendingSubScores
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingSubScores
argument_list|)
expr_stmt|;
block|}
name|pendingSubScores
index|[
name|subDocUpto
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
name|subDocUpto
operator|++
expr_stmt|;
if|if
condition|(
name|groupCompetes
condition|)
block|{
if|if
condition|(
name|subDocUpto
operator|==
literal|1
condition|)
block|{
assert|assert
operator|!
name|queueFull
assert|;
comment|//System.out.println("    init copy to bottomSlot=" + bottomSlot);
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
name|bottomSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
name|topGroupDoc
operator|=
name|doc
expr_stmt|;
block|}
else|else
block|{
comment|// Compare to bottomSlot
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive -- done
return|return;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
comment|// Ties with bottom, except we know this docID is
comment|//> docID in the queue (docs are visited in
comment|// order), so not competitive:
return|return;
block|}
block|}
comment|//System.out.println("       best w/in group!");
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
name|bottomSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
comment|// Necessary because some comparators cache
comment|// details of bottom slot; this forces them to
comment|// re-cache:
name|fc
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
name|topGroupDoc
operator|=
name|doc
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// We're not sure this group will make it into the
comment|// queue yet
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive -- done
comment|//System.out.println("    doc doesn't compete w/ top groups");
return|return;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
comment|// Ties with bottom, except we know this docID is
comment|//> docID in the queue (docs are visited in
comment|// order), so not competitive:
comment|//System.out.println("    doc doesn't compete w/ top groups");
return|return;
block|}
block|}
name|groupCompetes
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
name|bottomSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
comment|// Necessary because some comparators cache
comment|// details of bottom slot; this forces them to
comment|// re-cache:
name|fc
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
name|topGroupDoc
operator|=
name|doc
expr_stmt|;
comment|//System.out.println("        doc competes w/ top groups");
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|subDocUpto
operator|!=
literal|0
condition|)
block|{
name|processGroup
argument_list|()
expr_stmt|;
block|}
name|subDocUpto
operator|=
literal|0
expr_stmt|;
name|docBase
operator|=
name|readerContext
operator|.
name|docBase
expr_stmt|;
comment|//System.out.println("setNextReader base=" + docBase + " r=" + readerContext.reader);
name|lastDocPerGroupBits
operator|=
name|lastDocPerGroup
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|groupEndDocID
operator|=
operator|-
literal|1
expr_stmt|;
name|currentReaderContext
operator|=
name|readerContext
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

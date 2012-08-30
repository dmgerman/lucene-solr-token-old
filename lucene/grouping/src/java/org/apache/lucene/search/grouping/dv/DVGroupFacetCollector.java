begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping.dv
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
operator|.
name|dv
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
name|index
operator|.
name|AtomicReader
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
name|DocValues
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
name|DocValues
operator|.
name|Type
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
name|grouping
operator|.
name|AbstractGroupFacetCollector
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
name|BytesRef
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
name|SentinelIntSet
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
name|UnicodeUtil
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
name|Locale
import|;
end_import
begin_comment
comment|/**  * An implementation of {@link AbstractGroupFacetCollector} that computes grouped facets based on docvalues.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DVGroupFacetCollector
specifier|public
specifier|abstract
class|class
name|DVGroupFacetCollector
extends|extends
name|AbstractGroupFacetCollector
block|{
DECL|field|groupDvType
specifier|final
name|Type
name|groupDvType
decl_stmt|;
DECL|field|groupDiskResident
specifier|final
name|boolean
name|groupDiskResident
decl_stmt|;
DECL|field|facetFieldDvType
specifier|final
name|Type
name|facetFieldDvType
decl_stmt|;
DECL|field|facetDiskResident
specifier|final
name|boolean
name|facetDiskResident
decl_stmt|;
DECL|field|groupedFacetHits
specifier|final
name|List
argument_list|<
name|GroupedFacetHit
argument_list|>
name|groupedFacetHits
decl_stmt|;
DECL|field|segmentGroupedFacetHits
specifier|final
name|SentinelIntSet
name|segmentGroupedFacetHits
decl_stmt|;
comment|/**    * Factory method for creating the right implementation based on the group docvalues type and the facet docvalues    * type.    *    * Currently only the {@link Type#BYTES_VAR_SORTED} and the {@link Type#BYTES_FIXED_SORTED} are    * the only docvalues type supported for both the group and facet field.    *    * @param groupField        The group field    * @param groupDvType       The docvalues type for the group field    * @param groupDiskResident Whether the group docvalues should be disk resident    * @param facetField        The facet field    * @param facetDvType       The docvalues type for the facet field    * @param facetDiskResident Whether the facet docvalues should be disk resident    * @param facetPrefix       The facet prefix a facet entry should start with to be included.    * @param initialSize       The initial allocation size of the internal int set and group facet list which should roughly    *                          match the total number of expected unique groups. Be aware that the heap usage is    *                          4 bytes * initialSize.    * @return a<code>DVGroupFacetCollector</code> implementation    */
DECL|method|createDvGroupFacetCollector
specifier|public
specifier|static
name|DVGroupFacetCollector
name|createDvGroupFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Type
name|groupDvType
parameter_list|,
name|boolean
name|groupDiskResident
parameter_list|,
name|String
name|facetField
parameter_list|,
name|Type
name|facetDvType
parameter_list|,
name|boolean
name|facetDiskResident
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
switch|switch
condition|(
name|groupDvType
condition|)
block|{
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Group valueType %s not supported"
argument_list|,
name|groupDvType
argument_list|)
argument_list|)
throw|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|GroupSortedBR
operator|.
name|createGroupSortedFacetCollector
argument_list|(
name|groupField
argument_list|,
name|groupDvType
argument_list|,
name|groupDiskResident
argument_list|,
name|facetField
argument_list|,
name|facetDvType
argument_list|,
name|facetDiskResident
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Group valueType %s not supported"
argument_list|,
name|groupDvType
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|DVGroupFacetCollector
name|DVGroupFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Type
name|groupDvType
parameter_list|,
name|boolean
name|groupDiskResident
parameter_list|,
name|String
name|facetField
parameter_list|,
name|Type
name|facetFieldDvType
parameter_list|,
name|boolean
name|facetDiskResident
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupDvType
operator|=
name|groupDvType
expr_stmt|;
name|this
operator|.
name|groupDiskResident
operator|=
name|groupDiskResident
expr_stmt|;
name|this
operator|.
name|facetFieldDvType
operator|=
name|facetFieldDvType
expr_stmt|;
name|this
operator|.
name|facetDiskResident
operator|=
name|facetDiskResident
expr_stmt|;
name|groupedFacetHits
operator|=
operator|new
name|ArrayList
argument_list|<
name|GroupedFacetHit
argument_list|>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
name|segmentGroupedFacetHits
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|initialSize
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|class|GroupSortedBR
specifier|static
specifier|abstract
class|class
name|GroupSortedBR
extends|extends
name|DVGroupFacetCollector
block|{
DECL|field|facetSpare
specifier|final
name|BytesRef
name|facetSpare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|groupSpare
specifier|final
name|BytesRef
name|groupSpare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|groupFieldSource
name|DocValues
operator|.
name|SortedSource
name|groupFieldSource
decl_stmt|;
DECL|method|GroupSortedBR
name|GroupSortedBR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Type
name|groupDvType
parameter_list|,
name|boolean
name|groupDiskResident
parameter_list|,
name|String
name|facetField
parameter_list|,
name|Type
name|facetFieldDvType
parameter_list|,
name|boolean
name|facetDiskResident
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|groupDvType
argument_list|,
name|groupDiskResident
argument_list|,
name|facetField
argument_list|,
name|facetFieldDvType
argument_list|,
name|facetDiskResident
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
DECL|method|createGroupSortedFacetCollector
specifier|static
name|DVGroupFacetCollector
name|createGroupSortedFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Type
name|groupDvType
parameter_list|,
name|boolean
name|groupDiskResident
parameter_list|,
name|String
name|facetField
parameter_list|,
name|Type
name|facetDvType
parameter_list|,
name|boolean
name|facetDiskResident
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
switch|switch
condition|(
name|facetDvType
condition|)
block|{
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Facet valueType %s not supported"
argument_list|,
name|facetDvType
argument_list|)
argument_list|)
throw|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
operator|new
name|FacetSortedBR
argument_list|(
name|groupField
argument_list|,
name|groupDvType
argument_list|,
name|groupDiskResident
argument_list|,
name|facetField
argument_list|,
name|facetDvType
argument_list|,
name|facetDiskResident
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Facet valueType %s not supported"
argument_list|,
name|facetDvType
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|FacetSortedBR
specifier|static
class|class
name|FacetSortedBR
extends|extends
name|GroupSortedBR
block|{
DECL|field|facetFieldSource
specifier|private
name|DocValues
operator|.
name|SortedSource
name|facetFieldSource
decl_stmt|;
DECL|method|FacetSortedBR
name|FacetSortedBR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Type
name|groupDvType
parameter_list|,
name|boolean
name|groupDiskResident
parameter_list|,
name|String
name|facetField
parameter_list|,
name|Type
name|facetDvType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|groupDvType
argument_list|,
name|groupDiskResident
argument_list|,
name|facetField
argument_list|,
name|facetDvType
argument_list|,
name|diskResident
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
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
name|int
name|facetOrd
init|=
name|facetFieldSource
operator|.
name|ord
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetOrd
operator|<
name|startFacetOrd
operator|||
name|facetOrd
operator|>=
name|endFacetOrd
condition|)
block|{
return|return;
block|}
name|int
name|groupOrd
init|=
name|groupFieldSource
operator|.
name|ord
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|int
name|segmentGroupedFacetsIndex
init|=
operator|(
name|groupOrd
operator|*
name|facetFieldSource
operator|.
name|getValueCount
argument_list|()
operator|)
operator|+
name|facetOrd
decl_stmt|;
if|if
condition|(
name|segmentGroupedFacetHits
operator|.
name|exists
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
condition|)
block|{
return|return;
block|}
name|segmentTotalCount
operator|++
expr_stmt|;
name|segmentFacetCounts
index|[
name|facetOrd
index|]
operator|++
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
name|groupedFacetHits
operator|.
name|add
argument_list|(
operator|new
name|GroupedFacetHit
argument_list|(
name|groupFieldSource
operator|.
name|getByOrd
argument_list|(
name|groupOrd
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|,
name|facetFieldSource
operator|.
name|getByOrd
argument_list|(
name|facetOrd
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentFacetCounts
operator|!=
literal|null
condition|)
block|{
name|segmentResults
operator|.
name|add
argument_list|(
name|createSegmentResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|groupFieldSource
operator|=
name|getDocValuesSortedSource
argument_list|(
name|groupField
argument_list|,
name|groupDvType
argument_list|,
name|groupDiskResident
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|facetFieldSource
operator|=
name|getDocValuesSortedSource
argument_list|(
name|facetField
argument_list|,
name|facetFieldDvType
argument_list|,
name|facetDiskResident
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|segmentFacetCounts
operator|=
operator|new
name|int
index|[
name|facetFieldSource
operator|.
name|getValueCount
argument_list|()
index|]
expr_stmt|;
name|segmentTotalCount
operator|=
literal|0
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|GroupedFacetHit
name|groupedFacetHit
range|:
name|groupedFacetHits
control|)
block|{
name|int
name|facetOrd
init|=
name|facetFieldSource
operator|.
name|getOrdByValue
argument_list|(
name|groupedFacetHit
operator|.
name|facetValue
argument_list|,
name|facetSpare
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|groupOrd
init|=
name|groupFieldSource
operator|.
name|getOrdByValue
argument_list|(
name|groupedFacetHit
operator|.
name|groupValue
argument_list|,
name|groupSpare
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|segmentGroupedFacetsIndex
init|=
operator|(
name|groupOrd
operator|*
name|facetFieldSource
operator|.
name|getValueCount
argument_list|()
operator|)
operator|+
name|facetOrd
decl_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facetPrefix
operator|!=
literal|null
condition|)
block|{
name|startFacetOrd
operator|=
name|facetFieldSource
operator|.
name|getOrdByValue
argument_list|(
name|facetPrefix
argument_list|,
name|facetSpare
argument_list|)
expr_stmt|;
if|if
condition|(
name|startFacetOrd
operator|<
literal|0
condition|)
block|{
comment|// Points to the ord one higher than facetPrefix
name|startFacetOrd
operator|=
operator|-
name|startFacetOrd
operator|-
literal|1
expr_stmt|;
block|}
name|BytesRef
name|facetEndPrefix
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|facetPrefix
argument_list|)
decl_stmt|;
name|facetEndPrefix
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
name|endFacetOrd
operator|=
name|facetFieldSource
operator|.
name|getOrdByValue
argument_list|(
name|facetEndPrefix
argument_list|,
name|facetSpare
argument_list|)
expr_stmt|;
name|endFacetOrd
operator|=
operator|-
name|endFacetOrd
operator|-
literal|1
expr_stmt|;
comment|// Points to the ord one higher than facetEndPrefix
block|}
else|else
block|{
name|startFacetOrd
operator|=
literal|0
expr_stmt|;
name|endFacetOrd
operator|=
name|facetFieldSource
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createSegmentResult
specifier|protected
name|SegmentResult
name|createSegmentResult
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|startFacetOrd
operator|==
literal|0
operator|&&
name|facetFieldSource
operator|.
name|getByOrd
argument_list|(
name|startFacetOrd
argument_list|,
name|facetSpare
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|int
name|missing
init|=
name|segmentFacetCounts
index|[
literal|0
index|]
decl_stmt|;
name|int
name|total
init|=
name|segmentTotalCount
operator|-
name|segmentFacetCounts
index|[
literal|0
index|]
decl_stmt|;
return|return
operator|new
name|SegmentResult
argument_list|(
name|segmentFacetCounts
argument_list|,
name|total
argument_list|,
name|missing
argument_list|,
name|facetFieldSource
argument_list|,
name|endFacetOrd
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SegmentResult
argument_list|(
name|segmentFacetCounts
argument_list|,
name|segmentTotalCount
argument_list|,
name|facetFieldSource
argument_list|,
name|startFacetOrd
argument_list|,
name|endFacetOrd
argument_list|)
return|;
block|}
block|}
DECL|method|getDocValuesSortedSource
specifier|private
name|DocValues
operator|.
name|SortedSource
name|getDocValuesSortedSource
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|dvType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValues
name|dv
init|=
name|reader
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|DocValues
operator|.
name|Source
name|dvSource
decl_stmt|;
if|if
condition|(
name|dv
operator|!=
literal|null
condition|)
block|{
name|dvSource
operator|=
name|diskResident
condition|?
name|dv
operator|.
name|getDirectSource
argument_list|()
else|:
name|dv
operator|.
name|getSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dvSource
operator|=
name|DocValues
operator|.
name|getDefaultSortedSource
argument_list|(
name|dvType
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|dvSource
operator|.
name|asSortedSource
argument_list|()
return|;
block|}
DECL|class|SegmentResult
specifier|private
specifier|static
class|class
name|SegmentResult
extends|extends
name|AbstractGroupFacetCollector
operator|.
name|SegmentResult
block|{
DECL|field|facetFieldSource
specifier|final
name|DocValues
operator|.
name|SortedSource
name|facetFieldSource
decl_stmt|;
DECL|field|spare
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|SegmentResult
name|SegmentResult
parameter_list|(
name|int
index|[]
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|missing
parameter_list|,
name|DocValues
operator|.
name|SortedSource
name|facetFieldSource
parameter_list|,
name|int
name|endFacetOrd
parameter_list|)
block|{
name|super
argument_list|(
name|counts
argument_list|,
name|total
argument_list|,
name|missing
argument_list|,
name|endFacetOrd
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetFieldSource
operator|=
name|facetFieldSource
expr_stmt|;
name|this
operator|.
name|mergePos
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|mergePos
operator|<
name|maxTermPos
condition|)
block|{
name|mergeTerm
operator|=
name|facetFieldSource
operator|.
name|getByOrd
argument_list|(
name|mergePos
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|SegmentResult
name|SegmentResult
parameter_list|(
name|int
index|[]
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|DocValues
operator|.
name|SortedSource
name|facetFieldSource
parameter_list|,
name|int
name|startFacetOrd
parameter_list|,
name|int
name|endFacetOrd
parameter_list|)
block|{
name|super
argument_list|(
name|counts
argument_list|,
name|total
argument_list|,
literal|0
argument_list|,
name|endFacetOrd
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetFieldSource
operator|=
name|facetFieldSource
expr_stmt|;
name|this
operator|.
name|mergePos
operator|=
name|startFacetOrd
expr_stmt|;
if|if
condition|(
name|mergePos
operator|<
name|maxTermPos
condition|)
block|{
name|mergeTerm
operator|=
name|facetFieldSource
operator|.
name|getByOrd
argument_list|(
name|mergePos
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextTerm
specifier|protected
name|void
name|nextTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeTerm
operator|=
name|facetFieldSource
operator|.
name|getByOrd
argument_list|(
name|mergePos
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
begin_class
DECL|class|GroupedFacetHit
class|class
name|GroupedFacetHit
block|{
DECL|field|groupValue
specifier|final
name|BytesRef
name|groupValue
decl_stmt|;
DECL|field|facetValue
specifier|final
name|BytesRef
name|facetValue
decl_stmt|;
DECL|method|GroupedFacetHit
name|GroupedFacetHit
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|BytesRef
name|facetValue
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|facetValue
operator|=
name|facetValue
expr_stmt|;
block|}
block|}
end_class
end_unit

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
name|grouping
operator|.
name|AbstractSecondPassGroupingCollector
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
name|SearchGroup
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
name|Collection
import|;
end_import
begin_comment
comment|/**  * IDV based implementation of {@link AbstractSecondPassGroupingCollector}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DVSecondPassGroupingCollector
specifier|public
specifier|abstract
class|class
name|DVSecondPassGroupingCollector
parameter_list|<
name|GROUP_VALUE
parameter_list|>
extends|extends
name|AbstractSecondPassGroupingCollector
argument_list|<
name|GROUP_VALUE
argument_list|>
block|{
comment|/**    * Constructs a {@link DVSecondPassGroupingCollector}.    * Selects and constructs the most optimal second pass collector implementation for grouping by {@link DocValues}.    *    * @param groupField      The field to group by    * @param diskResident    Whether the values to group by should be disk resident    * @param type            The {@link Type} which is used to select a concrete implementation.    * @param searchGroups    The groups from the first phase search    * @param groupSort       The sort used for the groups    * @param withinGroupSort The sort used for documents inside a group    * @param maxDocsPerGroup The maximum number of documents to collect per group    * @param getScores       Whether to include scores for the documents inside a group    * @param getMaxScores    Whether to keep track of the higest score per group    * @param fillSortFields  Whether to include the sort values    * @return the most optimal second pass collector implementation for grouping by {@link DocValues}    * @throws IOException    If I/O related errors occur    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|create
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|DVSecondPassGroupingCollector
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|String
name|groupField
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
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
comment|// Type erasure b/c otherwise we have inconvertible types...
return|return
operator|(
name|DVSecondPassGroupingCollector
operator|)
operator|new
name|Lng
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|diskResident
argument_list|,
operator|(
name|Collection
operator|)
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
comment|// Type erasure b/c otherwise we have inconvertible types...
return|return
operator|(
name|DVSecondPassGroupingCollector
operator|)
operator|new
name|Dbl
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|diskResident
argument_list|,
operator|(
name|Collection
operator|)
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
return|;
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
comment|// Type erasure b/c otherwise we have inconvertible types...
return|return
operator|(
name|DVSecondPassGroupingCollector
operator|)
operator|new
name|BR
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|diskResident
argument_list|,
operator|(
name|Collection
operator|)
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
comment|// Type erasure b/c otherwise we have inconvertible types...
return|return
operator|(
name|DVSecondPassGroupingCollector
operator|)
operator|new
name|SortedBR
argument_list|(
name|groupField
argument_list|,
name|type
argument_list|,
name|diskResident
argument_list|,
operator|(
name|Collection
operator|)
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
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
literal|"ValueType %s not supported"
argument_list|,
name|type
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|field|groupField
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|valueType
specifier|final
name|DocValues
operator|.
name|Type
name|valueType
decl_stmt|;
DECL|field|diskResident
specifier|final
name|boolean
name|diskResident
decl_stmt|;
DECL|method|DVSecondPassGroupingCollector
name|DVSecondPassGroupingCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|GROUP_VALUE
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
name|this
operator|.
name|diskResident
operator|=
name|diskResident
expr_stmt|;
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
name|super
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
specifier|final
name|DocValues
name|dv
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
specifier|final
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
name|getDefaultSource
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
name|setDocValuesSources
argument_list|(
name|dvSource
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the idv source for concrete implementations to use.    *    * @param source The idv source to be used by concrete implementations    * @param readerContext The current reader context    */
DECL|method|setDocValuesSources
specifier|protected
specifier|abstract
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
function_decl|;
comment|/**    * @return The default source when no doc values are available.    * @param readerContext The current reader context    */
DECL|method|getDefaultSource
specifier|protected
name|DocValues
operator|.
name|Source
name|getDefaultSource
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|getDefaultSource
argument_list|(
name|valueType
argument_list|)
return|;
block|}
DECL|class|Lng
specifier|static
class|class
name|Lng
extends|extends
name|DVSecondPassGroupingCollector
argument_list|<
name|Long
argument_list|>
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|method|Lng
name|Lng
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|Long
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|diskResident
argument_list|,
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
block|}
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|Long
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|groupMap
operator|.
name|get
argument_list|(
name|source
operator|.
name|getInt
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|Dbl
specifier|static
class|class
name|Dbl
extends|extends
name|DVSecondPassGroupingCollector
argument_list|<
name|Double
argument_list|>
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|method|Dbl
name|Dbl
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|Double
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|diskResident
argument_list|,
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
block|}
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|Double
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|groupMap
operator|.
name|get
argument_list|(
name|source
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|BR
specifier|static
class|class
name|BR
extends|extends
name|DVSecondPassGroupingCollector
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|Source
name|source
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|BR
name|BR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|diskResident
argument_list|,
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
block|}
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|groupMap
operator|.
name|get
argument_list|(
name|source
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|spare
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
DECL|class|SortedBR
specifier|static
class|class
name|SortedBR
extends|extends
name|DVSecondPassGroupingCollector
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|source
specifier|private
name|DocValues
operator|.
name|SortedSource
name|source
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|ordSet
specifier|private
specifier|final
name|SentinelIntSet
name|ordSet
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|SortedBR
name|SortedBR
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|searchGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|valueType
argument_list|,
name|diskResident
argument_list|,
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|groupMap
operator|.
name|size
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|groupDocs
operator|=
operator|(
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
index|[]
operator|)
operator|new
name|SearchGroupDocs
index|[
name|ordSet
operator|.
name|keys
operator|.
name|length
index|]
expr_stmt|;
block|}
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|slot
init|=
name|ordSet
operator|.
name|find
argument_list|(
name|source
operator|.
name|ord
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|>=
literal|0
condition|)
block|{
return|return
name|groupDocs
index|[
name|slot
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setDocValuesSources
specifier|protected
name|void
name|setDocValuesSources
parameter_list|(
name|DocValues
operator|.
name|Source
name|source
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
operator|.
name|asSortedSource
argument_list|()
expr_stmt|;
name|ordSet
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|int
name|ord
init|=
name|this
operator|.
name|source
operator|.
name|getOrdByValue
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
name|spare
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|groupDocs
index|[
name|ordSet
operator|.
name|put
argument_list|(
name|ord
argument_list|)
index|]
operator|=
name|group
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultSource
specifier|protected
name|DocValues
operator|.
name|Source
name|getDefaultSource
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|getDefaultSortedSource
argument_list|(
name|valueType
argument_list|,
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit

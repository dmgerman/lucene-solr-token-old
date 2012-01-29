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
name|AtomicIndexReader
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
name|AbstractFirstPassGroupingCollector
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * IDV based Implementations of {@link AbstractFirstPassGroupingCollector}.  *  * @lucene.experimental   */
end_comment
begin_class
DECL|class|DVFirstPassGroupingCollector
specifier|public
specifier|abstract
class|class
name|DVFirstPassGroupingCollector
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
extends|extends
name|AbstractFirstPassGroupingCollector
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
block|{
DECL|field|groupField
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|diskResident
specifier|final
name|boolean
name|diskResident
decl_stmt|;
DECL|field|valueType
specifier|final
name|DocValues
operator|.
name|Type
name|valueType
decl_stmt|;
DECL|method|create
specifier|public
specifier|static
name|DVFirstPassGroupingCollector
name|create
parameter_list|(
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|boolean
name|diskResident
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
return|return
operator|new
name|Lng
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|Dbl
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
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
return|return
operator|new
name|BR
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
operator|new
name|SortedBR
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
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
DECL|method|DVFirstPassGroupingCollector
name|DVFirstPassGroupingCollector
parameter_list|(
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
name|String
name|groupField
parameter_list|,
name|boolean
name|diskResident
parameter_list|,
name|DocValues
operator|.
name|Type
name|valueType
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
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
name|diskResident
operator|=
name|diskResident
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
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
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the idv source for concrete implementations to use.    *    * @param source The idv source to be used by concrete implementations    */
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
name|DVFirstPassGroupingCollector
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
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
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
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocGroupValue
specifier|protected
name|Long
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getInt
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|copyDocGroupValue
specifier|protected
name|Long
name|copyDocGroupValue
parameter_list|(
name|Long
name|groupValue
parameter_list|,
name|Long
name|reuse
parameter_list|)
block|{
return|return
name|groupValue
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
name|DVFirstPassGroupingCollector
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
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
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
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocGroupValue
specifier|protected
name|Double
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|copyDocGroupValue
specifier|protected
name|Double
name|copyDocGroupValue
parameter_list|(
name|Double
name|groupValue
parameter_list|,
name|Double
name|reuse
parameter_list|)
block|{
return|return
name|groupValue
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
name|DVFirstPassGroupingCollector
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
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
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
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocGroupValue
specifier|protected
name|BytesRef
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|source
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|spare
argument_list|)
return|;
block|}
DECL|method|copyDocGroupValue
specifier|protected
name|BytesRef
name|copyDocGroupValue
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|BytesRef
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|.
name|copyBytes
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
else|else
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|groupValue
argument_list|)
return|;
block|}
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
name|DVFirstPassGroupingCollector
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|sortedSource
specifier|private
name|DocValues
operator|.
name|SortedSource
name|sortedSource
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
DECL|method|SortedBR
name|SortedBR
parameter_list|(
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
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
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|groupField
argument_list|,
name|diskResident
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocGroupValue
specifier|protected
name|BytesRef
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|sortedSource
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|spare
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyDocGroupValue
specifier|protected
name|BytesRef
name|copyDocGroupValue
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|BytesRef
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|.
name|copyBytes
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
else|else
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|groupValue
argument_list|)
return|;
block|}
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
parameter_list|)
block|{
name|this
operator|.
name|sortedSource
operator|=
name|source
operator|.
name|asSortedSource
argument_list|()
expr_stmt|;
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

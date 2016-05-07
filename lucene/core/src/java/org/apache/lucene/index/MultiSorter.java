begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MergeState
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
name|LeafFieldComparator
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
name|util
operator|.
name|Bits
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
name|packed
operator|.
name|PackedInts
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
name|packed
operator|.
name|PackedLongValues
import|;
end_import
begin_class
DECL|class|MultiSorter
specifier|final
class|class
name|MultiSorter
block|{
comment|/** Does a merge sort of the leaves of the incoming reader, returning {@link MergeState#DocMap} to map each leaf's    *  documents into the merged segment.  The documents for each incoming leaf reader must already be sorted by the same sort! */
DECL|method|sort
specifier|static
name|MergeState
operator|.
name|DocMap
index|[]
name|sort
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|SortField
name|fields
index|[]
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|CrossReaderComparator
index|[]
name|comparators
init|=
operator|new
name|CrossReaderComparator
index|[
name|fields
operator|.
name|length
index|]
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
name|fields
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
name|getComparator
argument_list|(
name|readers
argument_list|,
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|leafCount
init|=
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|PriorityQueue
argument_list|<
name|LeafAndDocID
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|LeafAndDocID
argument_list|>
argument_list|(
name|leafCount
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|LeafAndDocID
name|a
parameter_list|,
name|LeafAndDocID
name|b
parameter_list|)
block|{
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
name|int
name|cmp
init|=
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|a
operator|.
name|readerIndex
argument_list|,
name|a
operator|.
name|docID
argument_list|,
name|b
operator|.
name|readerIndex
argument_list|,
name|b
operator|.
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|<
literal|0
return|;
block|}
block|}
comment|// tie-break by docID natural order:
if|if
condition|(
name|a
operator|.
name|readerIndex
operator|!=
name|b
operator|.
name|readerIndex
condition|)
block|{
return|return
name|a
operator|.
name|readerIndex
operator|<
name|b
operator|.
name|readerIndex
return|;
block|}
return|return
name|a
operator|.
name|docID
operator|<
name|b
operator|.
name|docID
return|;
block|}
block|}
decl_stmt|;
name|PackedLongValues
operator|.
name|Builder
index|[]
name|builders
init|=
operator|new
name|PackedLongValues
operator|.
name|Builder
index|[
name|leafCount
index|]
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
name|leafCount
condition|;
name|i
operator|++
control|)
block|{
name|CodecReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|LeafAndDocID
argument_list|(
name|i
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builders
index|[
name|i
index|]
operator|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
block|}
name|int
name|mappedDocID
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LeafAndDocID
name|top
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
name|builders
index|[
name|top
operator|.
name|readerIndex
index|]
operator|.
name|add
argument_list|(
name|mappedDocID
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|liveDocs
operator|==
literal|null
operator|||
name|top
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|top
operator|.
name|docID
argument_list|)
condition|)
block|{
name|mappedDocID
operator|++
expr_stmt|;
block|}
name|top
operator|.
name|docID
operator|++
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|docID
operator|<
name|top
operator|.
name|maxDoc
condition|)
block|{
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
name|MergeState
operator|.
name|DocMap
index|[]
name|docMaps
init|=
operator|new
name|MergeState
operator|.
name|DocMap
index|[
name|leafCount
index|]
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
name|leafCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PackedLongValues
name|remapped
init|=
name|builders
index|[
name|i
index|]
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|docMaps
index|[
name|i
index|]
operator|=
operator|new
name|MergeState
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|remapped
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
return|return
name|docMaps
return|;
block|}
DECL|class|LeafAndDocID
specifier|private
specifier|static
class|class
name|LeafAndDocID
block|{
DECL|field|readerIndex
specifier|final
name|int
name|readerIndex
decl_stmt|;
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|method|LeafAndDocID
specifier|public
name|LeafAndDocID
parameter_list|(
name|int
name|readerIndex
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
block|}
DECL|interface|CrossReaderComparator
specifier|private
interface|interface
name|CrossReaderComparator
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|readerIndexA
parameter_list|,
name|int
name|docIDA
parameter_list|,
name|int
name|readerIndexB
parameter_list|,
name|int
name|docIDB
parameter_list|)
function_decl|;
block|}
DECL|method|getComparator
specifier|private
specifier|static
name|CrossReaderComparator
name|getComparator
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|,
name|SortField
name|sortField
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|sortField
operator|.
name|getType
argument_list|()
condition|)
block|{
comment|// ncommit: use segment-local ords for string sort
case|case
name|INT
case|:
block|{
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Bits
argument_list|>
name|docsWithFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CodecReader
name|reader
range|:
name|readers
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|docsWithFields
operator|.
name|add
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|reverseMul
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|reverseMul
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|reverseMul
operator|=
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Integer
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0
expr_stmt|;
block|}
return|return
operator|new
name|CrossReaderComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|readerIndexA
parameter_list|,
name|int
name|docIDA
parameter_list|,
name|int
name|readerIndexB
parameter_list|,
name|int
name|docIDB
parameter_list|)
block|{
name|int
name|valueA
decl_stmt|;
if|if
condition|(
name|docsWithFields
operator|.
name|get
argument_list|(
name|readerIndexA
argument_list|)
operator|.
name|get
argument_list|(
name|docIDA
argument_list|)
condition|)
block|{
name|valueA
operator|=
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|readerIndexA
argument_list|)
operator|.
name|get
argument_list|(
name|docIDA
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueA
operator|=
name|missingValue
expr_stmt|;
block|}
name|int
name|valueB
decl_stmt|;
if|if
condition|(
name|docsWithFields
operator|.
name|get
argument_list|(
name|readerIndexB
argument_list|)
operator|.
name|get
argument_list|(
name|docIDB
argument_list|)
condition|)
block|{
name|valueB
operator|=
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|readerIndexB
argument_list|)
operator|.
name|get
argument_list|(
name|docIDB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueB
operator|=
name|missingValue
expr_stmt|;
block|}
return|return
name|reverseMul
operator|*
name|Integer
operator|.
name|compare
argument_list|(
name|valueA
argument_list|,
name|valueB
argument_list|)
return|;
block|}
block|}
return|;
block|}
case|case
name|LONG
case|:
comment|// nocommit refactor/share at least numerics here:
block|{
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Bits
argument_list|>
name|docsWithFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CodecReader
name|reader
range|:
name|readers
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|docsWithFields
operator|.
name|add
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|reverseMul
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|reverseMul
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|reverseMul
operator|=
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Integer
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0
expr_stmt|;
block|}
return|return
operator|new
name|CrossReaderComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|readerIndexA
parameter_list|,
name|int
name|docIDA
parameter_list|,
name|int
name|readerIndexB
parameter_list|,
name|int
name|docIDB
parameter_list|)
block|{
name|long
name|valueA
decl_stmt|;
if|if
condition|(
name|docsWithFields
operator|.
name|get
argument_list|(
name|readerIndexA
argument_list|)
operator|.
name|get
argument_list|(
name|docIDA
argument_list|)
condition|)
block|{
name|valueA
operator|=
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|readerIndexA
argument_list|)
operator|.
name|get
argument_list|(
name|docIDA
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueA
operator|=
name|missingValue
expr_stmt|;
block|}
name|long
name|valueB
decl_stmt|;
if|if
condition|(
name|docsWithFields
operator|.
name|get
argument_list|(
name|readerIndexB
argument_list|)
operator|.
name|get
argument_list|(
name|docIDB
argument_list|)
condition|)
block|{
name|valueB
operator|=
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|readerIndexB
argument_list|)
operator|.
name|get
argument_list|(
name|docIDB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueB
operator|=
name|missingValue
expr_stmt|;
block|}
return|return
name|reverseMul
operator|*
name|Long
operator|.
name|compare
argument_list|(
name|valueA
argument_list|,
name|valueB
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|// nocommit do the rest:
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unhandled SortField.getType()="
operator|+
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit

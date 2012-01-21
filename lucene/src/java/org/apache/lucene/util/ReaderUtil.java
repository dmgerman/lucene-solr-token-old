begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInfo
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
name|FieldInfos
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
name|AtomicIndexReader
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
name|CompositeIndexReader
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
name|IndexReader
operator|.
name|CompositeReaderContext
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
name|ReaderContext
import|;
end_import
begin_comment
comment|/**  * Common util methods for dealing with {@link IndexReader}s.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|ReaderUtil
specifier|public
specifier|final
class|class
name|ReaderUtil
block|{
DECL|method|ReaderUtil
specifier|private
name|ReaderUtil
parameter_list|()
block|{}
comment|// no instance
DECL|class|Slice
specifier|public
specifier|static
class|class
name|Slice
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|Slice
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Slice
index|[
literal|0
index|]
decl_stmt|;
DECL|field|start
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|length
specifier|public
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|readerIndex
specifier|public
specifier|final
name|int
name|readerIndex
decl_stmt|;
DECL|method|Slice
specifier|public
name|Slice
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|readerIndex
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"slice start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|length
operator|+
literal|" readerIndex="
operator|+
name|readerIndex
return|;
block|}
block|}
comment|/**    * Gathers sub-readers from reader into a List.  See    * {@link Gather} for are more general way to gather    * whatever you need to, per reader.    *    * @lucene.experimental    *     * @param allSubReaders    * @param reader    */
DECL|method|gatherSubReaders
specifier|public
specifier|static
name|void
name|gatherSubReaders
parameter_list|(
specifier|final
name|List
argument_list|<
name|AtomicIndexReader
argument_list|>
name|allSubReaders
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
try|try
block|{
operator|new
name|Gather
argument_list|(
name|reader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicIndexReader
name|r
parameter_list|)
block|{
name|allSubReaders
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// won't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/** Recursively visits all sub-readers of a reader.  You    *  should subclass this and override the add method to    *  gather what you need.    *    * @lucene.experimental */
DECL|class|Gather
specifier|public
specifier|static
specifier|abstract
class|class
name|Gather
block|{
DECL|field|topReader
specifier|private
specifier|final
name|IndexReader
name|topReader
decl_stmt|;
DECL|method|Gather
specifier|public
name|Gather
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|topReader
operator|=
name|r
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
literal|0
argument_list|,
name|topReader
argument_list|)
return|;
block|}
DECL|method|run
specifier|public
name|int
name|run
parameter_list|(
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
name|docBase
argument_list|,
name|topReader
argument_list|)
return|;
block|}
DECL|method|run
specifier|private
name|int
name|run
parameter_list|(
name|int
name|base
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|AtomicIndexReader
condition|)
block|{
comment|// atomic reader
name|add
argument_list|(
name|base
argument_list|,
operator|(
name|AtomicIndexReader
operator|)
name|reader
argument_list|)
expr_stmt|;
name|base
operator|+=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|reader
operator|instanceof
name|CompositeIndexReader
operator|:
literal|"must be a composite reader"
assert|;
name|IndexReader
index|[]
name|subReaders
init|=
operator|(
operator|(
name|CompositeIndexReader
operator|)
name|reader
operator|)
operator|.
name|getSequentialSubReaders
argument_list|()
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|base
operator|=
name|run
argument_list|(
name|base
argument_list|,
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|base
return|;
block|}
DECL|method|add
specifier|protected
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicIndexReader
name|r
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|buildReaderContext
specifier|public
specifier|static
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|buildReaderContext
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ReaderContextBuilder
argument_list|(
name|reader
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|class|ReaderContextBuilder
specifier|public
specifier|static
class|class
name|ReaderContextBuilder
block|{
DECL|field|reader
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|field|leafOrd
specifier|private
name|int
name|leafOrd
init|=
literal|0
decl_stmt|;
DECL|field|leafDocBase
specifier|private
name|int
name|leafDocBase
init|=
literal|0
decl_stmt|;
DECL|method|ReaderContextBuilder
specifier|public
name|ReaderContextBuilder
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|leaves
operator|=
operator|new
name|AtomicReaderContext
index|[
name|numLeaves
argument_list|(
name|reader
argument_list|)
index|]
expr_stmt|;
block|}
DECL|method|build
specifier|public
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|build
parameter_list|()
block|{
return|return
name|build
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|build
specifier|private
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|build
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|ord
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|AtomicIndexReader
condition|)
block|{
name|AtomicReaderContext
name|atomic
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|parent
argument_list|,
operator|(
name|AtomicIndexReader
operator|)
name|reader
argument_list|,
name|ord
argument_list|,
name|docBase
argument_list|,
name|leafOrd
argument_list|,
name|leafDocBase
argument_list|)
decl_stmt|;
name|leaves
index|[
name|leafOrd
operator|++
index|]
operator|=
name|atomic
expr_stmt|;
name|leafDocBase
operator|+=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
return|return
name|atomic
return|;
block|}
else|else
block|{
name|CompositeIndexReader
name|cr
init|=
operator|(
name|CompositeIndexReader
operator|)
name|reader
decl_stmt|;
name|IndexReader
index|[]
name|sequentialSubReaders
init|=
name|cr
operator|.
name|getSequentialSubReaders
argument_list|()
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
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
index|[]
name|children
init|=
operator|new
name|ReaderContext
index|[
name|sequentialSubReaders
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|CompositeReaderContext
name|newParent
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|newParent
operator|=
operator|new
name|CompositeReaderContext
argument_list|(
name|cr
argument_list|,
name|children
argument_list|,
name|leaves
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newParent
operator|=
operator|new
name|CompositeReaderContext
argument_list|(
name|parent
argument_list|,
name|cr
argument_list|,
name|ord
argument_list|,
name|docBase
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
name|int
name|newDocBase
init|=
literal|0
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
name|sequentialSubReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|=
name|build
argument_list|(
name|newParent
argument_list|,
name|sequentialSubReaders
index|[
name|i
index|]
argument_list|,
name|i
argument_list|,
name|newDocBase
argument_list|)
expr_stmt|;
name|newDocBase
operator|+=
name|sequentialSubReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|newParent
return|;
block|}
block|}
DECL|method|numLeaves
specifier|private
name|int
name|numLeaves
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|numLeaves
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
try|try
block|{
operator|new
name|Gather
argument_list|(
name|reader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicIndexReader
name|r
parameter_list|)
block|{
name|numLeaves
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// won't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
return|return
name|numLeaves
index|[
literal|0
index|]
return|;
block|}
block|}
comment|/**    * Returns the context's leaves or the context itself as the only element of    * the returned array. If the context's #leaves() method returns    *<code>null</code> the given context must be an instance of    * {@link AtomicReaderContext}    */
DECL|method|leaves
specifier|public
specifier|static
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|(
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|context
parameter_list|)
block|{
assert|assert
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|isTopLevel
operator|:
literal|"context must be non-null& top-level"
assert|;
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|context
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|AtomicReaderContext
index|[]
block|{
operator|(
name|AtomicReaderContext
operator|)
name|context
block|}
return|;
block|}
return|return
name|leaves
return|;
block|}
comment|/**    * Walks up the reader tree and return the given context's top level reader    * context, or in other words the reader tree's root context.    */
DECL|method|getTopLevelContext
specifier|public
specifier|static
name|ReaderContext
name|getTopLevelContext
parameter_list|(
name|ReaderContext
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|context
parameter_list|)
block|{
while|while
condition|(
name|context
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|context
operator|=
name|context
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|context
return|;
block|}
comment|/**    * Returns index of the searcher/reader for document<code>n</code> in the    * array used to construct this searcher/reader.    */
DECL|method|subIndex
specifier|public
specifier|static
name|int
name|subIndex
parameter_list|(
name|int
name|n
parameter_list|,
name|int
index|[]
name|docStarts
parameter_list|)
block|{
comment|// find
comment|// searcher/reader for doc n:
name|int
name|size
init|=
name|docStarts
operator|.
name|length
decl_stmt|;
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|size
operator|-
literal|1
decl_stmt|;
comment|// for first element less than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|docStarts
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|size
operator|&&
name|docStarts
index|[
name|mid
operator|+
literal|1
index|]
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
comment|/**    * Returns index of the searcher/reader for document<code>n</code> in the    * array used to construct this searcher/reader.    */
DECL|method|subIndex
specifier|public
specifier|static
name|int
name|subIndex
parameter_list|(
name|int
name|n
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
comment|// find
comment|// searcher/reader for doc n:
name|int
name|size
init|=
name|leaves
operator|.
name|length
decl_stmt|;
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|size
operator|-
literal|1
decl_stmt|;
comment|// for first element less than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|leaves
index|[
name|mid
index|]
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|size
operator|&&
name|leaves
index|[
name|mid
operator|+
literal|1
index|]
operator|.
name|docBase
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
DECL|method|getIndexedFields
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getIndexedFields
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|getMergedFieldInfos
argument_list|(
name|reader
argument_list|)
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|isIndexed
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
comment|/** Call this to get the (merged) FieldInfos for a    *  composite reader */
DECL|method|getMergedFieldInfos
specifier|public
specifier|static
name|FieldInfos
name|getMergedFieldInfos
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AtomicIndexReader
argument_list|>
name|subReaders
init|=
operator|new
name|ArrayList
argument_list|<
name|AtomicIndexReader
argument_list|>
argument_list|()
decl_stmt|;
name|ReaderUtil
operator|.
name|gatherSubReaders
argument_list|(
name|subReaders
argument_list|,
name|reader
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicIndexReader
name|subReader
range|:
name|subReaders
control|)
block|{
name|fieldInfos
operator|.
name|add
argument_list|(
name|subReader
operator|.
name|getFieldInfos
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldInfos
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|codecs
operator|.
name|DocValuesFormat
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
name|BinaryDocValues
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
name|Accountable
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
name|Accountables
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
name|IntsRef
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/**  * A per-segment cache of documents' facet ordinals. Every  * {@link CachedOrds} holds the ordinals in a raw {@code  * int[]}, and therefore consumes as much RAM as the total  * number of ordinals found in the segment, but saves the  * CPU cost of decoding ordinals during facet counting.  *   *<p>  *<b>NOTE:</b> every {@link CachedOrds} is limited to 2.1B  * total ordinals. If that is a limitation for you then  * consider limiting the segment size to fewer documents, or  * use an alternative cache which pages through the category  * ordinals.  *   *<p>  *<b>NOTE:</b> when using this cache, it is advised to use  * a {@link DocValuesFormat} that does not cache the data in  * memory, at least for the category lists fields, or  * otherwise you'll be doing double-caching.  *  *<p>  *<b>NOTE:</b> create one instance of this and re-use it  * for all facet implementations (the cache is per-instance,  * not static).  */
end_comment
begin_class
DECL|class|CachedOrdinalsReader
specifier|public
class|class
name|CachedOrdinalsReader
extends|extends
name|OrdinalsReader
implements|implements
name|Accountable
block|{
DECL|field|source
specifier|private
specifier|final
name|OrdinalsReader
name|source
decl_stmt|;
DECL|field|ordsCache
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|CachedOrds
argument_list|>
name|ordsCache
init|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|CachedOrdinalsReader
specifier|public
name|CachedOrdinalsReader
parameter_list|(
name|OrdinalsReader
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
DECL|method|getCachedOrds
specifier|private
specifier|synchronized
name|CachedOrds
name|getCachedOrds
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|cacheKey
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|CachedOrds
name|ords
init|=
name|ordsCache
operator|.
name|get
argument_list|(
name|cacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|ords
operator|==
literal|null
condition|)
block|{
name|ords
operator|=
operator|new
name|CachedOrds
argument_list|(
name|source
operator|.
name|getReader
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|ordsCache
operator|.
name|put
argument_list|(
name|cacheKey
argument_list|,
name|ords
argument_list|)
expr_stmt|;
block|}
return|return
name|ords
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexFieldName
specifier|public
name|String
name|getIndexFieldName
parameter_list|()
block|{
return|return
name|source
operator|.
name|getIndexFieldName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|OrdinalsSegmentReader
name|getReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|CachedOrds
name|cachedOrds
init|=
name|getCachedOrds
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|OrdinalsSegmentReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
block|{
name|ordinals
operator|.
name|ints
operator|=
name|cachedOrds
operator|.
name|ordinals
expr_stmt|;
name|ordinals
operator|.
name|offset
operator|=
name|cachedOrds
operator|.
name|offsets
index|[
name|docID
index|]
expr_stmt|;
name|ordinals
operator|.
name|length
operator|=
name|cachedOrds
operator|.
name|offsets
index|[
name|docID
operator|+
literal|1
index|]
operator|-
name|ordinals
operator|.
name|offset
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Holds the cached ordinals in two parallel {@code int[]} arrays. */
DECL|class|CachedOrds
specifier|public
specifier|static
specifier|final
class|class
name|CachedOrds
implements|implements
name|Accountable
block|{
comment|/** Index into {@link #ordinals} for each document. */
DECL|field|offsets
specifier|public
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
comment|/** Holds ords for all docs. */
DECL|field|ordinals
specifier|public
specifier|final
name|int
index|[]
name|ordinals
decl_stmt|;
comment|/**      * Creates a new {@link CachedOrds} from the {@link BinaryDocValues}.      * Assumes that the {@link BinaryDocValues} is not {@code null}.      */
DECL|method|CachedOrds
specifier|public
name|CachedOrds
parameter_list|(
name|OrdinalsSegmentReader
name|source
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|offsets
operator|=
operator|new
name|int
index|[
name|maxDoc
operator|+
literal|1
index|]
expr_stmt|;
name|int
index|[]
name|ords
init|=
operator|new
name|int
index|[
name|maxDoc
index|]
decl_stmt|;
comment|// let's assume one ordinal per-document as an initial size
comment|// this aggregator is limited to Integer.MAX_VALUE total ordinals.
name|long
name|totOrds
init|=
literal|0
decl_stmt|;
specifier|final
name|IntsRef
name|values
init|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|offsets
index|[
name|docID
index|]
operator|=
operator|(
name|int
operator|)
name|totOrds
expr_stmt|;
name|source
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|long
name|nextLength
init|=
name|totOrds
operator|+
name|values
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|nextLength
operator|>
name|ords
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|nextLength
operator|>
name|ArrayUtil
operator|.
name|MAX_ARRAY_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"too many ordinals (>= "
operator|+
name|nextLength
operator|+
literal|") to cache"
argument_list|)
throw|;
block|}
name|ords
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ords
argument_list|,
operator|(
name|int
operator|)
name|nextLength
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|values
operator|.
name|ints
argument_list|,
literal|0
argument_list|,
name|ords
argument_list|,
operator|(
name|int
operator|)
name|totOrds
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|totOrds
operator|=
name|nextLength
expr_stmt|;
block|}
name|offsets
index|[
name|maxDoc
index|]
operator|=
operator|(
name|int
operator|)
name|totOrds
expr_stmt|;
comment|// if ords array is bigger by more than 10% of what we really need, shrink it
if|if
condition|(
operator|(
name|double
operator|)
name|totOrds
operator|/
name|ords
operator|.
name|length
operator|<
literal|0.9
condition|)
block|{
name|this
operator|.
name|ordinals
operator|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|totOrds
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ords
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|ordinals
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|totOrds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|ordinals
operator|=
name|ords
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|mem
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|this
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|offsets
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsets
operator|!=
name|ordinals
condition|)
block|{
name|mem
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|ordinals
argument_list|)
expr_stmt|;
block|}
return|return
name|mem
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
specifier|synchronized
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|bytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CachedOrds
name|ords
range|:
name|ordsCache
operator|.
name|values
argument_list|()
control|)
block|{
name|bytes
operator|+=
name|ords
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"segment"
argument_list|,
name|ordsCache
argument_list|)
return|;
block|}
block|}
end_class
end_unit

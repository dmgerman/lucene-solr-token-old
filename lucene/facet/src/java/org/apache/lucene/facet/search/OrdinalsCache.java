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
name|HashMap
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
name|facet
operator|.
name|encoding
operator|.
name|IntDecoder
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A per-segment cache of documents' category ordinals. Every {@link CachedOrds}  * holds the ordinals in a raw {@code int[]}, and therefore consumes as much RAM  * as the total number of ordinals found in the segment.  *   *<p>  *<b>NOTE:</b> every {@link CachedOrds} is limited to 2.1B total ordinals. If  * that is a limitation for you then consider limiting the segment size to less  * documents, or use an alternative cache which pages through the category  * ordinals.  *   *<p>  *<b>NOTE:</b> when using this cache, it is advised to use a  * {@link DocValuesFormat} that does not cache the data in memory, at least for  * the category lists fields, or otherwise you'll be doing double-caching.  */
end_comment
begin_class
DECL|class|OrdinalsCache
specifier|public
class|class
name|OrdinalsCache
block|{
comment|/** Holds the cached ordinals in two paralel {@code int[]} arrays. */
DECL|class|CachedOrds
specifier|public
specifier|static
specifier|final
class|class
name|CachedOrds
block|{
DECL|field|offsets
specifier|public
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
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
name|BinaryDocValues
name|dv
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|)
block|{
specifier|final
name|BytesRef
name|buf
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
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
name|int
name|totOrds
init|=
literal|0
decl_stmt|;
specifier|final
name|IntDecoder
name|decoder
init|=
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
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
name|totOrds
expr_stmt|;
name|dv
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// this document has facets
name|decoder
operator|.
name|decode
argument_list|(
name|buf
argument_list|,
name|values
argument_list|)
expr_stmt|;
if|if
condition|(
name|totOrds
operator|+
name|values
operator|.
name|length
operator|>=
name|ords
operator|.
name|length
condition|)
block|{
name|ords
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ords
argument_list|,
name|totOrds
operator|+
name|values
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ords
index|[
name|totOrds
operator|++
index|]
operator|=
name|values
operator|.
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
name|offsets
index|[
name|maxDoc
index|]
operator|=
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
block|}
comment|// outer map is a WeakHashMap which uses reader.getCoreCacheKey() as the weak
comment|// reference. When it's no longer referenced, the entire inner map can be
comment|// evicted.
DECL|field|ordsCache
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CachedOrds
argument_list|>
argument_list|>
name|ordsCache
init|=
operator|new
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CachedOrds
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Returns the {@link CachedOrds} relevant to the given    * {@link AtomicReaderContext}, or {@code null} if there is no    * {@link BinaryDocValues} in this reader for the requested    * {@link CategoryListParams#field}.    */
DECL|method|getCachedOrds
specifier|public
specifier|static
specifier|synchronized
name|CachedOrds
name|getCachedOrds
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|dv
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|clp
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|CachedOrds
argument_list|>
name|fieldCache
init|=
name|ordsCache
operator|.
name|get
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldCache
operator|==
literal|null
condition|)
block|{
name|fieldCache
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OrdinalsCache
operator|.
name|CachedOrds
argument_list|>
argument_list|()
expr_stmt|;
name|ordsCache
operator|.
name|put
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|fieldCache
argument_list|)
expr_stmt|;
block|}
name|CachedOrds
name|co
init|=
name|fieldCache
operator|.
name|get
argument_list|(
name|clp
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|co
operator|==
literal|null
condition|)
block|{
name|co
operator|=
operator|new
name|CachedOrds
argument_list|(
name|dv
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|clp
argument_list|)
expr_stmt|;
name|fieldCache
operator|.
name|put
argument_list|(
name|clp
operator|.
name|field
argument_list|,
name|co
argument_list|)
expr_stmt|;
block|}
return|return
name|co
return|;
block|}
comment|/** Returns how many bytes the static ords cache is    *  consuming. */
DECL|method|ramBytesUsed
specifier|public
specifier|synchronized
specifier|static
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|CachedOrds
argument_list|>
name|e
range|:
name|ordsCache
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|CachedOrds
name|co
range|:
name|e
operator|.
name|values
argument_list|()
control|)
block|{
name|size
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// CachedOrds reference in the map
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
comment|// CachedOrds object header
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|*
literal|2
comment|// 2 int[] (header)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|*
literal|2
comment|// 2 int[] (ref)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
name|co
operator|.
name|offsets
operator|.
name|length
comment|// sizeOf(offsets)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
name|co
operator|.
name|ordinals
operator|.
name|length
expr_stmt|;
comment|// sizeOf(ordinals)
block|}
block|}
return|return
name|size
return|;
block|}
comment|/** Clears all entries from the cache. */
DECL|method|clear
specifier|public
specifier|synchronized
specifier|static
name|void
name|clear
parameter_list|()
block|{
name|ordsCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

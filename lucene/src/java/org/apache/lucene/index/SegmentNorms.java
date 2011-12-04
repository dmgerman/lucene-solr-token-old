begin_unit
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
name|store
operator|.
name|FlushInfo
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
name|store
operator|.
name|IOContext
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|IndexOutput
import|;
end_import
begin_comment
comment|/**  * Byte[] referencing is used because a new norm object needs   * to be created for each clone, and the byte array is all   * that is needed for sharing between cloned readers.  The   * current norm referencing is for sharing between readers   * whereas the byte[] referencing is for copy on write which   * is independent of reader references (i.e. incRef, decRef).  */
end_comment
begin_class
DECL|class|SegmentNorms
specifier|final
class|class
name|SegmentNorms
implements|implements
name|Cloneable
block|{
DECL|field|refCount
name|int
name|refCount
init|=
literal|1
decl_stmt|;
comment|// If this instance is a clone, the originalNorm
comment|// references the Norm that has a real open IndexInput:
DECL|field|origNorm
specifier|private
name|SegmentNorms
name|origNorm
decl_stmt|;
DECL|field|in
specifier|private
name|IndexInput
name|in
decl_stmt|;
DECL|field|normSeek
specifier|private
name|long
name|normSeek
decl_stmt|;
comment|// null until bytes is set
DECL|field|bytesRef
specifier|private
name|AtomicInteger
name|bytesRef
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|number
specifier|private
name|int
name|number
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|SegmentReader
name|owner
decl_stmt|;
DECL|method|SegmentNorms
specifier|public
name|SegmentNorms
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|number
parameter_list|,
name|long
name|normSeek
parameter_list|,
name|SegmentReader
name|owner
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|normSeek
operator|=
name|normSeek
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
DECL|method|incRef
specifier|public
specifier|synchronized
name|void
name|incRef
parameter_list|()
block|{
assert|assert
name|refCount
operator|>
literal|0
operator|&&
operator|(
name|origNorm
operator|==
literal|null
operator|||
name|origNorm
operator|.
name|refCount
operator|>
literal|0
operator|)
assert|;
name|refCount
operator|++
expr_stmt|;
block|}
DECL|method|closeInput
specifier|private
name|void
name|closeInput
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|in
operator|!=
name|owner
operator|.
name|singleNormStream
condition|)
block|{
comment|// It's private to us -- just close it
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// We are sharing this with others -- decRef and
comment|// maybe close the shared norm stream
if|if
condition|(
name|owner
operator|.
name|singleNormRef
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|owner
operator|.
name|singleNormStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|owner
operator|.
name|singleNormStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|decRef
specifier|public
specifier|synchronized
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|refCount
operator|>
literal|0
operator|&&
operator|(
name|origNorm
operator|==
literal|null
operator|||
name|origNorm
operator|.
name|refCount
operator|>
literal|0
operator|)
assert|;
if|if
condition|(
operator|--
name|refCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|origNorm
operator|!=
literal|null
condition|)
block|{
name|origNorm
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|origNorm
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|closeInput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
assert|assert
name|bytesRef
operator|!=
literal|null
assert|;
name|bytesRef
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|bytes
operator|=
literal|null
expr_stmt|;
name|bytesRef
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|bytesRef
operator|==
literal|null
assert|;
block|}
block|}
block|}
comment|// Load& cache full bytes array.  Returns bytes.
DECL|method|bytes
specifier|public
specifier|synchronized
name|byte
index|[]
name|bytes
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|refCount
operator|>
literal|0
operator|&&
operator|(
name|origNorm
operator|==
literal|null
operator|||
name|origNorm
operator|.
name|refCount
operator|>
literal|0
operator|)
assert|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
comment|// value not yet read
assert|assert
name|bytesRef
operator|==
literal|null
assert|;
if|if
condition|(
name|origNorm
operator|!=
literal|null
condition|)
block|{
comment|// Ask origNorm to load so that for a series of
comment|// reopened readers we share a single read-only
comment|// byte[]
name|bytes
operator|=
name|origNorm
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|bytesRef
operator|=
name|origNorm
operator|.
name|bytesRef
expr_stmt|;
name|bytesRef
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// Once we've loaded the bytes we no longer need
comment|// origNorm:
name|origNorm
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|origNorm
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// We are the origNorm, so load the bytes for real
comment|// ourself:
specifier|final
name|int
name|count
init|=
name|owner
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[
name|count
index|]
expr_stmt|;
comment|// Since we are orig, in must not be null
assert|assert
name|in
operator|!=
literal|null
assert|;
comment|// Read from disk.
synchronized|synchronized
init|(
name|in
init|)
block|{
name|in
operator|.
name|seek
argument_list|(
name|normSeek
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|count
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|bytesRef
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|closeInput
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|bytes
return|;
block|}
comment|// Only for testing
DECL|method|bytesRef
name|AtomicInteger
name|bytesRef
parameter_list|()
block|{
return|return
name|bytesRef
return|;
block|}
comment|// Returns a copy of this Norm instance that shares
comment|// IndexInput& bytes with the original one
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
assert|assert
name|refCount
operator|>
literal|0
operator|&&
operator|(
name|origNorm
operator|==
literal|null
operator|||
name|origNorm
operator|.
name|refCount
operator|>
literal|0
operator|)
assert|;
name|SegmentNorms
name|clone
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|SegmentNorms
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|cnse
parameter_list|)
block|{
comment|// Cannot happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected CloneNotSupportedException"
argument_list|,
name|cnse
argument_list|)
throw|;
block|}
name|clone
operator|.
name|refCount
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
assert|assert
name|bytesRef
operator|!=
literal|null
assert|;
assert|assert
name|origNorm
operator|==
literal|null
assert|;
comment|// Clone holds a reference to my bytes:
name|clone
operator|.
name|bytesRef
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|bytesRef
operator|==
literal|null
assert|;
if|if
condition|(
name|origNorm
operator|==
literal|null
condition|)
block|{
comment|// I become the origNorm for the clone:
name|clone
operator|.
name|origNorm
operator|=
name|this
expr_stmt|;
block|}
name|clone
operator|.
name|origNorm
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
comment|// Only the origNorm will actually readBytes from in:
name|clone
operator|.
name|in
operator|=
literal|null
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class
end_unit

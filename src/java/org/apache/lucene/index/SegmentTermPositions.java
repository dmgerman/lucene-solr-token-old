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
begin_class
DECL|class|SegmentTermPositions
specifier|final
class|class
name|SegmentTermPositions
extends|extends
name|SegmentTermDocs
implements|implements
name|TermPositions
block|{
DECL|field|proxStream
specifier|private
name|IndexInput
name|proxStream
decl_stmt|;
DECL|field|proxCount
specifier|private
name|int
name|proxCount
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
comment|// these variables are being used to remember information
comment|// for a lazy skip
DECL|field|lazySkipPointer
specifier|private
name|long
name|lazySkipPointer
init|=
literal|0
decl_stmt|;
DECL|field|lazySkipDocCount
specifier|private
name|int
name|lazySkipDocCount
init|=
literal|0
decl_stmt|;
DECL|method|SegmentTermPositions
name|SegmentTermPositions
parameter_list|(
name|SegmentReader
name|p
parameter_list|)
block|{
name|super
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|this
operator|.
name|proxStream
operator|=
literal|null
expr_stmt|;
comment|// the proxStream will be cloned lazily when nextPosition() is called for the first time
block|}
DECL|method|seek
specifier|final
name|void
name|seek
parameter_list|(
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|seek
argument_list|(
name|ti
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
name|lazySkipPointer
operator|=
name|ti
operator|.
name|proxPointer
expr_stmt|;
name|lazySkipDocCount
operator|=
literal|0
expr_stmt|;
name|proxCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxStream
operator|!=
literal|null
condition|)
name|proxStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
specifier|final
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
comment|// perform lazy skips if neccessary
name|lazySkip
argument_list|()
expr_stmt|;
name|proxCount
operator|--
expr_stmt|;
return|return
name|position
operator|+=
name|proxStream
operator|.
name|readVInt
argument_list|()
return|;
block|}
DECL|method|skippingDoc
specifier|protected
specifier|final
name|void
name|skippingDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we remember to skip the remaining positions of the current
comment|// document lazily
name|lazySkipDocCount
operator|+=
name|freq
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we remember to skip a document lazily
name|lazySkipDocCount
operator|+=
name|proxCount
expr_stmt|;
if|if
condition|(
name|super
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// run super
name|proxCount
operator|=
name|freq
expr_stmt|;
comment|// note frequency
name|position
operator|=
literal|0
expr_stmt|;
comment|// reset position
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|read
specifier|public
specifier|final
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TermPositions does not support processing multiple documents in one call. Use TermDocs instead."
argument_list|)
throw|;
block|}
comment|/** Called by super.skipTo(). */
DECL|method|skipProx
specifier|protected
name|void
name|skipProx
parameter_list|(
name|long
name|proxPointer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we save the pointer, we might have to skip there lazily
name|lazySkipPointer
operator|=
name|proxPointer
expr_stmt|;
name|lazySkipDocCount
operator|=
literal|0
expr_stmt|;
name|proxCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|skipPositions
specifier|private
name|void
name|skipPositions
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|f
init|=
name|n
init|;
name|f
operator|>
literal|0
condition|;
name|f
operator|--
control|)
comment|// skip unread positions
name|proxStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
comment|// It is not always neccessary to move the prox pointer
comment|// to a new document after the freq pointer has been moved.
comment|// Consider for example a phrase query with two terms:
comment|// the freq pointer for term 1 has to move to document x
comment|// to answer the question if the term occurs in that document. But
comment|// only if term 2 also matches document x, the positions have to be
comment|// read to figure out if term 1 and term 2 appear next
comment|// to each other in document x and thus satisfy the query.
comment|// So we move the prox pointer lazily to the document
comment|// as soon as positions are requested.
DECL|method|lazySkip
specifier|private
name|void
name|lazySkip
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|proxStream
operator|==
literal|null
condition|)
block|{
comment|// clone lazily
name|proxStream
operator|=
operator|(
name|IndexInput
operator|)
name|parent
operator|.
name|proxStream
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lazySkipPointer
operator|!=
literal|0
condition|)
block|{
name|proxStream
operator|.
name|seek
argument_list|(
name|lazySkipPointer
argument_list|)
expr_stmt|;
name|lazySkipPointer
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|lazySkipDocCount
operator|!=
literal|0
condition|)
block|{
name|skipPositions
argument_list|(
name|lazySkipDocCount
argument_list|)
expr_stmt|;
name|lazySkipDocCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

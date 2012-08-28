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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/** Embeds a [read-only] SegmentInfo and adds per-commit  *  fields.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|SegmentInfoPerCommit
specifier|public
class|class
name|SegmentInfoPerCommit
block|{
DECL|field|info
specifier|public
specifier|final
name|SegmentInfo
name|info
decl_stmt|;
comment|// How many deleted docs in the segment:
DECL|field|delCount
specifier|private
name|int
name|delCount
decl_stmt|;
comment|// Generation number of the live docs file (-1 if there
comment|// are no deletes yet):
DECL|field|delGen
specifier|private
name|long
name|delGen
decl_stmt|;
DECL|field|sizeInBytes
specifier|private
specifier|volatile
name|long
name|sizeInBytes
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SegmentInfoPerCommit
specifier|public
name|SegmentInfoPerCommit
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|int
name|delCount
parameter_list|,
name|long
name|delGen
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
name|this
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
block|}
DECL|method|advanceDelGen
name|void
name|advanceDelGen
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
operator|-
literal|1
condition|)
block|{
name|delGen
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|delGen
operator|++
expr_stmt|;
block|}
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sizeInBytes
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|this
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|long
name|sum
init|=
name|info
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|files
argument_list|()
control|)
block|{
name|sum
operator|+=
name|info
operator|.
name|dir
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|sizeInBytes
operator|=
name|sum
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
DECL|method|files
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|()
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|info
operator|.
name|files
argument_list|()
argument_list|)
decl_stmt|;
comment|// Must separately add any live docs files:
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|this
argument_list|,
name|files
argument_list|)
expr_stmt|;
return|return
name|files
return|;
block|}
comment|// NOTE: only used in-RAM by IW to track buffered deletes;
comment|// this is never written to/read from the Directory
DECL|field|bufferedDeletesGen
specifier|private
name|long
name|bufferedDeletesGen
decl_stmt|;
DECL|method|getBufferedDeletesGen
name|long
name|getBufferedDeletesGen
parameter_list|()
block|{
return|return
name|bufferedDeletesGen
return|;
block|}
DECL|method|setBufferedDeletesGen
name|void
name|setBufferedDeletesGen
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|bufferedDeletesGen
operator|=
name|v
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|clearDelGen
name|void
name|clearDelGen
parameter_list|()
block|{
name|delGen
operator|=
operator|-
literal|1
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Sets the generation number of the live docs file.    * @see #getDelGen()    */
DECL|method|setDelGen
specifier|public
name|void
name|setDelGen
parameter_list|(
name|long
name|delGen
parameter_list|)
block|{
name|this
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/** Returns true if there are any deletions for the     * segment at this commit. */
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|delGen
operator|!=
operator|-
literal|1
return|;
block|}
comment|/**    * Returns the next available generation number    * of the live docs file.    */
DECL|method|getNextDelGen
specifier|public
name|long
name|getNextDelGen
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|delGen
operator|+
literal|1
return|;
block|}
block|}
comment|/**    * Returns generation number of the live docs file     * or -1 if there are no deletes yet.    */
DECL|method|getDelGen
specifier|public
name|long
name|getDelGen
parameter_list|()
block|{
return|return
name|delGen
return|;
block|}
comment|/**    * Returns the number of deleted docs in the segment.    */
DECL|method|getDelCount
specifier|public
name|int
name|getDelCount
parameter_list|()
block|{
return|return
name|delCount
return|;
block|}
DECL|method|setDelCount
name|void
name|setDelCount
parameter_list|(
name|int
name|delCount
parameter_list|)
block|{
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
assert|assert
name|delCount
operator|<=
name|info
operator|.
name|getDocCount
argument_list|()
assert|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|pendingDelCount
parameter_list|)
block|{
return|return
name|info
operator|.
name|toString
argument_list|(
name|dir
argument_list|,
name|delCount
operator|+
name|pendingDelCount
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|info
operator|.
name|toString
argument_list|(
name|info
operator|.
name|dir
argument_list|,
name|delCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|delGen
operator|!=
operator|-
literal|1
condition|)
block|{
name|s
operator|+=
literal|":delGen="
operator|+
name|delGen
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SegmentInfoPerCommit
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SegmentInfoPerCommit
argument_list|(
name|info
argument_list|,
name|delCount
argument_list|,
name|delGen
argument_list|)
return|;
block|}
block|}
end_class
end_unit

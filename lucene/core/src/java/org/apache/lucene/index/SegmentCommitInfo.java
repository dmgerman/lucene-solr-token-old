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
name|Collections
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
name|HashSet
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
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
DECL|class|SegmentCommitInfo
specifier|public
class|class
name|SegmentCommitInfo
block|{
comment|/** The {@link SegmentInfo} that we wrap. */
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
comment|// Normally 1+delGen, unless an exception was hit on last
comment|// attempt to write:
DECL|field|nextWriteDelGen
specifier|private
name|long
name|nextWriteDelGen
decl_stmt|;
comment|// Generation number of the FieldInfos (-1 if there are no updates)
DECL|field|fieldInfosGen
specifier|private
name|long
name|fieldInfosGen
decl_stmt|;
comment|// Normally 1+fieldInfosGen, unless an exception was hit on last attempt to
comment|// write
DECL|field|nextWriteFieldInfosGen
specifier|private
name|long
name|nextWriteFieldInfosGen
decl_stmt|;
comment|// Generation number of the DocValues (-1 if there are no updates)
DECL|field|docValuesGen
specifier|private
name|long
name|docValuesGen
decl_stmt|;
comment|// Normally 1+dvGen, unless an exception was hit on last attempt to
comment|// write
DECL|field|nextWriteDocValuesGen
specifier|private
name|long
name|nextWriteDocValuesGen
decl_stmt|;
comment|// Track the per-field DocValues update files
DECL|field|dvUpdatesFiles
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|dvUpdatesFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO should we add .files() to FieldInfosFormat, like we have on
comment|// LiveDocsFormat?
comment|// track the fieldInfos update files
DECL|field|fieldInfosFiles
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fieldInfosFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
comment|/**    * Sole constructor.    *     * @param info    *          {@link SegmentInfo} that we wrap    * @param delCount    *          number of deleted documents in this segment    * @param delGen    *          deletion generation number (used to name deletion files)    * @param fieldInfosGen    *          FieldInfos generation number (used to name field-infos files)    * @param docValuesGen    *          DocValues generation number (used to name doc-values updates files)    */
DECL|method|SegmentCommitInfo
specifier|public
name|SegmentCommitInfo
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|int
name|delCount
parameter_list|,
name|long
name|delGen
parameter_list|,
name|long
name|fieldInfosGen
parameter_list|,
name|long
name|docValuesGen
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
name|this
operator|.
name|nextWriteDelGen
operator|=
name|delGen
operator|==
operator|-
literal|1
condition|?
literal|1
else|:
name|delGen
operator|+
literal|1
expr_stmt|;
name|this
operator|.
name|fieldInfosGen
operator|=
name|fieldInfosGen
expr_stmt|;
name|this
operator|.
name|nextWriteFieldInfosGen
operator|=
name|fieldInfosGen
operator|==
operator|-
literal|1
condition|?
literal|1
else|:
name|fieldInfosGen
operator|+
literal|1
expr_stmt|;
name|this
operator|.
name|docValuesGen
operator|=
name|docValuesGen
expr_stmt|;
name|this
operator|.
name|nextWriteDocValuesGen
operator|=
name|docValuesGen
operator|==
operator|-
literal|1
condition|?
literal|1
else|:
name|docValuesGen
operator|+
literal|1
expr_stmt|;
block|}
comment|/** Returns the per-field DocValues updates files. */
DECL|method|getDocValuesUpdatesFiles
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getDocValuesUpdatesFiles
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|dvUpdatesFiles
argument_list|)
return|;
block|}
comment|/** Sets the DocValues updates file names, per field number. Does not deep clone the map. */
DECL|method|setDocValuesUpdatesFiles
specifier|public
name|void
name|setDocValuesUpdatesFiles
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|dvUpdatesFiles
parameter_list|)
block|{
name|this
operator|.
name|dvUpdatesFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|kv
range|:
name|dvUpdatesFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// rename the set
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|kv
operator|.
name|getValue
argument_list|()
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|info
operator|.
name|namedForThisSegment
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dvUpdatesFiles
operator|.
name|put
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the FieldInfos file names. */
DECL|method|getFieldInfosFiles
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getFieldInfosFiles
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fieldInfosFiles
argument_list|)
return|;
block|}
comment|/** Sets the FieldInfos file names. */
DECL|method|setFieldInfosFiles
specifier|public
name|void
name|setFieldInfosFiles
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|fieldInfosFiles
parameter_list|)
block|{
name|this
operator|.
name|fieldInfosFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|fieldInfosFiles
control|)
block|{
name|this
operator|.
name|fieldInfosFiles
operator|.
name|add
argument_list|(
name|info
operator|.
name|namedForThisSegment
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Called when we succeed in writing deletes */
DECL|method|advanceDelGen
name|void
name|advanceDelGen
parameter_list|()
block|{
name|delGen
operator|=
name|nextWriteDelGen
expr_stmt|;
name|nextWriteDelGen
operator|=
name|delGen
operator|+
literal|1
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/** Called if there was an exception while writing    *  deletes, so that we don't try to write to the same    *  file more than once. */
DECL|method|advanceNextWriteDelGen
name|void
name|advanceNextWriteDelGen
parameter_list|()
block|{
name|nextWriteDelGen
operator|++
expr_stmt|;
block|}
comment|/** Gets the nextWriteDelGen. */
DECL|method|getNextWriteDelGen
name|long
name|getNextWriteDelGen
parameter_list|()
block|{
return|return
name|nextWriteDelGen
return|;
block|}
comment|/** Sets the nextWriteDelGen. */
DECL|method|setNextWriteDelGen
name|void
name|setNextWriteDelGen
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|nextWriteDelGen
operator|=
name|v
expr_stmt|;
block|}
comment|/** Called when we succeed in writing a new FieldInfos generation. */
DECL|method|advanceFieldInfosGen
name|void
name|advanceFieldInfosGen
parameter_list|()
block|{
name|fieldInfosGen
operator|=
name|nextWriteFieldInfosGen
expr_stmt|;
name|nextWriteFieldInfosGen
operator|=
name|fieldInfosGen
operator|+
literal|1
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Called if there was an exception while writing a new generation of    * FieldInfos, so that we don't try to write to the same file more than once.    */
DECL|method|advanceNextWriteFieldInfosGen
name|void
name|advanceNextWriteFieldInfosGen
parameter_list|()
block|{
name|nextWriteFieldInfosGen
operator|++
expr_stmt|;
block|}
comment|/** Gets the nextWriteFieldInfosGen. */
DECL|method|getNextWriteFieldInfosGen
name|long
name|getNextWriteFieldInfosGen
parameter_list|()
block|{
return|return
name|nextWriteFieldInfosGen
return|;
block|}
comment|/** Sets the nextWriteFieldInfosGen. */
DECL|method|setNextWriteFieldInfosGen
name|void
name|setNextWriteFieldInfosGen
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|nextWriteFieldInfosGen
operator|=
name|v
expr_stmt|;
block|}
comment|/** Called when we succeed in writing a new DocValues generation. */
DECL|method|advanceDocValuesGen
name|void
name|advanceDocValuesGen
parameter_list|()
block|{
name|docValuesGen
operator|=
name|nextWriteDocValuesGen
expr_stmt|;
name|nextWriteDocValuesGen
operator|=
name|docValuesGen
operator|+
literal|1
expr_stmt|;
name|sizeInBytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Called if there was an exception while writing a new generation of    * DocValues, so that we don't try to write to the same file more than once.    */
DECL|method|advanceNextWriteDocValuesGen
name|void
name|advanceNextWriteDocValuesGen
parameter_list|()
block|{
name|nextWriteDocValuesGen
operator|++
expr_stmt|;
block|}
comment|/** Gets the nextWriteDocValuesGen. */
DECL|method|getNextWriteDocValuesGen
name|long
name|getNextWriteDocValuesGen
parameter_list|()
block|{
return|return
name|nextWriteDocValuesGen
return|;
block|}
comment|/** Sets the nextWriteDocValuesGen. */
DECL|method|setNextWriteDocValuesGen
name|void
name|setNextWriteDocValuesGen
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|nextWriteDocValuesGen
operator|=
name|v
expr_stmt|;
block|}
comment|/** Returns total size in bytes of all files for this    *  segment. */
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
name|long
name|sum
init|=
literal|0
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
comment|/** Returns all files in use by this segment. */
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
comment|// Start from the wrapped info's files:
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|info
operator|.
name|files
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO we could rely on TrackingDir.getCreatedFiles() (like we do for
comment|// updates) and then maybe even be able to remove LiveDocsFormat.files().
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
comment|// must separately add any field updates files
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|updatefiles
range|:
name|dvUpdatesFiles
operator|.
name|values
argument_list|()
control|)
block|{
name|files
operator|.
name|addAll
argument_list|(
name|updatefiles
argument_list|)
expr_stmt|;
block|}
comment|// must separately add fieldInfos files
name|files
operator|.
name|addAll
argument_list|(
name|fieldInfosFiles
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
comment|/** Returns true if there are any field updates for the segment in this commit. */
DECL|method|hasFieldUpdates
specifier|public
name|boolean
name|hasFieldUpdates
parameter_list|()
block|{
return|return
name|fieldInfosGen
operator|!=
operator|-
literal|1
return|;
block|}
comment|/** Returns the next available generation number of the FieldInfos files. */
DECL|method|getNextFieldInfosGen
specifier|public
name|long
name|getNextFieldInfosGen
parameter_list|()
block|{
return|return
name|nextWriteFieldInfosGen
return|;
block|}
comment|/**    * Returns the generation number of the field infos file or -1 if there are no    * field updates yet.    */
DECL|method|getFieldInfosGen
specifier|public
name|long
name|getFieldInfosGen
parameter_list|()
block|{
return|return
name|fieldInfosGen
return|;
block|}
comment|/** Returns the next available generation number of the DocValues files. */
DECL|method|getNextDocValuesGen
specifier|public
name|long
name|getNextDocValuesGen
parameter_list|()
block|{
return|return
name|nextWriteDocValuesGen
return|;
block|}
comment|/**    * Returns the generation number of the DocValues file or -1 if there are no    * doc-values updates yet.    */
DECL|method|getDocValuesGen
specifier|public
name|long
name|getDocValuesGen
parameter_list|()
block|{
return|return
name|docValuesGen
return|;
block|}
comment|/**    * Returns the next available generation number    * of the live docs file.    */
DECL|method|getNextDelGen
specifier|public
name|long
name|getNextDelGen
parameter_list|()
block|{
return|return
name|nextWriteDelGen
return|;
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
if|if
condition|(
name|delCount
argument_list|<
literal|0
operator|||
name|delCount
argument_list|>
name|info
operator|.
name|getDocCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid delCount="
operator|+
name|delCount
operator|+
literal|" (docCount="
operator|+
name|info
operator|.
name|getDocCount
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|delCount
operator|=
name|delCount
expr_stmt|;
block|}
comment|/** Returns a description of this segment. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|pendingDelCount
parameter_list|)
block|{
name|String
name|s
init|=
name|info
operator|.
name|toString
argument_list|(
name|delCount
operator|+
name|pendingDelCount
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
if|if
condition|(
name|fieldInfosGen
operator|!=
operator|-
literal|1
condition|)
block|{
name|s
operator|+=
literal|":fieldInfosGen="
operator|+
name|fieldInfosGen
expr_stmt|;
block|}
if|if
condition|(
name|docValuesGen
operator|!=
operator|-
literal|1
condition|)
block|{
name|s
operator|+=
literal|":dvGen="
operator|+
name|docValuesGen
expr_stmt|;
block|}
return|return
name|s
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
return|return
name|toString
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SegmentCommitInfo
name|clone
parameter_list|()
block|{
name|SegmentCommitInfo
name|other
init|=
operator|new
name|SegmentCommitInfo
argument_list|(
name|info
argument_list|,
name|delCount
argument_list|,
name|delGen
argument_list|,
name|fieldInfosGen
argument_list|,
name|docValuesGen
argument_list|)
decl_stmt|;
comment|// Not clear that we need to carry over nextWriteDelGen
comment|// (i.e. do we ever clone after a failed write and
comment|// before the next successful write?), but just do it to
comment|// be safe:
name|other
operator|.
name|nextWriteDelGen
operator|=
name|nextWriteDelGen
expr_stmt|;
name|other
operator|.
name|nextWriteFieldInfosGen
operator|=
name|nextWriteFieldInfosGen
expr_stmt|;
name|other
operator|.
name|nextWriteDocValuesGen
operator|=
name|nextWriteDocValuesGen
expr_stmt|;
comment|// deep clone
for|for
control|(
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|dvUpdatesFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|other
operator|.
name|dvUpdatesFiles
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|other
operator|.
name|fieldInfosFiles
operator|.
name|addAll
argument_list|(
name|fieldInfosFiles
argument_list|)
expr_stmt|;
return|return
name|other
return|;
block|}
block|}
end_class
end_unit

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
name|ArrayList
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
name|List
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
name|MergeInfo
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
name|FixedBitSet
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
name|SetOnce
operator|.
name|AlreadySetException
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
name|SetOnce
import|;
end_import
begin_comment
comment|/**  *<p>Expert: a MergePolicy determines the sequence of  * primitive merge operations.</p>  *   *<p>Whenever the segments in an index have been altered by  * {@link IndexWriter}, either the addition of a newly  * flushed segment, addition of many segments from  * addIndexes* calls, or a previous merge that may now need  * to cascade, {@link IndexWriter} invokes {@link  * #findMerges} to give the MergePolicy a chance to pick  * merges that are now required.  This method returns a  * {@link MergeSpecification} instance describing the set of  * merges that should be done, or null if no merges are  * necessary.  When IndexWriter.forceMerge is called, it calls  * {@link #findForcedMerges(SegmentInfos,int,Map)} and the MergePolicy should  * then return the necessary merges.</p>  *  *<p>Note that the policy can return more than one merge at  * a time.  In this case, if the writer is using {@link  * SerialMergeScheduler}, the merges will be run  * sequentially but if it is using {@link  * ConcurrentMergeScheduler} they will be run concurrently.</p>  *   *<p>The default MergePolicy is {@link  * TieredMergePolicy}.</p>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MergePolicy
specifier|public
specifier|abstract
class|class
name|MergePolicy
implements|implements
name|java
operator|.
name|io
operator|.
name|Closeable
implements|,
name|Cloneable
block|{
comment|/** A map of doc IDs. */
DECL|class|DocMap
specifier|public
specifier|static
specifier|abstract
class|class
name|DocMap
block|{
comment|/** Return the new doc ID according to its old value. */
DECL|method|map
specifier|public
specifier|abstract
name|int
name|map
parameter_list|(
name|int
name|old
parameter_list|)
function_decl|;
comment|/** Useful from an assert. */
DECL|method|isConsistent
name|boolean
name|isConsistent
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
specifier|final
name|FixedBitSet
name|targets
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|target
init|=
name|map
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|target
operator|<
literal|0
operator|||
name|target
operator|>=
name|maxDoc
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"out of range: "
operator|+
name|target
operator|+
literal|" not in [0-"
operator|+
name|maxDoc
operator|+
literal|"["
assert|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|targets
operator|.
name|get
argument_list|(
name|target
argument_list|)
condition|)
block|{
assert|assert
literal|false
operator|:
name|target
operator|+
literal|" is already taken ("
operator|+
name|i
operator|+
literal|")"
assert|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/** OneMerge provides the information necessary to perform    *  an individual primitive merge operation, resulting in    *  a single new segment.  The merge spec includes the    *  subset of segments to be merged as well as whether the    *  new segment should use the compound file format. */
DECL|class|OneMerge
specifier|public
specifier|static
class|class
name|OneMerge
block|{
DECL|field|info
name|SegmentInfoPerCommit
name|info
decl_stmt|;
comment|// used by IndexWriter
DECL|field|registerDone
name|boolean
name|registerDone
decl_stmt|;
comment|// used by IndexWriter
DECL|field|mergeGen
name|long
name|mergeGen
decl_stmt|;
comment|// used by IndexWriter
DECL|field|isExternal
name|boolean
name|isExternal
decl_stmt|;
comment|// used by IndexWriter
DECL|field|maxNumSegments
name|int
name|maxNumSegments
init|=
operator|-
literal|1
decl_stmt|;
comment|// used by IndexWriter
comment|/** Estimated size in bytes of the merged segment. */
DECL|field|estimatedMergeBytes
specifier|public
specifier|volatile
name|long
name|estimatedMergeBytes
decl_stmt|;
comment|// used by IndexWriter
comment|// Sum of sizeInBytes of all SegmentInfos; set by IW.mergeInit
DECL|field|totalMergeBytes
specifier|volatile
name|long
name|totalMergeBytes
decl_stmt|;
DECL|field|readers
name|List
argument_list|<
name|SegmentReader
argument_list|>
name|readers
decl_stmt|;
comment|// used by IndexWriter
comment|/** Segments to be merged. */
DECL|field|segments
specifier|public
specifier|final
name|List
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
name|segments
decl_stmt|;
comment|/** Number of documents in the merged segment. */
DECL|field|totalDocCount
specifier|public
specifier|final
name|int
name|totalDocCount
decl_stmt|;
DECL|field|aborted
name|boolean
name|aborted
decl_stmt|;
DECL|field|error
name|Throwable
name|error
decl_stmt|;
DECL|field|paused
name|boolean
name|paused
decl_stmt|;
comment|/** Sole constructor.      * @param segments List of {@link SegmentInfoPerCommit}s      *        to be merged. */
DECL|method|OneMerge
specifier|public
name|OneMerge
parameter_list|(
name|List
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
name|segments
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|==
name|segments
operator|.
name|size
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"segments must include at least one segment"
argument_list|)
throw|;
comment|// clone the list, as the in list may be based off original SegmentInfos and may be modified
name|this
operator|.
name|segments
operator|=
operator|new
name|ArrayList
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
argument_list|(
name|segments
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentInfoPerCommit
name|info
range|:
name|segments
control|)
block|{
name|count
operator|+=
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
name|totalDocCount
operator|=
name|count
expr_stmt|;
block|}
comment|/** Expert: Get the list of readers to merge. Note that this list does not      *  necessarily match the list of segments to merge and should only be used      *  to feed SegmentMerger to initialize a merge. When a {@link OneMerge}      *  reorders doc IDs, it must override {@link #getDocMap} too so that      *  deletes that happened during the merge can be applied to the newly      *  merged segment. */
DECL|method|getMergeReaders
specifier|public
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|getMergeReaders
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|readers
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexWriter has not initialized readers from the segment infos yet"
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|readers
init|=
operator|new
name|ArrayList
argument_list|<
name|AtomicReader
argument_list|>
argument_list|(
name|this
operator|.
name|readers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|this
operator|.
name|readers
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
condition|)
block|{
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|readers
argument_list|)
return|;
block|}
comment|/** Expert: If {@link #getMergeReaders()} reorders document IDs, this method      *  must be overridden to return a mapping from the<i>natural</i> doc ID      *  (the doc ID that would result from a natural merge) to the actual doc      *  ID. This mapping is used to apply deletions that happened during the      *  merge to the new segment. */
DECL|method|getDocMap
specifier|public
name|DocMap
name|getDocMap
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
block|{
return|return
operator|new
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|map
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docID
return|;
block|}
block|}
return|;
block|}
comment|/** Record that an exception occurred while executing      *  this merge */
DECL|method|setException
specifier|synchronized
name|void
name|setException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
comment|/** Retrieve previous exception set by {@link      *  #setException}. */
DECL|method|getException
specifier|synchronized
name|Throwable
name|getException
parameter_list|()
block|{
return|return
name|error
return|;
block|}
comment|/** Mark this merge as aborted.  If this is called      *  before the merge is committed then the merge will      *  not be committed. */
DECL|method|abort
specifier|synchronized
name|void
name|abort
parameter_list|()
block|{
name|aborted
operator|=
literal|true
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/** Returns true if this merge was aborted. */
DECL|method|isAborted
specifier|synchronized
name|boolean
name|isAborted
parameter_list|()
block|{
return|return
name|aborted
return|;
block|}
comment|/** Called periodically by {@link IndexWriter} while      *  merging to see if the merge is aborted. */
DECL|method|checkAborted
specifier|public
specifier|synchronized
name|void
name|checkAborted
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|MergeAbortedException
block|{
if|if
condition|(
name|aborted
condition|)
block|{
throw|throw
operator|new
name|MergeAbortedException
argument_list|(
literal|"merge is aborted: "
operator|+
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
throw|;
block|}
while|while
condition|(
name|paused
condition|)
block|{
try|try
block|{
comment|// In theory we could wait() indefinitely, but we
comment|// do 1000 msec, defensively
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
if|if
condition|(
name|aborted
condition|)
block|{
throw|throw
operator|new
name|MergeAbortedException
argument_list|(
literal|"merge is aborted: "
operator|+
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Set or clear whether this merge is paused paused (for example      *  {@link ConcurrentMergeScheduler} will pause merges      *  if too many are running). */
DECL|method|setPause
specifier|synchronized
specifier|public
name|void
name|setPause
parameter_list|(
name|boolean
name|paused
parameter_list|)
block|{
name|this
operator|.
name|paused
operator|=
name|paused
expr_stmt|;
if|if
condition|(
operator|!
name|paused
condition|)
block|{
comment|// Wakeup merge thread, if it's waiting
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns true if this merge is paused.      *      *  @see #setPause(boolean) */
DECL|method|getPause
specifier|synchronized
specifier|public
name|boolean
name|getPause
parameter_list|()
block|{
return|return
name|paused
return|;
block|}
comment|/** Returns a readable description of the current merge      *  state. */
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numSegments
init|=
name|segments
operator|.
name|size
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
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|segments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|(
name|dir
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" into "
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxNumSegments
operator|!=
operator|-
literal|1
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" [maxNumSegments="
operator|+
name|maxNumSegments
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aborted
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" [ABORTED]"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the total size in bytes of this merge. Note that this does not      * indicate the size of the merged segment, but the      * input total size. This is only set once the merge is      * initialized by IndexWriter.      */
DECL|method|totalBytesSize
specifier|public
name|long
name|totalBytesSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|totalMergeBytes
return|;
block|}
comment|/**      * Returns the total number of documents that are included with this merge.      * Note that this does not indicate the number of documents after the merge.      * */
DECL|method|totalNumDocs
specifier|public
name|int
name|totalNumDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentInfoPerCommit
name|info
range|:
name|segments
control|)
block|{
name|total
operator|+=
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
comment|/** Return {@link MergeInfo} describing this merge. */
DECL|method|getMergeInfo
specifier|public
name|MergeInfo
name|getMergeInfo
parameter_list|()
block|{
return|return
operator|new
name|MergeInfo
argument_list|(
name|totalDocCount
argument_list|,
name|estimatedMergeBytes
argument_list|,
name|isExternal
argument_list|,
name|maxNumSegments
argument_list|)
return|;
block|}
block|}
comment|/**    * A MergeSpecification instance provides the information    * necessary to perform multiple merges.  It simply    * contains a list of {@link OneMerge} instances.    */
DECL|class|MergeSpecification
specifier|public
specifier|static
class|class
name|MergeSpecification
block|{
comment|/**      * The subset of segments to be included in the primitive merge.      */
DECL|field|merges
specifier|public
specifier|final
name|List
argument_list|<
name|OneMerge
argument_list|>
name|merges
init|=
operator|new
name|ArrayList
argument_list|<
name|OneMerge
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Sole constructor.  Use {@link      *  #add(MergePolicy.OneMerge)} to add merges. */
DECL|method|MergeSpecification
specifier|public
name|MergeSpecification
parameter_list|()
block|{     }
comment|/** Adds the provided {@link OneMerge} to this      *  specification. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|OneMerge
name|merge
parameter_list|)
block|{
name|merges
operator|.
name|add
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a description of the merges in this     *  specification. */
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"MergeSpec:\n"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|merges
operator|.
name|size
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
name|count
condition|;
name|i
operator|++
control|)
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
literal|1
operator|+
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|merges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** Exception thrown if there are any problems while    *  executing a merge. */
DECL|class|MergeException
specifier|public
specifier|static
class|class
name|MergeException
extends|extends
name|RuntimeException
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|/** Create a {@code MergeException}. */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|String
name|message
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** Create a {@code MergeException}. */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** Returns the {@link Directory} of the index that hit      *  the exception. */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
block|}
comment|/** Thrown when a merge was explicity aborted because    *  {@link IndexWriter#close(boolean)} was called with    *<code>false</code>.  Normally this exception is    *  privately caught and suppresed by {@link IndexWriter}.  */
DECL|class|MergeAbortedException
specifier|public
specifier|static
class|class
name|MergeAbortedException
extends|extends
name|IOException
block|{
comment|/** Create a {@link MergeAbortedException}. */
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|()
block|{
name|super
argument_list|(
literal|"merge is aborted"
argument_list|)
expr_stmt|;
block|}
comment|/** Create a {@link MergeAbortedException} with a      *  specified message. */
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** {@link IndexWriter} that contains this instance. */
DECL|field|writer
specifier|protected
name|SetOnce
argument_list|<
name|IndexWriter
argument_list|>
name|writer
decl_stmt|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergePolicy
name|clone
parameter_list|()
block|{
name|MergePolicy
name|clone
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|MergePolicy
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
name|e
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|clone
operator|.
name|writer
operator|=
operator|new
name|SetOnce
argument_list|<
name|IndexWriter
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * Creates a new merge policy instance. Note that if you intend to use it    * without passing it to {@link IndexWriter}, you should call    * {@link #setIndexWriter(IndexWriter)}.    */
DECL|method|MergePolicy
specifier|public
name|MergePolicy
parameter_list|()
block|{
name|writer
operator|=
operator|new
name|SetOnce
argument_list|<
name|IndexWriter
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the {@link IndexWriter} to use by this merge policy. This method is    * allowed to be called only once, and is usually set by IndexWriter. If it is    * called more than once, {@link AlreadySetException} is thrown.    *     * @see SetOnce    */
DECL|method|setIndexWriter
specifier|public
name|void
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|.
name|set
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determine what set of merge operations are now necessary on the index.    * {@link IndexWriter} calls this whenever there is a change to the segments.    * This call is always synchronized on the {@link IndexWriter} instance so    * only one thread at a time will call this method.    * @param mergeTrigger the event that triggered the merge    * @param segmentInfos    *          the total set of segments in the index    */
DECL|method|findMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in    * order to merge to<= the specified segment count. {@link IndexWriter} calls this when its    * {@link IndexWriter#forceMerge} method is called. This call is always    * synchronized on the {@link IndexWriter} instance so only one thread at a    * time will call this method.    *     * @param segmentInfos    *          the total set of segments in the index    * @param maxSegmentCount    *          requested maximum number of segments in the index (currently this    *          is always 1)    * @param segmentsToMerge    *          contains the specific SegmentInfo instances that must be merged    *          away. This may be a subset of all    *          SegmentInfos.  If the value is True for a    *          given SegmentInfo, that means this segment was    *          an original segment present in the    *          to-be-merged index; else, it was a segment    *          produced by a cascaded merge.    */
DECL|method|findForcedMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findForcedMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Map
argument_list|<
name|SegmentInfoPerCommit
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in order to expunge all    * deletes from the index.    *     * @param segmentInfos    *          the total set of segments in the index    */
DECL|method|findForcedDeletesMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Release all resources for the policy.    */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns true if a new segment (regardless of its origin) should use the compound file format.    */
DECL|method|useCompoundFile
specifier|public
specifier|abstract
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentInfoPerCommit
name|newSegment
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * MergeTrigger is passed to    * {@link MergePolicy#findMerges(MergeTrigger, SegmentInfos)} to indicate the    * event that triggered the merge.    */
DECL|enum|MergeTrigger
specifier|public
specifier|static
enum|enum
name|MergeTrigger
block|{
comment|/**      * Merge was triggered by a segment flush.      */
DECL|enum constant|SEGMENT_FLUSH
name|SEGMENT_FLUSH
block|,
comment|/**      * Merge was triggered by a full flush. Full flushes      * can be caused by a commit, NRT reader reopen or a close call on the index writer.      */
DECL|enum constant|FULL_FLUSH
name|FULL_FLUSH
block|,
comment|/**      * Merge has been triggered explicitly by the user.      */
DECL|enum constant|EXPLICIT
name|EXPLICIT
block|,
comment|/**      * Merge was triggered by a successfully finished merge.      */
DECL|enum constant|MERGE_FINISHED
name|MERGE_FINISHED
block|,   }
block|}
end_class
end_unit

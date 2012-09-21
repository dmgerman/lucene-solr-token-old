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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|HashMap
import|;
end_import
begin_comment
comment|/** This {@link MergePolicy} is used for upgrading all existing segments of   * an index when calling {@link IndexWriter#forceMerge(int)}.   * All other methods delegate to the base {@code MergePolicy} given to the constructor.   * This allows for an as-cheap-as possible upgrade of an older index by only upgrading segments that   * are created by previous Lucene versions. forceMerge does no longer really merge;   * it is just used to&quot;forceMerge&quot; older segment versions away.   *<p>In general one would use {@link IndexUpgrader}, but for a fully customizeable upgrade,   * you can use this like any other {@code MergePolicy} and call {@link IndexWriter#forceMerge(int)}:   *<pre class="prettyprint lang-java">   *  IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_XX, new KeywordAnalyzer());   *  iwc.setMergePolicy(new UpgradeIndexMergePolicy(iwc.getMergePolicy()));   *  IndexWriter w = new IndexWriter(dir, iwc);   *  w.forceMerge(1);   *  w.close();   *</pre>   *<p><b>Warning:</b> This merge policy may reorder documents if the index was partially   * upgraded before calling forceMerge (e.g., documents were added). If your application relies   * on&quot;monotonicity&quot; of doc IDs (which means that the order in which the documents   * were added to the index is preserved), do a forceMerge(1) instead. Please note, the   * delegate {@code MergePolicy} may also reorder documents.   * @lucene.experimental   * @see IndexUpgrader   */
end_comment
begin_class
DECL|class|UpgradeIndexMergePolicy
specifier|public
class|class
name|UpgradeIndexMergePolicy
extends|extends
name|MergePolicy
block|{
comment|/** Wrapped {@link MergePolicy}. */
DECL|field|base
specifier|protected
specifier|final
name|MergePolicy
name|base
decl_stmt|;
comment|/** Wrap the given {@link MergePolicy} and intercept forceMerge requests to    * only upgrade segments written with previous Lucene versions. */
DECL|method|UpgradeIndexMergePolicy
specifier|public
name|UpgradeIndexMergePolicy
parameter_list|(
name|MergePolicy
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
comment|/** Returns if the given segment should be upgraded. The default implementation    * will return {@code !Constants.LUCENE_MAIN_VERSION.equals(si.getVersion())},    * so all segments created with a different version number than this Lucene version will    * get upgraded.    */
DECL|method|shouldUpgradeSegment
specifier|protected
name|boolean
name|shouldUpgradeSegment
parameter_list|(
name|SegmentInfoPerCommit
name|si
parameter_list|)
block|{
return|return
operator|!
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
operator|.
name|equals
argument_list|(
name|si
operator|.
name|info
operator|.
name|getVersion
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setIndexWriter
specifier|public
name|void
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|super
operator|.
name|setIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|base
operator|.
name|setIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|findMerges
argument_list|(
name|segmentInfos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedMerges
specifier|public
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
block|{
comment|// first find all old segments
specifier|final
name|Map
argument_list|<
name|SegmentInfoPerCommit
argument_list|,
name|Boolean
argument_list|>
name|oldSegments
init|=
operator|new
name|HashMap
argument_list|<
name|SegmentInfoPerCommit
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SegmentInfoPerCommit
name|si
range|:
name|segmentInfos
control|)
block|{
specifier|final
name|Boolean
name|v
init|=
name|segmentsToMerge
operator|.
name|get
argument_list|(
name|si
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|shouldUpgradeSegment
argument_list|(
name|si
argument_list|)
condition|)
block|{
name|oldSegments
operator|.
name|put
argument_list|(
name|si
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"findForcedMerges: segmentsToUpgrade="
operator|+
name|oldSegments
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldSegments
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
name|MergeSpecification
name|spec
init|=
name|base
operator|.
name|findForcedMerges
argument_list|(
name|segmentInfos
argument_list|,
name|maxSegmentCount
argument_list|,
name|oldSegments
argument_list|)
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
block|{
comment|// remove all segments that are in merge specification from oldSegments,
comment|// the resulting set contains all segments that are left over
comment|// and will be merged to one additional segment:
for|for
control|(
specifier|final
name|OneMerge
name|om
range|:
name|spec
operator|.
name|merges
control|)
block|{
name|oldSegments
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|om
operator|.
name|segments
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|oldSegments
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"findForcedMerges: "
operator|+
name|base
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" does not want to merge all old segments, merge remaining ones into new segment: "
operator|+
name|oldSegments
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
name|newInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SegmentInfoPerCommit
name|si
range|:
name|segmentInfos
control|)
block|{
if|if
condition|(
name|oldSegments
operator|.
name|containsKey
argument_list|(
name|si
argument_list|)
condition|)
block|{
name|newInfos
operator|.
name|add
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add the final merge
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
block|{
name|spec
operator|=
operator|new
name|MergeSpecification
argument_list|()
expr_stmt|;
block|}
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|newInfos
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|spec
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedDeletesMerges
specifier|public
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|findForcedDeletesMerges
argument_list|(
name|segmentInfos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|useCompoundFile
specifier|public
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
block|{
return|return
name|base
operator|.
name|useCompoundFile
argument_list|(
name|segments
argument_list|,
name|newSegment
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|base
operator|.
name|close
argument_list|()
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
literal|"["
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"->"
operator|+
name|base
operator|+
literal|"]"
return|;
block|}
DECL|method|verbose
specifier|private
name|boolean
name|verbose
parameter_list|()
block|{
specifier|final
name|IndexWriter
name|w
init|=
name|writer
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|w
operator|!=
literal|null
operator|&&
name|w
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"UPGMP"
argument_list|)
return|;
block|}
DECL|method|message
specifier|private
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|writer
operator|.
name|get
argument_list|()
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"UPGMP"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

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
name|Map
import|;
end_import
begin_comment
comment|/**  * A {@link MergePolicy} which never returns merges to execute. Use it if you  * want to prevent segment merges.  */
end_comment
begin_class
DECL|class|NoMergePolicy
specifier|public
specifier|final
class|class
name|NoMergePolicy
extends|extends
name|MergePolicy
block|{
comment|/** Singleton instance. */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|MergePolicy
name|INSTANCE
init|=
operator|new
name|NoMergePolicy
argument_list|()
decl_stmt|;
DECL|method|NoMergePolicy
specifier|private
name|NoMergePolicy
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
return|return
literal|null
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
name|SegmentCommitInfo
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
return|return
literal|null
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
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
return|return
literal|null
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
name|SegmentCommitInfo
name|newSegment
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
return|return
name|newSegment
operator|.
name|info
operator|.
name|getUseCompoundFile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|protected
name|long
name|size
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
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
literal|"NoMergePolicy"
return|;
block|}
block|}
end_class
end_unit

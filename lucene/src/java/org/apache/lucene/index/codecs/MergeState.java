begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
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
name|List
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
name|MergePolicy
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
name|PayloadProcessorProvider
operator|.
name|DirPayloadProcessor
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
name|PayloadProcessorProvider
operator|.
name|PayloadProcessor
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
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|/** Holds common state used during segment merging  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|MergeState
specifier|public
class|class
name|MergeState
block|{
DECL|field|fieldInfos
specifier|public
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|readers
specifier|public
name|List
argument_list|<
name|IndexReader
argument_list|>
name|readers
decl_stmt|;
comment|// Readers being merged
DECL|field|readerCount
specifier|public
name|int
name|readerCount
decl_stmt|;
comment|// Number of readers being merged
DECL|field|docMaps
specifier|public
name|int
index|[]
index|[]
name|docMaps
decl_stmt|;
comment|// Maps docIDs around deletions
DECL|field|delCounts
specifier|public
name|int
index|[]
name|delCounts
decl_stmt|;
comment|// Deletion count per reader
DECL|field|docBase
specifier|public
name|int
index|[]
name|docBase
decl_stmt|;
comment|// New docID base per reader
DECL|field|mergedDocCount
specifier|public
name|int
name|mergedDocCount
decl_stmt|;
comment|// Total # merged docs
DECL|field|multiDeletedDocs
specifier|public
name|Bits
name|multiDeletedDocs
decl_stmt|;
DECL|field|checkAbort
specifier|public
name|CheckAbort
name|checkAbort
decl_stmt|;
comment|// Updated per field;
DECL|field|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
decl_stmt|;
comment|// Used to process payloads
DECL|field|hasPayloadProcessorProvider
specifier|public
name|boolean
name|hasPayloadProcessorProvider
decl_stmt|;
DECL|field|dirPayloadProcessor
specifier|public
name|DirPayloadProcessor
index|[]
name|dirPayloadProcessor
decl_stmt|;
DECL|field|currentPayloadProcessor
specifier|public
name|PayloadProcessor
index|[]
name|currentPayloadProcessor
decl_stmt|;
DECL|class|CheckAbort
specifier|public
specifier|static
class|class
name|CheckAbort
block|{
DECL|field|workCount
specifier|private
name|double
name|workCount
decl_stmt|;
DECL|field|merge
specifier|private
name|MergePolicy
operator|.
name|OneMerge
name|merge
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|method|CheckAbort
specifier|public
name|CheckAbort
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|merge
operator|=
name|merge
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/**      * Records the fact that roughly units amount of work      * have been done since this method was last called.      * When adding time-consuming code into SegmentMerger,      * you should test different values for units to ensure      * that the time in between calls to merge.checkAborted      * is up to ~ 1 second.      */
DECL|method|work
specifier|public
name|void
name|work
parameter_list|(
name|double
name|units
parameter_list|)
throws|throws
name|MergePolicy
operator|.
name|MergeAbortedException
block|{
name|workCount
operator|+=
name|units
expr_stmt|;
if|if
condition|(
name|workCount
operator|>=
literal|10000.0
condition|)
block|{
name|merge
operator|.
name|checkAborted
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|workCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit

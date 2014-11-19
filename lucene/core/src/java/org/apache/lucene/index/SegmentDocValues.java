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
name|HashMap
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
name|codecs
operator|.
name|DocValuesProducer
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
name|util
operator|.
name|IOUtils
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
name|RefCount
import|;
end_import
begin_comment
comment|/**  * Manages the {@link DocValuesProducer} held by {@link SegmentReader} and  * keeps track of their reference counting.  */
end_comment
begin_class
DECL|class|SegmentDocValues
specifier|final
class|class
name|SegmentDocValues
block|{
DECL|field|genDVProducers
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
argument_list|>
name|genDVProducers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|newDocValuesProducer
specifier|private
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|newDocValuesProducer
parameter_list|(
name|SegmentCommitInfo
name|si
parameter_list|,
name|Directory
name|dir
parameter_list|,
specifier|final
name|Long
name|gen
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dvDir
init|=
name|dir
decl_stmt|;
name|String
name|segmentSuffix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|gen
operator|.
name|longValue
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|dvDir
operator|=
name|si
operator|.
name|info
operator|.
name|dir
expr_stmt|;
comment|// gen'd files are written outside CFS, so use SegInfo directory
name|segmentSuffix
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|gen
operator|.
name|longValue
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
expr_stmt|;
block|}
comment|// set SegmentReadState to list only the fields that are relevant to that gen
name|SegmentReadState
name|srs
init|=
operator|new
name|SegmentReadState
argument_list|(
name|dvDir
argument_list|,
name|si
operator|.
name|info
argument_list|,
name|infos
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|segmentSuffix
argument_list|)
decl_stmt|;
name|DocValuesFormat
name|dvFormat
init|=
name|si
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
return|return
operator|new
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
argument_list|(
name|dvFormat
operator|.
name|fieldsProducer
argument_list|(
name|srs
argument_list|)
argument_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
annotation|@
name|Override
specifier|protected
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|SegmentDocValues
operator|.
name|this
init|)
block|{
name|genDVProducers
operator|.
name|remove
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/** Returns the {@link DocValuesProducer} for the given generation. */
DECL|method|getDocValuesProducer
specifier|synchronized
name|DocValuesProducer
name|getDocValuesProducer
parameter_list|(
name|long
name|gen
parameter_list|,
name|SegmentCommitInfo
name|si
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
init|=
name|genDVProducers
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
if|if
condition|(
name|dvp
operator|==
literal|null
condition|)
block|{
name|dvp
operator|=
name|newDocValuesProducer
argument_list|(
name|si
argument_list|,
name|dir
argument_list|,
name|gen
argument_list|,
name|infos
argument_list|)
expr_stmt|;
assert|assert
name|dvp
operator|!=
literal|null
assert|;
name|genDVProducers
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|dvp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dvp
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
return|return
name|dvp
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Decrement the reference count of the given {@link DocValuesProducer}    * generations.     */
DECL|method|decRef
specifier|synchronized
name|void
name|decRef
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|dvProducersGens
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|t
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Long
name|gen
range|:
name|dvProducersGens
control|)
block|{
name|RefCount
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvp
init|=
name|genDVProducers
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
assert|assert
name|dvp
operator|!=
literal|null
operator|:
literal|"gen="
operator|+
name|gen
assert|;
try|try
block|{
name|dvp
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|=
name|th
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

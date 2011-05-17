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
name|PrintStream
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
name|AtomicLong
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
name|codecs
operator|.
name|PerDocConsumer
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
comment|/**  * Encapsulates all necessary state to initiate a {@link PerDocConsumer} and  * create all necessary files in order to consume and merge per-document values.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PerDocWriteState
specifier|public
class|class
name|PerDocWriteState
block|{
DECL|field|infoStream
specifier|public
specifier|final
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|directory
specifier|public
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segmentName
specifier|public
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|bytesUsed
specifier|public
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|field|segmentCodecs
specifier|public
specifier|final
name|SegmentCodecs
name|segmentCodecs
decl_stmt|;
DECL|field|codecId
specifier|public
specifier|final
name|int
name|codecId
decl_stmt|;
DECL|method|PerDocWriteState
name|PerDocWriteState
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|,
name|int
name|codecId
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentName
operator|=
name|segmentName
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|segmentCodecs
operator|=
name|fieldInfos
operator|.
name|buildSegmentCodecs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|codecId
operator|=
name|codecId
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
DECL|method|PerDocWriteState
name|PerDocWriteState
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|segmentCodecs
operator|=
name|state
operator|.
name|segmentCodecs
expr_stmt|;
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|codecId
operator|=
name|state
operator|.
name|codecId
expr_stmt|;
name|bytesUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|PerDocWriteState
name|PerDocWriteState
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|int
name|codecId
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|this
operator|.
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|segmentCodecs
operator|=
name|state
operator|.
name|segmentCodecs
expr_stmt|;
name|this
operator|.
name|codecId
operator|=
name|codecId
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|state
operator|.
name|bytesUsed
expr_stmt|;
block|}
DECL|method|codecIdAsString
specifier|public
name|String
name|codecIdAsString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|codecId
return|;
block|}
block|}
end_class
end_unit

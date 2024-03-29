begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package
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
name|codecs
operator|.
name|NormsConsumer
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
name|NormsFormat
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
name|NormsProducer
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
name|SegmentReadState
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
name|SegmentWriteState
import|;
end_import
begin_comment
comment|/**  * Lucene 5.0 Score normalization format.  * @deprecated Only for reading old 5.0-5.2 segments  */
end_comment
begin_class
annotation|@
name|Deprecated
class|class
DECL|class|Lucene50NormsFormat
name|Lucene50NormsFormat
extends|extends
name|NormsFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene50NormsFormat
specifier|public
name|Lucene50NormsFormat
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene50NormsProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene50NormsData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"nvd"
decl_stmt|;
DECL|field|METADATA_CODEC
specifier|static
specifier|final
name|String
name|METADATA_CODEC
init|=
literal|"Lucene50NormsMetadata"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|"nvm"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|DELTA_COMPRESSED
specifier|static
specifier|final
name|byte
name|DELTA_COMPRESSED
init|=
literal|0
decl_stmt|;
DECL|field|TABLE_COMPRESSED
specifier|static
specifier|final
name|byte
name|TABLE_COMPRESSED
init|=
literal|1
decl_stmt|;
DECL|field|CONST_COMPRESSED
specifier|static
specifier|final
name|byte
name|CONST_COMPRESSED
init|=
literal|2
decl_stmt|;
DECL|field|UNCOMPRESSED
specifier|static
specifier|final
name|byte
name|UNCOMPRESSED
init|=
literal|3
decl_stmt|;
DECL|field|INDIRECT
specifier|static
specifier|final
name|byte
name|INDIRECT
init|=
literal|4
decl_stmt|;
DECL|field|PATCHED_BITSET
specifier|static
specifier|final
name|byte
name|PATCHED_BITSET
init|=
literal|5
decl_stmt|;
DECL|field|PATCHED_TABLE
specifier|static
specifier|final
name|byte
name|PATCHED_TABLE
init|=
literal|6
decl_stmt|;
block|}
end_class
end_unit

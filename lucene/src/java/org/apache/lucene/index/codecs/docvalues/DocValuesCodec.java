begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.docvalues
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
operator|.
name|docvalues
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|IndexFileNames
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
name|PerDocWriteState
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
name|SegmentInfo
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
name|Codec
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|index
operator|.
name|codecs
operator|.
name|PerDocValues
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
name|values
operator|.
name|Writer
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * A codec that adds DocValues support to a given codec transparently.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesCodec
specifier|public
class|class
name|DocValuesCodec
extends|extends
name|Codec
block|{
DECL|field|other
specifier|private
specifier|final
name|Codec
name|other
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|method|DocValuesCodec
specifier|public
name|DocValuesCodec
parameter_list|(
name|Codec
name|other
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|other
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
DECL|method|DocValuesCodec
specifier|public
name|DocValuesCodec
parameter_list|(
name|Codec
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
specifier|final
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PerDocConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValuesConsumer
name|consumer
init|=
name|Writer
operator|.
name|create
argument_list|(
name|field
operator|.
name|getDocValues
argument_list|()
argument_list|,
name|docValuesId
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|codecId
argument_list|,
name|field
operator|.
name|number
argument_list|)
argument_list|,
comment|// TODO can we have a compound file per segment and codec for
comment|// docvalues?
name|state
operator|.
name|directory
argument_list|,
name|comparator
argument_list|,
name|state
operator|.
name|bytesUsed
argument_list|)
decl_stmt|;
return|return
name|consumer
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocValues
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|DocValuesProducerBase
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|codecId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|new
name|DocValuesProducerBase
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|codecId
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|other
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|other
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|method|docValuesId
specifier|static
name|String
name|docValuesId
parameter_list|(
name|String
name|segmentsName
parameter_list|,
name|int
name|codecID
parameter_list|,
name|int
name|fieldId
parameter_list|)
block|{
return|return
name|segmentsName
operator|+
literal|"_"
operator|+
name|codecID
operator|+
literal|"-"
operator|+
name|fieldId
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|int
name|codecId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfos
name|fieldInfos
init|=
name|segmentInfo
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
name|boolean
name|indexed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getCodecId
argument_list|()
operator|==
name|codecId
condition|)
block|{
name|indexed
operator||=
name|fieldInfo
operator|.
name|isIndexed
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|String
name|filename
init|=
name|docValuesId
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|codecId
argument_list|,
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|fieldInfo
operator|.
name|getDocValues
argument_list|()
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|dir
operator|.
name|fileExists
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|)
assert|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
case|case
name|INTS
case|:
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|dir
operator|.
name|fileExists
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|filename
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|)
assert|;
break|break;
default|default:
assert|assert
literal|false
assert|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|indexed
condition|)
block|{
name|other
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecId
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|other
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

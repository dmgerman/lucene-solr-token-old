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
name|HashMap
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
name|docvalues
operator|.
name|DocValuesConsumer
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
name|ValuesAttribute
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
comment|/**  * This is a DocConsumer that gathers all fields under the  * same name, and calls per-field consumers to process field  * by field.  This class doesn't doesn't do any "real" work  * of its own: it just forwards the fields to a  * DocFieldConsumer.  */
end_comment
begin_class
DECL|class|DocFieldProcessor
specifier|final
class|class
name|DocFieldProcessor
extends|extends
name|DocConsumer
block|{
DECL|field|docWriter
specifier|final
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
DECL|field|consumer
specifier|final
name|DocFieldConsumer
name|consumer
decl_stmt|;
DECL|field|fieldsWriter
specifier|final
name|StoredFieldsWriter
name|fieldsWriter
decl_stmt|;
DECL|field|docValues
specifier|final
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesConsumer
argument_list|>
name|docValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fieldsConsumer
specifier|private
name|FieldsConsumer
name|fieldsConsumer
decl_stmt|;
comment|// TODO this should be encapsulated in DocumentsWriter
DECL|method|docValuesConsumer
specifier|synchronized
name|DocValuesConsumer
name|docValuesConsumer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|,
name|String
name|name
parameter_list|,
name|ValuesAttribute
name|attr
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesConsumer
name|valuesConsumer
decl_stmt|;
if|if
condition|(
operator|(
name|valuesConsumer
operator|=
name|docValues
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|fieldInfo
operator|.
name|setDocValues
argument_list|(
name|attr
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsConsumer
operator|==
literal|null
condition|)
block|{
comment|/* nocommit -- this is a hack and only works since DocValuesCodec supports initializing the FieldsConsumer twice.          * we need to find a way that allows us to obtain a FieldsConsumer per DocumentsWriter. Currently some codecs rely on           * the SegmentsWriteState passed in right at the moment when the segment is flushed (doccount etc) but we need the consumer earlier           * to support docvalues and later on stored fields too.            */
name|SegmentWriteState
name|state
init|=
name|docWriter
operator|.
name|segWriteState
argument_list|()
decl_stmt|;
name|fieldsConsumer
operator|=
name|state
operator|.
name|codec
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
name|valuesConsumer
operator|=
name|fieldsConsumer
operator|.
name|addValuesField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|docValues
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|valuesConsumer
argument_list|)
expr_stmt|;
block|}
return|return
name|valuesConsumer
return|;
block|}
DECL|method|DocFieldProcessor
specifier|public
name|DocFieldProcessor
parameter_list|(
name|DocumentsWriter
name|docWriter
parameter_list|,
name|DocFieldConsumer
name|consumer
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|consumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|fieldsWriter
operator|=
operator|new
name|StoredFieldsWriter
argument_list|(
name|docWriter
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closeDocStore
specifier|public
name|void
name|closeDocStore
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|fieldsWriter
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Collection
argument_list|<
name|DocConsumerPerThread
argument_list|>
name|threads
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|DocFieldConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
argument_list|>
name|childThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|<
name|DocFieldConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DocConsumerPerThread
name|thread
range|:
name|threads
control|)
block|{
name|DocFieldProcessorPerThread
name|perThread
init|=
operator|(
name|DocFieldProcessorPerThread
operator|)
name|thread
decl_stmt|;
name|childThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|consumer
argument_list|,
name|perThread
operator|.
name|fields
argument_list|()
argument_list|)
expr_stmt|;
name|perThread
operator|.
name|trimFields
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
name|fieldsWriter
operator|.
name|flush
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|flush
argument_list|(
name|childThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
for|for
control|(
name|DocValuesConsumer
name|p
range|:
name|docValues
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|finish
argument_list|(
name|state
operator|.
name|numDocs
argument_list|)
expr_stmt|;
name|p
operator|.
name|files
argument_list|(
name|state
operator|.
name|flushedFiles
argument_list|)
expr_stmt|;
block|}
block|}
name|docValues
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldsConsumer
operator|!=
literal|null
condition|)
block|{
name|fieldsConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// nocommit this should go away
name|fieldsConsumer
operator|=
literal|null
expr_stmt|;
block|}
comment|// Important to save after asking consumer to flush so
comment|// consumer can alter the FieldInfo* if necessary.  EG,
comment|// FreqProxTermsWriter does this with
comment|// FieldInfo.storePayload.
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|fieldInfos
operator|.
name|write
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|fieldsWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
return|return
name|consumer
operator|.
name|freeRAM
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addThread
specifier|public
name|DocConsumerPerThread
name|addThread
parameter_list|(
name|DocumentsWriterThreadState
name|threadState
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocFieldProcessorPerThread
argument_list|(
name|threadState
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class
end_unit

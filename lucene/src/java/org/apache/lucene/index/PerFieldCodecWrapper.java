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
name|ArrayList
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
name|Iterator
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|codecs
operator|.
name|TermsConsumer
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
name|DocValues
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
comment|/**  * Enables native per field codec support. This class selects the codec used to  * write a field depending on the provided {@link SegmentCodecs}. For each field  * seen it resolves the codec based on the {@link FieldInfo#codecId} which is  * only valid during a segment merge. See {@link SegmentCodecs} javadoc for  * details.  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|PerFieldCodecWrapper
specifier|final
class|class
name|PerFieldCodecWrapper
extends|extends
name|Codec
block|{
DECL|field|segmentCodecs
specifier|private
specifier|final
name|SegmentCodecs
name|segmentCodecs
decl_stmt|;
DECL|method|PerFieldCodecWrapper
name|PerFieldCodecWrapper
parameter_list|(
name|SegmentCodecs
name|segmentCodecs
parameter_list|)
block|{
name|name
operator|=
literal|"PerField"
expr_stmt|;
name|this
operator|.
name|segmentCodecs
operator|=
name|segmentCodecs
expr_stmt|;
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
operator|new
name|FieldsWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|FieldsWriter
specifier|private
class|class
name|FieldsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|consumers
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|FieldsConsumer
argument_list|>
name|consumers
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldsConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsWriter
specifier|public
name|FieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|segmentCodecs
operator|==
name|state
operator|.
name|segmentCodecs
assert|;
specifier|final
name|Codec
index|[]
name|codecs
init|=
name|segmentCodecs
operator|.
name|codecs
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
name|codecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|codecs
index|[
name|i
index|]
operator|.
name|fieldsConsumer
argument_list|(
operator|new
name|SegmentWriteState
argument_list|(
name|state
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|getCodecId
argument_list|()
operator|!=
name|FieldInfo
operator|.
name|UNASSIGNED_CODEC_ID
assert|;
specifier|final
name|FieldsConsumer
name|fields
init|=
name|consumers
operator|.
name|get
argument_list|(
name|field
operator|.
name|getCodecId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fields
operator|.
name|addField
argument_list|(
name|field
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
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|FieldsConsumer
argument_list|>
name|it
init|=
name|consumers
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
block|}
DECL|class|FieldsReader
specifier|private
class|class
name|FieldsReader
extends|extends
name|FieldsProducer
block|{
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|Codec
argument_list|,
name|FieldsProducer
argument_list|>
name|producers
init|=
operator|new
name|HashMap
argument_list|<
name|Codec
argument_list|,
name|FieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
assert|assert
name|fi
operator|.
name|getCodecId
argument_list|()
operator|!=
name|FieldInfo
operator|.
name|UNASSIGNED_CODEC_ID
assert|;
name|Codec
name|codec
init|=
name|segmentCodecs
operator|.
name|codecs
index|[
name|fi
operator|.
name|getCodecId
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|producers
operator|.
name|containsKey
argument_list|(
name|codec
argument_list|)
condition|)
block|{
name|producers
operator|.
name|put
argument_list|(
name|codec
argument_list|,
name|codec
operator|.
name|fieldsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|dir
argument_list|,
name|si
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|indexDivisor
argument_list|,
name|fi
operator|.
name|getCodecId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|codecs
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|producers
operator|.
name|get
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// If we hit exception (eg, IOE because writer was
comment|// committing, or, for any other reason) we must
comment|// go back and close all FieldsProducers we opened:
for|for
control|(
name|FieldsProducer
name|fp
range|:
name|producers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|fp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress all exceptions here so we continue
comment|// to throw the original one
block|}
block|}
block|}
block|}
block|}
DECL|class|FieldsIterator
specifier|private
specifier|final
class|class
name|FieldsIterator
extends|extends
name|FieldsEnum
block|{
DECL|field|it
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
DECL|field|current
specifier|private
name|String
name|current
decl_stmt|;
DECL|method|FieldsIterator
specifier|public
name|FieldsIterator
parameter_list|()
block|{
name|it
operator|=
name|fields
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|codecs
operator|.
name|get
argument_list|(
name|current
argument_list|)
operator|.
name|terms
argument_list|(
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
return|return
name|terms
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldsProducer
name|fields
init|=
name|codecs
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
name|fields
operator|.
name|terms
argument_list|(
name|field
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
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|FieldsProducer
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|FieldsProducer
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|loadTermsIndex
argument_list|(
name|indexDivisor
argument_list|)
expr_stmt|;
block|}
block|}
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
operator|new
name|FieldsReader
argument_list|(
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
name|segmentInfo
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
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
name|info
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
comment|// ignore codecid since segmentCodec will assign it per codec
name|segmentCodecs
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
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
for|for
control|(
name|Codec
name|codec
range|:
name|segmentCodecs
operator|.
name|codecs
control|)
block|{
name|codec
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PerDocConsumers
argument_list|(
name|state
argument_list|)
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
return|return
operator|new
name|PerDocProducers
argument_list|(
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
name|segmentInfo
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
return|;
block|}
DECL|class|PerDocProducers
specifier|private
specifier|final
class|class
name|PerDocProducers
extends|extends
name|PerDocValues
block|{
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PerDocValues
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PerDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|PerDocProducers
specifier|public
name|PerDocProducers
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|Codec
argument_list|,
name|PerDocValues
argument_list|>
name|producers
init|=
operator|new
name|HashMap
argument_list|<
name|Codec
argument_list|,
name|PerDocValues
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
assert|assert
name|fi
operator|.
name|getCodecId
argument_list|()
operator|!=
name|FieldInfo
operator|.
name|UNASSIGNED_CODEC_ID
assert|;
name|Codec
name|codec
init|=
name|segmentCodecs
operator|.
name|codecs
index|[
name|fi
operator|.
name|getCodecId
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|producers
operator|.
name|containsKey
argument_list|(
name|codec
argument_list|)
condition|)
block|{
name|producers
operator|.
name|put
argument_list|(
name|codec
argument_list|,
name|codec
operator|.
name|docsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|dir
argument_list|,
name|si
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|indexDivisor
argument_list|,
name|fi
operator|.
name|getCodecId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|codecs
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|producers
operator|.
name|get
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// If we hit exception (eg, IOE because writer was
comment|// committing, or, for any other reason) we must
comment|// go back and close all FieldsProducers we opened:
for|for
control|(
name|PerDocValues
name|producer
range|:
name|producers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress all exceptions here so we continue
comment|// to throw the original one
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PerDocValues
name|perDocProducer
init|=
name|codecs
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|perDocProducer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|perDocProducer
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Iterator
argument_list|<
name|PerDocValues
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|PerDocValues
name|next
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
block|}
DECL|class|PerDocConsumers
specifier|private
specifier|final
class|class
name|PerDocConsumers
extends|extends
name|PerDocConsumer
block|{
DECL|field|consumers
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|PerDocConsumer
argument_list|>
name|consumers
init|=
operator|new
name|ArrayList
argument_list|<
name|PerDocConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|PerDocConsumers
specifier|public
name|PerDocConsumers
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|segmentCodecs
operator|==
name|state
operator|.
name|segmentCodecs
assert|;
specifier|final
name|Codec
index|[]
name|codecs
init|=
name|segmentCodecs
operator|.
name|codecs
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
name|codecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|codecs
index|[
name|i
index|]
operator|.
name|docsConsumer
argument_list|(
operator|new
name|PerDocWriteState
argument_list|(
name|state
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|PerDocConsumer
argument_list|>
name|it
init|=
name|consumers
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|PerDocConsumer
name|next
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|addValuesField
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
assert|assert
name|field
operator|.
name|getCodecId
argument_list|()
operator|!=
name|FieldInfo
operator|.
name|UNASSIGNED_CODEC_ID
assert|;
specifier|final
name|PerDocConsumer
name|perDoc
init|=
name|consumers
operator|.
name|get
argument_list|(
name|field
operator|.
name|getCodecId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|perDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|perDoc
operator|.
name|addValuesField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit

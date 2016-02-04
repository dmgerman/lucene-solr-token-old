begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|Collections
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
name|CodecUtil
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
name|codecs
operator|.
name|PostingsFormat
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
name|Terms
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
name|ChecksumIndexInput
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
name|IndexInput
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
name|Accountable
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
name|Accountables
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionPostingsFormat
operator|.
name|CODEC_NAME
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionPostingsFormat
operator|.
name|COMPLETION_CODEC_VERSION
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionPostingsFormat
operator|.
name|COMPLETION_VERSION_CURRENT
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionPostingsFormat
operator|.
name|DICT_EXTENSION
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionPostingsFormat
operator|.
name|INDEX_EXTENSION
import|;
end_import
begin_comment
comment|/**  *<p>  * Completion index (.cmp) is opened and read at instantiation to read in {@link SuggestField}  * numbers and their FST offsets in the Completion dictionary (.lkp).  *</p>  *<p>  * Completion dictionary (.lkp) is opened at instantiation and a field's FST is loaded  * into memory the first time it is requested via {@link #terms(String)}.  *</p>  *<p>  * NOTE: Only the footer is validated for Completion dictionary (.lkp) and not the checksum due  * to random access pattern and checksum validation being too costly at instantiation  *</p>  *  */
end_comment
begin_class
DECL|class|CompletionFieldsProducer
specifier|final
class|class
name|CompletionFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|delegateFieldsProducer
specifier|private
name|FieldsProducer
name|delegateFieldsProducer
decl_stmt|;
DECL|field|readers
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CompletionsTermsReader
argument_list|>
name|readers
decl_stmt|;
DECL|field|dictIn
specifier|private
name|IndexInput
name|dictIn
decl_stmt|;
comment|// copy ctr for merge instance
DECL|method|CompletionFieldsProducer
specifier|private
name|CompletionFieldsProducer
parameter_list|(
name|FieldsProducer
name|delegateFieldsProducer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CompletionsTermsReader
argument_list|>
name|readers
parameter_list|)
block|{
name|this
operator|.
name|delegateFieldsProducer
operator|=
name|delegateFieldsProducer
expr_stmt|;
name|this
operator|.
name|readers
operator|=
name|readers
expr_stmt|;
block|}
DECL|method|CompletionFieldsProducer
name|CompletionFieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|indexFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|delegateFieldsProducer
operator|=
literal|null
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|index
init|=
name|state
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|indexFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
init|)
block|{
comment|// open up dict file containing all fsts
name|String
name|dictFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|DICT_EXTENSION
argument_list|)
decl_stmt|;
name|dictIn
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|dictFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|dictIn
argument_list|,
name|CODEC_NAME
argument_list|,
name|COMPLETION_CODEC_VERSION
argument_list|,
name|COMPLETION_VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
comment|// just validate the footer for the dictIn
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|dictIn
argument_list|)
expr_stmt|;
comment|// open up index file (fieldNumber, offset)
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|index
argument_list|,
name|CODEC_NAME
argument_list|,
name|COMPLETION_CODEC_VERSION
argument_list|,
name|COMPLETION_VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
comment|// load delegate PF
name|PostingsFormat
name|delegatePostingsFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|index
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|delegateFieldsProducer
operator|=
name|delegatePostingsFormat
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// read suggest field numbers and their offsets in the terms file from index
name|int
name|numFields
init|=
name|index
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|readers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|numFields
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldNumber
init|=
name|index
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|long
name|offset
init|=
name|index
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|long
name|minWeight
init|=
name|index
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|long
name|maxWeight
init|=
name|index
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|byte
name|type
init|=
name|index
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|state
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
comment|// we don't load the FST yet
name|readers
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|CompletionsTermsReader
argument_list|(
name|dictIn
argument_list|,
name|offset
argument_list|,
name|minWeight
argument_list|,
name|maxWeight
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|delegateFieldsProducer
argument_list|,
name|dictIn
argument_list|)
expr_stmt|;
block|}
block|}
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|delegateFieldsProducer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dictIn
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|delegateFieldsProducer
argument_list|,
name|dictIn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|delegateFieldsProducer
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
comment|// TODO: checkIntegrity should checksum the dictionary and index
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|FieldsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompletionFieldsProducer
argument_list|(
name|delegateFieldsProducer
argument_list|,
name|readers
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|ramBytesUsed
init|=
name|delegateFieldsProducer
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
for|for
control|(
name|CompletionsTermsReader
name|reader
range|:
name|readers
operator|.
name|values
argument_list|()
control|)
block|{
name|ramBytesUsed
operator|+=
name|reader
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|accountableList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CompletionsTermsReader
argument_list|>
name|readerEntry
range|:
name|readers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|accountableList
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
name|readerEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|readerEntry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|accountableList
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|readers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
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
name|Terms
name|terms
init|=
name|delegateFieldsProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|CompletionTerms
argument_list|(
name|terms
argument_list|,
name|readers
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|readers
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class
end_unit

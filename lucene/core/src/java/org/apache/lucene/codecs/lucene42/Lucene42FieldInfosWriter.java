begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|FieldInfosWriter
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
operator|.
name|DocValuesType
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
operator|.
name|IndexOptions
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
name|store
operator|.
name|IndexOutput
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
begin_comment
comment|/**  * Lucene 4.2 FieldInfos writer.  *   * @see Lucene42FieldInfosFormat  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene42FieldInfosWriter
specifier|public
class|class
name|Lucene42FieldInfosWriter
extends|extends
name|FieldInfosWriter
block|{
comment|/** Sole constructor. */
DECL|method|Lucene42FieldInfosWriter
specifier|public
name|Lucene42FieldInfosWriter
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|FieldInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|IndexOptions
name|indexOptions
init|=
name|fi
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|hasVectors
argument_list|()
condition|)
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|STORE_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|omitsNorms
argument_list|()
condition|)
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_NORMS
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|hasPayloads
argument_list|()
condition|)
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|STORE_PAYLOADS
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|IS_INDEXED
expr_stmt|;
assert|assert
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
operator|||
operator|!
name|fi
operator|.
name|hasPayloads
argument_list|()
assert|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
condition|)
block|{
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|STORE_OFFSETS_IN_POSTINGS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
name|bits
operator||=
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_POSITIONS
expr_stmt|;
block|}
block|}
name|output
operator|.
name|writeString
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|fi
operator|.
name|number
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
comment|// pack the DV types in one byte
specifier|final
name|byte
name|dv
init|=
name|docValuesByte
argument_list|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|nrm
init|=
name|docValuesByte
argument_list|(
name|fi
operator|.
name|getNormType
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|dv
operator|&
operator|(
operator|~
literal|0xF
operator|)
operator|)
operator|==
literal|0
operator|&&
operator|(
name|nrm
operator|&
operator|(
operator|~
literal|0x0F
operator|)
operator|)
operator|==
literal|0
assert|;
name|byte
name|val
init|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|&
operator|(
operator|(
name|nrm
operator|<<
literal|4
operator|)
operator||
name|dv
operator|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|fi
operator|.
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
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
name|success
condition|)
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|docValuesByte
specifier|private
specifier|static
name|byte
name|docValuesByte
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|BINARY
condition|)
block|{
return|return
literal|2
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
return|return
literal|3
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit

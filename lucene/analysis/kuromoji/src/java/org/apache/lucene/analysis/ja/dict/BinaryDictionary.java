begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
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
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import
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
name|io
operator|.
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
name|DataInput
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
name|InputStreamDataInput
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
name|util
operator|.
name|IntsRef
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
comment|/**  * Base class for a binary-encoded in-memory dictionary.  */
end_comment
begin_class
DECL|class|BinaryDictionary
specifier|public
specifier|abstract
class|class
name|BinaryDictionary
implements|implements
name|Dictionary
block|{
DECL|field|DICT_FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|DICT_FILENAME_SUFFIX
init|=
literal|"$buffer.dat"
decl_stmt|;
DECL|field|TARGETMAP_FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|TARGETMAP_FILENAME_SUFFIX
init|=
literal|"$targetMap.dat"
decl_stmt|;
DECL|field|POSDICT_FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|POSDICT_FILENAME_SUFFIX
init|=
literal|"$posDict.dat"
decl_stmt|;
DECL|field|DICT_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|DICT_HEADER
init|=
literal|"kuromoji_dict"
decl_stmt|;
DECL|field|TARGETMAP_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|TARGETMAP_HEADER
init|=
literal|"kuromoji_dict_map"
decl_stmt|;
DECL|field|POSDICT_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|POSDICT_HEADER
init|=
literal|"kuromoji_dict_pos"
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|1
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|targetMapOffsets
DECL|field|targetMap
specifier|private
specifier|final
name|int
index|[]
name|targetMapOffsets
decl_stmt|,
name|targetMap
decl_stmt|;
DECL|field|posDict
specifier|private
specifier|final
name|String
index|[]
name|posDict
decl_stmt|;
DECL|field|inflTypeDict
specifier|private
specifier|final
name|String
index|[]
name|inflTypeDict
decl_stmt|;
DECL|field|inflFormDict
specifier|private
specifier|final
name|String
index|[]
name|inflFormDict
decl_stmt|;
DECL|method|BinaryDictionary
specifier|protected
name|BinaryDictionary
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|mapIS
init|=
literal|null
decl_stmt|,
name|dictIS
init|=
literal|null
decl_stmt|,
name|posIS
init|=
literal|null
decl_stmt|;
name|IOException
name|priorE
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|targetMapOffsets
init|=
literal|null
decl_stmt|,
name|targetMap
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|posDict
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|inflFormDict
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|inflTypeDict
init|=
literal|null
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mapIS
operator|=
name|getResource
argument_list|(
name|TARGETMAP_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|mapIS
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|mapIS
argument_list|)
expr_stmt|;
name|DataInput
name|in
init|=
operator|new
name|InputStreamDataInput
argument_list|(
name|mapIS
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|TARGETMAP_HEADER
argument_list|,
name|VERSION
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|targetMap
operator|=
operator|new
name|int
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
name|targetMapOffsets
operator|=
operator|new
name|int
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
name|int
name|accum
init|=
literal|0
decl_stmt|,
name|sourceId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|ofs
init|=
literal|0
init|;
name|ofs
operator|<
name|targetMap
operator|.
name|length
condition|;
name|ofs
operator|++
control|)
block|{
specifier|final
name|int
name|val
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|val
operator|&
literal|0x01
operator|)
operator|!=
literal|0
condition|)
block|{
name|targetMapOffsets
index|[
name|sourceId
index|]
operator|=
name|ofs
expr_stmt|;
name|sourceId
operator|++
expr_stmt|;
block|}
name|accum
operator|+=
name|val
operator|>>>
literal|1
expr_stmt|;
name|targetMap
index|[
name|ofs
index|]
operator|=
name|accum
expr_stmt|;
block|}
if|if
condition|(
name|sourceId
operator|+
literal|1
operator|!=
name|targetMapOffsets
operator|.
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"targetMap file format broken"
argument_list|)
throw|;
name|targetMapOffsets
index|[
name|sourceId
index|]
operator|=
name|targetMap
operator|.
name|length
expr_stmt|;
name|mapIS
operator|.
name|close
argument_list|()
expr_stmt|;
name|mapIS
operator|=
literal|null
expr_stmt|;
name|posIS
operator|=
name|getResource
argument_list|(
name|POSDICT_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|posIS
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|posIS
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|InputStreamDataInput
argument_list|(
name|posIS
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|POSDICT_HEADER
argument_list|,
name|VERSION
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|int
name|posSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|posDict
operator|=
operator|new
name|String
index|[
name|posSize
index|]
expr_stmt|;
name|inflTypeDict
operator|=
operator|new
name|String
index|[
name|posSize
index|]
expr_stmt|;
name|inflFormDict
operator|=
operator|new
name|String
index|[
name|posSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|posSize
condition|;
name|j
operator|++
control|)
block|{
name|posDict
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|inflTypeDict
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|inflFormDict
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
comment|// this is how we encode null inflections
if|if
condition|(
name|inflTypeDict
index|[
name|j
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|inflTypeDict
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|inflFormDict
index|[
name|j
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|inflFormDict
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|posIS
operator|.
name|close
argument_list|()
expr_stmt|;
name|posIS
operator|=
literal|null
expr_stmt|;
name|dictIS
operator|=
name|getResource
argument_list|(
name|DICT_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
comment|// no buffering here, as we load in one large buffer
name|in
operator|=
operator|new
name|InputStreamDataInput
argument_list|(
name|dictIS
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|DICT_HEADER
argument_list|,
name|VERSION
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|tmpBuffer
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|size
argument_list|)
decl_stmt|;
specifier|final
name|ReadableByteChannel
name|channel
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|dictIS
argument_list|)
decl_stmt|;
specifier|final
name|int
name|read
init|=
name|channel
operator|.
name|read
argument_list|(
name|tmpBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|!=
name|size
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot read whole dictionary"
argument_list|)
throw|;
block|}
name|dictIS
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictIS
operator|=
literal|null
expr_stmt|;
name|buffer
operator|=
name|tmpBuffer
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|priorE
operator|=
name|ioe
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorE
argument_list|,
name|mapIS
argument_list|,
name|posIS
argument_list|,
name|dictIS
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|targetMap
operator|=
name|targetMap
expr_stmt|;
name|this
operator|.
name|targetMapOffsets
operator|=
name|targetMapOffsets
expr_stmt|;
name|this
operator|.
name|posDict
operator|=
name|posDict
expr_stmt|;
name|this
operator|.
name|inflTypeDict
operator|=
name|inflTypeDict
expr_stmt|;
name|this
operator|.
name|inflFormDict
operator|=
name|inflFormDict
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
DECL|method|getResource
specifier|protected
specifier|final
name|InputStream
name|getResource
parameter_list|(
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getClassResource
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|suffix
argument_list|)
return|;
block|}
comment|// util, reused by ConnectionCosts and CharacterDefinition
DECL|method|getClassResource
specifier|public
specifier|static
specifier|final
name|InputStream
name|getClassResource
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|InputStream
name|is
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
name|suffix
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Not in classpath: "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
name|suffix
argument_list|)
throw|;
return|return
name|is
return|;
block|}
DECL|method|lookupWordIds
specifier|public
name|void
name|lookupWordIds
parameter_list|(
name|int
name|sourceId
parameter_list|,
name|IntsRef
name|ref
parameter_list|)
block|{
name|ref
operator|.
name|ints
operator|=
name|targetMap
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
name|targetMapOffsets
index|[
name|sourceId
index|]
expr_stmt|;
comment|// targetMapOffsets always has one more entry pointing behind last:
name|ref
operator|.
name|length
operator|=
name|targetMapOffsets
index|[
name|sourceId
operator|+
literal|1
index|]
operator|-
name|ref
operator|.
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeftId
specifier|public
name|int
name|getLeftId
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
argument_list|)
operator|>>>
literal|3
return|;
block|}
annotation|@
name|Override
DECL|method|getRightId
specifier|public
name|int
name|getRightId
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
argument_list|)
operator|>>>
literal|3
return|;
block|}
annotation|@
name|Override
DECL|method|getWordCost
specifier|public
name|int
name|getWordCost
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
operator|+
literal|2
argument_list|)
return|;
comment|// Skip id
block|}
annotation|@
name|Override
DECL|method|getBaseForm
specifier|public
name|String
name|getBaseForm
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surfaceForm
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|hasBaseFormData
argument_list|(
name|wordId
argument_list|)
condition|)
block|{
name|int
name|offset
init|=
name|baseFormOffset
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|int
name|data
init|=
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
name|int
name|prefix
init|=
name|data
operator|>>>
literal|4
decl_stmt|;
name|int
name|suffix
init|=
name|data
operator|&
literal|0xF
decl_stmt|;
name|char
name|text
index|[]
init|=
operator|new
name|char
index|[
name|prefix
operator|+
name|suffix
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|surfaceForm
argument_list|,
name|off
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|prefix
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
name|suffix
condition|;
name|i
operator|++
control|)
block|{
name|text
index|[
name|prefix
operator|+
name|i
index|]
operator|=
name|buffer
operator|.
name|getChar
argument_list|(
name|offset
operator|+
operator|(
name|i
operator|<<
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|text
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getReading
specifier|public
name|String
name|getReading
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|hasReadingData
argument_list|(
name|wordId
argument_list|)
condition|)
block|{
name|int
name|offset
init|=
name|readingOffset
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|int
name|readingData
init|=
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
return|return
name|readString
argument_list|(
name|offset
argument_list|,
name|readingData
operator|>>>
literal|1
argument_list|,
operator|(
name|readingData
operator|&
literal|1
operator|)
operator|==
literal|1
argument_list|)
return|;
block|}
else|else
block|{
comment|// the reading is the surface form, with hiragana shifted to katakana
name|char
name|text
index|[]
init|=
operator|new
name|char
index|[
name|len
index|]
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|surface
index|[
name|off
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ch
operator|>
literal|0x3040
operator|&&
name|ch
operator|<
literal|0x3097
condition|)
block|{
name|text
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ch
operator|+
literal|0x60
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|text
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPartOfSpeech
specifier|public
name|String
name|getPartOfSpeech
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|posDict
index|[
name|getLeftId
argument_list|(
name|wordId
argument_list|)
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getPronunciation
specifier|public
name|String
name|getPronunciation
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|hasPronunciationData
argument_list|(
name|wordId
argument_list|)
condition|)
block|{
name|int
name|offset
init|=
name|pronunciationOffset
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|int
name|pronunciationData
init|=
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
return|return
name|readString
argument_list|(
name|offset
argument_list|,
name|pronunciationData
operator|>>>
literal|1
argument_list|,
operator|(
name|pronunciationData
operator|&
literal|1
operator|)
operator|==
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getReading
argument_list|(
name|wordId
argument_list|,
name|surface
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
comment|// same as the reading
block|}
block|}
annotation|@
name|Override
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|inflTypeDict
index|[
name|getLeftId
argument_list|(
name|wordId
argument_list|)
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|inflFormDict
index|[
name|getLeftId
argument_list|(
name|wordId
argument_list|)
index|]
return|;
block|}
DECL|method|baseFormOffset
specifier|private
specifier|static
name|int
name|baseFormOffset
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|wordId
operator|+
literal|4
return|;
block|}
DECL|method|readingOffset
specifier|private
name|int
name|readingOffset
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
name|int
name|offset
init|=
name|baseFormOffset
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasBaseFormData
argument_list|(
name|wordId
argument_list|)
condition|)
block|{
name|int
name|baseFormLength
init|=
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xf
decl_stmt|;
return|return
name|offset
operator|+
operator|(
name|baseFormLength
operator|<<
literal|1
operator|)
return|;
block|}
else|else
block|{
return|return
name|offset
return|;
block|}
block|}
DECL|method|pronunciationOffset
specifier|private
name|int
name|pronunciationOffset
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
if|if
condition|(
name|hasReadingData
argument_list|(
name|wordId
argument_list|)
condition|)
block|{
name|int
name|offset
init|=
name|readingOffset
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|int
name|readingData
init|=
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
specifier|final
name|int
name|readingLength
decl_stmt|;
if|if
condition|(
operator|(
name|readingData
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|readingLength
operator|=
name|readingData
operator|&
literal|0xfe
expr_stmt|;
comment|// UTF-16: mask off kana bit
block|}
else|else
block|{
name|readingLength
operator|=
name|readingData
operator|>>>
literal|1
expr_stmt|;
block|}
return|return
name|offset
operator|+
name|readingLength
return|;
block|}
else|else
block|{
return|return
name|readingOffset
argument_list|(
name|wordId
argument_list|)
return|;
block|}
block|}
DECL|method|hasBaseFormData
specifier|private
name|boolean
name|hasBaseFormData
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
operator|(
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
argument_list|)
operator|&
name|HAS_BASEFORM
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|hasReadingData
specifier|private
name|boolean
name|hasReadingData
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
operator|(
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
argument_list|)
operator|&
name|HAS_READING
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|hasPronunciationData
specifier|private
name|boolean
name|hasPronunciationData
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
operator|(
name|buffer
operator|.
name|getShort
argument_list|(
name|wordId
argument_list|)
operator|&
name|HAS_PRONUNCIATION
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|readString
specifier|private
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|kana
parameter_list|)
block|{
name|char
name|text
index|[]
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
if|if
condition|(
name|kana
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|text
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|0x30A0
operator|+
operator|(
name|buffer
operator|.
name|get
argument_list|(
name|offset
operator|+
name|i
argument_list|)
operator|&
literal|0xff
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|text
index|[
name|i
index|]
operator|=
name|buffer
operator|.
name|getChar
argument_list|(
name|offset
operator|+
operator|(
name|i
operator|<<
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|text
argument_list|)
return|;
block|}
comment|/** flag that the entry has baseform data. otherwise its not inflected (same as surface form) */
DECL|field|HAS_BASEFORM
specifier|public
specifier|static
specifier|final
name|int
name|HAS_BASEFORM
init|=
literal|1
decl_stmt|;
comment|/** flag that the entry has reading data. otherwise reading is surface form converted to katakana */
DECL|field|HAS_READING
specifier|public
specifier|static
specifier|final
name|int
name|HAS_READING
init|=
literal|2
decl_stmt|;
comment|/** flag that the entry has pronunciation data. otherwise pronunciation is the reading */
DECL|field|HAS_PRONUNCIATION
specifier|public
specifier|static
specifier|final
name|int
name|HAS_PRONUNCIATION
init|=
literal|4
decl_stmt|;
block|}
end_class
end_unit

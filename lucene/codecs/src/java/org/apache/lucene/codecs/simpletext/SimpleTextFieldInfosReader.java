begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|FieldInfosReader
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
name|BytesRef
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
name|StringHelper
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
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextFieldInfosWriter
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * reads plaintext field infos files  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextFieldInfosReader
specifier|public
class|class
name|SimpleTextFieldInfosReader
extends|extends
name|FieldInfosReader
block|{
annotation|@
name|Override
DECL|method|read
specifier|public
name|FieldInfos
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|iocontext
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
name|segmentSuffix
argument_list|,
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|ChecksumIndexInput
name|input
init|=
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|iocontext
argument_list|)
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NUMFIELDS
argument_list|)
assert|;
specifier|final
name|int
name|size
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUMFIELDS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfo
name|infos
index|[]
init|=
operator|new
name|FieldInfo
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NAME
argument_list|)
assert|;
name|String
name|name
init|=
name|readString
argument_list|(
name|NAME
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NUMBER
argument_list|)
assert|;
name|int
name|fieldNumber
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUMBER
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|ISINDEXED
argument_list|)
assert|;
name|boolean
name|isIndexed
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|ISINDEXED
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
decl_stmt|;
if|if
condition|(
name|isIndexed
condition|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|INDEXOPTIONS
argument_list|)
assert|;
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|valueOf
argument_list|(
name|readString
argument_list|(
name|INDEXOPTIONS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexOptions
operator|=
literal|null
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|STORETV
argument_list|)
assert|;
name|boolean
name|storeTermVector
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|STORETV
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|PAYLOADS
argument_list|)
assert|;
name|boolean
name|storePayloads
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|PAYLOADS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NORMS
argument_list|)
assert|;
name|boolean
name|omitNorms
init|=
operator|!
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|NORMS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NORMS_TYPE
argument_list|)
assert|;
name|String
name|nrmType
init|=
name|readString
argument_list|(
name|NORMS_TYPE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesType
name|normsType
init|=
name|docValuesType
argument_list|(
name|nrmType
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|DOCVALUES
argument_list|)
assert|;
name|String
name|dvType
init|=
name|readString
argument_list|(
name|DOCVALUES
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesType
name|docValuesType
init|=
name|docValuesType
argument_list|(
name|dvType
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|DOCVALUES_GEN
argument_list|)
assert|;
specifier|final
name|long
name|dvGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|DOCVALUES_GEN
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NUM_ATTS
argument_list|)
assert|;
name|int
name|numAtts
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUM_ATTS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|atts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numAtts
condition|;
name|j
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|ATT_KEY
argument_list|)
assert|;
name|String
name|key
init|=
name|readString
argument_list|(
name|ATT_KEY
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|ATT_VALUE
argument_list|)
assert|;
name|String
name|value
init|=
name|readString
argument_list|(
name|ATT_VALUE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|atts
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|infos
index|[
name|i
index|]
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
name|normsType
argument_list|,
name|dvGen
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|atts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|fieldInfos
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|input
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
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|docValuesType
specifier|public
name|DocValuesType
name|docValuesType
parameter_list|(
name|String
name|dvType
parameter_list|)
block|{
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|dvType
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|DocValuesType
operator|.
name|valueOf
argument_list|(
name|dvType
argument_list|)
return|;
block|}
block|}
DECL|method|readString
specifier|private
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|scratch
operator|.
name|length
operator|-
name|offset
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
block|}
end_class
end_unit

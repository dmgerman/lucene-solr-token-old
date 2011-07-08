begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
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
comment|/**  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DefaultDocValuesConsumer
specifier|public
class|class
name|DefaultDocValuesConsumer
extends|extends
name|PerDocConsumer
block|{
DECL|field|segmentName
specifier|private
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|codecId
specifier|private
specifier|final
name|int
name|codecId
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|AtomicLong
name|bytesUsed
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
DECL|field|useCompoundFile
specifier|private
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|method|DefaultDocValuesConsumer
specifier|public
name|DefaultDocValuesConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|IOException
block|{
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
name|codecId
operator|=
name|state
operator|.
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
comment|//TODO maybe we should enable a global CFS that all codecs can pull on demand to further reduce the number of files?
name|this
operator|.
name|directory
operator|=
name|useCompoundFile
condition|?
name|state
operator|.
name|directory
operator|.
name|createCompoundOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
name|state
operator|.
name|codecId
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
else|:
name|state
operator|.
name|directory
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|this
operator|.
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
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
return|return
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
name|segmentName
argument_list|,
name|codecId
argument_list|,
name|field
operator|.
name|number
argument_list|)
argument_list|,
name|directory
argument_list|,
name|comparator
argument_list|,
name|bytesUsed
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|files
specifier|public
specifier|static
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
parameter_list|,
name|boolean
name|useCompoundFile
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
operator|&&
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
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|codecId
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|codecId
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
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
name|segmentInfo
operator|.
name|name
argument_list|,
name|codecId
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
argument_list|)
assert|;
assert|assert
name|dir
operator|.
name|fileExists
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|codecId
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
assert|;
return|return;
block|}
else|else
block|{
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
comment|// until here all types use an index
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
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
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
DECL|method|getDocValuesExtensions
specifier|public
specifier|static
name|void
name|getDocValuesExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
block|{
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
end_class
end_unit

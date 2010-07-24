begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.preflexrw
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
name|preflexrw
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
name|codecs
operator|.
name|preflex
operator|.
name|PreFlexCodec
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
name|preflex
operator|.
name|PreFlexFields
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
begin_comment
comment|/** Codec, only for testing, that can write and read the  *  pre-flex index format.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PreFlexRWCodec
specifier|public
class|class
name|PreFlexRWCodec
extends|extends
name|PreFlexCodec
block|{
DECL|field|termSortOrder
specifier|private
specifier|final
name|String
name|termSortOrder
decl_stmt|;
comment|// termSortOrder should be null (dynamically deteremined
comment|// by stack), "codepoint" or "utf16"
DECL|method|PreFlexRWCodec
specifier|public
name|PreFlexRWCodec
parameter_list|(
name|String
name|termSortOrder
parameter_list|)
block|{
comment|// NOTE: we impersonate the PreFlex codec so that it can
comment|// read the segments we write!
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|termSortOrder
operator|=
name|termSortOrder
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"PFW"
argument_list|)
expr_stmt|;
return|return
operator|new
name|PreFlexFieldsWriter
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
comment|// Whenever IW opens readers, eg for merging, we have to
comment|// keep terms order in UTF16:
name|boolean
name|unicodeSortOrder
decl_stmt|;
if|if
condition|(
name|termSortOrder
operator|==
literal|null
condition|)
block|{
name|unicodeSortOrder
operator|=
literal|true
expr_stmt|;
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
argument_list|()
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//System.out.println(trace[i].getClassName());
if|if
condition|(
literal|"org.apache.lucene.index.IndexWriter"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
condition|)
block|{
name|unicodeSortOrder
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
comment|//System.out.println("PRW: " + unicodeSortOrder);
block|}
else|else
block|{
name|unicodeSortOrder
operator|=
name|termSortOrder
operator|.
name|equals
argument_list|(
literal|"codepoint"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PreFlexFields
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
argument_list|,
name|unicodeSortOrder
argument_list|)
return|;
block|}
block|}
end_class
end_unit

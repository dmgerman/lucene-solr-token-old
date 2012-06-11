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
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|IndexFileNames
import|;
end_import
begin_comment
comment|/** For debugging, curiosity, transparency only!!  Do not  *  use this codec in production.  *  *<p>This codec stores all postings data in a single  *  human-readable text file (_N.pst).  You can view this in  *  any text editor, and even edit it to alter your index.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|SimpleTextPostingsFormat
specifier|public
class|class
name|SimpleTextPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|method|SimpleTextPostingsFormat
specifier|public
name|SimpleTextPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"SimpleText"
argument_list|)
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
name|SimpleTextFieldsWriter
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
operator|new
name|SimpleTextFieldsReader
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|/** Extension of freq postings file */
DECL|field|POSTINGS_EXTENSION
specifier|static
specifier|final
name|String
name|POSTINGS_EXTENSION
init|=
literal|"pst"
decl_stmt|;
DECL|method|getPostingsFileName
specifier|static
name|String
name|getPostingsFileName
parameter_list|(
name|String
name|segment
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
return|return
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|POSTINGS_EXTENSION
argument_list|)
return|;
block|}
block|}
end_class
end_unit

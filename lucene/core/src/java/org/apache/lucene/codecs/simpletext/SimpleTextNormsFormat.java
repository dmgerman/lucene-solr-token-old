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
name|util
operator|.
name|Comparator
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
name|NormsFormat
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
name|codecs
operator|.
name|PerDocProducer
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
name|AtomicReader
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
name|index
operator|.
name|DocValues
operator|.
name|Type
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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * plain-text norms format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextNormsFormat
specifier|public
class|class
name|SimpleTextNormsFormat
extends|extends
name|NormsFormat
block|{
DECL|field|NORMS_SEG_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|NORMS_SEG_SUFFIX
init|=
literal|"len"
decl_stmt|;
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
name|SimpleTextNormsPerDocConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocProducer
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
name|SimpleTextNormsPerDocProducer
argument_list|(
name|state
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Reads plain-text norms.    *<p>    *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>    *     * @lucene.experimental    */
DECL|class|SimpleTextNormsPerDocProducer
specifier|public
specifier|static
class|class
name|SimpleTextNormsPerDocProducer
extends|extends
name|SimpleTextPerDocProducer
block|{
DECL|method|SimpleTextNormsPerDocProducer
specifier|public
name|SimpleTextNormsPerDocProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|state
argument_list|,
name|comp
argument_list|,
name|NORMS_SEG_SUFFIX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canLoad
specifier|protected
name|boolean
name|canLoad
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|hasNorms
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|anyDocValuesFields
specifier|protected
name|boolean
name|anyDocValuesFields
parameter_list|(
name|FieldInfos
name|infos
parameter_list|)
block|{
return|return
name|infos
operator|.
name|hasNorms
argument_list|()
return|;
block|}
block|}
comment|/**    * Writes plain-text norms.    *<p>    *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>    *     * @lucene.experimental    */
DECL|class|SimpleTextNormsPerDocConsumer
specifier|public
specifier|static
class|class
name|SimpleTextNormsPerDocConsumer
extends|extends
name|SimpleTextPerDocConsumer
block|{
DECL|method|SimpleTextNormsPerDocConsumer
specifier|public
name|SimpleTextNormsPerDocConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|state
argument_list|,
name|NORMS_SEG_SUFFIX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesForMerge
specifier|protected
name|DocValues
name|getDocValuesForMerge
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|normValues
argument_list|(
name|info
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|canMerge
specifier|protected
name|boolean
name|canMerge
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|hasNorms
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
comment|// We don't have to remove files here: IndexFileDeleter
comment|// will do so
block|}
block|}
block|}
end_class
end_unit

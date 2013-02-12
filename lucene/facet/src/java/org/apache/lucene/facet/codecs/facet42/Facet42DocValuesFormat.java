begin_unit
begin_package
DECL|package|org.apache.lucene.facet.codecs.facet42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|codecs
operator|.
name|facet42
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
name|codecs
operator|.
name|DocValuesProducer
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
name|DocValuesFormat
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
name|SegmentWriteState
import|;
end_import
begin_comment
comment|/**  * DocValues format that only handles binary doc values and  * is optimized for usage with facets.  It uses more RAM than other  * formats in exchange for faster lookups.  *  *<p>  *<b>NOTE</b>: this format cannot handle more than 2 GB  * of facet data in a single segment.  If your usage may hit  * this limit, you can either use Lucene's default  * DocValuesFormat, limit the maximum segment size in your  * MergePolicy, or send us a patch fixing the limitation.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Facet42DocValuesFormat
specifier|public
specifier|final
class|class
name|Facet42DocValuesFormat
extends|extends
name|DocValuesFormat
block|{
DECL|field|CODEC
specifier|public
specifier|static
specifier|final
name|String
name|CODEC
init|=
literal|"FacetsDocValues"
decl_stmt|;
DECL|field|EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"fdv"
decl_stmt|;
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|method|Facet42DocValuesFormat
specifier|public
name|Facet42DocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Facet42"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
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
name|Facet42DocValuesConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|DocValuesProducer
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
name|Facet42DocValuesProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
end_class
end_unit

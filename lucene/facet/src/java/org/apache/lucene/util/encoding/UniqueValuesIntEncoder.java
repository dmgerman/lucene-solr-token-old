begin_unit
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|io
operator|.
name|OutputStream
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link IntEncoderFilter} which ensures only unique values are encoded. The  * implementation assumes the values given to {@link #encode(int)} are sorted.  * If this is not the case, you can chain this encoder with  * {@link SortingIntEncoder}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|UniqueValuesIntEncoder
specifier|public
specifier|final
class|class
name|UniqueValuesIntEncoder
extends|extends
name|IntEncoderFilter
block|{
comment|/**    * Denotes an illegal value which we can use to init 'prev' to. Since all    * encoded values are integers, this value is init to MAX_INT+1 and is of type    * long. Therefore we are guaranteed not to get this value in encode.    */
DECL|field|ILLEGAL_VALUE
specifier|private
specifier|static
specifier|final
name|long
name|ILLEGAL_VALUE
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
decl_stmt|;
DECL|field|prev
specifier|private
name|long
name|prev
init|=
name|ILLEGAL_VALUE
decl_stmt|;
comment|/** Constructs a new instance with the given encoder. */
DECL|method|UniqueValuesIntEncoder
specifier|public
name|UniqueValuesIntEncoder
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|)
block|{
name|super
argument_list|(
name|encoder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|prev
operator|!=
name|value
condition|)
block|{
name|encoder
operator|.
name|encode
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|prev
operator|=
name|value
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createMatchingDecoder
specifier|public
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
block|{
return|return
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|super
operator|.
name|reInit
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|prev
operator|=
name|ILLEGAL_VALUE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Unique ("
operator|+
name|encoder
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit

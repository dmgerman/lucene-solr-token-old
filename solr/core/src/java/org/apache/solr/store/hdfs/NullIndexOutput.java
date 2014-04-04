begin_unit
begin_package
DECL|package|org.apache.solr.store.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|hdfs
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
name|store
operator|.
name|IndexOutput
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|NullIndexOutput
specifier|public
class|class
name|NullIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{        }
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{        }
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|pos
operator|++
expr_stmt|;
name|updateLength
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|pos
operator|+=
name|length
expr_stmt|;
name|updateLength
argument_list|()
expr_stmt|;
block|}
DECL|method|updateLength
specifier|private
name|void
name|updateLength
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|>
name|length
condition|)
block|{
name|length
operator|=
name|pos
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
comment|// we don't write anything.
block|}
block|}
end_class
end_unit

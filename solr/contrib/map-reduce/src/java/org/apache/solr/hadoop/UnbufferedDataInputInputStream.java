begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|InputStreamReader
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
begin_class
DECL|class|UnbufferedDataInputInputStream
specifier|public
class|class
name|UnbufferedDataInputInputStream
extends|extends
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|DataInputInputStream
block|{
DECL|field|in
specifier|private
specifier|final
name|DataInputStream
name|in
decl_stmt|;
DECL|method|UnbufferedDataInputInputStream
specifier|public
name|UnbufferedDataInputInputStream
parameter_list|(
name|DataInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|DataInputInputStream
operator|.
name|constructInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFully
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|readFully
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFully
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|readFully
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|skipBytes
specifier|public
name|int
name|skipBytes
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|skipBytes
argument_list|(
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readBoolean
specifier|public
name|boolean
name|readBoolean
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readBoolean
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readUnsignedByte
specifier|public
name|int
name|readUnsignedByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readUnsignedByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readShort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readUnsignedShort
specifier|public
name|int
name|readUnsignedShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readUnsignedShort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readChar
specifier|public
name|char
name|readChar
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readChar
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readInt
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readFloat
specifier|public
name|float
name|readFloat
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readDouble
specifier|public
name|double
name|readDouble
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readDouble
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readLine
specifier|public
name|String
name|readLine
parameter_list|()
throws|throws
name|IOException
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readLine
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readUTF
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readUTF
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|()
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.store.db
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|db
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
name|store
operator|.
name|IndexInput
import|;
end_import
begin_class
DECL|class|DbIndexInput
specifier|public
class|class
name|DbIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|position
DECL|field|length
specifier|protected
name|long
name|position
init|=
literal|0L
decl_stmt|,
name|length
init|=
literal|0L
decl_stmt|;
DECL|field|directory
specifier|protected
name|DbDirectory
name|directory
decl_stmt|;
DECL|field|block
specifier|protected
name|Block
name|block
decl_stmt|;
DECL|field|file
specifier|protected
name|File
name|file
decl_stmt|;
DECL|method|DbIndexInput
specifier|protected
name|DbIndexInput
parameter_list|(
name|DbDirectory
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|file
operator|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File does not exist: "
operator|+
name|name
argument_list|)
throw|;
name|length
operator|=
name|file
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|block
operator|=
operator|new
name|Block
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
name|DbIndexInput
name|clone
init|=
operator|(
name|DbIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|block
operator|=
operator|new
name|Block
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|clone
operator|.
name|block
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|clone
operator|.
name|block
operator|.
name|get
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|+
literal|1
operator|>
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Reading past end of file"
argument_list|)
throw|;
name|int
name|blockPos
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|++
operator|&
name|DbIndexOutput
operator|.
name|BLOCK_MASK
argument_list|)
decl_stmt|;
name|byte
name|b
init|=
name|block
operator|.
name|getData
argument_list|()
index|[
name|blockPos
index|]
decl_stmt|;
if|if
condition|(
name|blockPos
operator|+
literal|1
operator|==
name|DbIndexOutput
operator|.
name|BLOCK_LEN
condition|)
block|{
name|block
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|+
name|len
operator|>
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Reading past end of file"
argument_list|)
throw|;
else|else
block|{
name|int
name|blockPos
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|&
name|DbIndexOutput
operator|.
name|BLOCK_MASK
argument_list|)
decl_stmt|;
while|while
condition|(
name|blockPos
operator|+
name|len
operator|>=
name|DbIndexOutput
operator|.
name|BLOCK_LEN
condition|)
block|{
name|int
name|blockLen
init|=
name|DbIndexOutput
operator|.
name|BLOCK_LEN
operator|-
name|blockPos
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|block
operator|.
name|getData
argument_list|()
argument_list|,
name|blockPos
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|len
operator|-=
name|blockLen
expr_stmt|;
name|offset
operator|+=
name|blockLen
expr_stmt|;
name|position
operator|+=
name|blockLen
expr_stmt|;
name|block
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|blockPos
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|block
operator|.
name|getData
argument_list|()
argument_list|,
name|blockPos
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
block|}
block|}
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"seeking past end of file"
argument_list|)
throw|;
if|if
condition|(
operator|(
name|pos
operator|>>>
name|DbIndexOutput
operator|.
name|BLOCK_SHIFT
operator|)
operator|!=
operator|(
name|position
operator|>>>
name|DbIndexOutput
operator|.
name|BLOCK_SHIFT
operator|)
condition|)
block|{
name|block
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
name|position
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|position
return|;
block|}
block|}
end_class
end_unit

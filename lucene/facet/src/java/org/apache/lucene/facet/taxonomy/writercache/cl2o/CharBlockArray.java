begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache.cl2o
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|cl2o
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Similar to {@link StringBuilder}, but with a more efficient growing strategy.  * This class uses char array blocks to grow.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CharBlockArray
class|class
name|CharBlockArray
implements|implements
name|Appendable
implements|,
name|Serializable
implements|,
name|CharSequence
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|DefaultBlockSize
specifier|private
specifier|final
specifier|static
name|int
name|DefaultBlockSize
init|=
literal|32
operator|*
literal|1024
decl_stmt|;
comment|// 32 KB default size
DECL|class|Block
specifier|final
specifier|static
class|class
name|Block
implements|implements
name|Serializable
implements|,
name|Cloneable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|chars
name|char
index|[]
name|chars
decl_stmt|;
DECL|field|length
name|int
name|length
decl_stmt|;
DECL|method|Block
name|Block
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|chars
operator|=
operator|new
name|char
index|[
name|size
index|]
expr_stmt|;
name|this
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|field|blocks
name|List
argument_list|<
name|Block
argument_list|>
name|blocks
decl_stmt|;
DECL|field|current
name|Block
name|current
decl_stmt|;
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
DECL|field|length
name|int
name|length
decl_stmt|;
DECL|method|CharBlockArray
name|CharBlockArray
parameter_list|()
block|{
name|this
argument_list|(
name|DefaultBlockSize
argument_list|)
expr_stmt|;
block|}
DECL|method|CharBlockArray
name|CharBlockArray
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|addBlock
argument_list|()
expr_stmt|;
block|}
DECL|method|addBlock
specifier|private
name|void
name|addBlock
parameter_list|()
block|{
name|this
operator|.
name|current
operator|=
operator|new
name|Block
argument_list|(
name|this
operator|.
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|.
name|add
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
block|}
DECL|method|blockIndex
name|int
name|blockIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|index
operator|/
name|blockSize
return|;
block|}
DECL|method|indexInBlock
name|int
name|indexInBlock
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|index
operator|%
name|blockSize
return|;
block|}
DECL|method|append
specifier|public
name|CharBlockArray
name|append
parameter_list|(
name|CharSequence
name|chars
parameter_list|)
block|{
return|return
name|append
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|append
specifier|public
name|CharBlockArray
name|append
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|current
operator|.
name|length
operator|==
name|this
operator|.
name|blockSize
condition|)
block|{
name|addBlock
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|current
operator|.
name|chars
index|[
name|this
operator|.
name|current
operator|.
name|length
operator|++
index|]
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|length
operator|++
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|append
specifier|public
name|CharBlockArray
name|append
parameter_list|(
name|CharSequence
name|chars
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|append
argument_list|(
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|append
specifier|public
name|CharBlockArray
name|append
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|offset
init|=
name|start
decl_stmt|;
name|int
name|remain
init|=
name|length
decl_stmt|;
while|while
condition|(
name|remain
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|current
operator|.
name|length
operator|==
name|this
operator|.
name|blockSize
condition|)
block|{
name|addBlock
argument_list|()
expr_stmt|;
block|}
name|int
name|toCopy
init|=
name|remain
decl_stmt|;
name|int
name|remainingInBlock
init|=
name|this
operator|.
name|blockSize
operator|-
name|this
operator|.
name|current
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|remainingInBlock
operator|<
name|toCopy
condition|)
block|{
name|toCopy
operator|=
name|remainingInBlock
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|current
operator|.
name|chars
argument_list|,
name|this
operator|.
name|current
operator|.
name|length
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|toCopy
expr_stmt|;
name|remain
operator|-=
name|toCopy
expr_stmt|;
name|this
operator|.
name|current
operator|.
name|length
operator|+=
name|toCopy
expr_stmt|;
block|}
name|this
operator|.
name|length
operator|+=
name|length
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|append
specifier|public
name|CharBlockArray
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|remain
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|remain
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|current
operator|.
name|length
operator|==
name|this
operator|.
name|blockSize
condition|)
block|{
name|addBlock
argument_list|()
expr_stmt|;
block|}
name|int
name|toCopy
init|=
name|remain
decl_stmt|;
name|int
name|remainingInBlock
init|=
name|this
operator|.
name|blockSize
operator|-
name|this
operator|.
name|current
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|remainingInBlock
operator|<
name|toCopy
condition|)
block|{
name|toCopy
operator|=
name|remainingInBlock
expr_stmt|;
block|}
name|s
operator|.
name|getChars
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|toCopy
argument_list|,
name|this
operator|.
name|current
operator|.
name|chars
argument_list|,
name|this
operator|.
name|current
operator|.
name|length
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|toCopy
expr_stmt|;
name|remain
operator|-=
name|toCopy
expr_stmt|;
name|this
operator|.
name|current
operator|.
name|length
operator|+=
name|toCopy
expr_stmt|;
block|}
name|this
operator|.
name|length
operator|+=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|Block
name|b
init|=
name|this
operator|.
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|b
operator|.
name|chars
index|[
name|indexInBlock
argument_list|(
name|index
argument_list|)
index|]
return|;
block|}
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|length
return|;
block|}
DECL|method|subSequence
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"subsequence not implemented yet"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|blockSize
operator|*
name|this
operator|.
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
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
name|this
operator|.
name|blocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|this
operator|.
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|chars
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectOutputStream
name|oos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|oos
operator|=
operator|new
name|ObjectOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|oos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|oos
operator|!=
literal|null
condition|)
block|{
name|oos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|open
specifier|public
specifier|static
name|CharBlockArray
name|open
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ObjectInputStream
name|ois
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ois
operator|=
operator|new
name|ObjectInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|CharBlockArray
name|a
init|=
operator|(
name|CharBlockArray
operator|)
name|ois
operator|.
name|readObject
argument_list|()
decl_stmt|;
return|return
name|a
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|ois
operator|!=
literal|null
condition|)
block|{
name|ois
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit

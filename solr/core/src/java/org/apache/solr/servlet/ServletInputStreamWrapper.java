begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|javax
operator|.
name|servlet
operator|.
name|ReadListener
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletInputStream
import|;
end_import
begin_import
import|import
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
name|SuppressForbidden
import|;
end_import
begin_comment
comment|/**  * Provides a convenient extension of the {@link ServletInputStream} class that can be subclassed by developers wishing  * to adapt the behavior of a Stream. One such example may be to override {@link #close()} to instead be a no-op as in  * SOLR-8933.  *  * This class implements the Wrapper or Decorator pattern. Methods default to calling through to the wrapped stream.  */
end_comment
begin_class
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"delegate methods"
argument_list|)
DECL|class|ServletInputStreamWrapper
specifier|public
class|class
name|ServletInputStreamWrapper
extends|extends
name|ServletInputStream
block|{
DECL|field|stream
specifier|final
name|ServletInputStream
name|stream
decl_stmt|;
DECL|method|ServletInputStreamWrapper
specifier|public
name|ServletInputStreamWrapper
parameter_list|(
name|ServletInputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|stream
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|stream
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
DECL|method|available
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|stream
operator|.
name|available
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isFinished
specifier|public
name|boolean
name|isFinished
parameter_list|()
block|{
return|return
name|stream
operator|.
name|isFinished
argument_list|()
return|;
block|}
DECL|method|isReady
specifier|public
name|boolean
name|isReady
parameter_list|()
block|{
return|return
name|stream
operator|.
name|isReady
argument_list|()
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|stream
operator|.
name|read
argument_list|()
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
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
return|return
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|stream
operator|.
name|mark
argument_list|(
name|readlimit
argument_list|)
expr_stmt|;
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
name|stream
operator|.
name|markSupported
argument_list|()
return|;
block|}
DECL|method|readLine
specifier|public
name|int
name|readLine
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
return|return
name|stream
operator|.
name|readLine
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|setReadListener
specifier|public
name|void
name|setReadListener
parameter_list|(
name|ReadListener
name|arg0
parameter_list|)
block|{
name|stream
operator|.
name|setReadListener
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|stream
operator|.
name|skip
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|stream
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

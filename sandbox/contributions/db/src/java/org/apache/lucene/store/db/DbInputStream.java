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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by the Open Source  * Applications Foundation on behalf of the Apache Software Foundation.  * For more information on the Open Source Applications Foundation, please see  *<http://www.osafoundation.org>.  * For more information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|InputStream
import|;
end_import
begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Db
import|;
end_import
begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbTxn
import|;
end_import
begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Dbt
import|;
end_import
begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbException
import|;
end_import
begin_comment
comment|/**  * @author Andi Vajda  */
end_comment
begin_class
DECL|class|DbInputStream
specifier|public
class|class
name|DbInputStream
extends|extends
name|InputStream
block|{
DECL|field|position
specifier|protected
name|long
name|position
init|=
literal|0L
decl_stmt|;
DECL|field|file
specifier|protected
name|File
name|file
decl_stmt|;
DECL|field|block
specifier|protected
name|Block
name|block
decl_stmt|;
DECL|field|txn
specifier|protected
name|DbTxn
name|txn
decl_stmt|;
DECL|field|files
DECL|field|blocks
specifier|protected
name|Db
name|files
decl_stmt|,
name|blocks
decl_stmt|;
DECL|method|DbInputStream
specifier|protected
name|DbInputStream
parameter_list|(
name|Db
name|files
parameter_list|,
name|Db
name|blocks
parameter_list|,
name|DbTxn
name|txn
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
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
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
name|files
argument_list|,
name|txn
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
name|blocks
argument_list|,
name|txn
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
name|DbInputStream
name|clone
init|=
operator|(
name|DbInputStream
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
name|blocks
argument_list|,
name|txn
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
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
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
name|int
name|blockPos
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|&
name|DbOutputStream
operator|.
name|BLOCK_MASK
argument_list|)
decl_stmt|;
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
while|while
condition|(
name|blockPos
operator|+
name|len
operator|>=
name|DbOutputStream
operator|.
name|BLOCK_LEN
condition|)
block|{
name|int
name|blockLen
init|=
name|DbOutputStream
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
name|blocks
argument_list|,
name|txn
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
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
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
name|DbOutputStream
operator|.
name|BLOCK_SHIFT
operator|)
operator|!=
operator|(
name|position
operator|>>>
name|DbOutputStream
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
name|blocks
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
name|position
operator|=
name|pos
expr_stmt|;
block|}
block|}
end_class
end_unit

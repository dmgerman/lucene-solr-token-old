begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|index
operator|.
name|*
import|;
end_import
begin_class
DECL|class|PhrasePositions
specifier|final
class|class
name|PhrasePositions
block|{
DECL|field|doc
name|int
name|doc
decl_stmt|;
comment|// current doc
DECL|field|position
name|int
name|position
decl_stmt|;
comment|// position in doc
DECL|field|count
name|int
name|count
decl_stmt|;
comment|// remaining pos in this doc
DECL|field|offset
name|int
name|offset
decl_stmt|;
comment|// position in phrase
DECL|field|tp
name|TermPositions
name|tp
decl_stmt|;
comment|// stream of positions
DECL|field|next
name|PhrasePositions
name|next
decl_stmt|;
comment|// used to make lists
DECL|method|PhrasePositions
name|PhrasePositions
parameter_list|(
name|TermPositions
name|t
parameter_list|,
name|int
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|tp
operator|=
name|t
expr_stmt|;
name|offset
operator|=
name|o
expr_stmt|;
block|}
DECL|method|next
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// increments to next doc
if|if
condition|(
operator|!
name|tp
operator|.
name|next
argument_list|()
condition|)
block|{
name|tp
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// sentinel value
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|tp
operator|.
name|doc
argument_list|()
expr_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|skipTo
specifier|final
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|tp
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|tp
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// sentinel value
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|tp
operator|.
name|doc
argument_list|()
expr_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|firstPosition
specifier|final
name|void
name|firstPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|count
operator|=
name|tp
operator|.
name|freq
argument_list|()
expr_stmt|;
comment|// read first pos
name|nextPosition
argument_list|()
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|final
name|boolean
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|--
operator|>
literal|0
condition|)
block|{
comment|// read subsequent pos's
name|position
operator|=
name|tp
operator|.
name|nextPosition
argument_list|()
operator|-
name|offset
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|util
operator|.
name|BitVector
import|;
end_import
begin_class
DECL|class|SegmentMergeInfo
specifier|final
class|class
name|SegmentMergeInfo
block|{
DECL|field|term
name|Term
name|term
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
DECL|field|termEnum
name|TermEnum
name|termEnum
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|postings
name|TermPositions
name|postings
decl_stmt|;
DECL|field|docMap
name|int
index|[]
name|docMap
init|=
literal|null
decl_stmt|;
comment|// maps around deleted docs
DECL|method|SegmentMergeInfo
name|SegmentMergeInfo
parameter_list|(
name|int
name|b
parameter_list|,
name|TermEnum
name|te
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|=
name|b
expr_stmt|;
name|reader
operator|=
name|r
expr_stmt|;
name|termEnum
operator|=
name|te
expr_stmt|;
name|term
operator|=
name|te
operator|.
name|term
argument_list|()
expr_stmt|;
name|postings
operator|=
name|reader
operator|.
name|termPositions
argument_list|()
expr_stmt|;
comment|// build array which maps document numbers around deletions
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|docMap
operator|=
operator|new
name|int
index|[
name|maxDoc
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|isDeleted
argument_list|(
name|i
argument_list|)
condition|)
name|docMap
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
else|else
name|docMap
index|[
name|i
index|]
operator|=
name|j
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|next
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
block|{
name|term
operator|=
name|termEnum
operator|.
name|term
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|term
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|postings
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

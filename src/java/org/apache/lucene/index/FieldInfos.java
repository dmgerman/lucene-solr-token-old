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
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|Directory
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
name|OutputStream
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
begin_class
DECL|class|FieldInfos
specifier|final
class|class
name|FieldInfos
block|{
DECL|field|byNumber
specifier|private
name|Vector
name|byNumber
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
name|Hashtable
name|byName
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|method|FieldInfos
name|FieldInfos
parameter_list|()
block|{
name|add
argument_list|(
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldInfos
name|FieldInfos
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|input
init|=
name|d
operator|.
name|openFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|read
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Adds field info for a Document. */
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Merges in information from another FieldInfos. */
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|FieldInfos
name|other
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|other
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|other
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|isIndexed
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|private
specifier|final
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
name|addInternal
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|!=
name|isIndexed
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field "
operator|+
name|name
operator|+
operator|(
name|fi
operator|.
name|isIndexed
condition|?
literal|" must"
else|:
literal|" cannot"
operator|)
operator|+
literal|" be an indexed field."
argument_list|)
throw|;
block|}
DECL|method|addInternal
specifier|private
specifier|final
name|void
name|addInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|byNumber
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|byNumber
operator|.
name|addElement
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|byName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
DECL|method|fieldNumber
specifier|final
name|int
name|fieldNumber
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|!=
literal|null
condition|)
return|return
name|fi
operator|.
name|number
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
DECL|method|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|FieldInfo
operator|)
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|fieldName
specifier|final
name|String
name|fieldName
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
operator|.
name|name
return|;
block|}
DECL|method|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|FieldInfo
operator|)
name|byNumber
operator|.
name|elementAt
argument_list|(
name|fieldNumber
argument_list|)
return|;
block|}
DECL|method|size
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|write
specifier|final
name|void
name|write
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|output
init|=
name|d
operator|.
name|createFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|final
name|void
name|write
parameter_list|(
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|fi
operator|.
name|isIndexed
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|private
specifier|final
name|void
name|read
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
name|addInternal
argument_list|(
name|input
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
argument_list|,
name|input
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.HTMLParser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|HTMLParser
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
name|*
import|;
end_import
begin_class
DECL|class|Test
class|class
name|Test
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
literal|"-dir"
operator|.
name|equals
argument_list|(
name|argv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|String
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
operator|.
name|list
argument_list|()
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|sort
argument_list|(
name|files
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|parse
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|parse
argument_list|(
operator|new
name|File
argument_list|(
name|argv
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|void
name|parse
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Title: "
operator|+
name|Entities
operator|.
name|encode
argument_list|(
name|parser
operator|.
name|getTitle
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Summary: "
operator|+
name|Entities
operator|.
name|encode
argument_list|(
name|parser
operator|.
name|getSummary
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LineNumberReader
name|reader
init|=
operator|new
name|LineNumberReader
argument_list|(
name|parser
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|l
init|=
name|reader
operator|.
name|readLine
argument_list|()
init|;
name|l
operator|!=
literal|null
condition|;
name|l
operator|=
name|reader
operator|.
name|readLine
argument_list|()
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

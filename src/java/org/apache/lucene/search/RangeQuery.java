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
name|Term
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
name|TermEnum
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
name|TermDocs
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
name|IndexReader
import|;
end_import
begin_comment
comment|/** A Query that matches documents within an exclusive range. */
end_comment
begin_class
DECL|class|RangeQuery
specifier|public
class|class
name|RangeQuery
extends|extends
name|Query
block|{
DECL|field|lowerTerm
specifier|private
name|Term
name|lowerTerm
decl_stmt|;
DECL|field|upperTerm
specifier|private
name|Term
name|upperTerm
decl_stmt|;
DECL|field|inclusive
specifier|private
name|boolean
name|inclusive
decl_stmt|;
comment|/** Constructs a query selecting all terms greater than       *<code>lowerTerm</code> but less than<code>upperTerm</code>.      * There must be at least one term and either term may be null--      * in which case there is no bound on that side, but if there are       * two term, both terms<b>must</b> be for the same field.      */
DECL|method|RangeQuery
specifier|public
name|RangeQuery
parameter_list|(
name|Term
name|lowerTerm
parameter_list|,
name|Term
name|upperTerm
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
block|{
if|if
condition|(
name|lowerTerm
operator|==
literal|null
operator|&&
name|upperTerm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"At least one term must be non-null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerTerm
operator|!=
literal|null
operator|&&
name|upperTerm
operator|!=
literal|null
operator|&&
name|lowerTerm
operator|.
name|field
argument_list|()
operator|!=
name|upperTerm
operator|.
name|field
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both terms must be for the same field"
argument_list|)
throw|;
block|}
name|this
operator|.
name|lowerTerm
operator|=
name|lowerTerm
expr_stmt|;
name|this
operator|.
name|upperTerm
operator|=
name|upperTerm
expr_stmt|;
name|this
operator|.
name|inclusive
operator|=
name|inclusive
expr_stmt|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|// if we have a lowerTerm, start there. otherwise, start at beginning
if|if
condition|(
name|lowerTerm
operator|==
literal|null
condition|)
name|lowerTerm
operator|=
operator|new
name|Term
argument_list|(
name|getField
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|TermEnum
name|enum
type|=
name|reader
operator|.
name|terms
decl|(
name|lowerTerm
decl|)
decl_stmt|;
try|try
block|{
name|String
name|lowerText
init|=
literal|null
decl_stmt|;
name|String
name|field
decl_stmt|;
name|boolean
name|checkLower
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|inclusive
condition|)
block|{
comment|// make adjustments to set to exclusive
if|if
condition|(
name|lowerTerm
operator|!=
literal|null
condition|)
block|{
name|lowerText
operator|=
name|lowerTerm
operator|.
name|text
argument_list|()
expr_stmt|;
name|checkLower
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|upperTerm
operator|!=
literal|null
condition|)
block|{
comment|// set upperTerm to an actual term in the index
name|TermEnum
name|uppEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
name|upperTerm
argument_list|)
decl_stmt|;
name|upperTerm
operator|=
name|uppEnum
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|testField
init|=
name|getField
argument_list|()
decl_stmt|;
do|do
block|{
name|Term
name|term
init|= enum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|testField
condition|)
block|{
if|if
condition|(
operator|!
name|checkLower
operator|||
name|term
operator|.
name|text
argument_list|()
operator|.
name|compareTo
argument_list|(
name|lowerText
argument_list|)
operator|>
literal|0
condition|)
block|{
name|checkLower
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|upperTerm
operator|!=
literal|null
condition|)
block|{
name|int
name|compare
init|=
name|upperTerm
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|/* if beyond the upper term, or is exclusive and                    * this is equal to the upper term, break out */
if|if
condition|(
operator|(
name|compare
operator|<
literal|0
operator|)
operator|||
operator|(
operator|!
name|inclusive
operator|&&
name|compare
operator|==
literal|0
operator|)
condition|)
break|break;
block|}
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|// set the boost
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// add to query
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(enum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
enum_decl|enum.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|combine
specifier|public
name|Query
name|combine
parameter_list|(
name|Query
index|[]
name|queries
parameter_list|)
block|{
return|return
name|Query
operator|.
name|mergeBooleanQueries
argument_list|(
name|queries
argument_list|)
return|;
block|}
DECL|method|getField
specifier|private
name|String
name|getField
parameter_list|()
block|{
return|return
operator|(
name|lowerTerm
operator|!=
literal|null
condition|?
name|lowerTerm
operator|.
name|field
argument_list|()
else|:
name|upperTerm
operator|.
name|field
argument_list|()
operator|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|inclusive
condition|?
literal|"["
else|:
literal|"{"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|lowerTerm
operator|!=
literal|null
condition|?
name|lowerTerm
operator|.
name|text
argument_list|()
else|:
literal|"null"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|upperTerm
operator|!=
literal|null
condition|?
name|upperTerm
operator|.
name|text
argument_list|()
else|:
literal|"null"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|inclusive
condition|?
literal|"]"
else|:
literal|"}"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

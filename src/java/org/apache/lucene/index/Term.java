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
begin_comment
comment|/**   A Term represents a word from text.  This is the unit of search.  It is   composed of two elements, the text of the word, as a string, and the name of   the field that the text occured in, an interned string.    Note that terms may represent more than words from text fields, but also   things like dates, email addresses, urls, etc.  */
end_comment
begin_class
DECL|class|Term
specifier|public
specifier|final
class|class
name|Term
implements|implements
name|Comparable
implements|,
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|text
name|String
name|text
decl_stmt|;
comment|/** Constructs a Term with the given field and text. */
DECL|method|Term
specifier|public
name|Term
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|)
block|{
name|this
argument_list|(
name|fld
argument_list|,
name|txt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Term
name|Term
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|,
name|boolean
name|intern
parameter_list|)
block|{
name|field
operator|=
name|intern
condition|?
name|fld
operator|.
name|intern
argument_list|()
else|:
name|fld
expr_stmt|;
comment|// field names are interned
name|text
operator|=
name|txt
expr_stmt|;
comment|// unless already known to be
block|}
comment|/** Returns the field of this term, an interned string.   The field indicates     the part of a document which this term came from. */
DECL|method|field
specifier|public
specifier|final
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Returns the text of this term.  In the case of words, this is simply the     text of the word.  In the case of dates and other types, this is an     encoding of the object as a string.  */
DECL|method|text
specifier|public
specifier|final
name|String
name|text
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/** Compares two terms, returning true iff they have the same       field and text. */
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|Term
name|other
init|=
operator|(
name|Term
operator|)
name|o
decl_stmt|;
return|return
name|field
operator|==
name|other
operator|.
name|field
operator|&&
name|text
operator|.
name|equals
argument_list|(
name|other
operator|.
name|text
argument_list|)
return|;
block|}
comment|/** Combines the hashCode() of the field and the text. */
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|+
name|text
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|Term
operator|)
name|other
argument_list|)
return|;
block|}
comment|/** Compares two terms, returning an integer which is less than zero iff this     term belongs after the argument, equal zero iff this term is equal to the     argument, and greater than zero iff this term belongs after the argument.      The ordering of terms is first by field, then by text.*/
DECL|method|compareTo
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|Term
name|other
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
name|other
operator|.
name|field
condition|)
comment|// fields are interned
return|return
name|text
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|text
argument_list|)
return|;
else|else
return|return
name|field
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
comment|/** Resets the field and text of a Term. */
DECL|method|set
specifier|final
name|void
name|set
parameter_list|(
name|String
name|fld
parameter_list|,
name|String
name|txt
parameter_list|)
block|{
name|field
operator|=
name|fld
expr_stmt|;
name|text
operator|=
name|txt
expr_stmt|;
block|}
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|field
operator|+
literal|":"
operator|+
name|text
return|;
block|}
DECL|method|readObject
specifier|private
name|void
name|readObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|in
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

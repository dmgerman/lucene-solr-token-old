begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package
begin_comment
comment|// SAX
end_comment
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParseException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import
begin_comment
comment|// Java
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import
begin_comment
comment|/**  * A SAX document handler to read and parse hyphenation patterns from a XML  * file.  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.   */
end_comment
begin_class
DECL|class|PatternParser
specifier|public
class|class
name|PatternParser
extends|extends
name|DefaultHandler
implements|implements
name|PatternConsumer
block|{
DECL|field|parser
name|XMLReader
name|parser
decl_stmt|;
DECL|field|currElement
name|int
name|currElement
decl_stmt|;
DECL|field|consumer
name|PatternConsumer
name|consumer
decl_stmt|;
DECL|field|token
name|StringBuilder
name|token
decl_stmt|;
DECL|field|exception
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|exception
decl_stmt|;
DECL|field|hyphenChar
name|char
name|hyphenChar
decl_stmt|;
DECL|field|errMsg
name|String
name|errMsg
decl_stmt|;
DECL|field|ELEM_CLASSES
specifier|static
specifier|final
name|int
name|ELEM_CLASSES
init|=
literal|1
decl_stmt|;
DECL|field|ELEM_EXCEPTIONS
specifier|static
specifier|final
name|int
name|ELEM_EXCEPTIONS
init|=
literal|2
decl_stmt|;
DECL|field|ELEM_PATTERNS
specifier|static
specifier|final
name|int
name|ELEM_PATTERNS
init|=
literal|3
decl_stmt|;
DECL|field|ELEM_HYPHEN
specifier|static
specifier|final
name|int
name|ELEM_HYPHEN
init|=
literal|4
decl_stmt|;
DECL|method|PatternParser
specifier|public
name|PatternParser
parameter_list|()
throws|throws
name|HyphenationException
block|{
name|token
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|parser
operator|=
name|createParser
argument_list|()
expr_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setErrorHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setEntityResolver
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|hyphenChar
operator|=
literal|'-'
expr_stmt|;
comment|// default
block|}
DECL|method|PatternParser
specifier|public
name|PatternParser
parameter_list|(
name|PatternConsumer
name|consumer
parameter_list|)
throws|throws
name|HyphenationException
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
block|}
DECL|method|setConsumer
specifier|public
name|void
name|setConsumer
parameter_list|(
name|PatternConsumer
name|consumer
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
block|}
comment|/**    * Parses a hyphenation pattern file.    *     * @param filename the filename    * @throws HyphenationException In case of an exception while parsing    */
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|HyphenationException
block|{
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|filename
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses a hyphenation pattern file.    *     * @param file the pattern file    * @throws HyphenationException In case of an exception while parsing    */
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|HyphenationException
block|{
try|try
block|{
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|file
operator|.
name|toURL
argument_list|()
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|HyphenationException
argument_list|(
literal|"Error converting the File '"
operator|+
name|file
operator|+
literal|"' to a URL: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parses a hyphenation pattern file.    *     * @param source the InputSource for the file    * @throws HyphenationException In case of an exception while parsing    */
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|InputSource
name|source
parameter_list|)
throws|throws
name|HyphenationException
block|{
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
throw|throw
operator|new
name|HyphenationException
argument_list|(
literal|"File not found: "
operator|+
name|fnfe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|HyphenationException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|HyphenationException
argument_list|(
name|errMsg
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates a SAX parser using JAXP    *     * @return the created SAX parser    */
DECL|method|createParser
specifier|static
name|XMLReader
name|createParser
parameter_list|()
block|{
try|try
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|newSAXParser
argument_list|()
operator|.
name|getXMLReader
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't create XMLReader: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|readToken
specifier|protected
name|String
name|readToken
parameter_list|(
name|StringBuffer
name|chars
parameter_list|)
block|{
name|String
name|word
decl_stmt|;
name|boolean
name|space
init|=
literal|false
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|space
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|space
condition|)
block|{
comment|// chars.delete(0,i);
for|for
control|(
name|int
name|countr
init|=
name|i
init|;
name|countr
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|;
name|countr
operator|++
control|)
block|{
name|chars
operator|.
name|setCharAt
argument_list|(
name|countr
operator|-
name|i
argument_list|,
name|chars
operator|.
name|charAt
argument_list|(
name|countr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|chars
operator|.
name|setLength
argument_list|(
name|chars
operator|.
name|length
argument_list|()
operator|-
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|word
operator|=
name|token
operator|.
name|toString
argument_list|()
expr_stmt|;
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|word
return|;
block|}
block|}
name|space
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|space
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|token
operator|.
name|append
argument_list|(
name|chars
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// chars.delete(0,i);
for|for
control|(
name|int
name|countr
init|=
name|i
init|;
name|countr
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|;
name|countr
operator|++
control|)
block|{
name|chars
operator|.
name|setCharAt
argument_list|(
name|countr
operator|-
name|i
argument_list|,
name|chars
operator|.
name|charAt
argument_list|(
name|countr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|chars
operator|.
name|setLength
argument_list|(
name|chars
operator|.
name|length
argument_list|()
operator|-
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|space
condition|)
block|{
name|word
operator|=
name|token
operator|.
name|toString
argument_list|()
expr_stmt|;
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|word
return|;
block|}
name|token
operator|.
name|append
argument_list|(
name|chars
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|getPattern
specifier|protected
specifier|static
name|String
name|getPattern
parameter_list|(
name|String
name|word
parameter_list|)
block|{
name|StringBuilder
name|pat
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|word
operator|.
name|length
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|pat
operator|.
name|append
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pat
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|normalizeException
specifier|protected
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|normalizeException
parameter_list|(
name|ArrayList
argument_list|<
name|?
argument_list|>
name|ex
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
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
name|ex
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|item
init|=
name|ex
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|String
condition|)
block|{
name|String
name|str
init|=
operator|(
name|String
operator|)
name|item
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|char
name|c
init|=
name|str
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
name|hyphenChar
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|add
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|char
index|[]
name|h
init|=
operator|new
name|char
index|[
literal|1
index|]
decl_stmt|;
name|h
index|[
literal|0
index|]
operator|=
name|hyphenChar
expr_stmt|;
comment|// we use here hyphenChar which is not necessarily
comment|// the one to be printed
name|res
operator|.
name|add
argument_list|(
operator|new
name|Hyphen
argument_list|(
operator|new
name|String
argument_list|(
name|h
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|res
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
DECL|method|getExceptionWord
specifier|protected
name|String
name|getExceptionWord
parameter_list|(
name|ArrayList
argument_list|<
name|?
argument_list|>
name|ex
parameter_list|)
block|{
name|StringBuilder
name|res
init|=
operator|new
name|StringBuilder
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
name|ex
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|item
init|=
name|ex
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|String
condition|)
block|{
name|res
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|(
operator|(
name|Hyphen
operator|)
name|item
operator|)
operator|.
name|noBreak
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Hyphen
operator|)
name|item
operator|)
operator|.
name|noBreak
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getInterletterValues
specifier|protected
specifier|static
name|String
name|getInterletterValues
parameter_list|(
name|String
name|pat
parameter_list|)
block|{
name|StringBuilder
name|il
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|word
init|=
name|pat
operator|+
literal|"a"
decl_stmt|;
comment|// add dummy letter to serve as sentinel
name|int
name|len
init|=
name|word
operator|.
name|length
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|il
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
name|il
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|il
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//
comment|// EntityResolver methods
comment|//
annotation|@
name|Override
DECL|method|resolveEntity
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
comment|// supply the internal hyphenation.dtd if possible
if|if
condition|(
operator|(
name|systemId
operator|!=
literal|null
operator|&&
name|systemId
operator|.
name|matches
argument_list|(
literal|"(?i).*\\bhyphenation.dtd\\b.*"
argument_list|)
operator|)
operator|||
operator|(
literal|"hyphenation-info"
operator|.
name|equals
argument_list|(
name|publicId
argument_list|)
operator|)
condition|)
block|{
comment|// System.out.println(this.getClass().getResource("hyphenation.dtd").toExternalForm());
return|return
operator|new
name|InputSource
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"hyphenation.dtd"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|//
comment|// ContentHandler methods
comment|//
comment|/**    * @see org.xml.sax.ContentHandler#startElement(java.lang.String,    *      java.lang.String, java.lang.String, org.xml.sax.Attributes)    */
annotation|@
name|Override
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|local
parameter_list|,
name|String
name|raw
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
block|{
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"hyphen-char"
argument_list|)
condition|)
block|{
name|String
name|h
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|h
operator|!=
literal|null
operator|&&
name|h
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|hyphenChar
operator|=
name|h
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"classes"
argument_list|)
condition|)
block|{
name|currElement
operator|=
name|ELEM_CLASSES
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"patterns"
argument_list|)
condition|)
block|{
name|currElement
operator|=
name|ELEM_PATTERNS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"exceptions"
argument_list|)
condition|)
block|{
name|currElement
operator|=
name|ELEM_EXCEPTIONS
expr_stmt|;
name|exception
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"hyphen"
argument_list|)
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|exception
operator|.
name|add
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|exception
operator|.
name|add
argument_list|(
operator|new
name|Hyphen
argument_list|(
name|attrs
operator|.
name|getValue
argument_list|(
literal|"pre"
argument_list|)
argument_list|,
name|attrs
operator|.
name|getValue
argument_list|(
literal|"no"
argument_list|)
argument_list|,
name|attrs
operator|.
name|getValue
argument_list|(
literal|"post"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|currElement
operator|=
name|ELEM_HYPHEN
expr_stmt|;
block|}
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see org.xml.sax.ContentHandler#endElement(java.lang.String,    *      java.lang.String, java.lang.String)    */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|local
parameter_list|,
name|String
name|raw
parameter_list|)
block|{
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|word
init|=
name|token
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|currElement
condition|)
block|{
case|case
name|ELEM_CLASSES
case|:
name|consumer
operator|.
name|addClass
argument_list|(
name|word
argument_list|)
expr_stmt|;
break|break;
case|case
name|ELEM_EXCEPTIONS
case|:
name|exception
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|exception
operator|=
name|normalizeException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|addException
argument_list|(
name|getExceptionWord
argument_list|(
name|exception
argument_list|)
argument_list|,
operator|(
name|ArrayList
operator|)
name|exception
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|ELEM_PATTERNS
case|:
name|consumer
operator|.
name|addPattern
argument_list|(
name|getPattern
argument_list|(
name|word
argument_list|)
argument_list|,
name|getInterletterValues
argument_list|(
name|word
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|ELEM_HYPHEN
case|:
comment|// nothing to do
break|break;
block|}
if|if
condition|(
name|currElement
operator|!=
name|ELEM_HYPHEN
condition|)
block|{
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currElement
operator|==
name|ELEM_HYPHEN
condition|)
block|{
name|currElement
operator|=
name|ELEM_EXCEPTIONS
expr_stmt|;
block|}
else|else
block|{
name|currElement
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * @see org.xml.sax.ContentHandler#characters(char[], int, int)    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|StringBuffer
name|chars
init|=
operator|new
name|StringBuffer
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|chars
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|String
name|word
init|=
name|readToken
argument_list|(
name|chars
argument_list|)
decl_stmt|;
while|while
condition|(
name|word
operator|!=
literal|null
condition|)
block|{
comment|// System.out.println("\"" + word + "\"");
switch|switch
condition|(
name|currElement
condition|)
block|{
case|case
name|ELEM_CLASSES
case|:
name|consumer
operator|.
name|addClass
argument_list|(
name|word
argument_list|)
expr_stmt|;
break|break;
case|case
name|ELEM_EXCEPTIONS
case|:
name|exception
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|exception
operator|=
name|normalizeException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|addException
argument_list|(
name|getExceptionWord
argument_list|(
name|exception
argument_list|)
argument_list|,
operator|(
name|ArrayList
operator|)
name|exception
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
case|case
name|ELEM_PATTERNS
case|:
name|consumer
operator|.
name|addPattern
argument_list|(
name|getPattern
argument_list|(
name|word
argument_list|)
argument_list|,
name|getInterletterValues
argument_list|(
name|word
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|word
operator|=
name|readToken
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a string of the location.    */
DECL|method|getLocationString
specifier|private
name|String
name|getLocationString
parameter_list|(
name|SAXParseException
name|ex
parameter_list|)
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|systemId
init|=
name|ex
operator|.
name|getSystemId
argument_list|()
decl_stmt|;
if|if
condition|(
name|systemId
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|systemId
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|systemId
operator|=
name|systemId
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
name|systemId
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
name|ex
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
name|ex
operator|.
name|getColumnNumber
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// getLocationString(SAXParseException):String
comment|// PatternConsumer implementation for testing purposes
DECL|method|addClass
specifier|public
name|void
name|addClass
parameter_list|(
name|String
name|c
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"class: "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|addException
specifier|public
name|void
name|addException
parameter_list|(
name|String
name|w
parameter_list|,
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"exception: "
operator|+
name|w
operator|+
literal|" : "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addPattern
specifier|public
name|void
name|addPattern
parameter_list|(
name|String
name|p
parameter_list|,
name|String
name|v
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"pattern: "
operator|+
name|p
operator|+
literal|" : "
operator|+
name|v
argument_list|)
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|PatternParser
name|pp
init|=
operator|new
name|PatternParser
argument_list|()
decl_stmt|;
name|pp
operator|.
name|setConsumer
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|pp
operator|.
name|parse
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

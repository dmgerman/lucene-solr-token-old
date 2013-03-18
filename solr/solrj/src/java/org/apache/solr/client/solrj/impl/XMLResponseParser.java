begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|ResponseParser
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
name|SolrDocument
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
name|SolrDocumentList
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
name|SolrException
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
name|DateUtil
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
name|NamedList
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
name|SimpleOrderedMap
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
name|XMLErrorLogger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|Reader
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
name|Date
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|XMLResponseParser
specifier|public
class|class
name|XMLResponseParser
extends|extends
name|ResponseParser
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XMLResponseParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
comment|// reuse the factory among all parser instances so things like string caches
comment|// won't be duplicated
DECL|field|factory
specifier|static
specifier|final
name|XMLInputFactory
name|factory
decl_stmt|;
static|static
block|{
name|factory
operator|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
try|try
block|{
comment|// nocommit: still true for 1.7?
comment|// The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
comment|// XMLInputFactory, as that implementation tries to cache and reuse the
comment|// XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
comment|// prevents this.
comment|// All other known open-source stax parsers (and the bea ref impl)
comment|// have thread-safe factories.
name|factory
operator|.
name|setProperty
argument_list|(
literal|"reuse-instance"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Other implementations will likely throw this exception since "reuse-instance"
comment|// isimplementation specific.
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to set the 'reuse-instance' property for the input factory: "
operator|+
name|factory
argument_list|)
expr_stmt|;
block|}
name|factory
operator|.
name|setXMLReporter
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
block|}
DECL|method|XMLResponseParser
specifier|public
name|XMLResponseParser
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getWriterType
specifier|public
name|String
name|getWriterType
parameter_list|()
block|{
return|return
literal|"xml"
return|;
block|}
annotation|@
name|Override
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|XMLStreamReader
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"parsing error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|processResponse
argument_list|(
name|parser
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
name|XMLStreamReader
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|in
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"parsing error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|processResponse
argument_list|(
name|parser
argument_list|)
return|;
block|}
comment|/**    * parse the text into a named list...    */
DECL|method|processResponse
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
block|{
try|try
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
init|;
name|event
operator|!=
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
condition|;
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
control|)
block|{
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"already read the response!"
argument_list|)
throw|;
block|}
comment|// only top-level element is "response
name|String
name|name
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"response"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"result"
argument_list|)
condition|)
block|{
name|response
operator|=
name|readNamedList
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"solr"
argument_list|)
condition|)
block|{
return|return
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"really needs to be response or result.  "
operator|+
literal|"not:"
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
return|return
name|response
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"parsing error"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
block|}
DECL|enum|KnownType
specifier|protected
enum|enum
name|KnownType
block|{
DECL|method|STR
DECL|method|STR
name|STR
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|txt
return|;
block|}
block|}
block|,
DECL|method|INT
DECL|method|INT
name|INT
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|txt
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|FLOAT
DECL|method|FLOAT
name|FLOAT
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Float
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|txt
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|DOUBLE
DECL|method|DOUBLE
name|DOUBLE
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Double
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|txt
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|LONG
DECL|method|LONG
name|LONG
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Long
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|txt
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|BOOL
DECL|method|BOOL
name|BOOL
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|txt
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|NULL
DECL|method|NULL
name|NULL
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|,
DECL|method|DATE
DECL|method|DATE
name|DATE
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Date
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
try|try
block|{
return|return
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|txt
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|,
DECL|method|ARR
DECL|method|ARR
name|ARR
argument_list|(
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|,
DECL|method|LST
DECL|method|LST
name|LST
argument_list|(
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|,
DECL|method|RESULT
DECL|method|RESULT
name|RESULT
argument_list|(
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|,
DECL|method|DOC
DECL|method|DOC
name|DOC
argument_list|(
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|;
DECL|field|isLeaf
specifier|final
name|boolean
name|isLeaf
decl_stmt|;
DECL|method|KnownType
name|KnownType
parameter_list|(
name|boolean
name|isLeaf
parameter_list|)
block|{
name|this
operator|.
name|isLeaf
operator|=
name|isLeaf
expr_stmt|;
block|}
DECL|method|read
specifier|public
specifier|abstract
name|Object
name|read
parameter_list|(
name|String
name|txt
parameter_list|)
function_decl|;
DECL|method|get
specifier|public
specifier|static
name|KnownType
name|get
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|KnownType
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
DECL|method|readNamedList
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|readNamedList
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|XMLStreamConstants
operator|.
name|START_ELEMENT
operator|!=
name|parser
operator|.
name|getEventType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"must be start element, not: "
operator|+
name|parser
operator|.
name|getEventType
argument_list|()
argument_list|)
throw|;
block|}
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|KnownType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
comment|// just eat up the events...
name|int
name|depth
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|parser
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|depth
operator|++
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// reset the text
name|type
operator|=
name|KnownType
operator|.
name|get
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this must be known type! not: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
name|name
operator|=
literal|null
expr_stmt|;
name|int
name|cnt
init|=
name|parser
operator|.
name|getAttributeCount
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|name
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|/** The name in a NamedList can actually be null         if( name == null ) {           throw new XMLStreamException( "requires 'name' attribute: "+parser.getLocalName(), parser.getLocation() );         }         **/
if|if
condition|(
operator|!
name|type
operator|.
name|isLeaf
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|LST
case|:
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|readNamedList
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|ARR
case|:
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|readArray
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|RESULT
case|:
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|readDocuments
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|DOC
case|:
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|readDocument
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"branch element not handled!"
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
operator|--
name|depth
operator|<
literal|0
condition|)
block|{
return|return
name|nl
return|;
block|}
comment|//System.out.println( "NL:ELEM:"+type+"::"+name+"::"+builder );
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|type
operator|.
name|read
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
comment|// TODO?  should this be trimmed? make sure it only gets one/two space?
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|builder
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|readArray
specifier|protected
name|List
argument_list|<
name|Object
argument_list|>
name|readArray
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|XMLStreamConstants
operator|.
name|START_ELEMENT
operator|!=
name|parser
operator|.
name|getEventType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"must be start element, not: "
operator|+
name|parser
operator|.
name|getEventType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
literal|"arr"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"must be 'arr', not: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|KnownType
name|type
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|parser
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|depth
operator|++
expr_stmt|;
name|KnownType
name|t
init|=
name|KnownType
operator|.
name|get
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this must be known type! not: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|t
expr_stmt|;
block|}
comment|/*** actually, there is no rule that arrays need the same type         else if( type != t&& !(t == KnownType.NULL || type == KnownType.NULL)) {           throw new RuntimeException( "arrays must have the same type! ("+type+"!="+t+") "+parser.getLocalName() );         }         ***/
name|type
operator|=
name|t
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// reset the text
if|if
condition|(
operator|!
name|type
operator|.
name|isLeaf
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|LST
case|:
name|vals
operator|.
name|add
argument_list|(
name|readNamedList
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|ARR
case|:
name|vals
operator|.
name|add
argument_list|(
name|readArray
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|RESULT
case|:
name|vals
operator|.
name|add
argument_list|(
name|readDocuments
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
case|case
name|DOC
case|:
name|vals
operator|.
name|add
argument_list|(
name|readDocument
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"branch element not handled!"
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
operator|--
name|depth
operator|<
literal|0
condition|)
block|{
return|return
name|vals
return|;
comment|// the last element is itself
block|}
comment|//System.out.println( "ARR:"+type+"::"+builder );
name|Object
name|val
init|=
name|type
operator|.
name|read
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
operator|&&
name|type
operator|!=
name|KnownType
operator|.
name|NULL
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"error reading value:"
operator|+
name|type
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
name|vals
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
comment|// TODO?  should this be trimmed? make sure it only gets one/two space?
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|builder
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|readDocuments
specifier|protected
name|SolrDocumentList
name|readDocuments
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
comment|// Parse the attributes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|v
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"numFound"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|docs
operator|.
name|setNumFound
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"start"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|docs
operator|.
name|setStart
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"maxScore"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|docs
operator|.
name|setMaxScore
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Read through each document
name|int
name|event
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|XMLStreamConstants
operator|.
name|START_ELEMENT
operator|==
name|event
condition|)
block|{
if|if
condition|(
operator|!
literal|"doc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"should be doc! "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" :: "
operator|+
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
name|docs
operator|.
name|add
argument_list|(
name|readDocument
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|==
name|event
condition|)
block|{
return|return
name|docs
return|;
comment|// only happens once
block|}
block|}
block|}
DECL|method|readDocument
specifier|protected
name|SolrDocument
name|readDocument
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|XMLStreamConstants
operator|.
name|START_ELEMENT
operator|!=
name|parser
operator|.
name|getEventType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"must be start element, not: "
operator|+
name|parser
operator|.
name|getEventType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
literal|"doc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"must be 'lst', not: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|KnownType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
comment|// just eat up the events...
name|int
name|depth
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|parser
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|depth
operator|++
expr_stmt|;
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// reset the text
name|type
operator|=
name|KnownType
operator|.
name|get
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this must be known type! not: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
name|name
operator|=
literal|null
expr_stmt|;
name|int
name|cnt
init|=
name|parser
operator|.
name|getAttributeCount
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|name
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"requires 'name' attribute: "
operator|+
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
comment|// Handle multi-valued fields
if|if
condition|(
name|type
operator|==
name|KnownType
operator|.
name|ARR
condition|)
block|{
for|for
control|(
name|Object
name|val
range|:
name|readArray
argument_list|(
name|parser
argument_list|)
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|depth
operator|--
expr_stmt|;
comment|// the array reading clears out the 'endElement'
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|KnownType
operator|.
name|LST
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|readNamedList
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|isLeaf
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nbot leaf!:"
operator|+
name|type
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"must be value or array"
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
operator|--
name|depth
operator|<
literal|0
condition|)
block|{
return|return
name|doc
return|;
block|}
comment|//System.out.println( "FIELD:"+type+"::"+name+"::"+builder );
name|Object
name|val
init|=
name|type
operator|.
name|read
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"error reading value:"
operator|+
name|type
argument_list|,
name|parser
operator|.
name|getLocation
argument_list|()
argument_list|)
throw|;
block|}
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
comment|// TODO?  should this be trimmed? make sure it only gets one/two space?
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|builder
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class
end_unit

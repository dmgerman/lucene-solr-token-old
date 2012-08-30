begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
package|;
end_package
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|DocumentBuilderFactory
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Helper methods for parsing XML  */
end_comment
begin_class
DECL|class|DOMUtils
specifier|public
class|class
name|DOMUtils
block|{
DECL|method|getChildByTagOrFail
specifier|public
specifier|static
name|Element
name|getChildByTagOrFail
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|kid
init|=
name|getChildByTagName
argument_list|(
name|e
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|kid
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|+
literal|" missing \""
operator|+
name|name
operator|+
literal|"\" child element"
argument_list|)
throw|;
block|}
return|return
name|kid
return|;
block|}
DECL|method|getFirstChildOrFail
specifier|public
specifier|static
name|Element
name|getFirstChildOrFail
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|kid
init|=
name|getFirstChildElement
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|kid
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|+
literal|" does not contain a child element"
argument_list|)
throw|;
block|}
return|return
name|kid
return|;
block|}
DECL|method|getAttributeOrFail
specifier|public
specifier|static
name|String
name|getAttributeOrFail
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|v
init|=
name|e
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|v
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|+
literal|" missing \""
operator|+
name|name
operator|+
literal|"\" attribute"
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
DECL|method|getAttributeWithInheritanceOrFail
specifier|public
specifier|static
name|String
name|getAttributeWithInheritanceOrFail
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|v
init|=
name|getAttributeWithInheritance
argument_list|(
name|e
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|v
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|+
literal|" missing \""
operator|+
name|name
operator|+
literal|"\" attribute"
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
DECL|method|getNonBlankTextOrFail
specifier|public
specifier|static
name|String
name|getNonBlankTextOrFail
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|v
init|=
name|getText
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|v
condition|)
name|v
operator|=
name|v
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|v
operator|||
literal|0
operator|==
name|v
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|+
literal|" has no text"
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
comment|/* Convenience method where there is only one child Element of a given name */
DECL|method|getChildByTagName
specifier|public
specifier|static
name|Element
name|getChildByTagName
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Node
name|kid
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|kid
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|)
operator|&&
operator|(
name|name
operator|.
name|equals
argument_list|(
name|kid
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|kid
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns an attribute value from this node, or first parent node with this attribute defined    *    * @param element    * @param attributeName    * @return A non-zero-length value if defined, otherwise null    */
DECL|method|getAttributeWithInheritance
specifier|public
specifier|static
name|String
name|getAttributeWithInheritance
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|attributeName
parameter_list|)
block|{
name|String
name|result
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|result
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|result
argument_list|)
operator|)
condition|)
block|{
name|Node
name|n
init|=
name|element
operator|.
name|getParentNode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|n
operator|==
name|element
operator|)
operator|||
operator|(
name|n
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|parent
init|=
operator|(
name|Element
operator|)
name|n
decl_stmt|;
return|return
name|getAttributeWithInheritance
argument_list|(
name|parent
argument_list|,
name|attributeName
argument_list|)
return|;
block|}
return|return
literal|null
return|;
comment|//we reached the top level of the document without finding attribute
block|}
return|return
name|result
return|;
block|}
comment|/* Convenience method where there is only one child Element of a given name */
DECL|method|getChildTextByTagName
specifier|public
specifier|static
name|String
name|getChildTextByTagName
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|tagName
parameter_list|)
block|{
name|Element
name|child
init|=
name|getChildByTagName
argument_list|(
name|e
argument_list|,
name|tagName
argument_list|)
decl_stmt|;
return|return
name|child
operator|!=
literal|null
condition|?
name|getText
argument_list|(
name|child
argument_list|)
else|:
literal|null
return|;
block|}
comment|/* Convenience method to append a new child with text*/
DECL|method|insertChild
specifier|public
specifier|static
name|Element
name|insertChild
parameter_list|(
name|Element
name|parent
parameter_list|,
name|String
name|tagName
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|Element
name|child
init|=
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createElement
argument_list|(
name|tagName
argument_list|)
decl_stmt|;
name|parent
operator|.
name|appendChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|child
operator|.
name|appendChild
argument_list|(
name|child
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
block|}
DECL|method|getAttribute
specifier|public
specifier|static
name|String
name|getAttribute
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|attributeName
parameter_list|,
name|String
name|deflt
parameter_list|)
block|{
name|String
name|result
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|result
argument_list|)
operator|)
condition|?
name|deflt
else|:
name|result
return|;
block|}
DECL|method|getAttribute
specifier|public
specifier|static
name|float
name|getAttribute
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|attributeName
parameter_list|,
name|float
name|deflt
parameter_list|)
block|{
name|String
name|result
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|result
argument_list|)
operator|)
condition|?
name|deflt
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|getAttribute
specifier|public
specifier|static
name|int
name|getAttribute
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|attributeName
parameter_list|,
name|int
name|deflt
parameter_list|)
block|{
name|String
name|result
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|result
argument_list|)
operator|)
condition|?
name|deflt
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|getAttribute
specifier|public
specifier|static
name|boolean
name|getAttribute
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|attributeName
parameter_list|,
name|boolean
name|deflt
parameter_list|)
block|{
name|String
name|result
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|==
literal|null
operator|)
operator|||
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|result
argument_list|)
operator|)
condition|?
name|deflt
else|:
name|Boolean
operator|.
name|valueOf
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/* Returns text of node and all child nodes - without markup */
comment|//MH changed to Node from Element 25/11/2005
DECL|method|getText
specifier|public
specifier|static
name|String
name|getText
parameter_list|(
name|Node
name|e
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|getTextBuffer
argument_list|(
name|e
argument_list|,
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getFirstChildElement
specifier|public
specifier|static
name|Element
name|getFirstChildElement
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
for|for
control|(
name|Node
name|kid
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
name|kid
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|kid
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getTextBuffer
specifier|private
specifier|static
name|void
name|getTextBuffer
parameter_list|(
name|Node
name|e
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
for|for
control|(
name|Node
name|kid
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
switch|switch
condition|(
name|kid
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
block|{
name|sb
operator|.
name|append
argument_list|(
name|kid
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
block|{
name|getTextBuffer
argument_list|(
name|kid
argument_list|,
name|sb
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|ENTITY_REFERENCE_NODE
case|:
block|{
name|getTextBuffer
argument_list|(
name|kid
argument_list|,
name|sb
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**    * Helper method to parse an XML file into a DOM tree, given a reader.    *    * @param is reader of the XML file to be parsed    * @return an org.w3c.dom.Document object    */
DECL|method|loadXML
specifier|public
specifier|static
name|Document
name|loadXML
parameter_list|(
name|Reader
name|is
parameter_list|)
block|{
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Parser configuration error"
argument_list|,
name|se
argument_list|)
throw|;
block|}
comment|// Step 3: parse the input file
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|db
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
comment|//doc = db.parse(is);
block|}
catch|catch
parameter_list|(
name|Exception
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error parsing file:"
operator|+
name|se
argument_list|,
name|se
argument_list|)
throw|;
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit

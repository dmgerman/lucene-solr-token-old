begin_unit
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|Text
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|tidy
operator|.
name|Tidy
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_comment
comment|/**  *  The<code>HtmlDocument</code> class creates a Lucene {@link  *  org.apache.lucene.document.Document} from an HTML document.<P>  *  *  It does this by using JTidy package. It can take input input  *  from {@link java.io.File} or {@link java.io.InputStream}.  *  *@author     Erik Hatcher  */
end_comment
begin_class
DECL|class|HtmlDocument
specifier|public
class|class
name|HtmlDocument
block|{
DECL|field|rawDoc
specifier|private
name|Element
name|rawDoc
decl_stmt|;
comment|//-------------------------------------------------------------
comment|// Constructors
comment|//-------------------------------------------------------------
comment|/**      *  Constructs an<code>HtmlDocument</code> from a {@link      *  java.io.File}.      *      *@param  file             the<code>File</code> containing the      *      HTML to parse      *@exception  IOException  if an I/O exception occurs      */
DECL|method|HtmlDocument
specifier|public
name|HtmlDocument
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Tidy
name|tidy
init|=
operator|new
name|Tidy
argument_list|()
decl_stmt|;
name|tidy
operator|.
name|setQuiet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tidy
operator|.
name|setShowWarnings
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|root
init|=
name|tidy
operator|.
name|parseDOM
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rawDoc
operator|=
name|root
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Constructs an<code>HtmlDocument</code> from an {@link      *  java.io.InputStream}.      *      *@param  is               the<code>InputStream</code>      *      containing the HTML      */
DECL|method|HtmlDocument
specifier|public
name|HtmlDocument
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|Tidy
name|tidy
init|=
operator|new
name|Tidy
argument_list|()
decl_stmt|;
name|tidy
operator|.
name|setQuiet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tidy
operator|.
name|setShowWarnings
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|root
init|=
name|tidy
operator|.
name|parseDOM
argument_list|(
name|is
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rawDoc
operator|=
name|root
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Creates a Lucene<code>Document</code> from an {@link      *  java.io.InputStream}.      *      *@param  is      */
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
DECL|method|getDocument
name|getDocument
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|HtmlDocument
name|htmlDoc
init|=
operator|new
name|HtmlDocument
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|luceneDoc
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
argument_list|()
decl_stmt|;
name|luceneDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
name|htmlDoc
operator|.
name|getTitle
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|luceneDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|htmlDoc
operator|.
name|getBody
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|luceneDoc
return|;
block|}
comment|//-------------------------------------------------------------
comment|// Public methods
comment|//-------------------------------------------------------------
comment|/**      *  Creates a Lucene<code>Document</code> from a {@link      *  java.io.File}.      *      *@param  file      *@exception  IOException      */
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
DECL|method|Document
name|Document
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|HtmlDocument
name|htmlDoc
init|=
operator|new
name|HtmlDocument
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|luceneDoc
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
argument_list|()
decl_stmt|;
name|luceneDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
name|htmlDoc
operator|.
name|getTitle
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|luceneDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|htmlDoc
operator|.
name|getBody
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|sw
operator|.
name|write
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|contents
operator|=
name|sw
operator|.
name|toString
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|luceneDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"rawcontents"
argument_list|,
name|contents
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|luceneDoc
return|;
block|}
comment|//-------------------------------------------------------------
comment|// Private methods
comment|//-------------------------------------------------------------
comment|/**      *  Runs<code>HtmlDocument</code> on the files specified on      *  the command line.      *      *@param  args           Command line arguments      *@exception  Exception  Description of Exception      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
comment|//         HtmlDocument doc = new HtmlDocument(new File(args[0]));
comment|//         System.out.println("Title = " + doc.getTitle());
comment|//         System.out.println("Body  = " + doc.getBody());
name|HtmlDocument
name|doc
init|=
operator|new
name|HtmlDocument
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Title = "
operator|+
name|doc
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Body  = "
operator|+
name|doc
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the title attribute of the<code>HtmlDocument</code>      *  object.      *      *@return    the title value      */
DECL|method|getTitle
specifier|public
name|String
name|getTitle
parameter_list|()
block|{
if|if
condition|(
name|rawDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|title
init|=
literal|""
decl_stmt|;
name|NodeList
name|nl
init|=
name|rawDoc
operator|.
name|getElementsByTagName
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|titleElement
init|=
operator|(
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
decl_stmt|;
name|Text
name|text
init|=
operator|(
name|Text
operator|)
name|titleElement
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|title
operator|=
name|text
operator|.
name|getData
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|title
return|;
block|}
comment|/**      *  Gets the bodyText attribute of the      *<code>HtmlDocument</code> object.      *      *@return    the bodyText value      */
DECL|method|getBody
specifier|public
name|String
name|getBody
parameter_list|()
block|{
if|if
condition|(
name|rawDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|body
init|=
literal|""
decl_stmt|;
name|NodeList
name|nl
init|=
name|rawDoc
operator|.
name|getElementsByTagName
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|body
operator|=
name|getBodyText
argument_list|(
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|body
return|;
block|}
comment|/**      *  Gets the bodyText attribute of the      *<code>HtmlDocument</code> object.      *      *@param  node  a DOM Node      *@return       The bodyText value      */
DECL|method|getBodyText
specifier|private
name|String
name|getBodyText
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NodeList
name|nl
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|buffer
operator|.
name|append
argument_list|(
name|getBodyText
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|buffer
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Text
operator|)
name|child
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
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

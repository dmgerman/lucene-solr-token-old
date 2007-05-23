begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileInputStream
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|io
operator|.
name|StringReader
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|net
operator|.
name|ProtocolException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_comment
comment|/**  * A simple utility class for posting raw updates to a Solr server,   * has a main method so it can be run on the command line.  *   */
end_comment
begin_class
DECL|class|SimplePostTool
specifier|public
class|class
name|SimplePostTool
block|{
DECL|field|DEFAULT_POST_URL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_POST_URL
init|=
literal|"http://localhost:8983/solr/update"
decl_stmt|;
DECL|field|POST_ENCODING
specifier|public
specifier|static
specifier|final
name|String
name|POST_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|VERSION_OF_THIS_TOOL
specifier|public
specifier|static
specifier|final
name|String
name|VERSION_OF_THIS_TOOL
init|=
literal|"1.2"
decl_stmt|;
DECL|field|SOLR_OK_RESPONSE_EXCERPT
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_OK_RESPONSE_EXCERPT
init|=
literal|"<int name=\"status\">0</int>"
decl_stmt|;
DECL|field|DEFAULT_COMMIT
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_COMMIT
init|=
literal|"yes"
decl_stmt|;
DECL|field|DATA_MODE_FILES
specifier|private
specifier|static
specifier|final
name|String
name|DATA_MODE_FILES
init|=
literal|"files"
decl_stmt|;
DECL|field|DATA_MODE_ARGS
specifier|private
specifier|static
specifier|final
name|String
name|DATA_MODE_ARGS
init|=
literal|"args"
decl_stmt|;
DECL|field|DATA_MODE_STDIN
specifier|private
specifier|static
specifier|final
name|String
name|DATA_MODE_STDIN
init|=
literal|"stdin"
decl_stmt|;
DECL|field|DEFAULT_DATA_MODE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DATA_MODE
init|=
name|DATA_MODE_FILES
decl_stmt|;
DECL|field|DATA_MODES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DATA_MODES
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|DATA_MODES
operator|.
name|add
argument_list|(
name|DATA_MODE_FILES
argument_list|)
expr_stmt|;
name|DATA_MODES
operator|.
name|add
argument_list|(
name|DATA_MODE_ARGS
argument_list|)
expr_stmt|;
name|DATA_MODES
operator|.
name|add
argument_list|(
name|DATA_MODE_STDIN
argument_list|)
expr_stmt|;
block|}
DECL|field|solrUrl
specifier|protected
name|URL
name|solrUrl
decl_stmt|;
DECL|class|PostException
specifier|private
class|class
name|PostException
extends|extends
name|RuntimeException
block|{
DECL|method|PostException
name|PostException
parameter_list|(
name|String
name|reason
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|reason
operator|+
literal|" (POST URL="
operator|+
name|solrUrl
operator|+
literal|")"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
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
block|{
name|info
argument_list|(
literal|"version "
operator|+
name|VERSION_OF_THIS_TOOL
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|<
name|args
operator|.
name|length
operator|&&
literal|"-help"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"This is a simple command line tool for POSTing raw XML to a Solr\n"
operator|+
literal|"port.  XML data can be read from files specified as commandline\n"
operator|+
literal|"args; as raw commandline arg strings; or via STDIN.\n"
operator|+
literal|"Examples:\n"
operator|+
literal|"  java -Ddata=files -jar post.jar *.xml\n"
operator|+
literal|"  java -Ddata=args  -jar post.jar '<delete><id>42</id></delete>'\n"
operator|+
literal|"  java -Ddata=stdin -jar post.jar< hd.xml\n"
operator|+
literal|"Other options controlled by System Properties include the Solr\n"
operator|+
literal|"URL to POST to, and whether a commit should be executed.  These\n"
operator|+
literal|"are the defaults for all System Properties...\n"
operator|+
literal|"  -Ddata="
operator|+
name|DEFAULT_DATA_MODE
operator|+
literal|"\n"
operator|+
literal|"  -Durl="
operator|+
name|DEFAULT_POST_URL
operator|+
literal|"\n"
operator|+
literal|"  -Dcommit="
operator|+
name|DEFAULT_COMMIT
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
return|return;
block|}
name|URL
name|u
init|=
literal|null
decl_stmt|;
try|try
block|{
name|u
operator|=
operator|new
name|URL
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"url"
argument_list|,
name|DEFAULT_POST_URL
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|fatal
argument_list|(
literal|"System Property 'url' is not a valid URL: "
operator|+
name|u
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SimplePostTool
name|t
init|=
operator|new
name|SimplePostTool
argument_list|(
name|u
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mode
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"data"
argument_list|,
name|DEFAULT_DATA_MODE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|DATA_MODES
operator|.
name|contains
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|fatal
argument_list|(
literal|"System Property 'data' is not valid for this tool: "
operator|+
name|mode
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|DATA_MODE_FILES
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
if|if
condition|(
literal|0
operator|<
name|args
operator|.
name|length
condition|)
block|{
name|info
argument_list|(
literal|"POSTing files to "
operator|+
name|u
operator|+
literal|".."
argument_list|)
expr_stmt|;
specifier|final
name|int
name|posted
init|=
name|t
operator|.
name|postFiles
argument_list|(
name|args
argument_list|,
literal|0
argument_list|)
decl_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DATA_MODE_ARGS
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
if|if
condition|(
literal|0
operator|<
name|args
operator|.
name|length
condition|)
block|{
name|info
argument_list|(
literal|"POSTing args to "
operator|+
name|u
operator|+
literal|".."
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|a
range|:
name|args
control|)
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|t
operator|.
name|postData
argument_list|(
operator|new
name|StringReader
argument_list|(
name|a
argument_list|)
argument_list|,
name|sw
argument_list|)
expr_stmt|;
name|warnIfNotExpectedResponse
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|SOLR_OK_RESPONSE_EXCERPT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|DATA_MODE_STDIN
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|info
argument_list|(
literal|"POSTing stdin to "
operator|+
name|u
operator|+
literal|".."
argument_list|)
expr_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|t
operator|.
name|postData
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|POST_ENCODING
argument_list|)
argument_list|,
name|sw
argument_list|)
expr_stmt|;
name|warnIfNotExpectedResponse
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|SOLR_OK_RESPONSE_EXCERPT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"commit"
argument_list|,
name|DEFAULT_COMMIT
argument_list|)
argument_list|)
condition|)
block|{
name|info
argument_list|(
literal|"COMMITting Solr index changes.."
argument_list|)
expr_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|t
operator|.
name|commit
argument_list|(
name|sw
argument_list|)
expr_stmt|;
name|warnIfNotExpectedResponse
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|SOLR_OK_RESPONSE_EXCERPT
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fatal
argument_list|(
literal|"Unexpected IOException "
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Post all filenames provided in args, return the number of files posted*/
DECL|method|postFiles
name|int
name|postFiles
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|int
name|startIndexInArgs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|filesPosted
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|startIndexInArgs
init|;
name|j
operator|<
name|args
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|File
name|srcFile
init|=
operator|new
name|File
argument_list|(
name|args
index|[
name|j
index|]
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|srcFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|info
argument_list|(
literal|"POSTing file "
operator|+
name|srcFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|postFile
argument_list|(
name|srcFile
argument_list|,
name|sw
argument_list|)
expr_stmt|;
name|filesPosted
operator|++
expr_stmt|;
name|warnIfNotExpectedResponse
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|SOLR_OK_RESPONSE_EXCERPT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|warn
argument_list|(
literal|"Cannot read input file: "
operator|+
name|srcFile
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filesPosted
return|;
block|}
comment|/** Check what Solr replied to a POST, and complain if it's not what we expected.    *  TODO: parse the response and check it XMLwise, here we just check it as an unparsed String      */
DECL|method|warnIfNotExpectedResponse
specifier|static
name|void
name|warnIfNotExpectedResponse
parameter_list|(
name|String
name|actual
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
if|if
condition|(
name|actual
operator|.
name|indexOf
argument_list|(
name|expected
argument_list|)
operator|<
literal|0
condition|)
block|{
name|warn
argument_list|(
literal|"Unexpected response from Solr: '"
operator|+
name|actual
operator|+
literal|"' does not contain '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|warn
specifier|static
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"SimplePostTool: WARNING: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|static
name|void
name|info
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SimplePostTool: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|fatal
specifier|static
name|void
name|fatal
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"SimplePostTool: FATAL: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an instance for posting data to the specified Solr URL     * (ie: "http://localhost:8983/solr/update")    */
DECL|method|SimplePostTool
specifier|public
name|SimplePostTool
parameter_list|(
name|URL
name|solrUrl
parameter_list|)
block|{
name|this
operator|.
name|solrUrl
operator|=
name|solrUrl
expr_stmt|;
name|warn
argument_list|(
literal|"Make sure your XML documents are encoded in "
operator|+
name|POST_ENCODING
operator|+
literal|", other encodings are not currently supported"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Does a simple commit operation     */
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|Writer
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|postData
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"<commit/>"
argument_list|)
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/**    * Opens the file and posts it's contents to the solrUrl,    * writes to response to output.    * @throws UnsupportedEncodingException     */
DECL|method|postFile
specifier|public
name|void
name|postFile
parameter_list|(
name|File
name|file
parameter_list|,
name|Writer
name|output
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnsupportedEncodingException
block|{
comment|// FIXME; use a real XML parser to read files, so as to support various encodings
comment|// (and we can only post well-formed XML anyway)
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|POST_ENCODING
argument_list|)
decl_stmt|;
try|try
block|{
name|postData
argument_list|(
name|reader
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PostException
argument_list|(
literal|"IOException while closing file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Reads data from the data reader and posts it to solr,    * writes to the response to output    */
DECL|method|postData
specifier|public
name|void
name|postData
parameter_list|(
name|Reader
name|data
parameter_list|,
name|Writer
name|output
parameter_list|)
block|{
name|HttpURLConnection
name|urlc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|urlc
operator|=
operator|(
name|HttpURLConnection
operator|)
name|solrUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
try|try
block|{
name|urlc
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProtocolException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PostException
argument_list|(
literal|"Shouldn't happen: HttpURLConnection doesn't support POST??"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|urlc
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|urlc
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|urlc
operator|.
name|setUseCaches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|urlc
operator|.
name|setAllowUserInteraction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|urlc
operator|.
name|setRequestProperty
argument_list|(
literal|"Content-type"
argument_list|,
literal|"text/xml; charset="
operator|+
name|POST_ENCODING
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|urlc
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|POST_ENCODING
argument_list|)
decl_stmt|;
name|pipe
argument_list|(
name|data
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PostException
argument_list|(
literal|"IOException while posting data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|InputStream
name|in
init|=
name|urlc
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|pipe
argument_list|(
name|reader
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PostException
argument_list|(
literal|"IOException while reading response"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fatal
argument_list|(
literal|"Connection error (is Solr running at "
operator|+
name|solrUrl
operator|+
literal|" ?): "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|urlc
operator|!=
literal|null
condition|)
name|urlc
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Pipes everything from the reader to the writer via a buffer    */
DECL|method|pipe
specifier|private
specifier|static
name|void
name|pipe
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

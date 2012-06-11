begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParsePosition
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Locale
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|TrecDocParser
operator|.
name|ParsePathType
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|StreamUtils
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|StringBuilderReader
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
name|ThreadInterruptedException
import|;
end_import
begin_comment
comment|/**  * Implements a {@link ContentSource} over the TREC collection.  *<p>  * Supports the following configuration parameters (on top of  * {@link ContentSource}):  *<ul>  *<li><b>work.dir</b> - specifies the working directory. Required if "docs.dir"  * denotes a relative path (<b>default=work</b>).  *<li><b>docs.dir</b> - specifies the directory where the TREC files reside.  * Can be set to a relative path if "work.dir" is also specified  * (<b>default=trec</b>).  *<li><b>trec.doc.parser</b> - specifies the {@link TrecDocParser} class to use for  * parsing the TREC documents content (<b>default=TrecGov2Parser</b>).  *<li><b>html.parser</b> - specifies the {@link HTMLParser} class to use for  * parsing the HTML parts of the TREC documents content (<b>default=DemoHTMLParser</b>).  *<li><b>content.source.encoding</b> - if not specified, ISO-8859-1 is used.  *<li><b>content.source.excludeIteration</b> - if true, do not append iteration number to docname  *</ul>  */
end_comment
begin_class
DECL|class|TrecContentSource
specifier|public
class|class
name|TrecContentSource
extends|extends
name|ContentSource
block|{
DECL|class|DateFormatInfo
specifier|private
specifier|static
specifier|final
class|class
name|DateFormatInfo
block|{
DECL|field|dfs
name|DateFormat
index|[]
name|dfs
decl_stmt|;
DECL|field|pos
name|ParsePosition
name|pos
decl_stmt|;
block|}
DECL|field|DOCNO
specifier|public
specifier|static
specifier|final
name|String
name|DOCNO
init|=
literal|"<DOCNO>"
decl_stmt|;
DECL|field|TERMINATING_DOCNO
specifier|public
specifier|static
specifier|final
name|String
name|TERMINATING_DOCNO
init|=
literal|"</DOCNO>"
decl_stmt|;
DECL|field|DOC
specifier|public
specifier|static
specifier|final
name|String
name|DOC
init|=
literal|"<DOC>"
decl_stmt|;
DECL|field|TERMINATING_DOC
specifier|public
specifier|static
specifier|final
name|String
name|TERMINATING_DOC
init|=
literal|"</DOC>"
decl_stmt|;
comment|/** separator between lines in the byffer */
DECL|field|NEW_LINE
specifier|public
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|DATE_FORMATS
specifier|private
specifier|static
specifier|final
name|String
name|DATE_FORMATS
index|[]
init|=
block|{
literal|"EEE, dd MMM yyyy kk:mm:ss z"
block|,
comment|// Tue, 09 Dec 2003 22:39:08 GMT
literal|"EEE MMM dd kk:mm:ss yyyy z"
block|,
comment|// Tue Dec 09 16:45:08 2003 EST
literal|"EEE, dd-MMM-':'y kk:mm:ss z"
block|,
comment|// Tue, 09 Dec 2003 22:39:08 GMT
literal|"EEE, dd-MMM-yyy kk:mm:ss z"
block|,
comment|// Tue, 09 Dec 2003 22:39:08 GMT
literal|"EEE MMM dd kk:mm:ss yyyy"
block|,
comment|// Tue Dec 09 16:45:08 2003
literal|"dd MMM yyyy"
block|,
comment|// 1 March 1994
literal|"MMM dd, yyyy"
block|,
comment|// February 3, 1994
literal|"yyMMdd"
block|,
comment|// 910513
literal|"hhmm z.z.z. MMM dd, yyyy"
block|,
comment|// 0901 u.t.c. April 28, 1994
block|}
decl_stmt|;
DECL|field|dateFormats
specifier|private
name|ThreadLocal
argument_list|<
name|DateFormatInfo
argument_list|>
name|dateFormats
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DateFormatInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|trecDocReader
specifier|private
name|ThreadLocal
argument_list|<
name|StringBuilderReader
argument_list|>
name|trecDocReader
init|=
operator|new
name|ThreadLocal
argument_list|<
name|StringBuilderReader
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|trecDocBuffer
specifier|private
name|ThreadLocal
argument_list|<
name|StringBuilder
argument_list|>
name|trecDocBuffer
init|=
operator|new
name|ThreadLocal
argument_list|<
name|StringBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|inputFiles
specifier|private
name|ArrayList
argument_list|<
name|File
argument_list|>
name|inputFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nextFile
specifier|private
name|int
name|nextFile
init|=
literal|0
decl_stmt|;
DECL|field|rawDocSize
specifier|private
name|int
name|rawDocSize
init|=
literal|0
decl_stmt|;
comment|// Use to synchronize threads on reading from the TREC documents.
DECL|field|lock
specifier|private
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// Required for test
DECL|field|reader
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|iteration
name|int
name|iteration
init|=
literal|0
decl_stmt|;
DECL|field|htmlParser
name|HTMLParser
name|htmlParser
decl_stmt|;
DECL|field|excludeDocnameIteration
specifier|private
name|boolean
name|excludeDocnameIteration
decl_stmt|;
DECL|field|trecDocParser
specifier|private
name|TrecDocParser
name|trecDocParser
init|=
operator|new
name|TrecGov2Parser
argument_list|()
decl_stmt|;
comment|// default
DECL|field|currPathType
name|ParsePathType
name|currPathType
decl_stmt|;
comment|// not private for tests
DECL|method|getDateFormatInfo
specifier|private
name|DateFormatInfo
name|getDateFormatInfo
parameter_list|()
block|{
name|DateFormatInfo
name|dfi
init|=
name|dateFormats
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|dfi
operator|==
literal|null
condition|)
block|{
name|dfi
operator|=
operator|new
name|DateFormatInfo
argument_list|()
expr_stmt|;
name|dfi
operator|.
name|dfs
operator|=
operator|new
name|SimpleDateFormat
index|[
name|DATE_FORMATS
operator|.
name|length
index|]
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
name|dfi
operator|.
name|dfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dfi
operator|.
name|dfs
index|[
name|i
index|]
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FORMATS
index|[
name|i
index|]
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|dfi
operator|.
name|dfs
index|[
name|i
index|]
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|dfi
operator|.
name|pos
operator|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dateFormats
operator|.
name|set
argument_list|(
name|dfi
argument_list|)
expr_stmt|;
block|}
return|return
name|dfi
return|;
block|}
DECL|method|getDocBuffer
specifier|private
name|StringBuilder
name|getDocBuffer
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
name|trecDocBuffer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|trecDocBuffer
operator|.
name|set
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
DECL|method|getTrecDocReader
name|Reader
name|getTrecDocReader
parameter_list|(
name|StringBuilder
name|docBuffer
parameter_list|)
block|{
name|StringBuilderReader
name|r
init|=
name|trecDocReader
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
operator|new
name|StringBuilderReader
argument_list|(
name|docBuffer
argument_list|)
expr_stmt|;
name|trecDocReader
operator|.
name|set
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r
operator|.
name|set
argument_list|(
name|docBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|getHtmlParser
name|HTMLParser
name|getHtmlParser
parameter_list|()
block|{
return|return
name|htmlParser
return|;
block|}
comment|/**    * Read until a line starting with the specified<code>lineStart</code>.    * @param buf buffer for collecting the data if so specified/     * @param lineStart line start to look for, must not be null.    * @param collectMatchLine whether to collect the matching line into<code>buffer</code>.    * @param collectAll whether to collect all lines into<code>buffer</code>.    * @throws IOException    * @throws NoMoreDataException    */
DECL|method|read
specifier|private
name|void
name|read
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|lineStart
parameter_list|,
name|boolean
name|collectMatchLine
parameter_list|,
name|boolean
name|collectAll
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoMoreDataException
block|{
name|String
name|sep
init|=
literal|""
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|rawDocSize
operator|+=
name|line
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|lineStart
operator|!=
literal|null
operator|&&
name|line
operator|.
name|startsWith
argument_list|(
name|lineStart
argument_list|)
condition|)
block|{
if|if
condition|(
name|collectMatchLine
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|NEW_LINE
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|collectAll
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|NEW_LINE
expr_stmt|;
block|}
block|}
block|}
DECL|method|openNextFile
name|void
name|openNextFile
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
name|currPathType
operator|=
literal|null
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextFile
operator|>=
name|inputFiles
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// exhausted files, start a new round, unless forever set to false.
if|if
condition|(
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|++
expr_stmt|;
block|}
name|File
name|f
init|=
name|inputFiles
operator|.
name|get
argument_list|(
name|nextFile
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"opening: "
operator|+
name|f
operator|+
literal|" length: "
operator|+
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|InputStream
name|inputStream
init|=
name|StreamUtils
operator|.
name|inputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// support either gzip, bzip2, or regular text file, by extension
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|StreamUtils
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|currPathType
operator|=
name|TrecDocParser
operator|.
name|pathType
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skipping 'bad' file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" due to "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
block|}
block|}
DECL|method|parseDate
specifier|public
name|Date
name|parseDate
parameter_list|(
name|String
name|dateStr
parameter_list|)
block|{
name|dateStr
operator|=
name|dateStr
operator|.
name|trim
argument_list|()
expr_stmt|;
name|DateFormatInfo
name|dfi
init|=
name|getDateFormatInfo
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
name|dfi
operator|.
name|dfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DateFormat
name|df
init|=
name|dfi
operator|.
name|dfs
index|[
name|i
index|]
decl_stmt|;
name|dfi
operator|.
name|pos
operator|.
name|setIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dfi
operator|.
name|pos
operator|.
name|setErrorIndex
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Date
name|d
init|=
name|df
operator|.
name|parse
argument_list|(
name|dateStr
argument_list|,
name|dfi
operator|.
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
comment|// Parse succeeded.
return|return
name|d
return|;
block|}
block|}
comment|// do not fail test just because a date could not be parsed
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"failed to parse date (assigning 'now') for: "
operator|+
name|dateStr
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
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
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"failed to close reader !"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
name|String
name|name
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|docBuf
init|=
name|getDocBuffer
argument_list|()
decl_stmt|;
name|ParsePathType
name|parsedPathType
decl_stmt|;
comment|// protect reading from the TREC files by multiple threads. The rest of the
comment|// method, i.e., parsing the content and returning the DocData can run unprotected.
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
block|}
comment|// 1. skip until doc start - required for all TREC formats
name|docBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|read
argument_list|(
name|docBuf
argument_list|,
name|DOC
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// save parsedFile for passing trecDataParser after the sync block, in
comment|// case another thread will open another file in between.
name|parsedPathType
operator|=
name|currPathType
expr_stmt|;
comment|// 2. name - required for all TREC formats
name|docBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|read
argument_list|(
name|docBuf
argument_list|,
name|DOCNO
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|name
operator|=
name|docBuf
operator|.
name|substring
argument_list|(
name|DOCNO
operator|.
name|length
argument_list|()
argument_list|,
name|docBuf
operator|.
name|indexOf
argument_list|(
name|TERMINATING_DOCNO
argument_list|,
name|DOCNO
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|excludeDocnameIteration
condition|)
block|{
name|name
operator|=
name|name
operator|+
literal|"_"
operator|+
name|iteration
expr_stmt|;
block|}
comment|// 3. read all until end of doc
name|docBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|read
argument_list|(
name|docBuf
argument_list|,
name|TERMINATING_DOC
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// count char length of text to be parsed (may be larger than the resulted plain doc body text).
name|addBytes
argument_list|(
name|docBuf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// This code segment relies on HtmlParser being thread safe. When we get
comment|// here, everything else is already private to that thread, so we're safe.
try|try
block|{
name|docData
operator|=
name|trecDocParser
operator|.
name|parse
argument_list|(
name|docData
argument_list|,
name|name
argument_list|,
name|this
argument_list|,
name|docBuf
argument_list|,
name|parsedPathType
argument_list|)
expr_stmt|;
name|addItem
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
return|return
name|docData
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// dirs
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|d
init|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.dir"
argument_list|,
literal|"trec"
argument_list|)
decl_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dataDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
comment|// files
name|collectFiles
argument_list|(
name|dataDir
argument_list|,
name|inputFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputFiles
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No files in dataDir: "
operator|+
name|dataDir
argument_list|)
throw|;
block|}
comment|// trec doc parser
try|try
block|{
name|String
name|trecDocParserClassName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"trec.doc.parser"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.TrecGov2Parser"
argument_list|)
decl_stmt|;
name|trecDocParser
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|trecDocParserClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|TrecDocParser
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Should not get here. Throw runtime exception.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// html parser
try|try
block|{
name|String
name|htmlParserClassName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"html.parser"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser"
argument_list|)
decl_stmt|;
name|htmlParser
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|htmlParserClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|HTMLParser
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Should not get here. Throw runtime exception.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// encoding
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
name|encoding
operator|=
literal|"ISO-8859-1"
expr_stmt|;
block|}
comment|// iteration exclusion in doc name
name|excludeDocnameIteration
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.excludeIteration"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Closeable
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
name|FileOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
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
begin_comment
comment|/** This class emulates the new Java 7 "Try-With-Resources" statement.  * Remove once Lucene is on Java 7.  * @lucene.internal */
end_comment
begin_class
DECL|class|IOUtils
specifier|public
specifier|final
class|class
name|IOUtils
block|{
comment|/**    * UTF-8 charset string    * @see Charset#forName(String)    */
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|String
name|UTF_8
init|=
literal|"UTF-8"
decl_stmt|;
comment|/**    * UTF-8 {@link Charset} instance to prevent repeated    * {@link Charset#forName(String)} lookups    */
DECL|field|CHARSET_UTF_8
specifier|public
specifier|static
specifier|final
name|Charset
name|CHARSET_UTF_8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|method|IOUtils
specifier|private
name|IOUtils
parameter_list|()
block|{}
comment|// no instance
comment|/**    *<p>Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions. Some of the<tt>Closeable</tt>s    * may be null, they are ignored. After everything is closed, method either throws<tt>priorException</tt>,    * if one is supplied, or the first of suppressed exceptions, or completes normally.</p>    *<p>Sample usage:<br/>    *<pre>    * Closeable resource1 = null, resource2 = null, resource3 = null;    * ExpectedException priorE = null;    * try {    *   resource1 = ...; resource2 = ...; resource3 = ...; // Acquisition may throw ExpectedException    *   ..do..stuff.. // May throw ExpectedException    * } catch (ExpectedException e) {    *   priorE = e;    * } finally {    *   closeWhileHandlingException(priorE, resource1, resource2, resource3);    * }    *</pre>    *</p>    * @param priorException<tt>null</tt> or an exception that will be rethrown after method completion    * @param objects         objects to call<tt>close()</tt> on    */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|void
name|closeWhileHandlingException
parameter_list|(
name|E
name|priorException
parameter_list|,
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|E
throws|,
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
operator|(
name|priorException
operator|==
literal|null
operator|)
condition|?
name|th
else|:
name|priorException
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|priorException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|priorException
throw|;
block|}
elseif|else
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/** @see #closeWhileHandlingException(Exception, Closeable...) */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|void
name|closeWhileHandlingException
parameter_list|(
name|E
name|priorException
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
throws|throws
name|E
throws|,
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
operator|(
name|priorException
operator|==
literal|null
operator|)
condition|?
name|th
else|:
name|priorException
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|priorException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|priorException
throw|;
block|}
elseif|else
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s.  Some of the    *<tt>Closeable</tt>s may be null; they are    * ignored.  After everything is closed, the method either    * throws the first exception it hit while closing, or    * completes normally if there were no exceptions.    *     * @param objects    *          objects to call<tt>close()</tt> on    */
DECL|method|close
specifier|public
specifier|static
name|void
name|close
parameter_list|(
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * @see #close(Closeable...)    */
DECL|method|close
specifier|public
specifier|static
name|void
name|close
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions.    * Some of the<tt>Closeable</tt>s may be null, they are ignored.    *     * @param objects    *          objects to call<tt>close()</tt> on    */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
name|void
name|closeWhileHandlingException
parameter_list|(
name|Closeable
modifier|...
name|objects
parameter_list|)
block|{
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
block|}
block|}
comment|/**    * @see #closeWhileHandlingException(Closeable...)    */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
name|void
name|closeWhileHandlingException
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
block|{
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
block|}
block|}
comment|/** This reflected {@link Method} is {@code null} before Java 7 */
DECL|field|SUPPRESS_METHOD
specifier|private
specifier|static
specifier|final
name|Method
name|SUPPRESS_METHOD
decl_stmt|;
static|static
block|{
name|Method
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|Throwable
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"addSuppressed"
argument_list|,
name|Throwable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|m
operator|=
literal|null
expr_stmt|;
block|}
name|SUPPRESS_METHOD
operator|=
name|m
expr_stmt|;
block|}
comment|/** adds a Throwable to the list of suppressed Exceptions of the first Throwable (if Java 7 is detected)    * @param exception this exception should get the suppressed one added    * @param suppressed the suppressed exception    */
DECL|method|addSuppressed
specifier|private
specifier|static
specifier|final
name|void
name|addSuppressed
parameter_list|(
name|Throwable
name|exception
parameter_list|,
name|Throwable
name|suppressed
parameter_list|)
block|{
if|if
condition|(
name|SUPPRESS_METHOD
operator|!=
literal|null
operator|&&
name|exception
operator|!=
literal|null
operator|&&
name|suppressed
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|SUPPRESS_METHOD
operator|.
name|invoke
argument_list|(
name|exception
argument_list|,
name|suppressed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore any exceptions caused by invoking (e.g. security constraints)
block|}
block|}
block|}
comment|/**    * Wrapping the given {@link InputStream} in a reader using a {@link CharsetDecoder}.    * Unlike Java's defaults this reader will throw an exception if your it detects     * the read charset doesn't match the expected {@link Charset}.     *<p>    * Decoding readers are useful to load configuration files, stopword lists or synonym files    * to detect character set problems. However, its not recommended to use as a common purpose     * reader.    *     * @param stream the stream to wrap in a reader    * @param charSet the expected charset    * @return a wrapping reader    */
DECL|method|getDecodingReader
specifier|public
specifier|static
name|Reader
name|getDecodingReader
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Charset
name|charSet
parameter_list|)
block|{
specifier|final
name|CharsetDecoder
name|charSetDecoder
init|=
name|charSet
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
return|return
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|charSetDecoder
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Opens a Reader for the given {@link File} using a {@link CharsetDecoder}.    * Unlike Java's defaults this reader will throw an exception if your it detects     * the read charset doesn't match the expected {@link Charset}.     *<p>    * Decoding readers are useful to load configuration files, stopword lists or synonym files    * to detect character set problems. However, its not recommended to use as a common purpose     * reader.    * @param file the file to open a reader on    * @param charSet the expected charset    * @return a reader to read the given file    */
DECL|method|getDecodingReader
specifier|public
specifier|static
name|Reader
name|getDecodingReader
parameter_list|(
name|File
name|file
parameter_list|,
name|Charset
name|charSet
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|stream
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
specifier|final
name|Reader
name|reader
init|=
name|getDecodingReader
argument_list|(
name|stream
argument_list|,
name|charSet
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Opens a Reader for the given resource using a {@link CharsetDecoder}.    * Unlike Java's defaults this reader will throw an exception if your it detects     * the read charset doesn't match the expected {@link Charset}.     *<p>    * Decoding readers are useful to load configuration files, stopword lists or synonym files    * to detect character set problems. However, its not recommended to use as a common purpose     * reader.    * @param clazz the class used to locate the resource    * @param resource the resource name to load    * @param charSet the expected charset    * @return a reader to read the given file    *     */
DECL|method|getDecodingReader
specifier|public
specifier|static
name|Reader
name|getDecodingReader
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|resource
parameter_list|,
name|Charset
name|charSet
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
expr_stmt|;
specifier|final
name|Reader
name|reader
init|=
name|getDecodingReader
argument_list|(
name|stream
argument_list|,
name|charSet
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteFilesIgnoringExceptions
specifier|public
specifier|static
name|void
name|deleteFilesIgnoringExceptions
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
modifier|...
name|files
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|files
control|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|/**    * Copy one file's contents to another file. The target will be overwritten    * if it exists. The source must exist.    */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|target
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|8
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|close
argument_list|(
name|fis
argument_list|,
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

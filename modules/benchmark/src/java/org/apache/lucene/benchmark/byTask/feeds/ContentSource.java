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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|CompressorException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|CompressorStreamFactory
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
begin_comment
comment|/**  * Represents content from a specified source, such as TREC, Reuters etc. A  * {@link ContentSource} is responsible for creating {@link DocData} objects for  * its documents to be consumed by {@link DocMaker}. It also keeps track  * of various statistics, such as how many documents were generated, size in  * bytes etc.  *<p>  * Supports the following configuration parameters:  *<ul>  *<li><b>content.source.forever</b> - specifies whether to generate documents  * forever (<b>default=true</b>).  *<li><b>content.source.verbose</b> - specifies whether messages should be  * output by the content source (<b>default=false</b>).  *<li><b>content.source.encoding</b> - specifies which encoding to use when  * reading the files of that content source. Certain implementations may define  * a default value if this parameter is not specified. (<b>default=null</b>).  *<li><b>content.source.log.step</b> - specifies for how many documents a  * message should be logged. If set to 0 it means no logging should occur.  *<b>NOTE:</b> if verbose is set to false, logging should not occur even if  * logStep is not 0 (<b>default=0</b>).  *</ul>  */
end_comment
begin_class
DECL|class|ContentSource
specifier|public
specifier|abstract
class|class
name|ContentSource
block|{
DECL|field|BZIP
specifier|private
specifier|static
specifier|final
name|int
name|BZIP
init|=
literal|0
decl_stmt|;
DECL|field|GZIP
specifier|private
specifier|static
specifier|final
name|int
name|GZIP
init|=
literal|1
decl_stmt|;
DECL|field|OTHER
specifier|private
specifier|static
specifier|final
name|int
name|OTHER
init|=
literal|2
decl_stmt|;
DECL|field|extensionToType
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|extensionToType
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|extensionToType
operator|.
name|put
argument_list|(
literal|".bz2"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|BZIP
argument_list|)
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".bzip"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|BZIP
argument_list|)
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".gz"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|GZIP
argument_list|)
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".gzip"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|GZIP
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|BUFFER_SIZE
specifier|protected
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1
operator|<<
literal|16
decl_stmt|;
comment|// 64K
DECL|field|bytesCount
specifier|private
name|long
name|bytesCount
decl_stmt|;
DECL|field|totalBytesCount
specifier|private
name|long
name|totalBytesCount
decl_stmt|;
DECL|field|docsCount
specifier|private
name|int
name|docsCount
decl_stmt|;
DECL|field|totalDocsCount
specifier|private
name|int
name|totalDocsCount
decl_stmt|;
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
DECL|field|forever
specifier|protected
name|boolean
name|forever
decl_stmt|;
DECL|field|logStep
specifier|protected
name|int
name|logStep
decl_stmt|;
DECL|field|verbose
specifier|protected
name|boolean
name|verbose
decl_stmt|;
DECL|field|encoding
specifier|protected
name|String
name|encoding
decl_stmt|;
DECL|field|csFactory
specifier|private
name|CompressorStreamFactory
name|csFactory
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
decl_stmt|;
comment|/** update count of bytes generated by this source */
DECL|method|addBytes
specifier|protected
specifier|final
specifier|synchronized
name|void
name|addBytes
parameter_list|(
name|long
name|numBytes
parameter_list|)
block|{
name|bytesCount
operator|+=
name|numBytes
expr_stmt|;
name|totalBytesCount
operator|+=
name|numBytes
expr_stmt|;
block|}
comment|/** update count of documents generated by this source */
DECL|method|addDoc
specifier|protected
specifier|final
specifier|synchronized
name|void
name|addDoc
parameter_list|()
block|{
operator|++
name|docsCount
expr_stmt|;
operator|++
name|totalDocsCount
expr_stmt|;
block|}
comment|/**    * A convenience method for collecting all the files of a content source from    * a given directory. The collected {@link File} instances are stored in the    * given<code>files</code>.    */
DECL|method|collectFiles
specifier|protected
specifier|final
name|void
name|collectFiles
parameter_list|(
name|File
name|dir
parameter_list|,
name|ArrayList
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return;
block|}
name|File
index|[]
name|dirFiles
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|dirFiles
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
name|dirFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|dirFiles
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|collectFiles
argument_list|(
name|file
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns an {@link InputStream} over the requested file. This method    * attempts to identify the appropriate {@link InputStream} instance to return    * based on the file name (e.g., if it ends with .bz2 or .bzip, return a    * 'bzip' {@link InputStream}).    */
DECL|method|getInputStream
specifier|protected
name|InputStream
name|getInputStream
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First, create a FileInputStream, as this will be required by all types.
comment|// Wrap with BufferedInputStream for better performance
name|InputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
name|fileName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|OTHER
decl_stmt|;
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|Integer
name|typeInt
init|=
name|extensionToType
operator|.
name|get
argument_list|(
name|fileName
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeInt
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|typeInt
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BZIP
case|:
comment|// According to BZip2CompressorInputStream's code, it reads the first
comment|// two file header chars ('B' and 'Z'). It is important to wrap the
comment|// underlying input stream with a buffered one since
comment|// Bzip2CompressorInputStream uses the read() method exclusively.
name|is
operator|=
name|csFactory
operator|.
name|createCompressorInputStream
argument_list|(
literal|"bzip2"
argument_list|,
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|GZIP
case|:
name|is
operator|=
name|csFactory
operator|.
name|createCompressorInputStream
argument_list|(
literal|"gz"
argument_list|,
name|is
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Do nothing, stay with FileInputStream
block|}
block|}
catch|catch
parameter_list|(
name|CompressorException
name|e
parameter_list|)
block|{
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
return|return
name|is
return|;
block|}
comment|/**    * Returns true whether it's time to log a message (depending on verbose and    * the number of documents generated).    */
DECL|method|shouldLog
specifier|protected
specifier|final
name|boolean
name|shouldLog
parameter_list|()
block|{
return|return
name|verbose
operator|&&
name|logStep
operator|>
literal|0
operator|&&
name|docsCount
operator|%
name|logStep
operator|==
literal|0
return|;
block|}
comment|/** Called when reading from this content source is no longer required. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of bytes generated since last reset. */
DECL|method|getBytesCount
specifier|public
specifier|final
name|long
name|getBytesCount
parameter_list|()
block|{
return|return
name|bytesCount
return|;
block|}
comment|/** Returns the number of generated documents since last reset. */
DECL|method|getDocsCount
specifier|public
specifier|final
name|int
name|getDocsCount
parameter_list|()
block|{
return|return
name|docsCount
return|;
block|}
DECL|method|getConfig
specifier|public
specifier|final
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** Returns the next {@link DocData} from the content source. */
DECL|method|getNextDocData
specifier|public
specifier|abstract
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
function_decl|;
comment|/** Returns the total number of bytes that were generated by this source. */
DECL|method|getTotalBytesCount
specifier|public
specifier|final
name|long
name|getTotalBytesCount
parameter_list|()
block|{
return|return
name|totalBytesCount
return|;
block|}
comment|/** Returns the total number of generated documents. */
DECL|method|getTotalDocsCount
specifier|public
specifier|final
name|int
name|getTotalDocsCount
parameter_list|()
block|{
return|return
name|totalDocsCount
return|;
block|}
comment|/**    * Resets the input for this content source, so that the test would behave as    * if it was just started, input-wise.    *<p>    *<b>NOTE:</b> the default implementation resets the number of bytes and    * documents generated since the last reset, so it's important to call    * super.resetInputs in case you override this method.    */
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|bytesCount
operator|=
literal|0
expr_stmt|;
name|docsCount
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Sets the {@link Config} for this content source. If you override this    * method, you must call super.setConfig.    */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|forever
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.forever"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|logStep
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.log.step"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verbose
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.verbose"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|encoding
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source.encoding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

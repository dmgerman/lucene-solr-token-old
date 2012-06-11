begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
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
name|utils
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
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|OutputStream
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
name|Locale
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
begin_comment
comment|/**  * Stream utilities.  */
end_comment
begin_class
DECL|class|StreamUtils
specifier|public
class|class
name|StreamUtils
block|{
comment|/** Buffer size used across the benchmark package */
DECL|field|BUFFER_SIZE
specifier|public
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
comment|/** File format type */
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
comment|/** BZIP2 is automatically used for<b>.bz2</b> and<b>.bzip2</b> extensions. */
DECL|enum constant|BZIP2
name|BZIP2
parameter_list|(
name|CompressorStreamFactory
operator|.
name|BZIP2
parameter_list|)
operator|,
comment|/** GZIP is automatically used for<b>.gz</b> and<b>.gzip</b> extensions. */
constructor|GZIP(CompressorStreamFactory.GZIP
DECL|enum constant|GZIP
block|)
enum|,
comment|/** Plain text is used for anything which is not GZIP or BZIP. */
DECL|enum constant|PLAIN
name|PLAIN
argument_list|(
literal|null
argument_list|)
enum|;
DECL|field|csfType
specifier|private
specifier|final
name|String
name|csfType
decl_stmt|;
DECL|method|Type
name|Type
parameter_list|(
name|String
name|csfType
parameter_list|)
block|{
name|this
operator|.
name|csfType
operator|=
name|csfType
expr_stmt|;
block|}
DECL|method|inputStream
specifier|private
name|InputStream
name|inputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|csfType
operator|==
literal|null
condition|?
name|in
else|:
operator|new
name|CompressorStreamFactory
argument_list|()
operator|.
name|createCompressorInputStream
argument_list|(
name|csfType
argument_list|,
name|in
argument_list|)
return|;
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
block|}
DECL|method|outputStream
specifier|private
name|OutputStream
name|outputStream
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|csfType
operator|==
literal|null
condition|?
name|os
else|:
operator|new
name|CompressorStreamFactory
argument_list|()
operator|.
name|createCompressorOutputStream
argument_list|(
name|csfType
argument_list|,
name|os
argument_list|)
return|;
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
block|}
block|}
end_class
begin_decl_stmt
DECL|field|extensionToType
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|extensionToType
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
argument_list|()
decl_stmt|;
end_decl_stmt
begin_static
static|static
block|{
comment|// these in are lower case, we will lower case at the test as well
name|extensionToType
operator|.
name|put
argument_list|(
literal|".bz2"
argument_list|,
name|Type
operator|.
name|BZIP2
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".bzip"
argument_list|,
name|Type
operator|.
name|BZIP2
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".gz"
argument_list|,
name|Type
operator|.
name|GZIP
argument_list|)
expr_stmt|;
name|extensionToType
operator|.
name|put
argument_list|(
literal|".gzip"
argument_list|,
name|Type
operator|.
name|GZIP
argument_list|)
expr_stmt|;
block|}
end_static
begin_comment
comment|/**    * Returns an {@link InputStream} over the requested file. This method    * attempts to identify the appropriate {@link InputStream} instance to return    * based on the file name (e.g., if it ends with .bz2 or .bzip, return a    * 'bzip' {@link InputStream}).    */
end_comment
begin_function
DECL|method|inputStream
specifier|public
specifier|static
name|InputStream
name|inputStream
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
name|in
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
return|return
name|fileType
argument_list|(
name|file
argument_list|)
operator|.
name|inputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
end_function
begin_comment
comment|/** Return the type of the file, or null if unknown */
end_comment
begin_function
DECL|method|fileType
specifier|private
specifier|static
name|Type
name|fileType
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|Type
name|type
init|=
literal|null
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
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|type
operator|=
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
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|type
operator|==
literal|null
condition|?
name|Type
operator|.
name|PLAIN
else|:
name|type
return|;
block|}
end_function
begin_comment
comment|/**    * Returns an {@link OutputStream} over the requested file, identifying    * the appropriate {@link OutputStream} instance similar to {@link #inputStream(File)}.    */
end_comment
begin_function
DECL|method|outputStream
specifier|public
specifier|static
name|OutputStream
name|outputStream
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First, create a FileInputStream, as this will be required by all types.
comment|// Wrap with BufferedInputStream for better performance
name|OutputStream
name|os
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
return|return
name|fileType
argument_list|(
name|file
argument_list|)
operator|.
name|outputStream
argument_list|(
name|os
argument_list|)
return|;
block|}
end_function
unit|}
end_unit

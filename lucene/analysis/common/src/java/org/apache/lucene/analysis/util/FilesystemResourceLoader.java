begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
begin_comment
comment|/**  * Simple {@link ResourceLoader} that opens resource files  * from the local file system, optionally resolving against  * a base directory.  *   *<p>This loader wraps a delegate {@link ResourceLoader}  * that is used to resolve all files, the current base directory  * does not contain. {@link #newInstance} is always resolved  * against the delegate, as a {@link ClassLoader} is needed.  *   *<p>You can chain several {@code FilesystemResourceLoader}s  * to allow lookup of files in more than one base directory.  */
end_comment
begin_class
DECL|class|FilesystemResourceLoader
specifier|public
specifier|final
class|class
name|FilesystemResourceLoader
implements|implements
name|ResourceLoader
block|{
DECL|field|baseDirectory
specifier|private
specifier|final
name|File
name|baseDirectory
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|ResourceLoader
name|delegate
decl_stmt|;
comment|/**    * Creates a resource loader that requires absolute filenames or relative to CWD    * to resolve resources. Files not found in file system and class lookups    * are delegated to context classloader.    */
DECL|method|FilesystemResourceLoader
specifier|public
name|FilesystemResourceLoader
parameter_list|()
block|{
name|this
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a resource loader that resolves resources against the given    * base directory (may be {@code null} to refer to CWD).    * Files not found in file system and class lookups are delegated to context    * classloader.    */
DECL|method|FilesystemResourceLoader
specifier|public
name|FilesystemResourceLoader
parameter_list|(
name|File
name|baseDirectory
parameter_list|)
block|{
name|this
argument_list|(
name|baseDirectory
argument_list|,
operator|new
name|ClasspathResourceLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a resource loader that resolves resources against the given    * base directory (may be {@code null} to refer to CWD).    * Files not found in file system and class lookups are delegated    * to the given delegate {@link ResourceLoader}.    */
DECL|method|FilesystemResourceLoader
specifier|public
name|FilesystemResourceLoader
parameter_list|(
name|File
name|baseDirectory
parameter_list|,
name|ResourceLoader
name|delegate
parameter_list|)
block|{
if|if
condition|(
name|baseDirectory
operator|!=
literal|null
operator|&&
operator|!
name|baseDirectory
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"baseDirectory is not a directory or null"
argument_list|)
throw|;
if|if
condition|(
name|delegate
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"delegate ResourceLoader may not be null"
argument_list|)
throw|;
name|this
operator|.
name|baseDirectory
operator|=
name|baseDirectory
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseDirectory
operator|!=
literal|null
operator|&&
operator|!
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|baseDirectory
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|openResource
argument_list|(
name|resource
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|newInstance
argument_list|(
name|cname
argument_list|,
name|expectedType
argument_list|)
return|;
block|}
block|}
end_class
end_unit

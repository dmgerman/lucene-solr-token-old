begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
comment|/**  * Three concrete implementations for ContentStream - one for File/URL/String  *   *  * @since solr 1.2  */
end_comment
begin_class
DECL|class|ContentStreamBase
specifier|public
specifier|abstract
class|class
name|ContentStreamBase
implements|implements
name|ContentStream
block|{
DECL|field|DEFAULT_CHARSET
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CHARSET
init|=
literal|"utf-8"
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|sourceInfo
specifier|protected
name|String
name|sourceInfo
decl_stmt|;
DECL|field|contentType
specifier|protected
name|String
name|contentType
decl_stmt|;
DECL|field|size
specifier|protected
name|Long
name|size
decl_stmt|;
comment|//---------------------------------------------------------------------
comment|//---------------------------------------------------------------------
DECL|method|getCharsetFromContentType
specifier|public
specifier|static
name|String
name|getCharsetFromContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|int
name|idx
init|=
name|contentType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|indexOf
argument_list|(
literal|"charset="
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
return|return
name|contentType
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|"charset="
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|//------------------------------------------------------------------------
comment|//------------------------------------------------------------------------
comment|/**    * Construct a<code>ContentStream</code> from a<code>URL</code>    *     * This uses a<code>URLConnection</code> to get the content stream    * @see  URLConnection    */
DECL|class|URLStream
specifier|public
specifier|static
class|class
name|URLStream
extends|extends
name|ContentStreamBase
block|{
DECL|field|url
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
DECL|method|URLStream
specifier|public
name|URLStream
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|sourceInfo
operator|=
literal|"url"
expr_stmt|;
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
name|URLConnection
name|conn
init|=
name|this
operator|.
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|contentType
operator|=
name|conn
operator|.
name|getContentType
argument_list|()
expr_stmt|;
name|name
operator|=
name|url
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
name|size
operator|=
operator|new
name|Long
argument_list|(
name|conn
operator|.
name|getContentLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conn
operator|.
name|getInputStream
argument_list|()
return|;
block|}
block|}
comment|/**    * Construct a<code>ContentStream</code> from a<code>File</code>    */
DECL|class|FileStream
specifier|public
specifier|static
class|class
name|FileStream
extends|extends
name|ContentStreamBase
block|{
DECL|field|file
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
DECL|method|FileStream
specifier|public
name|FileStream
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|=
name|f
expr_stmt|;
name|contentType
operator|=
literal|null
expr_stmt|;
comment|// ??
name|name
operator|=
name|file
operator|.
name|getName
argument_list|()
expr_stmt|;
name|size
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
name|sourceInfo
operator|=
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|char
name|first
init|=
operator|(
name|char
operator|)
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|'<'
condition|)
block|{
return|return
literal|"application/xml"
return|;
block|}
if|if
condition|(
name|first
operator|==
literal|'{'
condition|)
block|{
return|return
literal|"application/json"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
name|contentType
return|;
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**      * If an charset is defined (by the contentType) use that, otherwise       * use a file reader      */
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|charset
init|=
name|getCharsetFromContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
return|return
name|charset
operator|==
literal|null
condition|?
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
else|:
operator|new
name|InputStreamReader
argument_list|(
name|getStream
argument_list|()
argument_list|,
name|charset
argument_list|)
return|;
block|}
block|}
comment|/**    * Construct a<code>ContentStream</code> from a<code>String</code>    */
DECL|class|StringStream
specifier|public
specifier|static
class|class
name|StringStream
extends|extends
name|ContentStreamBase
block|{
DECL|field|str
specifier|private
specifier|final
name|String
name|str
decl_stmt|;
DECL|method|StringStream
specifier|public
name|StringStream
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|this
operator|.
name|str
operator|=
name|str
expr_stmt|;
name|contentType
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|size
operator|=
operator|new
name|Long
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|sourceInfo
operator|=
literal|"string"
expr_stmt|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
if|if
condition|(
name|contentType
operator|==
literal|null
operator|&&
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|char
name|first
init|=
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|'<'
condition|)
block|{
return|return
literal|"application/xml"
return|;
block|}
if|if
condition|(
name|first
operator|==
literal|'{'
condition|)
block|{
return|return
literal|"application/json"
return|;
block|}
comment|// find a comma? for CSV?
block|}
return|return
name|contentType
return|;
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|str
operator|.
name|getBytes
argument_list|(
name|DEFAULT_CHARSET
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * If an charset is defined (by the contentType) use that, otherwise       * use a StringReader      */
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|charset
init|=
name|getCharsetFromContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
return|return
name|charset
operator|==
literal|null
condition|?
operator|new
name|StringReader
argument_list|(
name|str
argument_list|)
else|:
operator|new
name|InputStreamReader
argument_list|(
name|getStream
argument_list|()
argument_list|,
name|charset
argument_list|)
return|;
block|}
block|}
comment|/**    * Base reader implementation.  If the contentType declares a     * charset use it, otherwise use "utf-8".    */
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|charset
init|=
name|getCharsetFromContentType
argument_list|(
name|getContentType
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|charset
operator|==
literal|null
condition|?
operator|new
name|InputStreamReader
argument_list|(
name|getStream
argument_list|()
argument_list|,
name|DEFAULT_CHARSET
argument_list|)
else|:
operator|new
name|InputStreamReader
argument_list|(
name|getStream
argument_list|()
argument_list|,
name|charset
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------------
comment|// Getters / Setters for overrideable attributes
comment|//------------------------------------------------------------------
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|contentType
return|;
block|}
DECL|method|setContentType
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getSize
specifier|public
name|Long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|setSize
specifier|public
name|void
name|setSize
parameter_list|(
name|Long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
DECL|method|getSourceInfo
specifier|public
name|String
name|getSourceInfo
parameter_list|()
block|{
return|return
name|sourceInfo
return|;
block|}
DECL|method|setSourceInfo
specifier|public
name|void
name|setSourceInfo
parameter_list|(
name|String
name|sourceInfo
parameter_list|)
block|{
name|this
operator|.
name|sourceInfo
operator|=
name|sourceInfo
expr_stmt|;
block|}
block|}
end_class
end_unit

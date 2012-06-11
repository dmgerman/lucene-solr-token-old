begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
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
name|SolrException
operator|.
name|ErrorCode
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
name|handler
operator|.
name|ContentStreamHandlerBase
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
name|handler
operator|.
name|loader
operator|.
name|ContentStreamLoader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MimeTypeException
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
name|util
operator|.
name|Collection
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
name|util
operator|.
name|Iterator
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
begin_comment
comment|/**  * Handler for rich documents like PDF or Word or any other file format that Tika handles that need the text to be extracted  * first from the document.  *<p/>  */
end_comment
begin_class
DECL|class|ExtractingRequestHandler
specifier|public
class|class
name|ExtractingRequestHandler
extends|extends
name|ContentStreamHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|private
specifier|transient
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ExtractingRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONFIG_LOCATION
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_LOCATION
init|=
literal|"tika.config"
decl_stmt|;
DECL|field|DATE_FORMATS
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FORMATS
init|=
literal|"date.formats"
decl_stmt|;
DECL|field|config
specifier|protected
name|TikaConfig
name|config
decl_stmt|;
DECL|field|dateFormats
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
init|=
name|DateUtil
operator|.
name|DEFAULT_DATE_FORMATS
decl_stmt|;
DECL|field|factory
specifier|protected
name|SolrContentHandlerFactory
name|factory
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
block|{
comment|//if relative,then relative to config dir, otherwise, absolute path
name|String
name|tikaConfigLoc
init|=
operator|(
name|String
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|CONFIG_LOCATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|tikaConfigLoc
operator|!=
literal|null
condition|)
block|{
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|tikaConfigLoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|configFile
operator|.
name|isAbsolute
argument_list|()
operator|==
literal|false
condition|)
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|,
name|configFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|config
operator|=
operator|new
name|TikaConfig
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|NamedList
name|configDateFormats
init|=
operator|(
name|NamedList
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|DATE_FORMATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|configDateFormats
operator|!=
literal|null
operator|&&
name|configDateFormats
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|dateFormats
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
name|it
init|=
name|configDateFormats
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|format
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding Date Format: "
operator|+
name|format
argument_list|)
expr_stmt|;
name|dateFormats
operator|.
name|add
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|config
operator|=
name|getDefaultConfig
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|factory
operator|=
name|createFactory
argument_list|()
expr_stmt|;
block|}
DECL|method|getDefaultConfig
specifier|private
name|TikaConfig
name|getDefaultConfig
parameter_list|(
name|ClassLoader
name|classLoader
parameter_list|)
throws|throws
name|MimeTypeException
throws|,
name|IOException
block|{
return|return
operator|new
name|TikaConfig
argument_list|(
name|classLoader
argument_list|)
return|;
block|}
DECL|method|createFactory
specifier|protected
name|SolrContentHandlerFactory
name|createFactory
parameter_list|()
block|{
return|return
operator|new
name|SolrContentHandlerFactory
argument_list|(
name|dateFormats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
operator|new
name|ExtractingDocumentLoader
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|config
argument_list|,
name|factory
argument_list|)
return|;
block|}
comment|// ////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add/Update Rich document"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
end_unit

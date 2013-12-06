begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|io
operator|.
name|IOUtils
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
name|cloud
operator|.
name|ZkController
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|cloud
operator|.
name|SolrZkClient
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
name|ContentStream
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
name|Config
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
name|CoreContainer
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
name|CoreDescriptor
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
name|SolrConfig
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
name|handler
operator|.
name|RequestHandlerBase
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
name|response
operator|.
name|RawResponseWriter
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|IOException
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
name|Locale
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
begin_comment
comment|/**  * This handler uses the RawResponseWriter to give client access to  * files inside ${solr.home}/conf  *<p/>  * If you want to selectively restrict access some configuration files, you can list  * these files in the hidden invariants.  For example to hide  * synonyms.txt and anotherfile.txt, you would register:  *<p/>  *<pre>  *&lt;requestHandler name="/admin/fileupdate" class="org.apache.solr.handler.admin.EditFileRequestHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;str name="echoParams"&gt;explicit&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="invariants"&gt;  *&lt;str name="hidden"&gt;synonyms.txt&lt;/str&gt;  *&lt;str name="hidden"&gt;anotherfile.txt&lt;/str&gt;  *&lt;str name="hidden"&gt;*&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *<p/>  * At present, there is only explicit file names (including path) or the glob '*' are supported. Variants like '*.xml'  * are NOT supported.ere  *<p/>  *<p/>  * The EditFileRequestHandler uses the {@link RawResponseWriter} (wt=raw) to return  * file contents.  If you need to use a different writer, you will need to change  * the registered invariant param for wt.  *<p/>  * If you want to override the contentType header returned for a given file, you can  * set it directly using: CONTENT_TYPE.  For example, to get a plain text  * version of schema.xml, try:  *<pre>  *   http://localhost:8983/solr/admin/fileedit?file=schema.xml&contentType=text/plain  *</pre>  *  * @since solr 4.7  *<p/>  *<p/>  *        You can use this handler to modify any files in the conf directory, e.g. solrconfig.xml  *        or schema.xml, or even in sub-directories (e.g. velocity/error.vm) by POSTing a file. Here's an example cURL command  *<pre>  *                                            curl -X POST --form "fileupload=@schema.new" 'http://localhost:8983/solr/collection1/admin/fileedit?op=write&file=schema.xml'  *</pre>  *  *        or  *<pre>  *                                            curl -X POST --form "fileupload=@error.new" 'http://localhost:8983/solr/collection1/admin/file?op=write&file=velocity/error.vm'  *</pre>  *  *        For the first iteration, this is probably going to be used from the Solr admin screen.  *  *        NOTE: Specifying a directory or simply leaving the any "file=XXX" parameters will list the contents of a directory.  *  *        NOTE:<b>You must reload the core/collection for any changes made via this handler to take effect!</b>  *  *        NOTE:<b>If the core does not load (say schema.xml is not well formed for instance) you may be unable to replace  *        the files with this interface.</b>  *  *        NOTE:<b>Leaving this handler enabled is a security risk! This handler should be disabled in all but trusted  *        (probably development only) environments!</b>  *  *        Configuration files in ZooKeeper are supported.  */
end_comment
begin_class
DECL|class|EditFileRequestHandler
specifier|public
class|class
name|EditFileRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EditFileRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OP_PARAM
specifier|private
specifier|final
specifier|static
name|String
name|OP_PARAM
init|=
literal|"op"
decl_stmt|;
DECL|field|OP_WRITE
specifier|private
specifier|final
specifier|static
name|String
name|OP_WRITE
init|=
literal|"write"
decl_stmt|;
DECL|field|OP_TEST
specifier|private
specifier|final
specifier|static
name|String
name|OP_TEST
init|=
literal|"test"
decl_stmt|;
DECL|field|stream
name|ContentStream
name|stream
decl_stmt|;
DECL|field|data
specifier|private
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
DECL|field|hiddenFiles
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
decl_stmt|;
DECL|method|EditFileRequestHandler
specifier|public
name|EditFileRequestHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
name|hiddenFiles
operator|=
name|ShowFileRequestHandler
operator|.
name|initHidden
argument_list|(
name|invariants
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
throws|,
name|IOException
block|{
name|CoreContainer
name|coreContainer
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|String
name|op
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|OP_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|OP_WRITE
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
operator|||
name|OP_TEST
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No file name specified for write operation."
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fname
operator|=
name|fname
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|stream
operator|=
name|getOneInputStream
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
return|return;
comment|// Error already in rsp.
block|}
name|data
operator|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// If it's "solrconfig.xml", try parsing it as that object. Otherwise, if it ends in '.xml',
comment|// see if it at least parses.
if|if
condition|(
literal|"solrconfig.xml"
operator|.
name|equals
argument_list|(
name|fname
argument_list|)
condition|)
block|{
try|try
block|{
operator|new
name|SolrConfig
argument_list|(
literal|"unused"
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid solr config file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|fname
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
comment|// At least do a rudimentary test, see if the thing parses.
try|try
block|{
operator|new
name|Config
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid XML file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|ShowFileRequestHandler
operator|.
name|isHiddenFile
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|fname
argument_list|,
literal|true
argument_list|,
name|hiddenFiles
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|writeToZooKeeper
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeToFileSystem
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// write the file contained in the parameter "file=XXX" to ZooKeeper. The file may be a path, e.g.
comment|// file=velocity/error.vm or file=schema.xml
comment|//
comment|// Important: Assumes that the file already exists in ZK, so far we aren't creating files there.
DECL|method|writeToZooKeeper
specifier|private
name|void
name|writeToZooKeeper
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|CoreContainer
name|coreContainer
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|String
name|adminFile
init|=
name|ShowFileRequestHandler
operator|.
name|getAdminFileFromZooKeeper
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|zkClient
argument_list|,
name|hiddenFiles
argument_list|)
decl_stmt|;
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|OP_TEST
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|OP_PARAM
argument_list|)
argument_list|)
condition|)
block|{
name|testReloadSuccess
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Persist the managed schema
try|try
block|{
comment|// Assumption: the path exists
name|zkClient
operator|.
name|setData
argument_list|(
name|adminFile
argument_list|,
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Saved "
operator|+
name|fname
operator|+
literal|" to ZooKeeper successfully."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot save file: "
operator|+
name|fname
operator|+
literal|" to Zookeeper, "
operator|+
literal|"ZooKeeper error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cannot save file: "
operator|+
name|fname
operator|+
literal|" to Zookeeper, "
operator|+
literal|"ZooKeeper error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Used when POSTing the configuration files to Solr (either ZooKeeper or locally).
comment|//
comment|// It takes some effort to insure that there is one (and only one) stream provided, there's no provision for
comment|// more than one stream at present.
DECL|method|getOneInputStream
specifier|private
name|ContentStream
name|getOneInputStream
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|String
name|file
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"You must specify a file for the write operation."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"You must specify a file for the write operation."
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Now, this is truly clumsy
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Input stream list was null for admin file write operation."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Input stream list was null for admin file write operation."
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Iterator
argument_list|<
name|ContentStream
argument_list|>
name|iter
init|=
name|streams
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No input streams were in the list for admin file write operation."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No input streams were in the list for admin file write operation."
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ContentStream
name|stream
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"More than one input stream was found for admin file write operation."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"More than one input stream was found for admin file write operation."
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|stream
return|;
block|}
comment|// Write the data passed in from the stream to the file indicated by the file=XXX parameter on the local file system
DECL|method|writeToFileSystem
specifier|private
name|void
name|writeToFileSystem
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|adminFile
init|=
name|ShowFileRequestHandler
operator|.
name|getAdminFileFromFileSystem
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|hiddenFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminFile
operator|==
literal|null
operator|||
name|adminFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminFile
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"File "
operator|+
name|fname
operator|+
literal|" was not found."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"File "
operator|+
name|fname
operator|+
literal|" was not found."
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|error
argument_list|(
literal|"File "
operator|+
name|fname
operator|+
literal|" is a directory."
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"File "
operator|+
name|fname
operator|+
literal|" is a directory."
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|OP_TEST
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|OP_PARAM
argument_list|)
argument_list|)
condition|)
block|{
name|testReloadSuccess
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
name|FileUtils
operator|.
name|copyInputStreamToFile
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
name|adminFile
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Successfully saved file "
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" locally"
argument_list|)
expr_stmt|;
block|}
DECL|method|testReloadSuccess
specifier|private
name|boolean
name|testReloadSuccess
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
comment|// Try writing the config to a temporary core and reloading to see that we don't allow people to shoot themselves
comment|// in the foot.
name|File
name|home
init|=
literal|null
decl_stmt|;
try|try
block|{
name|home
operator|=
operator|new
name|File
argument_list|(
name|FileUtils
operator|.
name|getTempDirectory
argument_list|()
argument_list|,
literal|"SOLR_5459"
argument_list|)
expr_stmt|;
comment|// Unlikely to name a core or collection this!
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
literal|"<solr></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// Use auto-discovery
name|File
name|coll
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"SOLR_5459"
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|CoreDescriptor
name|desc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|CoreContainer
name|coreContainer
init|=
name|desc
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
try|try
block|{
name|String
name|confPath
init|=
operator|(
operator|(
name|ZkSolrResourceLoader
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|)
operator|.
name|getCollectionZkPath
argument_list|()
decl_stmt|;
name|ZkController
operator|.
name|downloadConfigDir
argument_list|(
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|confPath
argument_list|,
operator|new
name|File
argument_list|(
name|coll
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error when attempting to download conf from ZooKeeper: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error when attempting to download conf from ZooKeeper"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|desc
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|coll
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|coll
argument_list|,
literal|"core.properties"
argument_list|)
argument_list|,
literal|"name=SOLR_5459"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeByteArrayToFile
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|coll
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|tryReloading
argument_list|(
name|rsp
argument_list|,
name|home
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught IO exception when trying to verify configs. "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Caught IO exception when trying to verify configs. "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|home
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught IO exception trying to delete temporary directory "
operator|+
name|home
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
comment|// Don't fail for this reason!
block|}
block|}
block|}
block|}
DECL|method|tryReloading
specifier|private
name|boolean
name|tryReloading
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|,
name|File
name|home
parameter_list|)
block|{
name|CoreContainer
name|cc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cc
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|home
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Exception
name|ex
range|:
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error when attempting to reload core: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error when attempting to reload core after writing config"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Admin Config File -- update config files directly"
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
literal|"$URL: https://svn.apache.org/repos/asf/lucene/dev/trunk/solr/core/src/java/org/apache/solr/handler/admin/ShowFileRequestHandler.java $"
return|;
block|}
block|}
end_class
end_unit
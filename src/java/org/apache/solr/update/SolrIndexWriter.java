begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|*
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
name|*
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
name|DirectoryFactory
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
name|StandardDirectoryFactory
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
name|schema
operator|.
name|IndexSchema
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
name|SolrPluginUtils
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
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|util
operator|.
name|Date
import|;
end_import
begin_comment
comment|/**  * An IndexWriter that is configured via Solr config mechanisms.  * * @version $Id$ * @since solr 0.9 */
end_comment
begin_class
DECL|class|SolrIndexWriter
specifier|public
class|class
name|SolrIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Opened Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|setSimilarity
argument_list|(
name|schema
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// setUseCompoundFile(false);
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
comment|//only set maxBufferedDocs
if|if
condition|(
name|config
operator|.
name|maxBufferedDocs
operator|!=
operator|-
literal|1
condition|)
block|{
name|setMaxBufferedDocs
argument_list|(
name|config
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|ramBufferSizeMB
operator|!=
operator|-
literal|1
condition|)
block|{
name|setRAMBufferSizeMB
argument_list|(
name|config
operator|.
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|termIndexInterval
operator|!=
operator|-
literal|1
condition|)
block|{
name|setTermIndexInterval
argument_list|(
name|config
operator|.
name|termIndexInterval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|maxMergeDocs
operator|!=
operator|-
literal|1
condition|)
name|setMaxMergeDocs
argument_list|(
name|config
operator|.
name|maxMergeDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|maxFieldLength
operator|!=
operator|-
literal|1
condition|)
name|setMaxFieldLength
argument_list|(
name|config
operator|.
name|maxFieldLength
argument_list|)
expr_stmt|;
name|String
name|className
init|=
name|config
operator|.
name|mergePolicyInfo
operator|==
literal|null
condition|?
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_POLICY_CLASSNAME
else|:
name|config
operator|.
name|mergePolicyInfo
operator|.
name|className
decl_stmt|;
name|MergePolicy
name|policy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|policy
operator|=
operator|(
name|MergePolicy
operator|)
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
literal|null
argument_list|,
operator|new
name|Class
index|[]
block|{
name|IndexWriter
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|policy
operator|=
operator|(
name|MergePolicy
operator|)
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|mergePolicyInfo
operator|!=
literal|null
condition|)
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|policy
argument_list|,
name|config
operator|.
name|mergePolicyInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
name|setMergePolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
if|if
condition|(
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|setUseCompoundFile
argument_list|(
name|config
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Use of compound file format cannot be configured if merge policy is not an instance "
operator|+
literal|"of LogMergePolicy. The configured policy's defaults will be used."
argument_list|)
expr_stmt|;
block|}
name|className
operator|=
name|config
operator|.
name|mergeSchedulerInfo
operator|==
literal|null
condition|?
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
else|:
name|config
operator|.
name|mergeSchedulerInfo
operator|.
name|className
expr_stmt|;
name|MergeScheduler
name|scheduler
init|=
operator|(
name|MergeScheduler
operator|)
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|mergeSchedulerInfo
operator|!=
literal|null
condition|)
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|scheduler
argument_list|,
name|config
operator|.
name|mergeSchedulerInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
name|setMergeScheduler
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|String
name|infoStreamFile
init|=
name|config
operator|.
name|infoStreamFile
decl_stmt|;
if|if
condition|(
name|infoStreamFile
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|infoStreamFile
argument_list|)
decl_stmt|;
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|infoStream
operator|=
operator|new
name|TimeLoggingPrintStream
argument_list|(
name|fos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
block|}
comment|//if (config.commitLockTimeout != -1) setWriteLockTimeout(config.commitLockTimeout);
block|}
block|}
DECL|method|getDirectory
specifier|public
specifier|static
name|Directory
name|getDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|directoryFactory
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
name|directoryFactory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|rawLockType
init|=
operator|(
literal|null
operator|==
name|config
operator|)
condition|?
literal|null
else|:
name|config
operator|.
name|lockType
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rawLockType
condition|)
block|{
comment|// we default to "simple" for backwards compatibility
name|log
operator|.
name|warn
argument_list|(
literal|"No lockType configured for "
operator|+
name|path
operator|+
literal|" assuming 'simple'"
argument_list|)
expr_stmt|;
name|rawLockType
operator|=
literal|"simple"
expr_stmt|;
block|}
specifier|final
name|String
name|lockType
init|=
name|rawLockType
operator|.
name|toLowerCase
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"simple"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// multiple SimpleFSLockFactory instances should be OK
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SimpleFSLockFactory
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"native"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|NativeFSLockFactory
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"single"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|d
operator|.
name|getLockFactory
argument_list|()
operator|instanceof
name|SingleInstanceLockFactory
operator|)
condition|)
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// Recipe for disaster
name|log
operator|.
name|error
argument_list|(
literal|"CONFIGURATION WARNING: locks are disabled on "
operator|+
name|path
argument_list|)
expr_stmt|;
name|d
operator|.
name|setLockFactory
argument_list|(
operator|new
name|NoLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unrecognized lockType: "
operator|+
name|rawLockType
argument_list|)
throw|;
block|}
return|return
name|d
return|;
block|}
comment|/** @deprecated remove when getDirectory(String,SolrIndexConfig) is gone */
DECL|field|LEGACY_DIR_FACTORY
specifier|private
specifier|static
name|DirectoryFactory
name|LEGACY_DIR_FACTORY
init|=
operator|new
name|StandardDirectoryFactory
argument_list|()
decl_stmt|;
static|static
block|{
name|LEGACY_DIR_FACTORY
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated use getDirectory(String path, DirectoryFactory directoryFactory, SolrIndexConfig config)    */
DECL|method|getDirectory
specifier|public
specifier|static
name|Directory
name|getDirectory
parameter_list|(
name|String
name|path
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"SolrIndexWriter is using LEGACY_DIR_FACTORY which means deprecated code is likely in use and SolrIndexWriter is ignoring any custom DirectoryFactory."
argument_list|)
expr_stmt|;
return|return
name|getDirectory
argument_list|(
name|path
argument_list|,
name|LEGACY_DIR_FACTORY
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    *    */
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|dirFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
name|dirFactory
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|false
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|dirFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
name|dirFactory
argument_list|,
literal|null
argument_list|)
argument_list|,
name|config
operator|.
name|luceneAutoCommit
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated    */
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|false
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated    */
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
name|config
argument_list|)
argument_list|,
name|config
operator|.
name|luceneAutoCommit
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|DirectoryFactory
name|dirFactory
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|,
name|IndexDeletionPolicy
name|delPolicy
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getDirectory
argument_list|(
name|path
argument_list|,
name|dirFactory
argument_list|,
name|config
argument_list|)
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|,
name|delPolicy
argument_list|,
operator|new
name|MaxFieldLength
argument_list|(
name|IndexWriter
operator|.
name|DEFAULT_MAX_FIELD_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * use DocumentBuilder now...    * private final void addField(Document doc, String name, String val) {    * SchemaField ftype = schema.getField(name);    *<p/>    * // we don't check for a null val ourselves because a solr.FieldType    * // might actually want to map it to something.  If createField()    * // returns null, then we don't store the field.    *<p/>    * Field field = ftype.createField(val, boost);    * if (field != null) doc.add(field);    * }    *<p/>    *<p/>    * public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {    * Document doc = new Document();    * for (int i=0; i<fieldNames.length; i++) {    * String name = fieldNames[i];    * String val = fieldNames[i];    *<p/>    * // first null is end of list.  client can reuse arrays if they want    * // and just write a single null if there is unused space.    * if (name==null) break;    *<p/>    * addField(doc,name,val);    * }    * addDocument(doc);    * }    * ****    */
DECL|field|isClosed
specifier|private
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"SolrIndexWriter was not closed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!"
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Helper class for adding timestamps to infoStream logging
DECL|class|TimeLoggingPrintStream
class|class
name|TimeLoggingPrintStream
extends|extends
name|PrintStream
block|{
DECL|field|dateFormat
specifier|private
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|method|TimeLoggingPrintStream
specifier|public
name|TimeLoggingPrintStream
parameter_list|(
name|OutputStream
name|underlyingOutputStream
parameter_list|,
name|boolean
name|autoFlush
parameter_list|)
block|{
name|super
argument_list|(
name|underlyingOutputStream
argument_list|,
name|autoFlush
argument_list|)
expr_stmt|;
name|this
operator|.
name|dateFormat
operator|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
expr_stmt|;
block|}
comment|// We might ideally want to override print(String) as well, but
comment|// looking through the code that writes to infoStream, it appears
comment|// that all the classes except CheckIndex just use println.
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|String
name|x
parameter_list|)
block|{
name|print
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|super
operator|.
name|println
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

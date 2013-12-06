begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
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
name|net
operator|.
name|URI
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
name|HashSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|filecache
operator|.
name|DistributedCache
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Job
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|RecordWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
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
begin_class
DECL|class|SolrOutputFormat
specifier|public
class|class
name|SolrOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|FileOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrOutputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The parameter used to pass the solr config zip file information. This will    * be the hdfs path to the configuration zip file    */
DECL|field|SETUP_OK
specifier|public
specifier|static
specifier|final
name|String
name|SETUP_OK
init|=
literal|"solr.output.format.setup"
decl_stmt|;
comment|/** The key used to pass the zip file name through the configuration. */
DECL|field|ZIP_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ZIP_NAME
init|=
literal|"solr.zip.name"
decl_stmt|;
comment|/**    * The base name of the zip file containing the configuration information.    * This file is passed via the distributed cache using a unique name, obtained    * via {@link #getZipName(Configuration jobConf)}.    */
DECL|field|ZIP_FILE_BASE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ZIP_FILE_BASE_NAME
init|=
literal|"solr.zip"
decl_stmt|;
comment|/**    * The key used to pass the boolean configuration parameter that instructs for    * regular or zip file output    */
DECL|field|OUTPUT_ZIP_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_ZIP_FILE
init|=
literal|"solr.output.zip.format"
decl_stmt|;
DECL|field|defaultSolrWriterThreadCount
specifier|static
name|int
name|defaultSolrWriterThreadCount
init|=
literal|0
decl_stmt|;
DECL|field|SOLR_WRITER_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_WRITER_THREAD_COUNT
init|=
literal|"solr.record.writer.num.threads"
decl_stmt|;
DECL|field|defaultSolrWriterQueueSize
specifier|static
name|int
name|defaultSolrWriterQueueSize
init|=
literal|1
decl_stmt|;
DECL|field|SOLR_WRITER_QUEUE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_WRITER_QUEUE_SIZE
init|=
literal|"solr.record.writer.max.queues.size"
decl_stmt|;
DECL|field|defaultSolrBatchSize
specifier|static
name|int
name|defaultSolrBatchSize
init|=
literal|20
decl_stmt|;
DECL|field|SOLR_RECORD_WRITER_BATCH_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_RECORD_WRITER_BATCH_SIZE
init|=
literal|"solr.record.writer.batch.size"
decl_stmt|;
DECL|field|SOLR_RECORD_WRITER_MAX_SEGMENTS
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_RECORD_WRITER_MAX_SEGMENTS
init|=
literal|"solr.record.writer.maxSegments"
decl_stmt|;
DECL|method|getSetupOk
specifier|public
specifier|static
name|String
name|getSetupOk
parameter_list|()
block|{
return|return
name|SETUP_OK
return|;
block|}
comment|/** Get the number of threads used for index writing */
DECL|method|setSolrWriterThreadCount
specifier|public
specifier|static
name|void
name|setSolrWriterThreadCount
parameter_list|(
name|int
name|count
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|SOLR_WRITER_THREAD_COUNT
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/** Set the number of threads used for index writing */
DECL|method|getSolrWriterThreadCount
specifier|public
specifier|static
name|int
name|getSolrWriterThreadCount
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|SOLR_WRITER_THREAD_COUNT
argument_list|,
name|defaultSolrWriterThreadCount
argument_list|)
return|;
block|}
comment|/**    * Set the maximum size of the the queue for documents to be written to the    * index.    */
DECL|method|setSolrWriterQueueSize
specifier|public
specifier|static
name|void
name|setSolrWriterQueueSize
parameter_list|(
name|int
name|count
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|SOLR_WRITER_QUEUE_SIZE
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/** Return the maximum size for the number of documents pending index writing. */
DECL|method|getSolrWriterQueueSize
specifier|public
specifier|static
name|int
name|getSolrWriterQueueSize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|SOLR_WRITER_QUEUE_SIZE
argument_list|,
name|defaultSolrWriterQueueSize
argument_list|)
return|;
block|}
comment|/**    * Return the file name portion of the configuration zip file, from the    * configuration.    */
DECL|method|getZipName
specifier|public
specifier|static
name|String
name|getZipName
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|ZIP_NAME
argument_list|,
name|ZIP_FILE_BASE_NAME
argument_list|)
return|;
block|}
comment|/**    * configure the job to output zip files of the output index, or full    * directory trees. Zip files are about 1/5th the size of the raw index, and    * much faster to write, but take more cpu to create.    *     * @param output true if should output zip files    * @param conf to use    */
DECL|method|setOutputZipFormat
specifier|public
specifier|static
name|void
name|setOutputZipFormat
parameter_list|(
name|boolean
name|output
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|OUTPUT_ZIP_FILE
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/**    * return true if the output should be a zip file of the index, rather than    * the raw index    *     * @param conf to use    * @return true if output zip files is on    */
DECL|method|isOutputZipFormat
specifier|public
specifier|static
name|boolean
name|isOutputZipFormat
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|OUTPUT_ZIP_FILE
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getOutputName
specifier|public
specifier|static
name|String
name|getOutputName
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|FileOutputFormat
operator|.
name|getOutputName
argument_list|(
name|job
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkOutputSpecs
specifier|public
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|checkOutputSpecs
argument_list|(
name|job
argument_list|)
expr_stmt|;
if|if
condition|(
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|SETUP_OK
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Solr home cache not set up!"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRecordWriter
specifier|public
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Utils
operator|.
name|getLogConfigFile
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|workDir
init|=
name|getDefaultWorkFile
argument_list|(
name|context
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|batchSize
init|=
name|getBatchSize
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrRecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|context
argument_list|,
name|workDir
argument_list|,
name|batchSize
argument_list|)
return|;
block|}
DECL|method|setupSolrHomeCache
specifier|public
specifier|static
name|void
name|setupSolrHomeCache
parameter_list|(
name|File
name|solrHomeDir
parameter_list|,
name|Job
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|solrHomeZip
init|=
name|createSolrHomeZip
argument_list|(
name|solrHomeDir
argument_list|)
decl_stmt|;
name|addSolrConfToDistributedCache
argument_list|(
name|job
argument_list|,
name|solrHomeZip
argument_list|)
expr_stmt|;
block|}
DECL|method|createSolrHomeZip
specifier|public
specifier|static
name|File
name|createSolrHomeZip
parameter_list|(
name|File
name|solrHomeDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createSolrHomeZip
argument_list|(
name|solrHomeDir
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|createSolrHomeZip
specifier|private
specifier|static
name|File
name|createSolrHomeZip
parameter_list|(
name|File
name|solrHomeDir
parameter_list|,
name|boolean
name|safeToModify
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|solrHomeDir
operator|==
literal|null
operator|||
operator|!
operator|(
name|solrHomeDir
operator|.
name|exists
argument_list|()
operator|&&
name|solrHomeDir
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid solr home: "
operator|+
name|solrHomeDir
argument_list|)
throw|;
block|}
name|File
name|solrHomeZip
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"solr"
argument_list|,
literal|".zip"
argument_list|)
decl_stmt|;
name|createZip
argument_list|(
name|solrHomeDir
argument_list|,
name|solrHomeZip
argument_list|)
expr_stmt|;
return|return
name|solrHomeZip
return|;
block|}
DECL|method|addSolrConfToDistributedCache
specifier|public
specifier|static
name|void
name|addSolrConfToDistributedCache
parameter_list|(
name|Job
name|job
parameter_list|,
name|File
name|solrHomeZip
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make a reasonably unique name for the zip file in the distributed cache
comment|// to avoid collisions if multiple jobs are running.
name|String
name|hdfsZipName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|'.'
operator|+
name|ZIP_FILE_BASE_NAME
decl_stmt|;
name|Configuration
name|jobConf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
name|ZIP_NAME
argument_list|,
name|hdfsZipName
argument_list|)
expr_stmt|;
name|Path
name|zipPath
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|,
name|getZipName
argument_list|(
name|jobConf
argument_list|)
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|jobConf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|solrHomeZip
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|zipPath
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|baseZipUrl
init|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|zipPath
operator|.
name|toString
argument_list|()
operator|+
literal|'#'
operator|+
name|getZipName
argument_list|(
name|jobConf
argument_list|)
argument_list|)
decl_stmt|;
name|DistributedCache
operator|.
name|addCacheArchive
argument_list|(
name|baseZipUrl
argument_list|,
name|jobConf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set Solr distributed cache: {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|job
operator|.
name|getCacheArchives
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set zipPath: {}"
argument_list|,
name|zipPath
argument_list|)
expr_stmt|;
comment|// Actually send the path for the configuration zip file
name|jobConf
operator|.
name|set
argument_list|(
name|SETUP_OK
argument_list|,
name|zipPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createZip
specifier|private
specifier|static
name|void
name|createZip
parameter_list|(
name|File
name|dir
parameter_list|,
name|File
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
argument_list|<
name|File
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
comment|// take only conf/ and lib/
for|for
control|(
name|String
name|allowedDirectory
range|:
name|SolrRecordWriter
operator|.
name|getAllowedConfigDirectories
argument_list|()
control|)
block|{
name|File
name|configDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|allowedDirectory
argument_list|)
decl_stmt|;
name|boolean
name|configDirExists
decl_stmt|;
comment|/** If the directory does not exist, and is required, bail out */
if|if
condition|(
operator|!
operator|(
name|configDirExists
operator|=
name|configDir
operator|.
name|exists
argument_list|()
operator|)
operator|&&
name|SolrRecordWriter
operator|.
name|isRequiredConfigDirectory
argument_list|(
name|allowedDirectory
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"required configuration directory %s is not present in %s"
argument_list|,
name|allowedDirectory
argument_list|,
name|dir
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|configDirExists
condition|)
block|{
continue|continue;
block|}
name|listFiles
argument_list|(
name|configDir
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// Store the files in the existing, allowed
comment|// directory configDir, in the list of files
comment|// to store in the zip file
block|}
name|out
operator|.
name|delete
argument_list|()
expr_stmt|;
name|int
name|subst
init|=
name|dir
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|ZipOutputStream
name|zos
init|=
operator|new
name|ZipOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|ZipEntry
name|ze
init|=
operator|new
name|ZipEntry
argument_list|(
name|f
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|subst
argument_list|)
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|ze
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|int
name|cnt
decl_stmt|;
while|while
condition|(
operator|(
name|cnt
operator|=
name|is
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
name|zos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|zos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|zos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|ZipEntry
name|ze
init|=
operator|new
name|ZipEntry
argument_list|(
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|ze
argument_list|)
expr_stmt|;
name|zos
operator|.
name|write
argument_list|(
literal|"<cores><core name=\"collection1\" instanceDir=\".\"/></cores>"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|zos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|zos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|zos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|listFiles
specifier|private
specifier|static
name|void
name|listFiles
parameter_list|(
name|File
name|dir
parameter_list|,
name|Set
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|File
index|[]
name|list
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
operator|&&
name|dir
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|dir
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|File
name|f
range|:
name|list
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listFiles
argument_list|(
name|f
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getBatchSize
specifier|public
specifier|static
name|int
name|getBatchSize
parameter_list|(
name|Configuration
name|jobConf
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
name|jobConf
operator|.
name|getInt
argument_list|(
name|SolrOutputFormat
operator|.
name|SOLR_RECORD_WRITER_BATCH_SIZE
argument_list|,
name|defaultSolrBatchSize
argument_list|)
return|;
block|}
DECL|method|setBatchSize
specifier|public
specifier|static
name|void
name|setBatchSize
parameter_list|(
name|int
name|count
parameter_list|,
name|Configuration
name|jobConf
parameter_list|)
block|{
name|jobConf
operator|.
name|setInt
argument_list|(
name|SOLR_RECORD_WRITER_BATCH_SIZE
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
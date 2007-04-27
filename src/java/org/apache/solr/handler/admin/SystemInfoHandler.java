begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DataInputStream
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
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|RuntimeMXBean
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
name|net
operator|.
name|InetAddress
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
name|lucene
operator|.
name|LucenePackage
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
name|handler
operator|.
name|RequestHandlerUtils
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
name|request
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
name|util
operator|.
name|SimpleOrderedMap
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
name|XML
import|;
end_import
begin_comment
comment|/**  * This handler returns system info  *   * NOTE: the response format is still likely to change.  It should be designed so  * that it works nicely with an XSLT transformation.  Untill we have a nice  * XSLT frontend for /admin, the format is still open to change.  *   * @author ryan  * @version $Id$  * @since solr 1.2  */
end_comment
begin_class
DECL|class|SystemInfoHandler
specifier|public
class|class
name|SystemInfoHandler
extends|extends
name|RequestHandlerBase
block|{
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
name|Exception
block|{
name|RequestHandlerUtils
operator|.
name|addExperimentalFormatWarning
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"core"
argument_list|,
name|getCoreInfo
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"lucene"
argument_list|,
name|getLuceneInfo
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"jvm"
argument_list|,
name|getJvmInfo
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"system"
argument_list|,
name|getSystemInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get system info    */
DECL|method|getCoreInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getCoreInfo
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|Exception
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"schema"
argument_list|,
name|schema
operator|!=
literal|null
condition|?
name|schema
operator|.
name|getName
argument_list|()
else|:
literal|"no schema!"
argument_list|)
expr_stmt|;
comment|// Host
name|InetAddress
name|addr
init|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"host"
argument_list|,
name|addr
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now
name|info
operator|.
name|add
argument_list|(
literal|"now"
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
comment|// Start Time
name|info
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
operator|new
name|Date
argument_list|(
name|core
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Solr Home
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|dirs
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"instance"
argument_list|,
operator|new
name|File
argument_list|(
name|Config
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"data"
argument_list|,
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
operator|new
name|File
argument_list|(
name|core
operator|.
name|getIndexDir
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"directory"
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
comment|/**    * Get system info    */
DECL|method|getSystemInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getSystemInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|OperatingSystemMXBean
name|os
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|os
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|os
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"arch"
argument_list|,
name|os
operator|.
name|getArch
argument_list|()
argument_list|)
expr_stmt|;
comment|// Java 1.6
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"systemLoadAverage"
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// com.sun.management.UnixOperatingSystemMXBean
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"openFileDescriptorCount"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"maxFileDescriptorCount"
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// com.sun.management.OperatingSystemMXBean
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"committedVirtualMemorySize"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"totalPhysicalMemorySize"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"totalSwapSpaceSize"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|addGetterIfAvaliable
argument_list|(
name|os
argument_list|,
literal|"processCpuTime"
argument_list|,
name|info
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|os
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
comment|// Try some command line things
name|info
operator|.
name|add
argument_list|(
literal|"uname"
argument_list|,
name|execute
argument_list|(
literal|"uname -a"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"ulimit"
argument_list|,
name|execute
argument_list|(
literal|"ulimit -n"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"uptime"
argument_list|,
name|execute
argument_list|(
literal|"uptime"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{}
comment|// ignore
return|return
name|info
return|;
block|}
comment|/**    * Try to run a getter function.  This is usefull because java 1.6 has a few extra    * usefull functions on the<code>OperatingSystemMXBean</code>    *     * If you are running a sun jvm, there are nice functions in:    * UnixOperatingSystemMXBean and com.sun.management.OperatingSystemMXBean    *     * it is package protected so it can be tested...    */
DECL|method|addGetterIfAvaliable
specifier|static
name|void
name|addGetterIfAvaliable
parameter_list|(
name|Object
name|obj
parameter_list|,
name|String
name|getter
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
comment|// This is a 1.6 functon, so lets do a little magic to *try* to make it work
try|try
block|{
name|String
name|n
init|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|getter
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|getter
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"get"
operator|+
name|n
argument_list|)
decl_stmt|;
name|Object
name|v
init|=
name|m
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
name|getter
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// don't worry, this only works for 1.6
block|}
comment|/**    * Utility function to execute a funciton    */
DECL|method|execute
specifier|private
specifier|static
name|String
name|execute
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Process
name|process
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|"(error executing: "
operator|+
name|cmd
operator|+
literal|")"
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get JVM Info - including memory info    */
DECL|method|getJvmInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getJvmInfo
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|jvm
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.version"
argument_list|)
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.name"
argument_list|)
argument_list|)
expr_stmt|;
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"processors"
argument_list|,
name|runtime
operator|.
name|availableProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|used
init|=
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|int
name|percentUsed
init|=
call|(
name|int
call|)
argument_list|(
operator|(
call|(
name|double
call|)
argument_list|(
name|used
argument_list|)
operator|/
operator|(
name|double
operator|)
name|runtime
operator|.
name|maxMemory
argument_list|()
operator|)
operator|*
literal|100
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|mem
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"free"
argument_list|,
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|runtime
operator|.
name|freeMemory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"total"
argument_list|,
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|runtime
operator|.
name|totalMemory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"max"
argument_list|,
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|runtime
operator|.
name|maxMemory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"used"
argument_list|,
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|used
argument_list|)
operator|+
literal|" (%"
operator|+
name|percentUsed
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"memory"
argument_list|,
name|mem
argument_list|)
expr_stmt|;
comment|// JMX properties -- probably should be moved to a different handler
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|jmx
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|RuntimeMXBean
name|mx
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
decl_stmt|;
name|jmx
operator|.
name|add
argument_list|(
literal|"bootclasspath"
argument_list|,
name|mx
operator|.
name|getBootClassPath
argument_list|()
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|add
argument_list|(
literal|"classpath"
argument_list|,
name|mx
operator|.
name|getClassPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// the input arguments passed to the Java virtual machine
comment|// which does not include the arguments to the main method.
name|jmx
operator|.
name|add
argument_list|(
literal|"commandLineArgs"
argument_list|,
name|mx
operator|.
name|getInputArguments
argument_list|()
argument_list|)
expr_stmt|;
comment|// a map of names and values of all system properties.
comment|//jmx.add( "SYSTEM PROPERTIES", mx.getSystemProperties());
name|jmx
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|(
name|mx
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|add
argument_list|(
literal|"upTimeMS"
argument_list|,
name|mx
operator|.
name|getUptime
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|jvm
operator|.
name|add
argument_list|(
literal|"jmx"
argument_list|,
name|jmx
argument_list|)
expr_stmt|;
return|return
name|jvm
return|;
block|}
DECL|method|getLuceneInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getLuceneInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|solrImplVersion
init|=
literal|""
decl_stmt|;
name|String
name|solrSpecVersion
init|=
literal|""
decl_stmt|;
name|String
name|luceneImplVersion
init|=
literal|""
decl_stmt|;
name|String
name|luceneSpecVersion
init|=
literal|""
decl_stmt|;
comment|// ---
name|Package
name|p
init|=
name|SolrCore
operator|.
name|class
operator|.
name|getPackage
argument_list|()
decl_stmt|;
name|StringWriter
name|tmp
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|solrImplVersion
operator|=
name|p
operator|.
name|getImplementationVersion
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|solrImplVersion
condition|)
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|solrImplVersion
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|solrImplVersion
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|tmp
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|solrSpecVersion
operator|=
name|p
operator|.
name|getSpecificationVersion
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|solrSpecVersion
condition|)
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|solrSpecVersion
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|solrSpecVersion
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|p
operator|=
name|LucenePackage
operator|.
name|class
operator|.
name|getPackage
argument_list|()
expr_stmt|;
name|tmp
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|luceneImplVersion
operator|=
name|p
operator|.
name|getImplementationVersion
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|luceneImplVersion
condition|)
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|luceneImplVersion
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|luceneImplVersion
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|tmp
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|luceneSpecVersion
operator|=
name|p
operator|.
name|getSpecificationVersion
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|luceneSpecVersion
condition|)
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|luceneSpecVersion
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|luceneSpecVersion
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|// Add it to the list
name|info
operator|.
name|add
argument_list|(
literal|"solr-spec-version"
argument_list|,
name|solrSpecVersion
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"solr-impl-version"
argument_list|,
name|solrImplVersion
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"lucene-spec-version"
argument_list|,
name|luceneSpecVersion
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"lucene-impl-version"
argument_list|,
name|luceneImplVersion
argument_list|)
expr_stmt|;
return|return
name|info
return|;
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
literal|"Get System Info"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
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

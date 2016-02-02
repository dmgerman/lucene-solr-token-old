begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|DataInput
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
name|DataOutput
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
name|InputStreamDataInput
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
name|OutputStreamDataOutput
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
name|util
operator|.
name|Constants
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
name|util
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|TestUtil
import|;
end_import
begin_comment
comment|/** Child process with silly naive TCP socket server to handle  *  between-node commands, launched for each node  by TestNRTReplication. */
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"MockRandom"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|,
literal|"SimpleText"
block|}
argument_list|)
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Stuff gets printed, important stuff for debugging a failure"
argument_list|)
DECL|class|SimpleServer
specifier|public
class|class
name|SimpleServer
extends|extends
name|LuceneTestCase
block|{
DECL|field|clientThreads
specifier|final
specifier|static
name|Set
argument_list|<
name|Thread
argument_list|>
name|clientThreads
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|stop
specifier|final
specifier|static
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|/** Handles one client connection */
DECL|class|ClientHandler
specifier|private
specifier|static
class|class
name|ClientHandler
extends|extends
name|Thread
block|{
comment|// We hold this just so we can close it to exit the process:
DECL|field|ss
specifier|private
specifier|final
name|ServerSocket
name|ss
decl_stmt|;
DECL|field|socket
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|method|ClientHandler
specifier|public
name|ClientHandler
parameter_list|(
name|ServerSocket
name|ss
parameter_list|,
name|Node
name|node
parameter_list|,
name|Socket
name|socket
parameter_list|)
block|{
name|this
operator|.
name|ss
operator|=
name|ss
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|128
argument_list|,
literal|65536
argument_list|)
expr_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_CONNECTIONS
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"new connection socket="
operator|+
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|//node.message("using stream buffer size=" + bufferSize);
name|InputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|socket
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
name|DataInput
name|in
init|=
operator|new
name|InputStreamDataInput
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|socket
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
name|DataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|bos
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|SimplePrimaryNode
condition|)
block|{
operator|(
operator|(
name|SimplePrimaryNode
operator|)
name|node
operator|)
operator|.
name|handleOneConnection
argument_list|(
name|random
argument_list|()
argument_list|,
name|ss
argument_list|,
name|stop
argument_list|,
name|is
argument_list|,
name|socket
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|SimpleReplicaNode
operator|)
name|node
operator|)
operator|.
name|handleOneConnection
argument_list|(
name|ss
argument_list|,
name|stop
argument_list|,
name|is
argument_list|,
name|socket
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
name|bos
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_CONNECTIONS
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"bos.flush done"
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|SocketException
operator|==
literal|false
operator|&&
name|t
operator|instanceof
name|NodeCommunicationException
operator|==
literal|false
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"unexpected exception handling client connection; now failing test:"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|ss
argument_list|)
expr_stmt|;
comment|// Test should fail with this:
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
else|else
block|{
name|node
operator|.
name|message
argument_list|(
literal|"exception "
operator|+
name|t
operator|+
literal|" handling client connection; ignoring"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Node
operator|.
name|VERBOSE_CONNECTIONS
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"socket.close done"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * currently, this only works/tested on Sun and IBM.    */
comment|// poached from TestIndexWriterOnJRECrash ... should we factor out to TestUtil?  seems dangerous to give it such "publicity"?
DECL|method|crashJRE
specifier|private
specifier|static
name|void
name|crashJRE
parameter_list|()
block|{
specifier|final
name|String
name|vendor
init|=
name|Constants
operator|.
name|JAVA_VENDOR
decl_stmt|;
specifier|final
name|boolean
name|supportsUnsafeNpeDereference
init|=
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Oracle"
argument_list|)
operator|||
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Sun"
argument_list|)
operator|||
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Apple"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|supportsUnsafeNpeDereference
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
decl_stmt|;
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"putAddress"
argument_list|,
name|long
operator|.
name|class
argument_list|,
name|long
operator|.
name|class
argument_list|)
decl_stmt|;
name|m
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't kill the JVM via Unsafe."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fallback attempt to Runtime.halt();
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|halt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't kill the JVM."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|// We couldn't get the JVM to crash for some reason.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JVM refuses to die!"
argument_list|)
throw|;
block|}
DECL|method|writeFilesMetaData
specifier|static
name|void
name|writeFilesMetaData
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
range|:
name|files
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|FileMetaData
name|fmd
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|fmd
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|fmd
operator|.
name|checksum
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fmd
operator|.
name|header
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|fmd
operator|.
name|header
argument_list|,
literal|0
argument_list|,
name|fmd
operator|.
name|header
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fmd
operator|.
name|footer
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|fmd
operator|.
name|footer
argument_list|,
literal|0
argument_list|,
name|fmd
operator|.
name|footer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readFilesMetaData
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|readFilesMetaData
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|fileCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("readFilesMetaData: fileCount=" + fileCount);
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
comment|//System.out.println("readFilesMetaData: fileName=" + fileName);
name|long
name|length
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|long
name|checksum
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|header
argument_list|,
literal|0
argument_list|,
name|header
operator|.
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|footer
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|footer
argument_list|,
literal|0
argument_list|,
name|footer
operator|.
name|length
argument_list|)
expr_stmt|;
name|files
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
operator|new
name|FileMetaData
argument_list|(
name|header
argument_list|,
name|footer
argument_list|,
name|length
argument_list|,
name|checksum
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
comment|/** Pulls CopyState off the wire */
DECL|method|readCopyState
specifier|static
name|CopyState
name|readCopyState
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Decode a new CopyState
name|byte
index|[]
name|infosBytes
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|infosBytes
argument_list|,
literal|0
argument_list|,
name|infosBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|long
name|gen
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|long
name|version
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
init|=
name|readFilesMetaData
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|completedMergeFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|completedMergeFiles
operator|.
name|add
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|primaryGen
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
return|return
operator|new
name|CopyState
argument_list|(
name|files
argument_list|,
name|version
argument_list|,
name|gen
argument_list|,
name|infosBytes
argument_list|,
name|completedMergeFiles
argument_list|,
name|primaryGen
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.nodeid"
argument_list|)
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"main child "
operator|+
name|id
argument_list|)
expr_stmt|;
name|Path
name|indexPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.indexpath"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|isPrimary
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.isPrimary"
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|int
name|primaryTCPPort
decl_stmt|;
name|long
name|forcePrimaryVersion
decl_stmt|;
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
name|forcePrimaryVersion
operator|=
operator|-
literal|1
expr_stmt|;
name|primaryTCPPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.primaryTCPPort"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|primaryTCPPort
operator|=
operator|-
literal|1
expr_stmt|;
name|forcePrimaryVersion
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.forcePrimaryVersion"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|primaryGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.primaryGen"
argument_list|)
argument_list|)
decl_stmt|;
name|Node
operator|.
name|globalStartNS
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.startNS"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|doRandomCrash
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.doRandomCrash"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|doRandomClose
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.doRandomClose"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|doFlipBitsDuringCopy
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.nrtreplication.doFlipBitsDuringCopy"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Create server socket that we listen for incoming requests on:
try|try
init|(
specifier|final
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
init|)
block|{
name|int
name|tcpPort
init|=
operator|(
operator|(
name|InetSocketAddress
operator|)
name|ss
operator|.
name|getLocalSocketAddress
argument_list|()
operator|)
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nPORT: "
operator|+
name|tcpPort
argument_list|)
expr_stmt|;
specifier|final
name|Node
name|node
decl_stmt|;
if|if
condition|(
name|isPrimary
condition|)
block|{
name|node
operator|=
operator|new
name|SimplePrimaryNode
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexPath
argument_list|,
name|id
argument_list|,
name|tcpPort
argument_list|,
name|primaryGen
argument_list|,
name|forcePrimaryVersion
argument_list|,
literal|null
argument_list|,
name|doFlipBitsDuringCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nCOMMIT VERSION: "
operator|+
operator|(
operator|(
name|PrimaryNode
operator|)
name|node
operator|)
operator|.
name|getLastCommitVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|node
operator|=
operator|new
name|SimpleReplicaNode
argument_list|(
name|random
argument_list|()
argument_list|,
name|id
argument_list|,
name|tcpPort
argument_list|,
name|indexPath
argument_list|,
name|primaryGen
argument_list|,
name|primaryTCPPort
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
if|if
condition|(
name|re
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"replica cannot start"
argument_list|)
condition|)
block|{
comment|// this is "OK": it means MDW's refusal to delete a segments_N commit point means we cannot start:
name|assumeTrue
argument_list|(
name|re
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
throw|throw
name|re
throw|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nINFOS VERSION: "
operator|+
name|node
operator|.
name|getCurrentSearchingVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|doRandomClose
operator|||
name|doRandomCrash
condition|)
block|{
specifier|final
name|int
name|waitForMS
decl_stmt|;
if|if
condition|(
name|isPrimary
condition|)
block|{
name|waitForMS
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|waitForMS
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
name|boolean
name|doClose
decl_stmt|;
if|if
condition|(
name|doRandomCrash
operator|==
literal|false
condition|)
block|{
name|doClose
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doRandomClose
condition|)
block|{
name|doClose
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doClose
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|doClose
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: will close after "
operator|+
operator|(
name|waitForMS
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: will crash after "
operator|+
operator|(
name|waitForMS
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
block|}
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|endTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|waitForMS
operator|*
literal|1000000L
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|endTime
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
if|if
condition|(
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|stop
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|doClose
condition|)
block|{
try|try
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: now force close server socket after "
operator|+
operator|(
name|waitForMS
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|node
operator|.
name|state
operator|=
literal|"top-closing"
expr_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: now crash JVM after "
operator|+
operator|(
name|waitForMS
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|crashJRE
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|isPrimary
condition|)
block|{
name|t
operator|.
name|setName
argument_list|(
literal|"crasher P"
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|setName
argument_list|(
literal|"crasher R"
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
comment|// So that if node exits naturally, this thread won't prevent process exit:
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nNODE STARTED"
argument_list|)
expr_stmt|;
comment|//List<Thread> clientThreads = new ArrayList<>();
comment|// Naive thread-per-connection server:
while|while
condition|(
literal|true
condition|)
block|{
name|Socket
name|socket
decl_stmt|;
try|try
block|{
name|socket
operator|=
name|ss
operator|.
name|accept
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|se
parameter_list|)
block|{
comment|// when ClientHandler closes our ss we will hit this
name|node
operator|.
name|message
argument_list|(
literal|"top: server socket exc; now exit"
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
name|thread
init|=
operator|new
name|ClientHandler
argument_list|(
name|ss
argument_list|,
name|node
argument_list|,
name|socket
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientThreads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
comment|// Prune finished client threads:
name|Iterator
argument_list|<
name|Thread
argument_list|>
name|it
init|=
name|clientThreads
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
name|Thread
name|t
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
operator|==
literal|false
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|//node.message(clientThreads.size() + " client threads are still alive");
block|}
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Make sure all client threads are done, else we get annoying (yet ultimately "harmless") messages about threads still running /
comment|// lingering for them to finish from the child processes:
for|for
control|(
name|Thread
name|clientThread
range|:
name|clientThreads
control|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: join clientThread="
operator|+
name|clientThread
argument_list|)
expr_stmt|;
name|clientThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|node
operator|.
name|message
argument_list|(
literal|"top: done join clientThread="
operator|+
name|clientThread
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|message
argument_list|(
literal|"done join all client threads; now close node"
argument_list|)
expr_stmt|;
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

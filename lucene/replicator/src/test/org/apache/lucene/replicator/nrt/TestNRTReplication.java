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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|LineFileDocs
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SeedUtils
import|;
end_import
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|List
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicLong
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|// MockRandom's .sd file has no index header/footer:
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
DECL|class|TestNRTReplication
specifier|public
class|class
name|TestNRTReplication
extends|extends
name|LuceneTestCase
block|{
comment|/** cwd where we start each child (server) node */
DECL|field|childTempDir
specifier|private
name|Path
name|childTempDir
decl_stmt|;
DECL|field|nodeStartCounter
specifier|final
name|AtomicLong
name|nodeStartCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/** Launches a child "server" (separate JVM), which is either primary or replica node */
DECL|method|startNode
name|NodeProcess
name|startNode
parameter_list|(
name|int
name|primaryTCPPort
parameter_list|,
specifier|final
name|int
name|id
parameter_list|,
name|Path
name|indexPath
parameter_list|,
name|boolean
name|isPrimary
parameter_list|,
name|long
name|forcePrimaryVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"bin"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"java"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Xmx512m"
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryTCPPort
operator|!=
operator|-
literal|1
condition|)
block|{
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.primaryTCPPort="
operator|+
name|primaryTCPPort
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
comment|// We cannot start a replica when there is no primary:
return|return
literal|null
return|;
block|}
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.closeorcrash=false"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.node=true"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.nodeid="
operator|+
name|id
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.startNS="
operator|+
name|Node
operator|.
name|globalStartNS
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.indexpath="
operator|+
name|indexPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPrimary
condition|)
block|{
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.isPrimary=true"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.forcePrimaryVersion="
operator|+
name|forcePrimaryVersion
argument_list|)
expr_stmt|;
block|}
name|long
name|myPrimaryGen
init|=
literal|0
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nrtreplication.primaryGen="
operator|+
name|myPrimaryGen
argument_list|)
expr_stmt|;
comment|// Mixin our own counter because this is called from a fresh thread which means the seed otherwise isn't changing each time we spawn a
comment|// new node:
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
operator|*
name|nodeStartCounter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.seed="
operator|+
name|SeedUtils
operator|.
name|formatSeed
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-ea"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"org.junit.runner.JUnitCore"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"SimpleServer"
argument_list|)
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"child process command: "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Important, so that the scary looking hs_err_<pid>.log appear under our test temp dir:
name|pb
operator|.
name|directory
argument_list|(
name|childTempDir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|Process
name|p
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|BufferedReader
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|p
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|uee
argument_list|)
throw|;
block|}
name|int
name|tcpPort
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|initCommitVersion
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|initInfosVersion
init|=
operator|-
literal|1
decl_stmt|;
name|Pattern
name|logTimeStart
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[0-9\\.]+s .*"
argument_list|)
decl_stmt|;
name|boolean
name|willCrash
init|=
literal|false
decl_stmt|;
name|boolean
name|sawExistingSegmentsFile
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|l
init|=
name|r
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"top: node="
operator|+
name|id
operator|+
literal|" failed to start"
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|message
argument_list|(
literal|"exit value="
operator|+
name|p
operator|.
name|exitValue
argument_list|()
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"top: now fail test replica R"
operator|+
name|id
operator|+
literal|" failed to start"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"replica R"
operator|+
name|id
operator|+
literal|" failed to start"
argument_list|)
throw|;
block|}
if|if
condition|(
name|logTimeStart
operator|.
name|matcher
argument_list|(
name|l
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// Already a well-formed log output:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|.
name|startsWith
argument_list|(
literal|"PORT: "
argument_list|)
condition|)
block|{
name|tcpPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|l
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|l
operator|.
name|startsWith
argument_list|(
literal|"COMMIT VERSION: "
argument_list|)
condition|)
block|{
name|initCommitVersion
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|l
operator|.
name|substring
argument_list|(
literal|16
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|l
operator|.
name|startsWith
argument_list|(
literal|"INFOS VERSION: "
argument_list|)
condition|)
block|{
name|initInfosVersion
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|l
operator|.
name|substring
argument_list|(
literal|15
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|l
operator|.
name|contains
argument_list|(
literal|"will crash after"
argument_list|)
condition|)
block|{
name|willCrash
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|l
operator|.
name|startsWith
argument_list|(
literal|"NODE STARTED"
argument_list|)
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|l
operator|.
name|contains
argument_list|(
literal|"replica cannot start: existing segments file="
argument_list|)
condition|)
block|{
name|sawExistingSegmentsFile
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|finalWillCrash
init|=
name|willCrash
decl_stmt|;
comment|// Baby sits the child process, pulling its stdout and printing to our stdout:
name|AtomicBoolean
name|nodeClosing
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|Thread
name|pumper
init|=
name|ThreadPumper
operator|.
name|start
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|message
argument_list|(
literal|"now wait for process "
operator|+
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
name|message
argument_list|(
literal|"done wait for process "
operator|+
name|p
argument_list|)
expr_stmt|;
name|int
name|exitValue
init|=
name|p
operator|.
name|exitValue
argument_list|()
decl_stmt|;
name|message
argument_list|(
literal|"exit value="
operator|+
name|exitValue
operator|+
literal|" willCrash="
operator|+
name|finalWillCrash
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitValue
operator|!=
literal|0
condition|)
block|{
comment|// should fail test
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"node "
operator|+
name|id
operator|+
literal|" process had unexpected non-zero exit status="
operator|+
name|exitValue
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|,
name|r
argument_list|,
name|System
operator|.
name|out
argument_list|,
literal|null
argument_list|,
name|nodeClosing
argument_list|)
decl_stmt|;
name|pumper
operator|.
name|setName
argument_list|(
literal|"pump"
operator|+
name|id
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"top: node="
operator|+
name|id
operator|+
literal|" started at tcpPort="
operator|+
name|tcpPort
operator|+
literal|" initCommitVersion="
operator|+
name|initCommitVersion
operator|+
literal|" initInfosVersion="
operator|+
name|initInfosVersion
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeProcess
argument_list|(
name|p
argument_list|,
name|id
argument_list|,
name|tcpPort
argument_list|,
name|pumper
argument_list|,
name|isPrimary
argument_list|,
name|initCommitVersion
argument_list|,
name|initInfosVersion
argument_list|,
name|nodeClosing
argument_list|)
return|;
block|}
DECL|method|testReplicateDeleteAllDocuments
specifier|public
name|void
name|testReplicateDeleteAllDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
operator|.
name|globalStartNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|childTempDir
operator|=
name|createTempDir
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"change thread name from "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"main"
argument_list|)
expr_stmt|;
name|Path
name|primaryPath
init|=
name|createTempDir
argument_list|(
literal|"primary"
argument_list|)
decl_stmt|;
name|NodeProcess
name|primary
init|=
name|startNode
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
name|primaryPath
argument_list|,
literal|true
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Path
name|replicaPath
init|=
name|createTempDir
argument_list|(
literal|"replica"
argument_list|)
decl_stmt|;
name|NodeProcess
name|replica
init|=
name|startNode
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|,
literal|1
argument_list|,
name|replicaPath
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Tell primary current replicas:
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SET_REPLICAS
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|replica
operator|.
name|id
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|replica
operator|.
name|tcpPort
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
comment|// Index 10 docs into primary:
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|primaryC
init|=
operator|new
name|Connection
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|)
decl_stmt|;
name|primaryC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_INDEXING
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addOrUpdateDocument
argument_list|(
name|primaryC
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Nothing in replica index yet
name|Connection
name|replicaC
init|=
operator|new
name|Connection
argument_list|(
name|replica
operator|.
name|tcpPort
argument_list|)
decl_stmt|;
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|version1
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|version1
argument_list|)
expr_stmt|;
name|int
name|hitCount
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion1
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion1
operator|>
literal|0
argument_list|)
expr_stmt|;
name|long
name|version2
decl_stmt|;
comment|// Wait for replica to show the change
while|while
condition|(
literal|true
condition|)
block|{
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|version2
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|hitCount
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|version2
operator|==
name|primaryVersion1
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
comment|// good!
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// Delete all docs from primary
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Inefficiently:
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
literal|10
condition|;
name|id
operator|++
control|)
block|{
name|primary
operator|.
name|deleteDocument
argument_list|(
name|primaryC
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Efficiently:
name|primary
operator|.
name|deleteAllDocuments
argument_list|(
name|primaryC
argument_list|)
expr_stmt|;
block|}
comment|// Replica still shows 10 docs:
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|version3
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|version2
argument_list|,
name|version3
argument_list|)
expr_stmt|;
name|hitCount
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion2
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion2
operator|>
name|primaryVersion1
argument_list|)
expr_stmt|;
comment|// Wait for replica to show the change
name|long
name|version4
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|version4
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|hitCount
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|version4
operator|==
name|primaryVersion2
condition|)
block|{
name|assertTrue
argument_list|(
name|version4
operator|>
name|version3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
comment|// good!
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// Index 10 docs again:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addOrUpdateDocument
argument_list|(
name|primaryC
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion3
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion3
operator|>
name|primaryVersion2
argument_list|)
expr_stmt|;
comment|// Wait for replica to show the change
while|while
condition|(
literal|true
condition|)
block|{
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|version5
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|hitCount
operator|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|version5
operator|==
name|primaryVersion3
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|version5
operator|>
name|version4
argument_list|)
expr_stmt|;
comment|// good!
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|replicaC
operator|.
name|close
argument_list|()
expr_stmt|;
name|primaryC
operator|.
name|close
argument_list|()
expr_stmt|;
name|replica
operator|.
name|close
argument_list|()
expr_stmt|;
name|primary
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReplicateForceMerge
specifier|public
name|void
name|testReplicateForceMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
operator|.
name|globalStartNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|childTempDir
operator|=
name|createTempDir
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"change thread name from "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"main"
argument_list|)
expr_stmt|;
name|Path
name|primaryPath
init|=
name|createTempDir
argument_list|(
literal|"primary"
argument_list|)
decl_stmt|;
name|NodeProcess
name|primary
init|=
name|startNode
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
name|primaryPath
argument_list|,
literal|true
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Path
name|replicaPath
init|=
name|createTempDir
argument_list|(
literal|"replica"
argument_list|)
decl_stmt|;
name|NodeProcess
name|replica
init|=
name|startNode
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|,
literal|1
argument_list|,
name|replicaPath
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Tell primary current replicas:
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SET_REPLICAS
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|replica
operator|.
name|id
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|replica
operator|.
name|tcpPort
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
comment|// Index 10 docs into primary:
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|primaryC
init|=
operator|new
name|Connection
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|)
decl_stmt|;
name|primaryC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_INDEXING
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addOrUpdateDocument
argument_list|(
name|primaryC
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion1
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion1
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Index 10 more docs into primary:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addOrUpdateDocument
argument_list|(
name|primaryC
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion2
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion2
operator|>
name|primaryVersion1
argument_list|)
expr_stmt|;
name|primary
operator|.
name|forceMerge
argument_list|(
name|primaryC
argument_list|)
expr_stmt|;
comment|// Refresh primary, which also pushes to replica:
name|long
name|primaryVersion3
init|=
name|primary
operator|.
name|flush
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|primaryVersion3
operator|>
name|primaryVersion2
argument_list|)
expr_stmt|;
name|Connection
name|replicaC
init|=
operator|new
name|Connection
argument_list|(
name|replica
operator|.
name|tcpPort
argument_list|)
decl_stmt|;
comment|// Wait for replica to show the change
while|while
condition|(
literal|true
condition|)
block|{
name|replicaC
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_SEARCH_ALL
argument_list|)
expr_stmt|;
name|replicaC
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|version
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|int
name|hitCount
init|=
name|replicaC
operator|.
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|==
name|primaryVersion3
condition|)
block|{
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
comment|// good!
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|replicaC
operator|.
name|close
argument_list|()
expr_stmt|;
name|primaryC
operator|.
name|close
argument_list|()
expr_stmt|;
name|replica
operator|.
name|close
argument_list|()
expr_stmt|;
name|primary
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|message
specifier|static
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%5.3fs       :     parent [%11s] %s"
argument_list|,
operator|(
name|now
operator|-
name|Node
operator|.
name|globalStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

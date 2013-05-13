begin_unit
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|ClientConnectionManager
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingClientConnectionManager
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Handler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|thread
operator|.
name|QueuedThreadPool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
literal|"Lucene3x"
argument_list|)
DECL|class|ReplicatorTestCase
specifier|public
specifier|abstract
class|class
name|ReplicatorTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|BASE_PORT
specifier|private
specifier|static
specifier|final
name|int
name|BASE_PORT
init|=
literal|7000
decl_stmt|;
comment|// if a test calls newServer() multiple times, or some ports already failed,
comment|// don't start from BASE_PORT again
DECL|field|lastPortUsed
specifier|private
specifier|static
name|int
name|lastPortUsed
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|clientConnectionManager
specifier|private
specifier|static
name|ClientConnectionManager
name|clientConnectionManager
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassReplicatorTestCase
specifier|public
specifier|static
name|void
name|afterClassReplicatorTestCase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|clientConnectionManager
operator|!=
literal|null
condition|)
block|{
name|clientConnectionManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clientConnectionManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Returns a new {@link Server HTTP Server} instance. To obtain its port, use    * {@link #serverPort(Server)}.    */
DECL|method|newHttpServer
specifier|public
specifier|static
specifier|synchronized
name|Server
name|newHttpServer
parameter_list|(
name|Handler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|lastPortUsed
operator|==
operator|-
literal|1
condition|?
name|BASE_PORT
else|:
name|lastPortUsed
operator|+
literal|1
decl_stmt|;
name|Server
name|server
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|server
operator|=
operator|new
name|Server
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|QueuedThreadPool
name|threadPool
init|=
operator|new
name|QueuedThreadPool
argument_list|()
decl_stmt|;
name|threadPool
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|setMaxIdleTimeMs
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|server
operator|.
name|setThreadPool
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
comment|// this will test the port
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// if here, port is available
name|lastPortUsed
operator|=
name|port
expr_stmt|;
return|return
name|server
return|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|stopHttpServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
comment|// this is ok, we'll try the next port until successful.
operator|++
name|port
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns a {@link Server}'s port. This method assumes that no    * {@link Connector}s were added to the Server besides the default one.    */
DECL|method|serverPort
specifier|public
specifier|static
name|int
name|serverPort
parameter_list|(
name|Server
name|httpServer
parameter_list|)
block|{
return|return
name|httpServer
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
return|;
block|}
comment|/**    * Stops the given HTTP Server instance. This method does its best to guarantee    * that no threads will be left running following this method.    */
DECL|method|stopHttpServer
specifier|public
specifier|static
name|void
name|stopHttpServer
parameter_list|(
name|Server
name|httpServer
parameter_list|)
throws|throws
name|Exception
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|httpServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a {@link ClientConnectionManager}.    *<p>    *<b>NOTE:</b> do not {@link ClientConnectionManager#shutdown()} this    * connection manager, it will be shutdown automatically after all tests have    * finished.    */
DECL|method|getClientConnectionManager
specifier|public
specifier|static
specifier|synchronized
name|ClientConnectionManager
name|getClientConnectionManager
parameter_list|()
block|{
if|if
condition|(
name|clientConnectionManager
operator|==
literal|null
condition|)
block|{
name|PoolingClientConnectionManager
name|ccm
init|=
operator|new
name|PoolingClientConnectionManager
argument_list|()
decl_stmt|;
name|ccm
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|ccm
operator|.
name|setMaxTotal
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|clientConnectionManager
operator|=
name|ccm
expr_stmt|;
block|}
return|return
name|clientConnectionManager
return|;
block|}
block|}
end_class
end_unit

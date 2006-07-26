begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.db4o
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|db4o
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
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
name|Proxy
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseFeed
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|Component
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|Scope
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ScopeVisitor
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|configuration
operator|.
name|Requiered
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
name|gdata
operator|.
name|storage
operator|.
name|IDGenerator
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
name|gdata
operator|.
name|storage
operator|.
name|Storage
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
name|gdata
operator|.
name|storage
operator|.
name|StorageController
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
name|gdata
operator|.
name|storage
operator|.
name|StorageException
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
name|gdata
operator|.
name|storage
operator|.
name|db4o
operator|.
name|DB4oStorage
operator|.
name|DB4oEntry
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
name|gdata
operator|.
name|utils
operator|.
name|Pool
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
name|gdata
operator|.
name|utils
operator|.
name|PoolObjectFactory
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
name|gdata
operator|.
name|utils
operator|.
name|SimpleObjectPool
import|;
end_import
begin_import
import|import
name|com
operator|.
name|db4o
operator|.
name|Db4o
import|;
end_import
begin_import
import|import
name|com
operator|.
name|db4o
operator|.
name|ObjectContainer
import|;
end_import
begin_import
import|import
name|com
operator|.
name|db4o
operator|.
name|ObjectServer
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import
begin_comment
comment|/**  * The DB4o StorageContorller can be used as a persitence component for the  * gdata-server. To use DB4o a third party jar needs to added to the lib  * directory of the project. If the jar is not available in the lib directory  * all db4o dependent class won't be included in the build.  *<p>  * If the jar is present in the lib directory this class can be configured as a  * {@link org.apache.lucene.gdata.server.registry.ComponentType#STORAGECONTROLLER}  * via the<i>gdata-config.xml</i> file. For detailed config documentation see  * the wiki page.  *</p>  *<p>  * The DB4oController can run as a client or as a server to serve other running  * db4o clients in the network. To achive the best performance out of the db4o  * caching layer connections to the server will be reused in a connection pool.  * A connection will not be shared withing more than one thread. The controller  * release one connection per request and returns the connection when the  * request has been destroyed.  *</p>  * @see<a href="http://www.db4o.com">db4o website</a>  * @see org.apache.lucene.gdata.utils.Pool  *   *   * @author Simon Willnauer  *   */
end_comment
begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|STORAGECONTROLLER
argument_list|)
annotation|@
name|Scope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|ScopeType
operator|.
name|REQUEST
argument_list|)
DECL|class|DB4oController
specifier|public
class|class
name|DB4oController
implements|implements
name|StorageController
implements|,
name|ScopeVisitor
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DB4oController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|threadLocalStorage
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Storage
argument_list|>
name|threadLocalStorage
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Storage
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|containerPool
specifier|private
name|Pool
argument_list|<
name|ObjectContainer
argument_list|>
name|containerPool
decl_stmt|;
DECL|field|server
specifier|private
name|ObjectServer
name|server
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|final
name|IDGenerator
name|idGenerator
decl_stmt|;
DECL|field|weakReferences
specifier|private
name|boolean
name|weakReferences
decl_stmt|;
DECL|field|runAsServer
specifier|private
name|boolean
name|runAsServer
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|filePath
specifier|private
name|String
name|filePath
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|password
specifier|private
name|String
name|password
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
decl_stmt|;
DECL|field|containerPoolSize
specifier|private
name|int
name|containerPoolSize
decl_stmt|;
comment|/**      * @throws NoSuchAlgorithmException      *       */
DECL|method|DB4oController
specifier|public
name|DB4oController
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
block|{
name|this
operator|.
name|idGenerator
operator|=
operator|new
name|IDGenerator
argument_list|(
literal|15
argument_list|)
expr_stmt|;
block|}
DECL|method|releaseContainer
name|ObjectContainer
name|releaseContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|server
operator|.
name|openClient
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.StorageController#destroy()      */
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|this
operator|.
name|containerPool
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|this
operator|.
name|idGenerator
operator|.
name|stopIDGenerator
argument_list|()
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.StorageController#getStorage()      */
DECL|method|getStorage
specifier|public
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
block|{
name|Storage
name|retVal
init|=
name|this
operator|.
name|threadLocalStorage
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|retVal
operator|!=
literal|null
condition|)
return|return
name|retVal
return|;
name|retVal
operator|=
operator|new
name|DB4oStorage
argument_list|(
name|this
operator|.
name|containerPool
operator|.
name|aquire
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadLocalStorage
operator|.
name|set
argument_list|(
name|retVal
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#initialize()      */
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialize "
operator|+
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|DB4oEntry
operator|.
name|class
argument_list|)
operator|.
name|objectField
argument_list|(
literal|"updated"
argument_list|)
operator|.
name|indexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|BaseEntry
operator|.
name|class
argument_list|)
operator|.
name|objectField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|indexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|BaseFeed
operator|.
name|class
argument_list|)
operator|.
name|objectField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|indexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|GDataAccount
operator|.
name|class
argument_list|)
operator|.
name|objectField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|indexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|ServerBaseFeed
operator|.
name|class
argument_list|)
operator|.
name|cascadeOnDelete
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|ServerBaseFeed
operator|.
name|class
argument_list|)
operator|.
name|maximumActivationDepth
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|BaseFeed
operator|.
name|class
argument_list|)
operator|.
name|minimumActivationDepth
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|BaseEntry
operator|.
name|class
argument_list|)
operator|.
name|minimumActivationDepth
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|BaseFeed
operator|.
name|class
argument_list|)
operator|.
name|cascadeOnDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|DB4oEntry
operator|.
name|class
argument_list|)
operator|.
name|cascadeOnDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|objectClass
argument_list|(
name|GDataAccount
operator|.
name|class
argument_list|)
operator|.
name|cascadeOnDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Db4o
operator|.
name|configure
argument_list|()
operator|.
name|weakReferences
argument_list|(
name|this
operator|.
name|weakReferences
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|runAsServer
condition|)
block|{
name|this
operator|.
name|server
operator|=
name|Db4o
operator|.
name|openServer
argument_list|(
name|this
operator|.
name|filePath
argument_list|,
name|this
operator|.
name|port
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|grantAccess
argument_list|(
name|this
operator|.
name|user
argument_list|,
name|this
operator|.
name|password
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|InvocationHandler
name|handler
init|=
operator|new
name|ObjectServerDecorator
argument_list|(
name|this
operator|.
name|user
argument_list|,
name|this
operator|.
name|password
argument_list|,
name|this
operator|.
name|host
argument_list|,
name|this
operator|.
name|port
argument_list|)
decl_stmt|;
name|this
operator|.
name|server
operator|=
operator|(
name|ObjectServer
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|ObjectServer
operator|.
name|class
block|}
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
name|PoolObjectFactory
argument_list|<
name|ObjectContainer
argument_list|>
name|factory
init|=
operator|new
name|ObjectContinerFactory
argument_list|(
name|this
operator|.
name|server
argument_list|)
decl_stmt|;
name|this
operator|.
name|containerPool
operator|=
operator|new
name|SimpleObjectPool
argument_list|<
name|ObjectContainer
argument_list|>
argument_list|(
name|this
operator|.
name|containerPoolSize
argument_list|,
name|factory
argument_list|)
expr_stmt|;
try|try
block|{
name|createAdminAccount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAdminAccount
specifier|private
name|void
name|createAdminAccount
parameter_list|()
throws|throws
name|StorageException
block|{
name|GDataAccount
name|adminAccount
init|=
name|GDataAccount
operator|.
name|createAdminAccount
argument_list|()
decl_stmt|;
name|visiteInitialize
argument_list|()
expr_stmt|;
name|Storage
name|sto
init|=
name|this
operator|.
name|getStorage
argument_list|()
decl_stmt|;
try|try
block|{
name|sto
operator|.
name|getAccount
argument_list|(
name|adminAccount
operator|.
name|getName
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
name|this
operator|.
name|getStorage
argument_list|()
operator|.
name|storeAccount
argument_list|(
name|adminAccount
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|visiteDestroy
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.StorageController#releaseId()      */
DECL|method|releaseId
specifier|public
name|String
name|releaseId
parameter_list|()
block|{
try|try
block|{
return|return
name|this
operator|.
name|idGenerator
operator|.
name|getUID
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
literal|"ID producer has been interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ScopeVisitor#visiteInitialize()      */
DECL|method|visiteInitialize
specifier|public
name|void
name|visiteInitialize
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Opened Storage -- request initialized"
argument_list|)
expr_stmt|;
name|Storage
name|storage
init|=
name|this
operator|.
name|threadLocalStorage
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|storage
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Storage already opened"
argument_list|)
expr_stmt|;
return|return;
block|}
name|storage
operator|=
operator|new
name|DB4oStorage
argument_list|(
name|this
operator|.
name|containerPool
operator|.
name|aquire
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadLocalStorage
operator|.
name|set
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ScopeVisitor#visiteDestroy()      */
DECL|method|visiteDestroy
specifier|public
name|void
name|visiteDestroy
parameter_list|()
block|{
name|Storage
name|storage
init|=
name|this
operator|.
name|threadLocalStorage
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|storage
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"no Storage opened -- threadlocal returned null"
argument_list|)
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|containerPool
operator|.
name|release
argument_list|(
operator|(
operator|(
name|DB4oStorage
operator|)
name|storage
operator|)
operator|.
name|getContainer
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadLocalStorage
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Closed Storage -- request destroyed"
argument_list|)
expr_stmt|;
block|}
DECL|class|ObjectContinerFactory
specifier|private
specifier|static
class|class
name|ObjectContinerFactory
implements|implements
name|PoolObjectFactory
argument_list|<
name|ObjectContainer
argument_list|>
block|{
DECL|field|server
specifier|private
specifier|final
name|ObjectServer
name|server
decl_stmt|;
DECL|method|ObjectContinerFactory
name|ObjectContinerFactory
parameter_list|(
specifier|final
name|ObjectServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
comment|/**          * @see org.apache.lucene.gdata.utils.PoolObjectFactory#getInstance()          */
DECL|method|getInstance
specifier|public
name|ObjectContainer
name|getInstance
parameter_list|()
block|{
return|return
name|this
operator|.
name|server
operator|.
name|openClient
argument_list|()
return|;
block|}
comment|/**          * @param type -          *            object container to destroy (close)          * @see org.apache.lucene.gdata.utils.PoolObjectFactory#destroyInstance(Object)          */
DECL|method|destroyInstance
specifier|public
name|void
name|destroyInstance
parameter_list|(
name|ObjectContainer
name|type
parameter_list|)
block|{
name|type
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return Returns the filePath.      */
DECL|method|getFilePath
specifier|public
name|String
name|getFilePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|filePath
return|;
block|}
comment|/**      * @param filePath      *            The filePath to set.      */
DECL|method|setFilePath
specifier|public
name|void
name|setFilePath
parameter_list|(
name|String
name|filePath
parameter_list|)
block|{
name|this
operator|.
name|filePath
operator|=
name|filePath
expr_stmt|;
block|}
comment|/**      * @return Returns the host.      */
DECL|method|getHost
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|this
operator|.
name|host
return|;
block|}
comment|/**      * @param host      *            The host to set.      */
annotation|@
name|Requiered
DECL|method|setHost
specifier|public
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
block|}
comment|/**      * @return Returns the password.      */
DECL|method|getPassword
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|this
operator|.
name|password
return|;
block|}
comment|/**      * @param password      *            The password to set.      */
annotation|@
name|Requiered
DECL|method|setPassword
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * @return Returns the port.      */
DECL|method|getPort
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|this
operator|.
name|port
return|;
block|}
comment|/**      * @param port      *            The port to set.      */
annotation|@
name|Requiered
DECL|method|setPort
specifier|public
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
comment|/**      * @return Returns the runAsServer.      */
DECL|method|isRunAsServer
specifier|public
name|boolean
name|isRunAsServer
parameter_list|()
block|{
return|return
name|this
operator|.
name|runAsServer
return|;
block|}
comment|/**      * @param runAsServer      *            The runAsServer to set.      */
annotation|@
name|Requiered
DECL|method|setRunAsServer
specifier|public
name|void
name|setRunAsServer
parameter_list|(
name|boolean
name|runAsServer
parameter_list|)
block|{
name|this
operator|.
name|runAsServer
operator|=
name|runAsServer
expr_stmt|;
block|}
comment|/**      * @return Returns the user.      */
DECL|method|getUser
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
comment|/**      * @param user      *            The user to set.      */
annotation|@
name|Requiered
DECL|method|setUser
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
comment|/**      * @return Returns the weakReferences.      */
DECL|method|isUseWeakReferences
specifier|public
name|boolean
name|isUseWeakReferences
parameter_list|()
block|{
return|return
name|this
operator|.
name|weakReferences
return|;
block|}
comment|/**      * @param weakReferences      *            The weakReferences to set.      */
annotation|@
name|Requiered
DECL|method|setUseWeakReferences
specifier|public
name|void
name|setUseWeakReferences
parameter_list|(
name|boolean
name|weakReferences
parameter_list|)
block|{
name|this
operator|.
name|weakReferences
operator|=
name|weakReferences
expr_stmt|;
block|}
comment|/**      * @return Returns the containerPoolSize.      */
DECL|method|getContainerPoolSize
specifier|public
name|int
name|getContainerPoolSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerPoolSize
return|;
block|}
comment|/**      * @param containerPoolSize      *            The containerPoolSize to set.      */
annotation|@
name|Requiered
DECL|method|setContainerPoolSize
specifier|public
name|void
name|setContainerPoolSize
parameter_list|(
name|int
name|containerPoolSize
parameter_list|)
block|{
name|this
operator|.
name|containerPoolSize
operator|=
name|containerPoolSize
operator|<
literal|1
condition|?
literal|1
else|:
name|containerPoolSize
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"host: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|host
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"port: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|port
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"pool size: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|containerPoolSize
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"runs as server: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|runAsServer
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"use weak references: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|weakReferences
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"user: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|user
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"password length: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|password
operator|==
literal|null
condition|?
literal|"no password"
else|:
name|this
operator|.
name|password
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

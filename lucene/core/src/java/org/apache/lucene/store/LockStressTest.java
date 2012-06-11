begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|IOException
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
begin_comment
comment|/**  * Simple standalone tool that forever acquires& releases a  * lock using a specific LockFactory.  Run without any args  * to see usage.  *  * @see VerifyingLockFactory  * @see LockVerifyServer  */
end_comment
begin_class
DECL|class|LockStressTest
specifier|public
class|class
name|LockStressTest
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|6
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nUsage: java org.apache.lucene.store.LockStressTest myID verifierHostOrIP verifierPort lockFactoryClassName lockDirName sleepTime\n"
operator|+
literal|"\n"
operator|+
literal|"  myID = int from 0 .. 255 (should be unique for test process)\n"
operator|+
literal|"  verifierHostOrIP = host name or IP address where LockVerifyServer is running\n"
operator|+
literal|"  verifierPort = port that LockVerifyServer is listening on\n"
operator|+
literal|"  lockFactoryClassName = primary LockFactory class that we will use\n"
operator|+
literal|"  lockDirName = path to the lock directory (only set for Simple/NativeFSLockFactory\n"
operator|+
literal|"  sleepTimeMS = milliseconds to pause betweeen each lock obtain/release\n"
operator|+
literal|"\n"
operator|+
literal|"You should run multiple instances of this process, each with its own\n"
operator|+
literal|"unique ID, and each pointing to the same lock directory, to verify\n"
operator|+
literal|"that locking is working correctly.\n"
operator|+
literal|"\n"
operator|+
literal|"Make sure you are first running LockVerifyServer.\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|myID
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|myID
argument_list|<
literal|0
operator|||
name|myID
argument_list|>
literal|255
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"myID must be a unique int 0..255"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|verifierHost
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|int
name|verifierPort
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|lockFactoryClassName
init|=
name|args
index|[
literal|3
index|]
decl_stmt|;
specifier|final
name|String
name|lockDirName
init|=
name|args
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|int
name|sleepTimeMS
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
decl_stmt|;
name|LockFactory
name|lockFactory
decl_stmt|;
try|try
block|{
name|lockFactory
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|lockFactoryClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|LockFactory
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IllegalAccessException when instantiating LockClass "
operator|+
name|lockFactoryClassName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"InstantiationException when instantiating LockClass "
operator|+
name|lockFactoryClassName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to cast LockClass "
operator|+
name|lockFactoryClassName
operator|+
literal|" instance to a LockFactory"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to find LockClass "
operator|+
name|lockFactoryClassName
argument_list|)
throw|;
block|}
name|File
name|lockDir
init|=
operator|new
name|File
argument_list|(
name|lockDirName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockFactory
operator|instanceof
name|FSLockFactory
condition|)
block|{
operator|(
operator|(
name|FSLockFactory
operator|)
name|lockFactory
operator|)
operator|.
name|setLockDir
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
name|lockFactory
operator|.
name|setLockPrefix
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|LockFactory
name|verifyLF
init|=
operator|new
name|VerifyingLockFactory
argument_list|(
operator|(
name|byte
operator|)
name|myID
argument_list|,
name|lockFactory
argument_list|,
name|verifierHost
argument_list|,
name|verifierPort
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|verifyLF
operator|.
name|makeLock
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|obtained
init|=
literal|false
decl_stmt|;
try|try
block|{
name|obtained
operator|=
name|l
operator|.
name|obtain
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|obtained
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"l"
argument_list|)
expr_stmt|;
name|l
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTimeMS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

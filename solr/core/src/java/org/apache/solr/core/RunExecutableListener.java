begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|search
operator|.
name|SolrIndexSearcher
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|ArrayList
import|;
end_import
begin_comment
comment|/**  */
end_comment
begin_class
DECL|class|RunExecutableListener
class|class
name|RunExecutableListener
extends|extends
name|AbstractSolrEventListener
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|RunExecutableListener
specifier|public
name|RunExecutableListener
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
DECL|field|cmd
specifier|protected
name|String
index|[]
name|cmd
decl_stmt|;
DECL|field|dir
specifier|protected
name|File
name|dir
decl_stmt|;
DECL|field|envp
specifier|protected
name|String
index|[]
name|envp
decl_stmt|;
DECL|field|wait
specifier|protected
name|boolean
name|wait
init|=
literal|true
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
name|List
name|cmdlist
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|cmdlist
operator|.
name|add
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"exe"
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|lst
init|=
operator|(
name|List
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"args"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lst
operator|!=
literal|null
condition|)
name|cmdlist
operator|.
name|addAll
argument_list|(
name|lst
argument_list|)
expr_stmt|;
name|cmd
operator|=
operator|(
name|String
index|[]
operator|)
name|cmdlist
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|cmdlist
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|lst
operator|=
operator|(
name|List
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"env"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lst
operator|!=
literal|null
condition|)
block|{
name|envp
operator|=
operator|(
name|String
index|[]
operator|)
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"dir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
operator|||
name|str
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|str
operator|.
name|equals
argument_list|(
literal|"."
argument_list|)
operator|||
name|str
operator|.
name|equals
argument_list|(
literal|"./"
argument_list|)
condition|)
block|{
name|dir
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"wait"
argument_list|)
argument_list|)
operator|||
name|Boolean
operator|.
name|FALSE
operator|.
name|equals
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"wait"
argument_list|)
argument_list|)
condition|)
name|wait
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * External executable listener.    *     * @param callback Unused (As of solr 1.4-dev)    * @return Error code indicating if the command has executed successfully.<br />    *  0 , indicates normal termination.<br />    *  non-zero , otherwise.    */
DECL|method|exec
specifier|protected
name|int
name|exec
parameter_list|(
name|String
name|callback
parameter_list|)
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
try|try
block|{
name|boolean
name|doLog
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
if|if
condition|(
name|doLog
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"About to exec "
operator|+
name|cmd
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Process
name|proc
decl_stmt|;
try|try
block|{
name|proc
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
argument_list|,
name|envp
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|err
parameter_list|)
block|{
comment|// Create better error message
if|if
condition|(
name|err
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"posix_spawn"
argument_list|)
operator|||
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"UNIXProcess"
argument_list|)
operator|)
condition|)
block|{
name|Error
name|newErr
init|=
operator|new
name|Error
argument_list|(
literal|"Error forking command due to JVM locale bug (see https://issues.apache.org/jira/browse/SOLR-6387): "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|newErr
operator|.
name|setStackTrace
argument_list|(
name|err
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
name|err
operator|=
name|newErr
expr_stmt|;
block|}
throw|throw
name|err
throw|;
block|}
if|if
condition|(
name|wait
condition|)
block|{
try|try
block|{
name|ret
operator|=
name|proc
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ret
operator|=
name|INVALID_PROCESS_RETURN_CODE
expr_stmt|;
block|}
block|}
if|if
condition|(
name|wait
operator|&&
name|doLog
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Executable "
operator|+
name|cmd
index|[
literal|0
index|]
operator|+
literal|" returned "
operator|+
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// don't throw exception, just log it...
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ret
operator|=
name|INVALID_PROCESS_RETURN_CODE
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{
comment|// anything generic need to be passed to the external program?
comment|// the directory of the index?  the command that caused it to be
comment|// invoked?  the version of the index?
name|exec
argument_list|(
literal|"postCommit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
name|exec
argument_list|(
literal|"newSearcher"
argument_list|)
expr_stmt|;
block|}
comment|/** Non-zero value for an invalid return code **/
DECL|field|INVALID_PROCESS_RETURN_CODE
specifier|private
specifier|static
name|int
name|INVALID_PROCESS_RETURN_CODE
init|=
operator|-
literal|1
decl_stmt|;
block|}
end_class
end_unit

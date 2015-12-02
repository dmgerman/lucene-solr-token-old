begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_import
import|import static
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
name|params
operator|.
name|SolrParams
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
name|common
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
name|update
operator|.
name|CommitUpdateCommand
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
begin_comment
comment|/**  *<p>  * Gives system administrators a way to ignore explicit commit or optimize requests from clients.  * The factory can be configured to return a specific HTTP response code, default is 403, and  * optional response message, such as to warn the client application that its request was ignored.  *</p>  */
end_comment
begin_class
DECL|class|IgnoreCommitOptimizeUpdateProcessorFactory
specifier|public
class|class
name|IgnoreCommitOptimizeUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
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
DECL|field|DEFAULT_RESPONSE_MSG
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_RESPONSE_MSG
init|=
literal|"Explicit commit/optimize requests are forbidden!"
decl_stmt|;
DECL|field|errorCode
specifier|protected
name|ErrorCode
name|errorCode
decl_stmt|;
DECL|field|responseMsg
specifier|protected
name|String
name|responseMsg
decl_stmt|;
DECL|field|ignoreOptimizeOnly
specifier|protected
name|boolean
name|ignoreOptimizeOnly
init|=
literal|false
decl_stmt|;
comment|// default behavior is to ignore commits and optimize
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|NamedList
name|args
parameter_list|)
block|{
name|SolrParams
name|params
init|=
operator|(
name|args
operator|!=
literal|null
operator|)
condition|?
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|errorCode
operator|=
name|ErrorCode
operator|.
name|FORBIDDEN
expr_stmt|;
comment|// default is 403 error
name|responseMsg
operator|=
name|DEFAULT_RESPONSE_MSG
expr_stmt|;
name|ignoreOptimizeOnly
operator|=
literal|false
expr_stmt|;
return|return;
block|}
name|ignoreOptimizeOnly
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"ignoreOptimizeOnly"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|statusCode
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"statusCode"
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|)
decl_stmt|;
if|if
condition|(
name|statusCode
operator|==
literal|200
condition|)
block|{
name|errorCode
operator|=
literal|null
expr_stmt|;
comment|// not needed but makes the logic clearer
name|responseMsg
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"responseMessage"
argument_list|)
expr_stmt|;
comment|// OK to be null for 200's
block|}
else|else
block|{
name|errorCode
operator|=
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|statusCode
argument_list|)
expr_stmt|;
if|if
condition|(
name|errorCode
operator|==
name|ErrorCode
operator|.
name|UNKNOWN
condition|)
block|{
comment|// only allow the error codes supported by the SolrException.ErrorCode class
name|StringBuilder
name|validCodes
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|appended
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ErrorCode
name|code
range|:
name|ErrorCode
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|code
operator|!=
name|ErrorCode
operator|.
name|UNKNOWN
condition|)
block|{
if|if
condition|(
name|appended
operator|++
operator|>
literal|0
condition|)
name|validCodes
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|validCodes
operator|.
name|append
argument_list|(
name|code
operator|.
name|code
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configured status code "
operator|+
name|statusCode
operator|+
literal|" not supported! Please choose one of: "
operator|+
name|validCodes
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|// must always have a response message if sending an error code
name|responseMsg
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"responseMessage"
argument_list|,
name|DEFAULT_RESPONSE_MSG
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|IgnoreCommitOptimizeUpdateProcessor
argument_list|(
name|rsp
argument_list|,
name|this
argument_list|,
name|next
argument_list|)
return|;
block|}
DECL|class|IgnoreCommitOptimizeUpdateProcessor
specifier|static
class|class
name|IgnoreCommitOptimizeUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|rsp
specifier|private
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|errorCode
specifier|private
specifier|final
name|ErrorCode
name|errorCode
decl_stmt|;
DECL|field|responseMsg
specifier|private
specifier|final
name|String
name|responseMsg
decl_stmt|;
DECL|field|ignoreOptimizeOnly
specifier|private
specifier|final
name|boolean
name|ignoreOptimizeOnly
decl_stmt|;
DECL|method|IgnoreCommitOptimizeUpdateProcessor
name|IgnoreCommitOptimizeUpdateProcessor
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|,
name|IgnoreCommitOptimizeUpdateProcessorFactory
name|factory
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|factory
operator|.
name|errorCode
expr_stmt|;
name|this
operator|.
name|responseMsg
operator|=
name|factory
operator|.
name|responseMsg
expr_stmt|;
name|this
operator|.
name|ignoreOptimizeOnly
operator|=
name|factory
operator|.
name|ignoreOptimizeOnly
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ignoreOptimizeOnly
operator|&&
operator|!
name|cmd
operator|.
name|optimize
condition|)
block|{
comment|// we're setup to only ignore optimize requests so it's OK to pass this commit on down the line
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|COMMIT_END_POINT
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// this is a targeted commit from replica to leader needed for recovery, so can't be ignored
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|cmdType
init|=
name|cmd
operator|.
name|optimize
condition|?
literal|"optimize"
else|:
literal|"commit"
decl_stmt|;
if|if
condition|(
name|errorCode
operator|!=
literal|null
condition|)
block|{
name|IgnoreCommitOptimizeUpdateProcessorFactory
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"{} from client application ignored with error code: {}"
argument_list|,
name|cmdType
argument_list|,
name|errorCode
operator|.
name|code
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|errorCode
argument_list|,
name|responseMsg
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// errorcode is null, treat as a success with an optional message warning the commit request was ignored
name|IgnoreCommitOptimizeUpdateProcessorFactory
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"{} from client application ignored with status code: 200"
argument_list|,
name|cmdType
argument_list|)
expr_stmt|;
if|if
condition|(
name|responseMsg
operator|!=
literal|null
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|responseHeader
init|=
name|rsp
operator|.
name|getResponseHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseHeader
operator|!=
literal|null
condition|)
block|{
name|responseHeader
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|responseHeader
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|responseHeader
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
name|responseHeader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|messages
operator|.
name|Message
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
name|messages
operator|.
name|MessageImpl
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
name|messages
operator|.
name|NLS
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
name|messages
operator|.
name|NLSException
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
name|queryParser
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
import|;
end_import
begin_comment
comment|/**  *<p>  * This exception should be thrown if something wrong happens when dealing with  * {@link QueryNode}s.  *</p>  *<p>  * It also supports NLS messages.  *</p>  *   * @see Message  * @see NLS  * @see NLSException  * @see QueryNode  */
end_comment
begin_class
DECL|class|QueryNodeException
specifier|public
class|class
name|QueryNodeException
extends|extends
name|Exception
implements|implements
name|NLSException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5962648855261624214L
decl_stmt|;
DECL|field|message
specifier|protected
name|Message
name|message
init|=
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|EMPTY_MESSAGE
argument_list|)
decl_stmt|;
DECL|method|QueryNodeException
specifier|public
name|QueryNodeException
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|QueryNodeException
specifier|public
name|QueryNodeException
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryNodeException
specifier|public
name|QueryNodeException
parameter_list|(
name|Message
name|message
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|.
name|getKey
argument_list|()
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|getMessageObject
specifier|public
name|Message
name|getMessageObject
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|getLocalizedMessage
argument_list|()
return|;
block|}
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|()
block|{
return|return
name|getLocalizedMessage
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
return|return
name|this
operator|.
name|message
operator|.
name|getLocalizedMessage
argument_list|(
name|locale
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
operator|.
name|getKey
argument_list|()
operator|+
literal|": "
operator|+
name|getLocalizedMessage
argument_list|()
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * A Transformer implementation which logs messages in a given template format.  *<p/>  * Refer to<a href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.4  */
end_comment
begin_class
DECL|class|LogTransformer
specifier|public
class|class
name|LogTransformer
extends|extends
name|Transformer
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LogTransformer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|VariableResolver
name|vr
init|=
name|context
operator|.
name|getVariableResolver
argument_list|()
decl_stmt|;
name|String
name|expr
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|LOG_TEMPLATE
argument_list|)
decl_stmt|;
name|String
name|level
init|=
name|vr
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|LOG_LEVEL
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|==
literal|null
operator|||
name|level
operator|==
literal|null
condition|)
return|return
name|row
return|;
if|if
condition|(
literal|"info"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
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
name|vr
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"trace"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"warn"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|warn
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"error"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isErrorEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|error
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"debug"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|row
return|;
block|}
DECL|field|LOG_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|LOG_TEMPLATE
init|=
literal|"logTemplate"
decl_stmt|;
DECL|field|LOG_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|LOG_LEVEL
init|=
literal|"logLevel"
decl_stmt|;
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DateTools
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
name|document
operator|.
name|DateTools
operator|.
name|Resolution
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
name|config
operator|.
name|QueryConfigHandler
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
name|standard
operator|.
name|processors
operator|.
name|ParametricRangeQueryNodeProcessor
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
name|AttributeImpl
import|;
end_import
begin_comment
comment|/**  * This attribute is used by {@link ParametricRangeQueryNodeProcessor} processor  * and must be defined in the {@link QueryConfigHandler}. This attribute tells  * the processor which {@link Resolution} to use when parsing the date.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.DateResolutionAttribute  */
end_comment
begin_class
DECL|class|DateResolutionAttributeImpl
specifier|public
class|class
name|DateResolutionAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|DateResolutionAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6804360312723049526L
decl_stmt|;
DECL|field|dateResolution
specifier|private
name|DateTools
operator|.
name|Resolution
name|dateResolution
init|=
literal|null
decl_stmt|;
DECL|method|DateResolutionAttributeImpl
specifier|public
name|DateResolutionAttributeImpl
parameter_list|()
block|{
name|dateResolution
operator|=
literal|null
expr_stmt|;
comment|//default in 2.4
block|}
DECL|method|setDateResolution
specifier|public
name|void
name|setDateResolution
parameter_list|(
name|DateTools
operator|.
name|Resolution
name|dateResolution
parameter_list|)
block|{
name|this
operator|.
name|dateResolution
operator|=
name|dateResolution
expr_stmt|;
block|}
DECL|method|getDateResolution
specifier|public
name|DateTools
operator|.
name|Resolution
name|getDateResolution
parameter_list|()
block|{
return|return
name|this
operator|.
name|dateResolution
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|DateResolutionAttributeImpl
condition|)
block|{
name|DateResolutionAttributeImpl
name|dateResAttr
init|=
operator|(
name|DateResolutionAttributeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|dateResAttr
operator|.
name|getDateResolution
argument_list|()
operator|==
name|getDateResolution
argument_list|()
operator|||
name|dateResAttr
operator|.
name|getDateResolution
argument_list|()
operator|.
name|equals
argument_list|(
name|getDateResolution
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|dateResolution
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|this
operator|.
name|dateResolution
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<dateResolutionAttribute dateResolution='"
operator|+
name|this
operator|.
name|dateResolution
operator|+
literal|"'/>"
return|;
block|}
block|}
end_class
end_unit

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
comment|/**  * This attribute is used by processor {@link ParametricRangeQueryNodeProcessor}  * and must be defined in the {@link QueryConfigHandler}. This attribute tells  * the processor what is the default {@link Locale} used to parse a date.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.LowercaseExpandedTermsAttribute  */
end_comment
begin_class
DECL|class|LowercaseExpandedTermsAttributeImpl
specifier|public
class|class
name|LowercaseExpandedTermsAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|LowercaseExpandedTermsAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2804760312723049527L
decl_stmt|;
DECL|field|lowercaseExpandedTerms
specifier|private
name|boolean
name|lowercaseExpandedTerms
init|=
literal|true
decl_stmt|;
DECL|method|LowercaseExpandedTermsAttributeImpl
specifier|public
name|LowercaseExpandedTermsAttributeImpl
parameter_list|()
block|{
name|lowercaseExpandedTerms
operator|=
literal|true
expr_stmt|;
comment|// default in 2.4
block|}
DECL|method|setLowercaseExpandedTerms
specifier|public
name|void
name|setLowercaseExpandedTerms
parameter_list|(
name|boolean
name|lowercaseExpandedTerms
parameter_list|)
block|{
name|this
operator|.
name|lowercaseExpandedTerms
operator|=
name|lowercaseExpandedTerms
expr_stmt|;
block|}
DECL|method|isLowercaseExpandedTerms
specifier|public
name|boolean
name|isLowercaseExpandedTerms
parameter_list|()
block|{
return|return
name|this
operator|.
name|lowercaseExpandedTerms
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
name|LowercaseExpandedTermsAttributeImpl
operator|&&
operator|(
operator|(
name|LowercaseExpandedTermsAttributeImpl
operator|)
name|other
operator|)
operator|.
name|lowercaseExpandedTerms
operator|==
name|this
operator|.
name|lowercaseExpandedTerms
condition|)
block|{
return|return
literal|true
return|;
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
name|this
operator|.
name|lowercaseExpandedTerms
condition|?
operator|-
literal|1
else|:
name|Integer
operator|.
name|MAX_VALUE
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
literal|"<lowercaseExpandedTerms lowercaseExpandedTerms="
operator|+
name|this
operator|.
name|lowercaseExpandedTerms
operator|+
literal|"/>"
return|;
block|}
block|}
end_class
end_unit

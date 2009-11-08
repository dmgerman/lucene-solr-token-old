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
name|PhraseSlopQueryNodeProcessor
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
name|search
operator|.
name|FuzzyQuery
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
comment|/**  * This attribute is used by {@link PhraseSlopQueryNodeProcessor} processor and  * must be defined in the {@link QueryConfigHandler}. This attribute tells the  * processor what is the default phrase slop when no slop is defined in a  * phrase.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.FuzzyAttribute  */
end_comment
begin_class
DECL|class|FuzzyAttributeImpl
specifier|public
class|class
name|FuzzyAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|FuzzyAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2104763012527049527L
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
DECL|field|minSimilarity
specifier|private
name|float
name|minSimilarity
init|=
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
decl_stmt|;
DECL|method|FuzzyAttributeImpl
specifier|public
name|FuzzyAttributeImpl
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|setPrefixLength
specifier|public
name|void
name|setPrefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
block|}
DECL|method|getPrefixLength
specifier|public
name|int
name|getPrefixLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefixLength
return|;
block|}
DECL|method|setFuzzyMinSimilarity
specifier|public
name|void
name|setFuzzyMinSimilarity
parameter_list|(
name|float
name|minSimilarity
parameter_list|)
block|{
name|this
operator|.
name|minSimilarity
operator|=
name|minSimilarity
expr_stmt|;
block|}
DECL|method|getFuzzyMinSimilarity
specifier|public
name|float
name|getFuzzyMinSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|minSimilarity
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
name|FuzzyAttributeImpl
operator|&&
name|other
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|FuzzyAttributeImpl
operator|)
name|other
operator|)
operator|.
name|prefixLength
operator|==
name|this
operator|.
name|prefixLength
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
name|Integer
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|prefixLength
argument_list|)
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
literal|"<fuzzyAttribute prefixLength="
operator|+
name|this
operator|.
name|prefixLength
operator|+
literal|"/>"
return|;
block|}
block|}
end_class
end_unit

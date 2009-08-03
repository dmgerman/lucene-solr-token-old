begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.original.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
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
name|original
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
name|search
operator|.
name|MultiTermQuery
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
name|MultiTermQuery
operator|.
name|RewriteMethod
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
comment|/**  * This attribute is used by {@link ParametricRangeQueryNodeProcessor} processor  * and should be defined in the {@link QueryConfigHandler} used by this  * processor. It basically tells the processor which {@link RewriteMethod} to  * use.<br/>  *   * @see MultiTermRewriteMethodAttribute  */
end_comment
begin_class
DECL|class|MultiTermRewriteMethodAttributeImpl
specifier|public
class|class
name|MultiTermRewriteMethodAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|MultiTermRewriteMethodAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2104763012723049527L
decl_stmt|;
DECL|field|multiTermRewriteMethod
specifier|private
name|MultiTermQuery
operator|.
name|RewriteMethod
name|multiTermRewriteMethod
init|=
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
decl_stmt|;
DECL|method|MultiTermRewriteMethodAttributeImpl
specifier|public
name|MultiTermRewriteMethodAttributeImpl
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|setMultiTermRewriteMethod
specifier|public
name|void
name|setMultiTermRewriteMethod
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
block|{
name|multiTermRewriteMethod
operator|=
name|method
expr_stmt|;
block|}
DECL|method|getMultiTermRewriteMethod
specifier|public
name|MultiTermQuery
operator|.
name|RewriteMethod
name|getMultiTermRewriteMethod
parameter_list|()
block|{
return|return
name|multiTermRewriteMethod
return|;
block|}
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
name|MultiTermRewriteMethodAttributeImpl
operator|&&
operator|(
operator|(
name|MultiTermRewriteMethodAttributeImpl
operator|)
name|other
operator|)
operator|.
name|multiTermRewriteMethod
operator|==
name|this
operator|.
name|multiTermRewriteMethod
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
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|multiTermRewriteMethod
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<multiTermRewriteMethod multiTermRewriteMethod="
operator|+
name|this
operator|.
name|multiTermRewriteMethod
operator|+
literal|"/>"
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.nodes
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
name|nodes
package|;
end_package
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
name|FieldQueryNode
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link PrefixWildcardQueryNode} represents wildcardquery that matches abc*  * or *. This does not apply to phrases, this is a special case on the original  * lucene parser. TODO: refactor the code to remove this special case from the  * parser. and probably do it on a Processor  */
end_comment
begin_class
DECL|class|PrefixWildcardQueryNode
specifier|public
class|class
name|PrefixWildcardQueryNode
extends|extends
name|WildcardQueryNode
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6851557641826407515L
decl_stmt|;
comment|/**    * @param field    *          - field name    * @param text    *          - value including the wildcard    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|PrefixWildcardQueryNode
specifier|public
name|PrefixWildcardQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|PrefixWildcardQueryNode
specifier|public
name|PrefixWildcardQueryNode
parameter_list|(
name|FieldQueryNode
name|fqn
parameter_list|)
block|{
name|this
argument_list|(
name|fqn
operator|.
name|getField
argument_list|()
argument_list|,
name|fqn
operator|.
name|getText
argument_list|()
argument_list|,
name|fqn
operator|.
name|getBegin
argument_list|()
argument_list|,
name|fqn
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<prefixWildcard field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' term='"
operator|+
name|this
operator|.
name|text
operator|+
literal|"'/>"
return|;
block|}
DECL|method|cloneTree
specifier|public
name|PrefixWildcardQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|PrefixWildcardQueryNode
name|clone
init|=
operator|(
name|PrefixWildcardQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to do here
return|return
name|clone
return|;
block|}
block|}
end_class
end_unit

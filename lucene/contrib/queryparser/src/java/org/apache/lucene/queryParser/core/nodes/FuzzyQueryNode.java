begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core.nodes
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
operator|.
name|nodes
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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_comment
comment|/**  * A {@link FuzzyQueryNode} represents a element that contains  * field/text/similarity tuple  */
end_comment
begin_class
DECL|class|FuzzyQueryNode
specifier|public
class|class
name|FuzzyQueryNode
extends|extends
name|FieldQueryNode
block|{
DECL|field|similarity
specifier|private
name|float
name|similarity
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
decl_stmt|;
comment|/**    * @param field    *          Name of the field query will use.    * @param termStr    *          Term token to use for building term for the query    */
comment|/**    * @param field    *          - Field name    * @param term    *          - Value    * @param minSimilarity    *          - similarity value    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|FuzzyQueryNode
specifier|public
name|FuzzyQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|CharSequence
name|term
parameter_list|,
name|float
name|minSimilarity
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
name|term
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|minSimilarity
expr_stmt|;
name|setLeaf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
if|if
condition|(
name|isDefaultField
argument_list|(
name|this
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
name|getTermEscaped
argument_list|(
name|escaper
argument_list|)
operator|+
literal|"~"
operator|+
name|this
operator|.
name|similarity
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|":"
operator|+
name|getTermEscaped
argument_list|(
name|escaper
argument_list|)
operator|+
literal|"~"
operator|+
name|this
operator|.
name|similarity
return|;
block|}
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
literal|"<fuzzy field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' similarity='"
operator|+
name|this
operator|.
name|similarity
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
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|float
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|FuzzyQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|FuzzyQueryNode
name|clone
init|=
operator|(
name|FuzzyQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|clone
operator|.
name|similarity
operator|=
name|this
operator|.
name|similarity
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * @return the similarity    */
DECL|method|getSimilarity
specifier|public
name|float
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
block|}
end_class
end_unit

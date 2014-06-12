begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|AttributeSource
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
name|ToStringUtils
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
name|automaton
operator|.
name|CompiledAutomaton
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
name|automaton
operator|.
name|LightAutomaton
import|;
end_import
begin_comment
comment|/**  * A {@link Query} that will match terms against a finite-state machine.  *<p>  * This query will match documents that contain terms accepted by a given  * finite-state machine. The automaton can be constructed with the  * {@link org.apache.lucene.util.automaton} API. Alternatively, it can be  * created from a regular expression with {@link RegexpQuery} or from  * the standard Lucene wildcard syntax with {@link WildcardQuery}.  *</p>  *<p>  * When the query is executed, it will create an equivalent DFA of the  * finite-state machine, and will enumerate the term dictionary in an  * intelligent way to reduce the number of comparisons. For example: the regular  * expression of<code>[dl]og?</code> will make approximately four comparisons:  * do, dog, lo, and log.  *</p>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AutomatonQuery
specifier|public
class|class
name|AutomatonQuery
extends|extends
name|MultiTermQuery
block|{
comment|/** the automaton to match index terms against */
DECL|field|lightAutomaton
specifier|protected
specifier|final
name|LightAutomaton
name|lightAutomaton
decl_stmt|;
DECL|field|compiled
specifier|protected
specifier|final
name|CompiledAutomaton
name|compiled
decl_stmt|;
comment|/** term containing the field, and possibly some pattern structure */
DECL|field|term
specifier|protected
specifier|final
name|Term
name|term
decl_stmt|;
comment|/**    * Create a new AutomatonQuery from an {@link Automaton}.    *     * @param term Term containing field and possibly some pattern structure. The    *        term text is ignored.    * @param automaton Automaton to run, terms that are accepted are considered a    *        match.    */
DECL|method|AutomatonQuery
specifier|public
name|AutomatonQuery
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|,
name|LightAutomaton
name|automaton
parameter_list|)
block|{
name|super
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|lightAutomaton
operator|=
name|automaton
expr_stmt|;
name|this
operator|.
name|compiled
operator|=
operator|new
name|CompiledAutomaton
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compiled
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|compiled
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|AutomatonQuery
name|other
init|=
operator|(
name|AutomatonQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|compiled
operator|.
name|equals
argument_list|(
name|other
operator|.
name|compiled
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|term
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" {"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|lightAutomaton
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns the light automaton used to create this query */
DECL|method|getLightAutomaton
specifier|public
name|LightAutomaton
name|getLightAutomaton
parameter_list|()
block|{
return|return
name|lightAutomaton
return|;
block|}
block|}
end_class
end_unit

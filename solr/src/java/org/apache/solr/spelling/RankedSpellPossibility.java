begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|List
import|;
end_import
begin_class
DECL|class|RankedSpellPossibility
specifier|public
class|class
name|RankedSpellPossibility
implements|implements
name|Comparable
argument_list|<
name|RankedSpellPossibility
argument_list|>
block|{
DECL|field|corrections
specifier|private
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|corrections
decl_stmt|;
DECL|field|rank
specifier|private
name|int
name|rank
decl_stmt|;
comment|//Rank poorer suggestions ahead of better ones for use with a PriorityQueue
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|RankedSpellPossibility
name|rcl
parameter_list|)
block|{
return|return
operator|new
name|Integer
argument_list|(
name|rcl
operator|.
name|rank
argument_list|)
operator|.
name|compareTo
argument_list|(
name|rank
argument_list|)
return|;
block|}
DECL|method|getCorrections
specifier|public
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|getCorrections
parameter_list|()
block|{
return|return
name|corrections
return|;
block|}
DECL|method|setCorrections
specifier|public
name|void
name|setCorrections
parameter_list|(
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|corrections
parameter_list|)
block|{
name|this
operator|.
name|corrections
operator|=
name|corrections
expr_stmt|;
block|}
DECL|method|getRank
specifier|public
name|int
name|getRank
parameter_list|()
block|{
return|return
name|rank
return|;
block|}
DECL|method|setRank
specifier|public
name|void
name|setRank
parameter_list|(
name|int
name|rank
parameter_list|)
block|{
name|this
operator|.
name|rank
operator|=
name|rank
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"rank="
argument_list|)
operator|.
name|append
argument_list|(
name|rank
argument_list|)
expr_stmt|;
if|if
condition|(
name|corrections
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SpellCheckCorrection
name|corr
range|:
name|corrections
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"     "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getOriginal
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getCorrection
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getNumberOfOccurences
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit

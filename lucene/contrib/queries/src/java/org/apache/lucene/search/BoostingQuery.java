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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
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
name|BooleanQuery
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
name|DefaultSimilarity
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
name|Query
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
name|IndexSearcher
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
name|Similarity
import|;
end_import
begin_comment
comment|/**  * The BoostingQuery class can be used to effectively demote results that match a given query.   * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,   * but reduces their overall score:  *  *     Query balancedQuery = new BoostingQuery(positiveQuery, negativeQuery, 0.01f);  * In this scenario the positiveQuery contains the mandatory, desirable criteria which is used to   * select all matching documents, and the negativeQuery contains the undesirable elements which   * are simply used to lessen the scores. Documents that match the negativeQuery have their score   * multiplied by the supplied "boost" parameter, so this should be less than 1 to achieve a   * demoting effect  *   * This code was originally made available here: [WWW] http://marc.theaimsgroup.com/?l=lucene-user&m=108058407130459&w=2  * and is documented here: http://wiki.apache.org/lucene-java/CommunityContributions  */
end_comment
begin_class
DECL|class|BoostingQuery
specifier|public
class|class
name|BoostingQuery
extends|extends
name|Query
block|{
DECL|field|boost
specifier|private
name|float
name|boost
decl_stmt|;
comment|// the amount to boost by
DECL|field|match
specifier|private
name|Query
name|match
decl_stmt|;
comment|// query to match
DECL|field|context
specifier|private
name|Query
name|context
decl_stmt|;
comment|// boost when matches too
DECL|method|BoostingQuery
specifier|public
name|BoostingQuery
parameter_list|(
name|Query
name|match
parameter_list|,
name|Query
name|context
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
name|this
operator|.
name|context
operator|=
operator|(
name|Query
operator|)
name|context
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// clone before boost
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
comment|// ignore context-only matches
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|getSimilarity
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|max
parameter_list|)
block|{
switch|switch
condition|(
name|overlap
condition|)
block|{
case|case
literal|1
case|:
comment|// matched only one clause
return|return
literal|1.0f
return|;
comment|// use the score as-is
case|case
literal|2
case|:
comment|// matched both clauses
return|return
name|boost
return|;
comment|// multiply by boost
default|default:
return|return
literal|0.0f
return|;
block|}
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|match
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
return|return
name|result
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
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|context
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|context
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|match
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|match
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
name|obj
operator|==
literal|null
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
name|BoostingQuery
name|other
init|=
operator|(
name|BoostingQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|boost
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|context
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
name|context
operator|.
name|equals
argument_list|(
name|other
operator|.
name|context
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|match
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
name|match
operator|.
name|equals
argument_list|(
name|other
operator|.
name|match
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
return|return
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|"/"
operator|+
name|context
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class
end_unit

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
name|nodes
operator|.
name|FieldQueryNode
import|;
end_import
begin_comment
comment|/**  * This query node represents a range query.  *   * @see ParametricRangeQueryNodeProcessor  * @see org.apache.lucene.search.TermRangeQuery  */
end_comment
begin_class
DECL|class|TermRangeQueryNode
specifier|public
class|class
name|TermRangeQueryNode
extends|extends
name|AbstractRangeQueryNode
argument_list|<
name|FieldQueryNode
argument_list|>
block|{
DECL|method|TermRangeQueryNode
specifier|public
name|TermRangeQueryNode
parameter_list|(
name|FieldQueryNode
name|lower
parameter_list|,
name|FieldQueryNode
name|upper
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|)
block|{
name|setBounds
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

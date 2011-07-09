begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.builders
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
name|builders
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
name|QueryNodeException
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
name|nodes
operator|.
name|FieldQueryNode
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
name|nodes
operator|.
name|QueryNode
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
name|util
operator|.
name|StringUtils
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
name|nodes
operator|.
name|TermRangeQueryNode
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
name|MultiTermRewriteMethodProcessor
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
name|TermRangeQuery
import|;
end_import
begin_comment
comment|/**  * Builds a {@link TermRangeQuery} object from a {@link TermRangeQueryNode}  * object.  */
end_comment
begin_class
DECL|class|TermRangeQueryNodeBuilder
specifier|public
class|class
name|TermRangeQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|TermRangeQueryNodeBuilder
specifier|public
name|TermRangeQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|TermRangeQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|TermRangeQueryNode
name|rangeNode
init|=
operator|(
name|TermRangeQueryNode
operator|)
name|queryNode
decl_stmt|;
name|FieldQueryNode
name|upper
init|=
name|rangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|FieldQueryNode
name|lower
init|=
name|rangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|String
name|field
init|=
name|StringUtils
operator|.
name|toString
argument_list|(
name|rangeNode
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|TermRangeQuery
name|rangeQuery
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
name|field
argument_list|,
name|lower
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|upper
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|rangeNode
operator|.
name|isLowerInclusive
argument_list|()
argument_list|,
name|rangeNode
operator|.
name|isUpperInclusive
argument_list|()
argument_list|)
decl_stmt|;
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
operator|(
name|MultiTermQuery
operator|.
name|RewriteMethod
operator|)
name|queryNode
operator|.
name|getTag
argument_list|(
name|MultiTermRewriteMethodProcessor
operator|.
name|TAG_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|rangeQuery
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
return|return
name|rangeQuery
return|;
block|}
block|}
end_class
end_unit

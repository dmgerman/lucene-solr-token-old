begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
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
name|search
operator|.
name|spans
operator|.
name|SpanBoostQuery
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|queryparser
operator|.
name|xml
operator|.
name|DOMUtils
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
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * Builder for {@link SpanOrQuery}  */
end_comment
begin_class
DECL|class|SpanOrBuilder
specifier|public
class|class
name|SpanOrBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|factory
specifier|private
specifier|final
name|SpanQueryBuilder
name|factory
decl_stmt|;
DECL|method|SpanOrBuilder
specifier|public
name|SpanOrBuilder
parameter_list|(
name|SpanQueryBuilder
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|List
argument_list|<
name|SpanQuery
argument_list|>
name|clausesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|kid
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
name|kid
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|SpanQuery
name|clause
init|=
name|factory
operator|.
name|getSpanQuery
argument_list|(
operator|(
name|Element
operator|)
name|kid
argument_list|)
decl_stmt|;
name|clausesList
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
block|}
name|SpanQuery
index|[]
name|clauses
init|=
name|clausesList
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|clausesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|soq
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|clauses
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanBoostQuery
argument_list|(
name|soq
argument_list|,
name|boost
argument_list|)
return|;
block|}
block|}
end_class
end_unit

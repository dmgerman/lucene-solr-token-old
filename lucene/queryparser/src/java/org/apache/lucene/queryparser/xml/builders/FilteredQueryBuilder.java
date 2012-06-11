begin_unit
begin_comment
comment|/*  * Created on 25-Jan-2006  */
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
name|Filter
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
name|FilteredQuery
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
name|FilterBuilder
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
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|QueryBuilder
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|FilteredQueryBuilder
specifier|public
class|class
name|FilteredQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|filterFactory
specifier|private
specifier|final
name|FilterBuilder
name|filterFactory
decl_stmt|;
DECL|field|queryFactory
specifier|private
specifier|final
name|QueryBuilder
name|queryFactory
decl_stmt|;
DECL|method|FilteredQueryBuilder
specifier|public
name|FilteredQueryBuilder
parameter_list|(
name|FilterBuilder
name|filterFactory
parameter_list|,
name|QueryBuilder
name|queryFactory
parameter_list|)
block|{
name|this
operator|.
name|filterFactory
operator|=
name|filterFactory
expr_stmt|;
name|this
operator|.
name|queryFactory
operator|=
name|queryFactory
expr_stmt|;
block|}
comment|/* (non-Javadoc)     * @see org.apache.lucene.xmlparser.QueryObjectBuilder#process(org.w3c.dom.Element)     */
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|filterElement
init|=
name|DOMUtils
operator|.
name|getChildByTagOrFail
argument_list|(
name|e
argument_list|,
literal|"Filter"
argument_list|)
decl_stmt|;
name|filterElement
operator|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|filterElement
argument_list|)
expr_stmt|;
name|Filter
name|f
init|=
name|filterFactory
operator|.
name|getFilter
argument_list|(
name|filterElement
argument_list|)
decl_stmt|;
name|Element
name|queryElement
init|=
name|DOMUtils
operator|.
name|getChildByTagOrFail
argument_list|(
name|e
argument_list|,
literal|"Query"
argument_list|)
decl_stmt|;
name|queryElement
operator|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|queryElement
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|queryFactory
operator|.
name|getQuery
argument_list|(
name|queryElement
argument_list|)
decl_stmt|;
name|FilteredQuery
name|fq
init|=
operator|new
name|FilteredQuery
argument_list|(
name|q
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|fq
operator|.
name|setBoost
argument_list|(
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
argument_list|)
expr_stmt|;
return|return
name|fq
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|flexible
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
name|search
operator|.
name|BooleanClause
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
name|Query
import|;
end_import
begin_comment
comment|/**  * This class defines utility methods to (help) parse query strings into  * {@link Query} objects.  */
end_comment
begin_class
DECL|class|QueryParserUtil
specifier|final
specifier|public
class|class
name|QueryParserUtil
block|{
comment|/**    * Parses a query which searches on the fields specified.    *<p>    * If x fields are specified, this effectively constructs:    *     *<pre>    *&lt;code&gt;    * (field1:query1) (field2:query2) (field3:query3)...(fieldx:queryx)    *&lt;/code&gt;    *</pre>    *     * @param queries    *          Queries strings to parse    * @param fields    *          Fields to search on    * @param analyzer    *          Analyzer to use    * @throws IllegalArgumentException    *           if the length of the queries array differs from the length of the    *           fields array    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
index|[]
name|queries
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|queries
operator|.
name|length
operator|!=
name|fields
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queries.length != fields.length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|()
decl_stmt|;
name|qp
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|queries
index|[
name|i
index|]
argument_list|,
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
comment|/**    * Parses a query, searching on the fields specified. Use this if you need to    * specify certain fields as required, and others as prohibited.    *<p>    *     *<pre>    * Usage:    *&lt;code&gt;    * String[] fields = {&quot;filename&quot;,&quot;contents&quot;,&quot;description&quot;};    * BooleanClause.Occur[] flags = {BooleanClause.Occur.SHOULD,    *                BooleanClause.Occur.MUST,    *                BooleanClause.Occur.MUST_NOT};    * MultiFieldQueryParser.parse(&quot;query&quot;, fields, flags, analyzer);    *&lt;/code&gt;    *</pre>    *<p>    * The code above would construct a query:    *     *<pre>    *&lt;code&gt;    * (filename:query) +(contents:query) -(description:query)    *&lt;/code&gt;    *</pre>    *     * @param query    *          Query string to parse    * @param fields    *          Fields to search on    * @param flags    *          Flags describing the fields    * @param analyzer    *          Analyzer to use    * @throws IllegalArgumentException    *           if the length of the fields array differs from the length of the    *           flags array    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|fields
operator|.
name|length
operator|!=
name|flags
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fields.length != flags.length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|()
decl_stmt|;
name|qp
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|,
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|flags
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
comment|/**    * Parses a query, searching on the fields specified. Use this if you need to    * specify certain fields as required, and others as prohibited.    *<p>    *     *<pre>    * Usage:    *&lt;code&gt;    * String[] query = {&quot;query1&quot;,&quot;query2&quot;,&quot;query3&quot;};    * String[] fields = {&quot;filename&quot;,&quot;contents&quot;,&quot;description&quot;};    * BooleanClause.Occur[] flags = {BooleanClause.Occur.SHOULD,    *                BooleanClause.Occur.MUST,    *                BooleanClause.Occur.MUST_NOT};    * MultiFieldQueryParser.parse(query, fields, flags, analyzer);    *&lt;/code&gt;    *</pre>    *<p>    * The code above would construct a query:    *     *<pre>    *&lt;code&gt;    * (filename:query1) +(contents:query2) -(description:query3)    *&lt;/code&gt;    *</pre>    *     * @param queries    *          Queries string to parse    * @param fields    *          Fields to search on    * @param flags    *          Flags describing the fields    * @param analyzer    *          Analyzer to use    * @throws IllegalArgumentException    *           if the length of the queries, fields, and flags array differ    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
index|[]
name|queries
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
operator|!
operator|(
name|queries
operator|.
name|length
operator|==
name|fields
operator|.
name|length
operator|&&
name|queries
operator|.
name|length
operator|==
name|flags
operator|.
name|length
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queries, fields, and flags array have have different length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|()
decl_stmt|;
name|qp
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|queries
index|[
name|i
index|]
argument_list|,
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|flags
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
comment|/**    * Returns a String where those characters that TextParser expects to be    * escaped are escaped by a preceding<code>\</code>.    */
DECL|method|escape
specifier|public
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// These characters are part of the query syntax and must be escaped
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|||
name|c
operator|==
literal|'+'
operator|||
name|c
operator|==
literal|'-'
operator|||
name|c
operator|==
literal|'!'
operator|||
name|c
operator|==
literal|'('
operator|||
name|c
operator|==
literal|')'
operator|||
name|c
operator|==
literal|':'
operator|||
name|c
operator|==
literal|'^'
operator|||
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|']'
operator|||
name|c
operator|==
literal|'\"'
operator|||
name|c
operator|==
literal|'{'
operator|||
name|c
operator|==
literal|'}'
operator|||
name|c
operator|==
literal|'~'
operator|||
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'?'
operator|||
name|c
operator|==
literal|'|'
operator|||
name|c
operator|==
literal|'&'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
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

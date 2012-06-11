begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core
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
name|core
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|builders
operator|.
name|QueryBuilder
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
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|parser
operator|.
name|SyntaxParser
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
name|processors
operator|.
name|QueryNodeProcessor
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *<p>  * This class is a helper for the query parser framework, it does all the three  * query parser phrases at once: text parsing, query processing and query  * building.  *</p>  *<p>  * It contains methods that allows the user to change the implementation used on  * the three phases.  *</p>  *   * @see QueryNodeProcessor  * @see SyntaxParser  * @see QueryBuilder  * @see QueryConfigHandler  */
end_comment
begin_class
DECL|class|QueryParserHelper
specifier|public
class|class
name|QueryParserHelper
block|{
DECL|field|processor
specifier|private
name|QueryNodeProcessor
name|processor
decl_stmt|;
DECL|field|syntaxParser
specifier|private
name|SyntaxParser
name|syntaxParser
decl_stmt|;
DECL|field|builder
specifier|private
name|QueryBuilder
name|builder
decl_stmt|;
DECL|field|config
specifier|private
name|QueryConfigHandler
name|config
decl_stmt|;
comment|/**    * Creates a query parser helper object using the specified configuration,    * text parser, processor and builder.    *     * @param queryConfigHandler    *          the query configuration handler that will be initially set to this    *          helper    * @param syntaxParser    *          the text parser that will be initially set to this helper    * @param processor    *          the query processor that will be initially set to this helper    * @param builder    *          the query builder that will be initially set to this helper    *     * @see QueryNodeProcessor    * @see SyntaxParser    * @see QueryBuilder    * @see QueryConfigHandler    */
DECL|method|QueryParserHelper
specifier|public
name|QueryParserHelper
parameter_list|(
name|QueryConfigHandler
name|queryConfigHandler
parameter_list|,
name|SyntaxParser
name|syntaxParser
parameter_list|,
name|QueryNodeProcessor
name|processor
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|syntaxParser
operator|=
name|syntaxParser
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|queryConfigHandler
expr_stmt|;
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|queryConfigHandler
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the processor object used to process the query node tree, it    * returns<code>null</code> if no processor is used.    *     * @return the actual processor used to process the query node tree,    *<code>null</code> if no processor is used    *     * @see QueryNodeProcessor    * @see #setQueryNodeProcessor(QueryNodeProcessor)    */
DECL|method|getQueryNodeProcessor
specifier|public
name|QueryNodeProcessor
name|getQueryNodeProcessor
parameter_list|()
block|{
return|return
name|processor
return|;
block|}
comment|/**    * Sets the processor that will be used to process the query node tree. If    * there is any {@link QueryConfigHandler} returned by    * {@link #getQueryConfigHandler()}, it will be set on the processor. The    * argument can be<code>null</code>, which means that no processor will be    * used to process the query node tree.    *     * @param processor    *          the processor that will be used to process the query node tree,    *          this argument can be<code>null</code>    *     * @see #getQueryNodeProcessor()    * @see QueryNodeProcessor    */
DECL|method|setQueryNodeProcessor
specifier|public
name|void
name|setQueryNodeProcessor
parameter_list|(
name|QueryNodeProcessor
name|processor
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|this
operator|.
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|getQueryConfigHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the text parser that will be used to parse the query string, it cannot    * be<code>null</code>.    *     * @param syntaxParser    *          the text parser that will be used to parse the query string    *     * @see #getSyntaxParser()    * @see SyntaxParser    */
DECL|method|setSyntaxParser
specifier|public
name|void
name|setSyntaxParser
parameter_list|(
name|SyntaxParser
name|syntaxParser
parameter_list|)
block|{
if|if
condition|(
name|syntaxParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"textParser should not be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|syntaxParser
operator|=
name|syntaxParser
expr_stmt|;
block|}
comment|/**    * The query builder that will be used to build an object from the query node    * tree. It cannot be<code>null</code>.    *     * @param queryBuilder    *          the query builder used to build something from the query node tree    *     * @see #getQueryBuilder()    * @see QueryBuilder    */
DECL|method|setQueryBuilder
specifier|public
name|void
name|setQueryBuilder
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queryBuilder should not be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|builder
operator|=
name|queryBuilder
expr_stmt|;
block|}
comment|/**    * Returns the query configuration handler, which is used during the query    * node tree processing. It can be<code>null</code>.    *     * @return the query configuration handler used on the query processing,    *<code>null</code> if not query configuration handler is defined    *     * @see QueryConfigHandler    * @see #setQueryConfigHandler(QueryConfigHandler)    */
DECL|method|getQueryConfigHandler
specifier|public
name|QueryConfigHandler
name|getQueryConfigHandler
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/**    * Returns the query builder used to build a object from the query node tree.    * The object produced by this builder is returned by    * {@link #parse(String, String)}.    *     * @return the query builder    *     * @see #setQueryBuilder(QueryBuilder)    * @see QueryBuilder    */
DECL|method|getQueryBuilder
specifier|public
name|QueryBuilder
name|getQueryBuilder
parameter_list|()
block|{
return|return
name|this
operator|.
name|builder
return|;
block|}
comment|/**    * Returns the text parser used to build a query node tree from a query    * string. The default text parser instance returned by this method is a    * {@link SyntaxParser}.    *     * @return the text parse used to build query node trees.    *     * @see SyntaxParser    * @see #setSyntaxParser(SyntaxParser)    */
DECL|method|getSyntaxParser
specifier|public
name|SyntaxParser
name|getSyntaxParser
parameter_list|()
block|{
return|return
name|this
operator|.
name|syntaxParser
return|;
block|}
comment|/**    * Sets the query configuration handler that will be used during query    * processing. It can be<code>null</code>. It's also set to the processor    * returned by {@link #getQueryNodeProcessor()}.    *     * @param config    *          the query configuration handler used during query processing, it    *          can be<code>null</code>    *     * @see #getQueryConfigHandler()    * @see QueryConfigHandler    */
DECL|method|setQueryConfigHandler
specifier|public
name|void
name|setQueryConfigHandler
parameter_list|(
name|QueryConfigHandler
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|QueryNodeProcessor
name|processor
init|=
name|getQueryNodeProcessor
argument_list|()
decl_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Parses a query string to an object, usually some query object.<br/>    *<br/>    * In this method the three phases are executed:<br/>    *<br/>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1st - the query string is parsed using the    * text parser returned by {@link #getSyntaxParser()}, the result is a query    * node tree<br/>    *<br/>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2nd - the query node tree is processed by the    * processor returned by {@link #getQueryNodeProcessor()}<br/>    *<br/>    *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3th - a object is built from the query node    * tree using the builder returned by {@link #getQueryBuilder()}    *     * @param query    *          the query string    * @param defaultField    *          the default field used by the text parser    *     * @return the object built from the query    *     * @throws QueryNodeException    *           if something wrong happens along the three phases    */
DECL|method|parse
specifier|public
name|Object
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|defaultField
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|QueryNode
name|queryTree
init|=
name|getSyntaxParser
argument_list|()
operator|.
name|parse
argument_list|(
name|query
argument_list|,
name|defaultField
argument_list|)
decl_stmt|;
name|QueryNodeProcessor
name|processor
init|=
name|getQueryNodeProcessor
argument_list|()
decl_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|queryTree
operator|=
name|processor
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
block|}
return|return
name|getQueryBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
block|}
end_class
end_unit

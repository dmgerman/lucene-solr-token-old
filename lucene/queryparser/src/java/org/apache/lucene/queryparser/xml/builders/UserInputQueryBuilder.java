begin_unit
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
name|classic
operator|.
name|ParseException
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
name|classic
operator|.
name|QueryParser
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
name|util
operator|.
name|Version
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
comment|/**  * UserInputQueryBuilder uses 1 of 2 strategies for thread-safe parsing:  * 1) Synchronizing access to "parse" calls on a previously supplied QueryParser  * or..  * 2) creating a new QueryParser object for each parse request  */
end_comment
begin_class
DECL|class|UserInputQueryBuilder
specifier|public
class|class
name|UserInputQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|unSafeParser
specifier|private
name|QueryParser
name|unSafeParser
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|defaultField
specifier|private
name|String
name|defaultField
decl_stmt|;
comment|/**    * This constructor has the disadvantage of not being able to change choice of default field name    *    * @param parser thread un-safe query parser    */
DECL|method|UserInputQueryBuilder
specifier|public
name|UserInputQueryBuilder
parameter_list|(
name|QueryParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|unSafeParser
operator|=
name|parser
expr_stmt|;
block|}
DECL|method|UserInputQueryBuilder
specifier|public
name|UserInputQueryBuilder
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|defaultField
operator|=
name|defaultField
expr_stmt|;
block|}
comment|/* (non-Javadoc)     * @see org.apache.lucene.xmlparser.QueryObjectBuilder#process(org.w3c.dom.Element)     */
annotation|@
name|Override
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
name|String
name|text
init|=
name|DOMUtils
operator|.
name|getText
argument_list|(
name|e
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|q
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|unSafeParser
operator|!=
literal|null
condition|)
block|{
comment|//synchronize on unsafe parser
synchronized|synchronized
init|(
name|unSafeParser
init|)
block|{
name|q
operator|=
name|unSafeParser
operator|.
name|parse
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|,
name|defaultField
argument_list|)
decl_stmt|;
comment|//Create new parser
name|QueryParser
name|parser
init|=
name|createQueryParser
argument_list|(
name|fieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
name|q
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
name|q
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Method to create a QueryParser - designed to be overridden    *    * @return QueryParser    */
DECL|method|createQueryParser
specifier|protected
name|QueryParser
name|createQueryParser
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
operator|new
name|QueryParser
argument_list|(
name|fieldName
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.ext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ext
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
name|queryParser
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
begin_comment
comment|/**  * This class represents an extension base class to the Lucene standard  * {@link QueryParser}. The {@link QueryParser} is generated by the JavaCC  * parser generator. Changing or adding functionality or syntax in the standard  * query parser requires changes to the JavaCC source file. To enable extending  * the standard query parser without changing the JavaCC sources and re-generate  * the parser the {@link ParserExtension} can be customized and plugged into an  * instance of {@link ExtendableQueryParser}, a direct subclass of  * {@link QueryParser}.  *   * @see Extensions  * @see ExtendableQueryParser  */
end_comment
begin_class
DECL|class|ParserExtension
specifier|public
specifier|abstract
class|class
name|ParserExtension
block|{
comment|/**    * Processes the given {@link ExtensionQuery} and returns a corresponding    * {@link Query} instance. Subclasses must either return a {@link Query}    * instance or raise a {@link ParseException}. This method must not return    *<code>null</code>.    *     * @param query    *          the extension query    * @return a new query instance    * @throws ParseException    *           if the query can not be parsed.    */
DECL|method|parse
specifier|public
specifier|abstract
name|Query
name|parse
parameter_list|(
specifier|final
name|ExtensionQuery
name|query
parameter_list|)
throws|throws
name|ParseException
function_decl|;
block|}
end_class
end_unit

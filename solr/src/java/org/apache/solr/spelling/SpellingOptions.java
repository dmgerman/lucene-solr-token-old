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
name|Token
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|SpellingOptions
specifier|public
class|class
name|SpellingOptions
block|{
comment|/**    * The tokens to spell check    */
DECL|field|tokens
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
decl_stmt|;
comment|/**    * An optional {@link org.apache.lucene.index.IndexReader}    */
DECL|field|reader
specifier|public
name|IndexReader
name|reader
decl_stmt|;
comment|/**    * The number of suggestions to return, if there are any.  Defaults to 1.    */
DECL|field|count
specifier|public
name|int
name|count
init|=
literal|1
decl_stmt|;
comment|/**    * Return only those results that are more popular, as defined by the implementation    */
DECL|field|onlyMorePopular
specifier|public
name|boolean
name|onlyMorePopular
decl_stmt|;
comment|/**    * Provide additional, per implementation, information about the results    */
DECL|field|extendedResults
specifier|public
name|boolean
name|extendedResults
decl_stmt|;
comment|/**    * Optionally restrict the results to have a minimum accuracy level.  Per Implementation.    * By default set to Float.MIN_VALUE.    */
DECL|field|accuracy
specifier|public
name|float
name|accuracy
init|=
name|Float
operator|.
name|MIN_VALUE
decl_stmt|;
comment|/**    * Any other custom params can be passed through.  May be null and is null by default.    */
DECL|field|customParams
specifier|public
name|SolrParams
name|customParams
decl_stmt|;
DECL|method|SpellingOptions
specifier|public
name|SpellingOptions
parameter_list|()
block|{   }
comment|//A couple of convenience ones
DECL|method|SpellingOptions
specifier|public
name|SpellingOptions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
DECL|method|SpellingOptions
specifier|public
name|SpellingOptions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|SpellingOptions
specifier|public
name|SpellingOptions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
DECL|method|SpellingOptions
specifier|public
name|SpellingOptions
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|boolean
name|extendedResults
parameter_list|,
name|float
name|accuracy
parameter_list|,
name|SolrParams
name|customParams
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|onlyMorePopular
operator|=
name|onlyMorePopular
expr_stmt|;
name|this
operator|.
name|extendedResults
operator|=
name|extendedResults
expr_stmt|;
name|this
operator|.
name|accuracy
operator|=
name|accuracy
expr_stmt|;
name|this
operator|.
name|customParams
operator|=
name|customParams
expr_stmt|;
block|}
block|}
end_class
end_unit

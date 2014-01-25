begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|fst
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
name|search
operator|.
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|analyzing
operator|.
name|FreeTextSuggester
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
name|IOUtils
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
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|schema
operator|.
name|FieldType
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
name|spelling
operator|.
name|suggest
operator|.
name|LookupFactory
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**   * LookupFactory implementation for {@link FreeTextSuggester}  * */
end_comment
begin_class
DECL|class|FreeTextLookupFactory
specifier|public
class|class
name|FreeTextLookupFactory
extends|extends
name|LookupFactory
block|{
comment|/**    * The analyzer used at "query-time" and "build-time" to analyze suggestions.    */
DECL|field|QUERY_ANALYZER
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_ANALYZER
init|=
literal|"suggestFreeTextAnalyzerFieldType"
decl_stmt|;
comment|/**     * The n-gram model to use in the underlying suggester; Default value is 2.    * */
DECL|field|NGRAMS
specifier|public
specifier|static
specifier|final
name|String
name|NGRAMS
init|=
literal|"ngrams"
decl_stmt|;
comment|/**    * The separator to use in the underlying suggester;    * */
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"separator"
decl_stmt|;
comment|/**    * File name for the automaton.    */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"ftsta.bin"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|Object
name|fieldTypeName
init|=
name|params
operator|.
name|get
argument_list|(
name|QUERY_ANALYZER
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldTypeName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|QUERY_ANALYZER
operator|+
literal|" parameter is mandatory"
argument_list|)
throw|;
block|}
name|FieldType
name|ft
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeByName
argument_list|(
name|fieldTypeName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|fieldTypeName
operator|.
name|toString
argument_list|()
operator|+
literal|" is not defined in the schema"
argument_list|)
throw|;
block|}
name|Analyzer
name|indexAnalyzer
init|=
name|ft
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|Analyzer
name|queryAnalyzer
init|=
name|ft
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
name|int
name|grams
init|=
operator|(
name|params
operator|.
name|get
argument_list|(
name|NGRAMS
argument_list|)
operator|!=
literal|null
operator|)
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|NGRAMS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|FreeTextSuggester
operator|.
name|DEFAULT_GRAMS
decl_stmt|;
name|byte
name|separator
init|=
operator|(
name|params
operator|.
name|get
argument_list|(
name|SEPARATOR
argument_list|)
operator|!=
literal|null
operator|)
condition|?
name|params
operator|.
name|get
argument_list|(
name|SEPARATOR
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
index|[
literal|0
index|]
else|:
name|FreeTextSuggester
operator|.
name|DEFAULT_SEPARATOR
decl_stmt|;
return|return
operator|new
name|FreeTextSuggester
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|grams
argument_list|,
name|separator
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|storeFileName
specifier|public
name|String
name|storeFileName
parameter_list|()
block|{
return|return
name|FILENAME
return|;
block|}
block|}
end_class
end_unit
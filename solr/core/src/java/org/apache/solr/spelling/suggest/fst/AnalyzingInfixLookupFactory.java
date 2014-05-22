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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|AnalyzingInfixSuggester
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
name|store
operator|.
name|FSDirectory
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
comment|/**  * Factory for {@link AnalyzingInfixSuggester}  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AnalyzingInfixLookupFactory
specifier|public
class|class
name|AnalyzingInfixLookupFactory
extends|extends
name|LookupFactory
block|{
comment|/**    * The analyzer used at "query-time" and "build-time" to analyze suggestions.    */
DECL|field|QUERY_ANALYZER
specifier|protected
specifier|static
specifier|final
name|String
name|QUERY_ANALYZER
init|=
literal|"suggestAnalyzerFieldType"
decl_stmt|;
comment|/**    * The path where the underlying index is stored    * if no index is found, it will be generated by    * the AnalyzingInfixSuggester    */
DECL|field|INDEX_PATH
specifier|protected
specifier|static
specifier|final
name|String
name|INDEX_PATH
init|=
literal|"indexPath"
decl_stmt|;
comment|/**    * Minimum number of leading characters before PrefixQuery is used (default 4).     * Prefixes shorter than this are indexed as character ngrams     * (increasing index size but making lookups faster)    */
DECL|field|MIN_PREFIX_CHARS
specifier|protected
specifier|static
specifier|final
name|String
name|MIN_PREFIX_CHARS
init|=
literal|"minPrefixChars"
decl_stmt|;
comment|/**     * Default path where the index for the suggester is stored/loaded from    * */
DECL|field|DEFAULT_INDEX_PATH
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_INDEX_PATH
init|=
literal|"analyzingInfixSuggesterIndexDir"
decl_stmt|;
comment|/**    * File name for the automaton.    */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"iwfsta.bin"
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
comment|// mandatory parameter
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
name|getIndexAnalyzer
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
comment|// optional parameters
name|String
name|indexPath
init|=
name|params
operator|.
name|get
argument_list|(
name|INDEX_PATH
argument_list|)
operator|!=
literal|null
condition|?
name|params
operator|.
name|get
argument_list|(
name|INDEX_PATH
argument_list|)
operator|.
name|toString
argument_list|()
else|:
name|DEFAULT_INDEX_PATH
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
operator|.
name|isAbsolute
argument_list|()
operator|==
literal|false
condition|)
block|{
name|indexPath
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|indexPath
expr_stmt|;
block|}
name|int
name|minPrefixChars
init|=
name|params
operator|.
name|get
argument_list|(
name|MIN_PREFIX_CHARS
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MIN_PREFIX_CHARS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
decl_stmt|;
try|try
block|{
return|return
operator|new
name|AnalyzingInfixSuggester
argument_list|(
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
argument_list|,
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
argument_list|)
argument_list|,
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|minPrefixChars
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
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

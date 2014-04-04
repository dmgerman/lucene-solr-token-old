begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|spell
operator|.
name|Dictionary
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
name|FileDictionary
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/**  * Factory for {@link FileDictionary}  */
end_comment
begin_class
DECL|class|FileDictionaryFactory
specifier|public
class|class
name|FileDictionaryFactory
extends|extends
name|DictionaryFactory
block|{
comment|/** Label for defining fieldDelimiter to be used */
DECL|field|FIELD_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_DELIMITER
init|=
literal|"fieldDelimiter"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Dictionary
name|create
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
comment|// should not happen; implies setParams was not called
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Value of params not set"
argument_list|)
throw|;
block|}
name|String
name|sourceLocation
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|Suggester
operator|.
name|LOCATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceLocation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|Suggester
operator|.
name|LOCATION
operator|+
literal|" parameter is mandatory for using FileDictionary"
argument_list|)
throw|;
block|}
name|String
name|fieldDelimiter
init|=
operator|(
name|params
operator|.
name|get
argument_list|(
name|FIELD_DELIMITER
argument_list|)
operator|!=
literal|null
operator|)
condition|?
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|FIELD_DELIMITER
argument_list|)
else|:
name|FileDictionary
operator|.
name|DEFAULT_FIELD_DELIMITER
decl_stmt|;
try|try
block|{
return|return
operator|new
name|FileDictionary
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|sourceLocation
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|fieldDelimiter
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
block|}
end_class
end_unit

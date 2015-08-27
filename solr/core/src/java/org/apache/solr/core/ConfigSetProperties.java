begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|ConfigSetProperties
specifier|public
class|class
name|ConfigSetProperties
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConfigSetProperties
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FILENAME
init|=
literal|"configsetprops.json"
decl_stmt|;
DECL|field|IMMUTABLE_CONFIGSET_ARG
specifier|public
specifier|static
specifier|final
name|String
name|IMMUTABLE_CONFIGSET_ARG
init|=
literal|"immutable"
decl_stmt|;
comment|/**    * Return the properties associated with the ConfigSet (e.g. immutable)    *    * @param loader the resource loader    * @param name the name of the config set properties file    * @return the properties in a NamedList    */
DECL|method|readFromResourceLoader
specifier|public
specifier|static
name|NamedList
name|readFromResourceLoader
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|InputStreamReader
name|reader
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|name
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrResourceNotFoundException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Did not find ConfigSet properties, assuming default properties: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to load reader for ConfigSet properties: "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|readFromInputStream
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readFromInputStream
specifier|public
specifier|static
name|NamedList
name|readFromInputStream
parameter_list|(
name|InputStreamReader
name|reader
parameter_list|)
block|{
try|try
block|{
name|JSONParser
name|jsonParser
init|=
operator|new
name|JSONParser
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
name|jsonParser
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|object
operator|instanceof
name|Map
operator|)
condition|)
block|{
specifier|final
name|String
name|objectClass
init|=
name|object
operator|==
literal|null
condition|?
literal|"null"
else|:
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid JSON type "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected Map"
argument_list|)
throw|;
block|}
return|return
operator|new
name|NamedList
argument_list|(
operator|(
name|Map
operator|)
name|object
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to load ConfigSet properties"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit

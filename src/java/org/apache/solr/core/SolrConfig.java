begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InputStream
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrConfig
specifier|public
class|class
name|SolrConfig
block|{
DECL|field|config
specifier|public
specifier|static
name|Config
name|config
decl_stmt|;
static|static
block|{
name|RuntimeException
name|e
init|=
literal|null
decl_stmt|;
name|String
name|file
init|=
literal|"solrconfig.xml"
decl_stmt|;
name|InputStream
name|is
init|=
name|Config
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|config
operator|=
operator|new
name|Config
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
literal|"/config/"
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error in solrconfig.xml"
argument_list|,
name|ee
argument_list|)
throw|;
block|}
name|Config
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"Loaded Config solrconfig.xml"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|*
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Some tests need to reach outside the classpath to get certain resources (e.g. the example configuration).  * This class provides some paths to allow them to do this.  * @lucene.internal  */
end_comment
begin_class
DECL|class|ExternalPaths
specifier|public
class|class
name|ExternalPaths
block|{
comment|/**    *<p>    * The main directory path for the solr source being built if it can be determined.  If it     * can not be determined -- possily because the current context is a client code base     * using hte test frameowrk -- then this variable will be null.    *</p>    *<p>    * Note that all other static paths available in this class are derived from the source     * home, and if it is null, those paths will just be relative to 'null' and may not be     * meaningful.    */
DECL|field|SOURCE_HOME
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_HOME
init|=
name|determineSourceHome
argument_list|()
decl_stmt|;
comment|/* @see #SOURCE_HOME */
DECL|field|WEBAPP_HOME
specifier|public
specifier|static
name|String
name|WEBAPP_HOME
init|=
operator|new
name|File
argument_list|(
name|SOURCE_HOME
argument_list|,
literal|"webapp/web"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|/* @see #SOURCE_HOME */
DECL|field|SCHEMALESS_CONFIGSET
specifier|public
specifier|static
name|String
name|SCHEMALESS_CONFIGSET
init|=
operator|new
name|File
argument_list|(
name|SOURCE_HOME
argument_list|,
literal|"server/solr/configsets/data_driven_schema_configs/conf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|TECHPRODUCTS_CONFIGSET
specifier|public
specifier|static
name|String
name|TECHPRODUCTS_CONFIGSET
init|=
operator|new
name|File
argument_list|(
name|SOURCE_HOME
argument_list|,
literal|"server/solr/configsets/sample_techproducts_configs/conf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|/* @see #SOURCE_HOME */
DECL|field|EXAMPLE_MULTICORE_HOME
specifier|public
specifier|static
name|String
name|EXAMPLE_MULTICORE_HOME
init|=
operator|new
name|File
argument_list|(
name|SOURCE_HOME
argument_list|,
literal|"example/multicore"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|SERVER_HOME
specifier|public
specifier|static
name|String
name|SERVER_HOME
init|=
operator|new
name|File
argument_list|(
name|SOURCE_HOME
argument_list|,
literal|"server/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|/**    * Ugly, ugly hack to determine the example home without depending on the CWD    * this is needed for example/multicore tests which reside outside the classpath.    * if the source home can't be determined, this method returns null.    */
DECL|method|determineSourceHome
specifier|static
name|String
name|determineSourceHome
parameter_list|()
block|{
try|try
block|{
name|File
name|file
decl_stmt|;
try|try
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
literal|"solr/conf"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"solr/conf"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// If there is no "solr/conf" in the classpath, fall back to searching from the current directory.
name|file
operator|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|File
name|base
init|=
name|file
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"solr/CHANGES.txt"
argument_list|)
operator|.
name|exists
argument_list|()
operator|)
operator|&&
literal|null
operator|!=
name|base
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
literal|null
operator|==
name|base
operator|)
condition|?
literal|null
else|:
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"solr/"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// all bets are off
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit

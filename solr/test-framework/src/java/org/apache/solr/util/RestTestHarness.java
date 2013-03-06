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
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|IO
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import
begin_comment
comment|/**  * Facilitates testing Solr's REST API via a provided embedded Jetty  */
end_comment
begin_class
DECL|class|RestTestHarness
specifier|public
class|class
name|RestTestHarness
extends|extends
name|BaseTestHarness
block|{
DECL|field|serverProvider
specifier|private
name|RESTfulServerProvider
name|serverProvider
decl_stmt|;
DECL|method|RestTestHarness
specifier|public
name|RestTestHarness
parameter_list|(
name|RESTfulServerProvider
name|serverProvider
parameter_list|)
block|{
name|this
operator|.
name|serverProvider
operator|=
name|serverProvider
expr_stmt|;
block|}
DECL|method|getBaseURL
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
name|serverProvider
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
comment|/**    * Validates a "query" response against an array of XPath test strings    *    * @param request the Query to process    * @return null if all good, otherwise the first test that fails.    * @exception Exception any exception in the response.    * @exception java.io.IOException if there is a problem writing the XML    */
DECL|method|validateQuery
specifier|public
name|String
name|validateQuery
parameter_list|(
name|String
name|request
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|res
init|=
name|query
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|validateXPath
argument_list|(
name|res
argument_list|,
name|tests
argument_list|)
return|;
block|}
comment|/**    * Processes a "query" using a URL path (with no context path) + optional query params,    * e.g. "/schema/fields?indent=on"    *    * @param request the URL path and optional query params    * @return The response to the query    * @exception Exception any exception in the response.    */
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|String
name|request
parameter_list|)
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|getBaseURL
argument_list|()
operator|+
name|request
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|InputStream
name|inputStream
init|=
literal|null
decl_stmt|;
name|StringWriter
name|strWriter
decl_stmt|;
try|try
block|{
try|try
block|{
name|inputStream
operator|=
name|connection
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|inputStream
operator|=
name|connection
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
block|}
name|strWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|strWriter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
return|return
name|strWriter
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|checkQueryStatus
specifier|public
name|String
name|checkQueryStatus
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|code
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|response
init|=
name|query
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|String
name|valid
init|=
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']="
operator|+
name|code
argument_list|)
decl_stmt|;
return|return
operator|(
literal|null
operator|==
name|valid
operator|)
condition|?
literal|null
else|:
name|response
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"?!? static xpath has bug?"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|checkQueryStatus
argument_list|(
literal|"/admin/cores?action=RELOAD"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|xml
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RELOAD failed:\n"
operator|+
name|xml
argument_list|)
throw|;
block|}
block|}
comment|/**    * Processes an "update" (add, commit or optimize) and    * returns the response as a String.    *    * @param xml The XML of the update    * @return The XML response to the update    */
annotation|@
name|Override
DECL|method|update
specifier|public
name|String
name|update
parameter_list|(
name|String
name|xml
parameter_list|)
block|{
try|try
block|{
return|return
name|query
argument_list|(
literal|"/update?stream.base="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|xml
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit

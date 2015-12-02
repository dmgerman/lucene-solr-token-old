begin_unit
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
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
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|util
operator|.
name|Enumeration
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterRegistration
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|RequestDispatcher
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Servlet
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRegistration
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|SessionCookieConfig
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|SessionTrackingMode
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterRegistration
operator|.
name|Dynamic
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|descriptor
operator|.
name|JspConfigDescriptor
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
name|collections
operator|.
name|iterators
operator|.
name|IteratorEnumeration
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpClientConfigurer
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|Krb5HttpClientConfigurer
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|KerberosPlugin
specifier|public
class|class
name|KerberosPlugin
extends|extends
name|AuthenticationPlugin
implements|implements
name|HttpClientInterceptorPlugin
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|kerberosConfigurer
name|HttpClientConfigurer
name|kerberosConfigurer
init|=
operator|new
name|Krb5HttpClientConfigurer
argument_list|()
decl_stmt|;
DECL|field|kerberosFilter
name|Filter
name|kerberosFilter
init|=
operator|new
name|KerberosFilter
argument_list|()
decl_stmt|;
DECL|field|NAME_RULES_PARAM
specifier|final
name|String
name|NAME_RULES_PARAM
init|=
literal|"solr.kerberos.name.rules"
decl_stmt|;
DECL|field|COOKIE_DOMAIN_PARAM
specifier|final
name|String
name|COOKIE_DOMAIN_PARAM
init|=
literal|"solr.kerberos.cookie.domain"
decl_stmt|;
DECL|field|COOKIE_PATH_PARAM
specifier|final
name|String
name|COOKIE_PATH_PARAM
init|=
literal|"solr.kerberos.cookie.path"
decl_stmt|;
DECL|field|PRINCIPAL_PARAM
specifier|final
name|String
name|PRINCIPAL_PARAM
init|=
literal|"solr.kerberos.principal"
decl_stmt|;
DECL|field|KEYTAB_PARAM
specifier|final
name|String
name|KEYTAB_PARAM
init|=
literal|"solr.kerberos.keytab"
decl_stmt|;
DECL|field|TOKEN_VALID_PARAM
specifier|final
name|String
name|TOKEN_VALID_PARAM
init|=
literal|"solr.kerberos.token.valid"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"kerberos.name.rules"
argument_list|,
name|NAME_RULES_PARAM
argument_list|,
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"token.valid"
argument_list|,
name|TOKEN_VALID_PARAM
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"cookie.domain"
argument_list|,
name|COOKIE_DOMAIN_PARAM
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"cookie.path"
argument_list|,
name|COOKIE_PATH_PARAM
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"kerberos.principal"
argument_list|,
name|PRINCIPAL_PARAM
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|putParam
argument_list|(
name|params
argument_list|,
literal|"kerberos.keytab"
argument_list|,
name|KEYTAB_PARAM
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Params: "
operator|+
name|params
argument_list|)
expr_stmt|;
name|FilterConfig
name|conf
init|=
operator|new
name|FilterConfig
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
name|noContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
return|return
operator|new
name|IteratorEnumeration
argument_list|(
name|params
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|param
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFilterName
parameter_list|()
block|{
return|return
literal|"KerberosFilter"
return|;
block|}
block|}
decl_stmt|;
name|kerberosFilter
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
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
literal|"Error initializing kerberos authentication plugin: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|putParam
specifier|private
name|void
name|putParam
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|internalParamName
parameter_list|,
name|String
name|externalParamName
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|externalParamName
argument_list|,
name|defaultValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Missing required parameter '"
operator|+
name|externalParamName
operator|+
literal|"'."
argument_list|)
throw|;
block|}
name|params
operator|.
name|put
argument_list|(
name|internalParamName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|void
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|req
parameter_list|,
name|ServletResponse
name|rsp
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Request to authenticate using kerberos: "
operator|+
name|req
argument_list|)
expr_stmt|;
name|kerberosFilter
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|chain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClientConfigurer
specifier|public
name|HttpClientConfigurer
name|getClientConfigurer
parameter_list|()
block|{
return|return
name|kerberosConfigurer
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|kerberosFilter
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|field|noContext
specifier|protected
specifier|static
name|ServletContext
name|noContext
init|=
operator|new
name|ServletContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setSessionTrackingModes
parameter_list|(
name|Set
argument_list|<
name|SessionTrackingMode
argument_list|>
name|sessionTrackingModes
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|boolean
name|setInitParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|object
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|log
parameter_list|(
name|Exception
name|exception
parameter_list|,
name|String
name|msg
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|log
parameter_list|(
name|String
name|msg
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|String
name|getVirtualServerName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|SessionCookieConfig
name|getSessionCookieConfig
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|Servlet
argument_list|>
name|getServlets
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|ServletRegistration
argument_list|>
name|getServletRegistrations
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ServletRegistration
name|getServletRegistration
parameter_list|(
name|String
name|servletName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getServletNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getServletContextName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Servlet
name|getServlet
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ServletException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getServerInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getResourcePaths
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getResourceAsStream
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|URL
name|getResource
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|MalformedURLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRealPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|RequestDispatcher
name|getNamedDispatcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMinorVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMimeType
parameter_list|(
name|String
name|file
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMajorVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|JspConfigDescriptor
name|getJspConfigDescriptor
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|FilterRegistration
argument_list|>
name|getFilterRegistrations
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|FilterRegistration
name|getFilterRegistration
parameter_list|(
name|String
name|filterName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SessionTrackingMode
argument_list|>
name|getEffectiveSessionTrackingModes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEffectiveMinorVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEffectiveMajorVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SessionTrackingMode
argument_list|>
name|getDefaultSessionTrackingModes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ServletContext
name|getContext
parameter_list|(
name|String
name|uripath
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClassLoader
name|getClassLoader
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getAttributeNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|declareRoles
parameter_list|(
name|String
modifier|...
name|roleNames
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Servlet
parameter_list|>
name|T
name|createServlet
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|ServletException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|EventListener
parameter_list|>
name|T
name|createListener
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|ServletException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Filter
parameter_list|>
name|T
name|createFilter
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|ServletException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|javax
operator|.
name|servlet
operator|.
name|ServletRegistration
operator|.
name|Dynamic
name|addServlet
parameter_list|(
name|String
name|servletName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Servlet
argument_list|>
name|servletClass
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|javax
operator|.
name|servlet
operator|.
name|ServletRegistration
operator|.
name|Dynamic
name|addServlet
parameter_list|(
name|String
name|servletName
parameter_list|,
name|Servlet
name|servlet
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|javax
operator|.
name|servlet
operator|.
name|ServletRegistration
operator|.
name|Dynamic
name|addServlet
parameter_list|(
name|String
name|servletName
parameter_list|,
name|String
name|className
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addListener
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|EventListener
argument_list|>
name|listenerClass
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|EventListener
parameter_list|>
name|void
name|addListener
parameter_list|(
name|T
name|t
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|addListener
parameter_list|(
name|String
name|className
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|Dynamic
name|addFilter
parameter_list|(
name|String
name|filterName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Dynamic
name|addFilter
parameter_list|(
name|String
name|filterName
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Dynamic
name|addFilter
parameter_list|(
name|String
name|filterName
parameter_list|,
name|String
name|className
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit

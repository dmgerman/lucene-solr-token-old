begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *<code>SolrQueryResponse</code> is used by a query handler to return  * the response to a query request.  *  *<p>  *<a name="returnable_data" /><b>Note On Returnable Data...</b><br/>  * A<code>SolrQueryResponse</code> may contain the following types of  * Objects generated by the<code>SolrRequestHandler</code> that processed  * the request.  *</p>  *<ul>  *<li>{@link String}</li>  *<li>{@link Integer}</li>  *<li>{@link Long}</li>  *<li>{@link Float}</li>  *<li>{@link Double}</li>  *<li>{@link Boolean}</li>  *<li>{@link Date}</li>  *<li>{@link org.apache.solr.search.DocList}</li>  *<li>{@link Map} containing any of the items in this list</li>  *<li>{@link NamedList} containing any of the items in this list</li>  *<li>{@link Collection} containing any of the items in this list</li>  *<li>Array containing any of the items in this list</li>  *<li>null</li>  *</ul>  *  * @version $Id$  * @since solr 0.9  */
end_comment
begin_class
DECL|class|SolrQueryResponse
specifier|public
class|class
name|SolrQueryResponse
block|{
comment|/**    * Container for user defined values    * @see #getValues    * @see #add    * @see #setAllValues    * @see<a href="#returnable_data">Note on Returnable Data</a>    */
DECL|field|values
specifier|protected
name|NamedList
name|values
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
comment|/**    * Container for storing information that should be logged by Solr before returning.    */
DECL|field|toLog
specifier|protected
name|NamedList
name|toLog
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
DECL|field|defaultReturnFields
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|defaultReturnFields
decl_stmt|;
comment|// error if this is set...
DECL|field|err
specifier|protected
name|Exception
name|err
decl_stmt|;
comment|/**    * Should this response be tagged with HTTP caching headers?    */
DECL|field|httpCaching
specifier|protected
name|boolean
name|httpCaching
init|=
literal|true
decl_stmt|;
comment|/***    // another way of returning an error   int errCode;   String errMsg;   ***/
DECL|method|SolrQueryResponse
specifier|public
name|SolrQueryResponse
parameter_list|()
block|{   }
comment|/**    * Gets data to be returned in this response    * @see<a href="#returnable_data">Note on Returnable Data</a>    */
DECL|method|getValues
specifier|public
name|NamedList
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|/**    * Sets data to be returned in this response    * @see<a href="#returnable_data">Note on Returnable Data</a>    */
DECL|method|setAllValues
specifier|public
name|void
name|setAllValues
parameter_list|(
name|NamedList
name|nameValuePairs
parameter_list|)
block|{
name|values
operator|=
name|nameValuePairs
expr_stmt|;
block|}
comment|/**    * Sets the document field names of fields to return by default when    * returning DocLists    */
DECL|method|setReturnFields
specifier|public
name|void
name|setReturnFields
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|defaultReturnFields
operator|=
name|fields
expr_stmt|;
block|}
comment|// TODO: should this be represented as a String[] such
comment|// that order can be maintained if needed?
comment|/**    * Gets the document field names of fields to return by default when    * returning DocLists    */
DECL|method|getReturnFields
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getReturnFields
parameter_list|()
block|{
return|return
name|defaultReturnFields
return|;
block|}
comment|/**    * Appends a named value to the list of named values to be returned.    * @param name  the name of the value - may be null if unnamed    * @param val   the value to add - also may be null since null is a legal value    * @see<a href="#returnable_data">Note on Returnable Data</a>    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Causes an error to be returned instead of the results.    */
DECL|method|setException
specifier|public
name|void
name|setException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|err
operator|=
name|e
expr_stmt|;
block|}
comment|/**    * Returns an Exception if there was a fatal error in processing the request.    * Returns null if the request succeeded.    */
DECL|method|getException
specifier|public
name|Exception
name|getException
parameter_list|()
block|{
return|return
name|err
return|;
block|}
comment|/**    * The endtime of the request in milliseconds.    * Used to calculate query time.    * @see #setEndTime(long)    * @see #getEndTime()    */
DECL|field|endtime
specifier|protected
name|long
name|endtime
decl_stmt|;
comment|/**    * Get the time in milliseconds when the response officially finished.     */
DECL|method|getEndTime
specifier|public
name|long
name|getEndTime
parameter_list|()
block|{
if|if
condition|(
name|endtime
operator|==
literal|0
condition|)
block|{
name|setEndTime
argument_list|()
expr_stmt|;
block|}
return|return
name|endtime
return|;
block|}
comment|/**    * Stop the timer for how long this query took.    * @see #setEndTime(long)    */
DECL|method|setEndTime
specifier|public
name|long
name|setEndTime
parameter_list|()
block|{
return|return
name|setEndTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Set the in milliseconds when the response officially finished.     * @see #setEndTime()    */
DECL|method|setEndTime
specifier|public
name|long
name|setEndTime
parameter_list|(
name|long
name|endtime
parameter_list|)
block|{
if|if
condition|(
name|endtime
operator|!=
literal|0
condition|)
block|{
name|this
operator|.
name|endtime
operator|=
name|endtime
expr_stmt|;
block|}
return|return
name|this
operator|.
name|endtime
return|;
block|}
comment|/** Repsonse header to be logged */
DECL|method|getResponseHeader
specifier|public
name|NamedList
name|getResponseHeader
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|header
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
decl_stmt|;
return|return
name|header
return|;
block|}
comment|/** Add a value to be logged.    *     * @param name name of the thing to log    * @param val value of the thing to log    */
DECL|method|addToLog
specifier|public
name|void
name|addToLog
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|toLog
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/** Get loggable items.    *     * @return things to log    */
DECL|method|getToLog
specifier|public
name|NamedList
name|getToLog
parameter_list|()
block|{
return|return
name|toLog
return|;
block|}
comment|/**    * Enables or disables the emission of HTTP caching headers for this response.    * @param httpCaching true=emit caching headers, false otherwise    */
DECL|method|setHttpCaching
specifier|public
name|void
name|setHttpCaching
parameter_list|(
name|boolean
name|httpCaching
parameter_list|)
block|{
name|this
operator|.
name|httpCaching
operator|=
name|httpCaching
expr_stmt|;
block|}
comment|/**    * Should this response emit HTTP caching headers?    * @return true=yes emit headers, false otherwise    */
DECL|method|isHttpCaching
specifier|public
name|boolean
name|isHttpCaching
parameter_list|()
block|{
return|return
name|this
operator|.
name|httpCaching
return|;
block|}
block|}
end_class
end_unit

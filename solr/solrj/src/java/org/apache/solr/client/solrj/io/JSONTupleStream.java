begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
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
name|io
package|;
end_package
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|SolrRequest
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
name|SolrServerException
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
name|InputStreamResponseParser
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
name|request
operator|.
name|QueryRequest
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
name|ModifiableSolrParams
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/*   Queries a Solr instance, and maps SolrDocs to Tuples.   Initial version works with the json format and only SolrDocs are handled. */
end_comment
begin_class
DECL|class|JSONTupleStream
specifier|public
class|class
name|JSONTupleStream
block|{
DECL|field|path
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|path
decl_stmt|;
comment|// future... for more general stream handling
DECL|field|reader
specifier|private
name|Reader
name|reader
decl_stmt|;
DECL|field|parser
specifier|private
name|JSONParser
name|parser
decl_stmt|;
DECL|field|atDocs
specifier|private
name|boolean
name|atDocs
decl_stmt|;
DECL|method|JSONTupleStream
specifier|public
name|JSONTupleStream
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|JSONParser
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|// temporary...
DECL|method|create
specifier|public
specifier|static
name|JSONTupleStream
name|create
parameter_list|(
name|SolrClient
name|server
parameter_list|,
name|SolrParams
name|requestParams
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|p
init|=
name|requestParams
operator|.
name|get
argument_list|(
literal|"qt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|ModifiableSolrParams
name|modifiableSolrParams
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|requestParams
decl_stmt|;
name|modifiableSolrParams
operator|.
name|remove
argument_list|(
literal|"qt"
argument_list|)
expr_stmt|;
block|}
name|QueryRequest
name|query
init|=
operator|new
name|QueryRequest
argument_list|(
name|requestParams
argument_list|)
decl_stmt|;
name|query
operator|.
name|setPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|query
operator|.
name|setResponseParser
argument_list|(
operator|new
name|InputStreamResponseParser
argument_list|(
literal|"json"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|genericResponse
init|=
name|server
operator|.
name|request
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
operator|(
name|InputStream
operator|)
name|genericResponse
operator|.
name|get
argument_list|(
literal|"stream"
argument_list|)
decl_stmt|;
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
operator|new
name|JSONTupleStream
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** returns the next Tuple or null */
DECL|method|next
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|atDocs
condition|)
block|{
name|boolean
name|found
init|=
name|advanceToDocs
argument_list|()
decl_stmt|;
name|atDocs
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|found
condition|)
return|return
literal|null
return|;
block|}
comment|// advance past ARRAY_START (in the case that we just advanced to docs, or OBJECT_END left over from the last call.
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|==
name|JSONParser
operator|.
name|ARRAY_END
condition|)
return|return
literal|null
return|;
name|Object
name|o
init|=
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
name|parser
argument_list|)
decl_stmt|;
comment|// right now, getVal will leave the last event read as OBJECT_END
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|expect
specifier|private
name|void
name|expect
parameter_list|(
name|int
name|parserEventType
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|!=
name|parserEventType
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"JSONTupleStream: expected "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|parserEventType
argument_list|)
operator|+
literal|" but got "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|event
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|expect
specifier|private
name|void
name|expect
parameter_list|(
name|String
name|mapKey
parameter_list|)
block|{     }
DECL|method|advanceToMapKey
specifier|private
name|boolean
name|advanceToMapKey
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|deepSearch
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|String
name|val
init|=
name|parser
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
break|break;
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
return|return
literal|false
return|;
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
if|if
condition|(
name|deepSearch
condition|)
block|{
name|boolean
name|found
init|=
name|advanceToMapKey
argument_list|(
name|key
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|advanceToMapKey
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
name|skipArray
argument_list|(
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|skipArray
specifier|private
name|void
name|skipArray
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|deepSearch
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
name|advanceToMapKey
argument_list|(
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
name|skipArray
argument_list|(
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_END
case|:
return|return;
block|}
block|}
block|}
DECL|method|advanceToDocs
specifier|private
name|boolean
name|advanceToDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|expect
argument_list|(
name|JSONParser
operator|.
name|OBJECT_START
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
name|advanceToMapKey
argument_list|(
literal|"docs"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|JSONParser
operator|.
name|ARRAY_START
argument_list|)
expr_stmt|;
return|return
name|found
return|;
block|}
block|}
end_class
end_unit

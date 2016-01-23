begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|util
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Fieldable
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
name|UnicodeUtil
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
name|CommonParams
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
name|request
operator|.
name|SolrQueryRequest
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
name|SchemaField
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
name|DocIterator
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
name|DocList
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
comment|/**  * A description of the PHP serialization format can be found here:  * http://www.hurring.com/scott/code/perl/serialize/  *  *<p>  * In order to support PHP Serialized strings with a proper byte count, This ResponseWriter  * must know if the Writers passed to it will result in an output of CESU-8 (UTF-8 w/o support  * for large code points outside of the BMP)  *<p>  * Currently Solr assumes that all Jetty servlet containers (detected using the "jetty.home"  * system property) use CESU-8 instead of UTF-8 (verified to the current release of 6.1.20).  *<p>  * In installations where Solr auto-detects incorrectly, the Solr Administrator should set the  * "solr.phps.cesu8" system property to either "true" or "false" accordingly.  */
end_comment
begin_class
DECL|class|PHPSerializedResponseWriter
specifier|public
class|class
name|PHPSerializedResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|CONTENT_TYPE_PHP_UTF8
specifier|static
name|String
name|CONTENT_TYPE_PHP_UTF8
init|=
literal|"text/x-php-serialized;charset=UTF-8"
decl_stmt|;
comment|// Is this servlet container's UTF-8 encoding actually CESU-8 (i.e. lacks support for
comment|// large characters outside the BMP).
DECL|field|CESU8
name|boolean
name|CESU8
init|=
literal|false
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|n
parameter_list|)
block|{
name|String
name|cesu8Setting
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.phps.cesu8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cesu8Setting
operator|!=
literal|null
condition|)
block|{
name|CESU8
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|cesu8Setting
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// guess at the setting.
comment|// Jetty up until 6.1.20 at least (and probably versions after) uses CESU8
name|CESU8
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.home"
argument_list|)
operator|!=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|PHPSerializedWriter
name|w
init|=
operator|new
name|PHPSerializedWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|,
name|CESU8
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|CONTENT_TYPE_TEXT_UTF8
return|;
block|}
block|}
end_class
begin_class
DECL|class|PHPSerializedWriter
class|class
name|PHPSerializedWriter
extends|extends
name|JSONWriter
block|{
DECL|field|CESU8
specifier|final
specifier|private
name|boolean
name|CESU8
decl_stmt|;
DECL|field|utf8
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8
decl_stmt|;
DECL|method|PHPSerializedWriter
specifier|public
name|PHPSerializedWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|boolean
name|CESU8
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|this
operator|.
name|CESU8
operator|=
name|CESU8
expr_stmt|;
name|this
operator|.
name|utf8
operator|=
name|CESU8
condition|?
literal|null
else|:
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
expr_stmt|;
comment|// never indent serialized PHP data
name|doIndent
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|writeResponse
specifier|public
name|void
name|writeResponse
parameter_list|()
throws|throws
name|IOException
block|{
name|Boolean
name|omitHeader
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|omitHeader
operator|!=
literal|null
operator|&&
name|omitHeader
condition|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|remove
argument_list|(
literal|"responseHeader"
argument_list|)
expr_stmt|;
name|writeNamedList
argument_list|(
literal|null
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNamedList
specifier|public
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNamedListAsMapMangled
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDoc
specifier|public
name|void
name|writeDoc
parameter_list|(
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|Fieldable
argument_list|>
name|fields
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|returnFields
parameter_list|,
name|Map
name|pseudoFields
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Fieldable
argument_list|>
name|single
init|=
operator|new
name|ArrayList
argument_list|<
name|Fieldable
argument_list|>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|MultiValueField
argument_list|>
name|multi
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MultiValueField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|ff
range|:
name|fields
control|)
block|{
name|String
name|fname
init|=
name|ff
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|returnFields
operator|!=
literal|null
operator|&&
operator|!
name|returnFields
operator|.
name|contains
argument_list|(
name|fname
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// if the field is multivalued, it may have other values further on... so
comment|// build up a list for each multi-valued field.
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|MultiValueField
name|mf
init|=
name|multi
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|mf
operator|==
literal|null
condition|)
block|{
name|mf
operator|=
operator|new
name|MultiValueField
argument_list|(
name|sf
argument_list|,
name|ff
argument_list|)
expr_stmt|;
name|multi
operator|.
name|put
argument_list|(
name|fname
argument_list|,
name|mf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mf
operator|.
name|fields
operator|.
name|add
argument_list|(
name|ff
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|single
operator|.
name|add
argument_list|(
name|ff
argument_list|)
expr_stmt|;
block|}
block|}
comment|// obtain number of fields in doc
name|writeArrayOpener
argument_list|(
name|single
operator|.
name|size
argument_list|()
operator|+
name|multi
operator|.
name|size
argument_list|()
operator|+
operator|(
operator|(
name|pseudoFields
operator|!=
literal|null
operator|)
condition|?
name|pseudoFields
operator|.
name|size
argument_list|()
else|:
literal|0
operator|)
argument_list|)
expr_stmt|;
comment|// output single value fields
for|for
control|(
name|Fieldable
name|ff
range|:
name|single
control|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|ff
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|writeKey
argument_list|(
name|ff
operator|.
name|name
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sf
operator|.
name|write
argument_list|(
name|this
argument_list|,
name|ff
operator|.
name|name
argument_list|()
argument_list|,
name|ff
argument_list|)
expr_stmt|;
block|}
comment|// output multi value fields
for|for
control|(
name|MultiValueField
name|mvf
range|:
name|multi
operator|.
name|values
argument_list|()
control|)
block|{
name|writeKey
argument_list|(
name|mvf
operator|.
name|sfield
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeArrayOpener
argument_list|(
name|mvf
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Fieldable
name|ff
range|:
name|mvf
operator|.
name|fields
control|)
block|{
name|writeKey
argument_list|(
name|i
operator|++
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mvf
operator|.
name|sfield
operator|.
name|write
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
name|ff
argument_list|)
expr_stmt|;
block|}
name|writeArrayCloser
argument_list|()
expr_stmt|;
block|}
comment|// output pseudo fields
if|if
condition|(
name|pseudoFields
operator|!=
literal|null
operator|&&
name|pseudoFields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writeMap
argument_list|(
literal|null
argument_list|,
name|pseudoFields
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|writeArrayCloser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDocList
specifier|public
name|void
name|writeDocList
parameter_list|(
name|String
name|name
parameter_list|,
name|DocList
name|ids
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|Map
name|otherFields
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|includeScore
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|includeScore
operator|=
name|fields
operator|.
name|contains
argument_list|(
literal|"score"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|includeScore
operator|)
operator|||
name|fields
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|fields
operator|=
literal|null
expr_stmt|;
comment|// null means return all stored fields
block|}
block|}
name|int
name|sz
init|=
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
name|writeMapOpener
argument_list|(
name|includeScore
condition|?
literal|4
else|:
literal|3
argument_list|)
expr_stmt|;
name|writeKey
argument_list|(
literal|"numFound"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
literal|null
argument_list|,
name|ids
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|writeKey
argument_list|(
literal|"start"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
literal|null
argument_list|,
name|ids
operator|.
name|offset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeScore
condition|)
block|{
name|writeKey
argument_list|(
literal|"maxScore"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeFloat
argument_list|(
literal|null
argument_list|,
name|ids
operator|.
name|maxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeKey
argument_list|(
literal|"docs"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeArrayOpener
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|ids
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|id
argument_list|,
name|fields
argument_list|)
decl_stmt|;
name|writeKey
argument_list|(
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeDoc
argument_list|(
literal|null
argument_list|,
name|doc
argument_list|,
name|fields
argument_list|,
operator|(
name|includeScore
condition|?
name|iterator
operator|.
name|score
argument_list|()
else|:
literal|0.0f
operator|)
argument_list|,
name|includeScore
argument_list|)
expr_stmt|;
block|}
name|writeMapCloser
argument_list|()
expr_stmt|;
if|if
condition|(
name|otherFields
operator|!=
literal|null
condition|)
block|{
name|writeMap
argument_list|(
literal|null
argument_list|,
name|otherFields
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|writeMapCloser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
index|[]
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeMapOpener
argument_list|(
name|val
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeKey
argument_list|(
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeVal
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|val
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writeMapCloser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterator
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
name|vals
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|val
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|val
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeArray
argument_list|(
name|name
argument_list|,
name|vals
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapOpener
specifier|public
name|void
name|writeMapOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
comment|// negative size value indicates that something has gone wrong
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Map size must not be negative"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"a:"
operator|+
name|size
operator|+
literal|":{"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapSeparator
specifier|public
name|void
name|writeMapSeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|writeMapCloser
specifier|public
name|void
name|writeMapCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayOpener
specifier|public
name|void
name|writeArrayOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
comment|// negative size value indicates that something has gone wrong
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Array size must not be negative"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"a:"
operator|+
name|size
operator|+
literal|":{"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArraySeparator
specifier|public
name|void
name|writeArraySeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|writeArrayCloser
specifier|public
name|void
name|writeArrayCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"N;"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeKey
specifier|protected
name|void
name|writeKey
parameter_list|(
name|String
name|fname
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStr
argument_list|(
literal|null
argument_list|,
name|fname
argument_list|,
name|needsEscaping
argument_list|)
expr_stmt|;
block|}
DECL|method|writeKey
name|void
name|writeKey
parameter_list|(
name|int
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
literal|null
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
name|val
condition|?
literal|"b:1;"
else|:
literal|"b:0;"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'t'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"i:"
operator|+
name|val
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"d:"
operator|+
name|val
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStr
specifier|public
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
comment|// serialized PHP strings don't need to be escaped at all, however the
comment|// string size reported needs be the number of bytes rather than chars.
name|int
name|nBytes
decl_stmt|;
if|if
condition|(
name|CESU8
condition|)
block|{
name|nBytes
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|<=
literal|'\u007f'
condition|)
block|{
name|nBytes
operator|+=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|<=
literal|'\u07ff'
condition|)
block|{
name|nBytes
operator|+=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|nBytes
operator|+=
literal|3
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
argument_list|)
expr_stmt|;
name|nBytes
operator|=
name|utf8
operator|.
name|length
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"s:"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|nBytes
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|":\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\";"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

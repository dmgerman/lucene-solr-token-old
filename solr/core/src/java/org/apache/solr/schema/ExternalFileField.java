begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|StorableField
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|SortField
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
name|response
operator|.
name|TextResponseWriter
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
name|QParser
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
name|function
operator|.
name|FileFloatSource
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
name|Map
import|;
end_import
begin_comment
comment|/** Get values from an external file instead of the index.  *  *<p/><code>keyField</code> will normally be the unique key field, but it doesn't have to be.  *<ul><li> It's OK to have a keyField value that can't be found in the index</li>  *<li>It's OK to have some documents without a keyField in the file (defVal is used as the default)</li>  *<li>It's OK for a keyField value to point to multiple documents (no uniqueness requirement)</li>  *</ul>  *<code>valType</code> is a reference to another fieldType to define the value type of this field  * (must currently be TrieFloatField or FloatField (valType="pfloat|float|tfloat") if used).  * This parameter has never been implemented. As of Solr 3.6/4.0 it is optional and can be omitted.  *  * The format of the external file is simply newline separated keyFieldValue=floatValue.  *<br/>Example:  *<br/><code>doc33=1.414</code>  *<br/><code>doc34=3.14159</code>  *<br/><code>doc40=42</code>  *  *<p/>Solr looks for the external file in the index directory under the name of  * external_&lt;fieldname&gt; or external_&lt;fieldname&gt;.*  *  *<p/>If any files of the latter pattern appear, the last (after being sorted by name) will be used and previous versions will be deleted.  * This is to help support systems where one may not be able to overwrite a file (like Windows, if the file is in use).  *<p/>If the external file has already been loaded, and it is changed, those changes will not be visible until a commit has been done.  *<p/>The external file may be sorted or unsorted by the key field, but it will be substantially slower (untested) if it isn't sorted.  *<p/>Fields of this type may currently only be used as a ValueSource in a FunctionQuery.  *  * @see ExternalFileFieldReloader  */
end_comment
begin_class
DECL|class|ExternalFileField
specifier|public
class|class
name|ExternalFileField
extends|extends
name|FieldType
block|{
DECL|field|ftype
specifier|private
name|FieldType
name|ftype
decl_stmt|;
DECL|field|keyFieldName
specifier|private
name|String
name|keyFieldName
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|defVal
specifier|private
name|float
name|defVal
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|restrictProps
argument_list|(
name|SORT_MISSING_FIRST
operator||
name|SORT_MISSING_LAST
argument_list|)
expr_stmt|;
comment|// valType has never been used for anything except to throw an error, so make it optional since the
comment|// code (see getValueSource) gives you a FileFloatSource.
name|String
name|ftypeS
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"valType"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ftypeS
operator|!=
literal|null
condition|)
block|{
name|ftype
operator|=
name|schema
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|ftypeS
argument_list|)
expr_stmt|;
if|if
condition|(
name|ftype
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|ftype
operator|instanceof
name|FloatField
operator|)
operator|&&
operator|!
operator|(
name|ftype
operator|instanceof
name|TrieFloatField
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Only float and pfloat (Trie|Float)Field are currently supported as external field type.  Got "
operator|+
name|ftypeS
argument_list|)
throw|;
block|}
block|}
name|keyFieldName
operator|=
name|args
operator|.
name|remove
argument_list|(
literal|"keyField"
argument_list|)
expr_stmt|;
name|String
name|defValS
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"defVal"
argument_list|)
decl_stmt|;
name|defVal
operator|=
name|defValS
operator|==
literal|null
condition|?
literal|0
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|defValS
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|FileFloatSource
name|source
init|=
name|getFileFloatSource
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|source
operator|.
name|getSortField
argument_list|(
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
return|return
name|getFileFloatSource
argument_list|(
name|field
argument_list|,
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
operator|.
name|getDataDir
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a FileFloatSource for the given field, using the datadir from the    * IndexSchema    * @param field the field to get a source for    * @return a FileFloatSource    */
DECL|method|getFileFloatSource
specifier|public
name|FileFloatSource
name|getFileFloatSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
return|return
name|getFileFloatSource
argument_list|(
name|field
argument_list|,
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getDataDir
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a FileFloatSource for the given field.  Call this in preference to    * getFileFloatSource(SchemaField) if this may be called before the Core is    * fully initialised (eg in SolrEventListener calls).    * @param field the field to get a source for    * @param datadir the data directory in which to look for the external file    * @return a FileFloatSource    */
DECL|method|getFileFloatSource
specifier|public
name|FileFloatSource
name|getFileFloatSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|datadir
parameter_list|)
block|{
comment|// Because the float source uses a static cache, all source objects will
comment|// refer to the same data.
return|return
operator|new
name|FileFloatSource
argument_list|(
name|field
argument_list|,
name|getKeyField
argument_list|()
argument_list|,
name|defVal
argument_list|,
name|datadir
argument_list|)
return|;
block|}
comment|// If no key field is defined, we use the unique key field
DECL|method|getKeyField
specifier|private
name|SchemaField
name|getKeyField
parameter_list|()
block|{
return|return
name|keyFieldName
operator|==
literal|null
condition|?
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
else|:
name|schema
operator|.
name|getField
argument_list|(
name|keyFieldName
argument_list|)
return|;
block|}
block|}
end_class
end_unit
